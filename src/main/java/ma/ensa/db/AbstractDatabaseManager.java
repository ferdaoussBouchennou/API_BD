package ma.ensa.db;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

/*Classe abstraite qui implémente les méthodes communes aux gestionnaires de bases de données
Avec gestion améliorée des ressources via try-with-resources */
public abstract class AbstractDatabaseManager implements DatabaseManager, AutoCloseable {

    @Getter
    protected String url;
    @Getter @Setter
    protected String username;
    @Getter @Setter
    protected String password;
    protected Connection connection;
    protected SQLDialect sqlDialect;

    public AbstractDatabaseManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connexion fermée avec succès.");
        }
    }

    /*Implémentation de AutoCloseable pour permettre l'utilisation dans un try-with-resources*/
    @Override
    public void close() throws SQLException {
        disconnect();
    }

    @Override
    public List<Map<String, Object>> executeQuery(String query, Object... params) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        // Utilisation de try-with-resources pour fermeture automatique des ressources
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, query, params);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Parcourir les résultats
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                // Ajouter chaque colonne dans la Map
                for (int i = 1; i <= columnCount; i++) {
                    // Pour Oracle, utiliser getColumnLabel au lieu de getColumnName
                    // et stocker en minuscules pour uniformiser avec les autres SGBD
                    String columnName = metaData.getColumnLabel(i).toLowerCase();
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }
        }
        return resultList;
    }

    @Override
    public int executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, query, params)) {
            // Exécuter la mise à jour
            return stmt.executeUpdate();
        }
    }

    /**
     * Prépare une requête paramétrée avec les valeurs fournies
     *conn la connexion à utiliser
     * query la requête SQL avec placeholders (?)
     *params les paramètres à insérer dans la requête
     *le PreparedStatement configuré
     */
    private PreparedStatement prepareStatement(Connection conn, String query, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        // Définir les paramètres
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt;
    }

    private Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            // Si nous sommes dans une transaction, retourner la connexion existante
            // mais ne pas la fermer à la fin du try-with-resources
            return createNonClosableConnectionProxy(connection);
        } else {
            // Créer une nouvelle connexion qui sera fermée automatiquement
            return connect();
        }
    }

    @Override
    public void beginTransaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = connect();
        }
        connection.setAutoCommit(false);
        System.out.println("Transaction démarrée.");
    }

    @Override
    public void commitTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("Transaction validée.");
        }
    }

    @Override
    public void rollbackTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
            connection.setAutoCommit(true);
            System.out.println("Transaction annulée.");
        }
    }

    @Override
    public SQLDialect getSQLDialect() {
        return sqlDialect;
    }

    /*
     * Crée un proxy pour la connexion qui ignore les appels à close()
     * conn La connexion à wrapper
     * @return Un proxy de connexion qui ne peut pas être fermé
     */
    private Connection createNonClosableConnectionProxy(final Connection conn) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] { Connection.class },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // Intercepter uniquement la méthode close()
                        if ("close".equals(method.getName())) {
                            // Ne rien faire
                            return null;
                        }
                        // Pour toutes les autres méthodes, déléguer à la connexion réelle
                        return method.invoke(conn, args);
                    }
                }
        );
    }
}