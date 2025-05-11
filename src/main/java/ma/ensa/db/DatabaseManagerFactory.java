package ma.ensa.db;

import ma.ensa.util.DBConfigLoader;

/* Fabrique pour créer des instances de DatabaseManager selon le type demandé (MySQL, PostgreSQL, SQLServer) */
public class DatabaseManagerFactory {

    private final DBConfigLoader configLoader;

    public DatabaseManagerFactory(DBConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    /**
     * Crée un DatabaseManager selon le type spécifié
     * @param dbType Type de base de données (mysql, postgresql, sqlserver)
     * @return L'instance de DatabaseManager correspondante
     * @throws IllegalArgumentException si le type de base de données n'est pas supporté
     */
    public DatabaseManager createDatabaseManager(String dbType) {
        String[] dbInfo = configLoader.getDatabaseInfo(dbType);

        switch (dbType.toLowerCase()) {
            case "mysql":
                return new MySQLManager(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);

            case "postgresql":
                return new PostgreSQLManager(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);

            case "sqlserver":
                return new SQLServerManager(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);
            case "oracle":
                return new OracleManager(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);
            default:
                throw new IllegalArgumentException("Type de base de données non supporté: " + dbType);
        }
    }

    /**
     * Crée un DatabaseManager pour le type de base de données par défaut
     * @return L'instance de DatabaseManager correspondante
     */
    public DatabaseManager createDefaultDatabaseManager() {
        String defaultDbType = configLoader.getDefaultDatabaseType();
        return createDatabaseManager(defaultDbType);
    }
}