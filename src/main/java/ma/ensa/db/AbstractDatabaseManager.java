package ma.ensa.db;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Classe abstraite qui implémente les méthodes communes aux gestionnaires de bases de données
 * Avec gestion améliorée des ressources via try-with-resources
 */
public abstract class AbstractDatabaseManager implements DatabaseManager, AutoCloseable {

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

    /**
     * Implémentation de AutoCloseable pour permettre l'utilisation dans un try-with-resources
     */
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
        // Utilisation de try-with-resources pour fermeture automatique des ressources
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, query, params)) {

            // Exécuter la mise à jour
            return stmt.executeUpdate();
        }
    }

    /**
     * Prépare une requête paramétrée avec les valeurs fournies
     *
     * @param conn la connexion à utiliser
     * @param query la requête SQL avec placeholders (?)
     * @param params les paramètres à insérer dans la requête
     * @return le PreparedStatement configuré
     * @throws SQLException si une erreur se produit lors de la préparation
     */
    private PreparedStatement prepareStatement(Connection conn, String query, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);

        // Définir les paramètres
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt;
    }

    /**
     * Obtient une connexion, soit la connexion active dans une transaction, soit une nouvelle connexion
     * @return Une connexion à la base de données
     * @throws SQLException Si une erreur de connexion se produit
     */
    private Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            // Si nous sommes dans une transaction, retourner la connexion existante
            // mais ne pas la fermer à la fin du try-with-resources
            return new NonClosableConnectionWrapper(connection);
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

    /**
     * Classe wrapper pour éviter que la connexion de transaction ne soit fermée
     * par le try-with-resources
     */
    private static class NonClosableConnectionWrapper implements Connection {
        private final Connection wrapped;

        public NonClosableConnectionWrapper(Connection wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void close() throws SQLException {
            // Ne rien faire lors de la fermeture
            // La connexion de transaction est gérée explicitement
        }

        // Déléguer toutes les autres méthodes à la connexion sous-jacente

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return wrapped.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return wrapped.isWrapperFor(iface);
        }

        @Override
        public Statement createStatement() throws SQLException {
            return wrapped.createStatement();
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return wrapped.prepareStatement(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return wrapped.prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return wrapped.nativeSQL(sql);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            wrapped.setAutoCommit(autoCommit);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return wrapped.getAutoCommit();
        }

        @Override
        public void commit() throws SQLException {
            wrapped.commit();
        }

        @Override
        public void rollback() throws SQLException {
            wrapped.rollback();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return wrapped.isClosed();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return wrapped.getMetaData();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            wrapped.setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return wrapped.isReadOnly();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            wrapped.setCatalog(catalog);
        }

        @Override
        public String getCatalog() throws SQLException {
            return wrapped.getCatalog();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            wrapped.setTransactionIsolation(level);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return wrapped.getTransactionIsolation();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return wrapped.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            wrapped.clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return wrapped.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return wrapped.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return wrapped.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return wrapped.getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            wrapped.setTypeMap(map);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            wrapped.setHoldability(holdability);
        }

        @Override
        public int getHoldability() throws SQLException {
            return wrapped.getHoldability();
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return wrapped.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return wrapped.setSavepoint(name);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            wrapped.rollback(savepoint);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            wrapped.releaseSavepoint(savepoint);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return wrapped.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return wrapped.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return wrapped.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return wrapped.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return wrapped.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return wrapped.prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            return wrapped.createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            return wrapped.createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return wrapped.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return wrapped.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return wrapped.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            wrapped.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            wrapped.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return wrapped.getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return wrapped.getClientInfo();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return wrapped.createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return wrapped.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            wrapped.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            return wrapped.getSchema();
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            wrapped.abort(executor);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            wrapped.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return wrapped.getNetworkTimeout();
        }
    }
}