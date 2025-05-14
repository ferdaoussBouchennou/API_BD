package ma.ensa.test;

import ma.ensa.db.DatabaseManager;
import ma.ensa.db.DatabaseManagerFactory;
import ma.ensa.util.DBConfigLoader;

/*Classe de test pour le gestionnaire PostgreSQL*/
public class PostgreSQLManagerTest extends AbstractDatabaseManagerTest {

    @Override
    protected DatabaseManager createDatabaseManager() throws Exception {
        // Charger la configuration
        DBConfigLoader configLoader = new DBConfigLoader("db.properties");
        DatabaseManagerFactory factory = new DatabaseManagerFactory(configLoader);

        // Créer le gestionnaire PostgreSQL
        return factory.createDatabaseManager("postgresql");
    }
}