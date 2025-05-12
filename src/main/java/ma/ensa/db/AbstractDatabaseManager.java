package ma.ensa.db;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe abstraite qui implémente les méthodes communes aux gestionnaires de bases de données
 */
public abstract class AbstractDatabaseManager implements DatabaseManager {

    @Getter
    protected String url;

    @Getter @Setter
    protected String username;

    @Getter @Setter
    protected String password;

    protected Connection connection;

    /**
     * Le dialecte SQL spécifique à l'implémentation
     */
    protected SQLDialect sqlDialect;

    /**
     * Constructeur avec paramètres pour initialiser les informations de connexion
     * @param url URL de connexion à la base de données
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     */
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

    @Override
    public List<Map<String, Object>> executeQuery(String query, Object... params) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(query);

            // Définir les paramètres
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // Exécuter la requête
            rs = stmt.executeQuery();
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
        } finally {
            // Fermer les ressources
            if (conn != null && conn != connection) {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                conn.close();
            } else {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            }
        }

        return resultList;
    }

    @Override
    public int executeUpdate(String query, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(query);

            // Définir les paramètres
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // Exécuter la mise à jour
            return stmt.executeUpdate();
        } finally {
            // Fermer les ressources si nous ne sommes pas dans une transaction
            if (conn != null && conn != connection) {
                if (stmt != null) stmt.close();
                conn.close();
            } else {
                // Fermer seulement le Statement, pas la connexion
                if (stmt != null) stmt.close();
            }
        }
    }

    /**
     * Obtient une connexion, soit la connexion active dans une transaction, soit une nouvelle connexion
     * @return Une connexion à la base de données
     * @throws SQLException Si une erreur de connexion se produit
     */
    private Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            // Utiliser la connexion existante si nous sommes dans une transaction
            return connection;
        } else {
            // Créer une nouvelle connexion
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
}