package ma.ensa;

import ma.ensa.db.DatabaseManager;
import ma.ensa.db.DatabaseManagerFactory;
import ma.ensa.util.DBConfigLoader;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String TABLE_NAME = "personnes";

    public static void main(String[] args) {
        try {
            // Charger la configuration
            DBConfigLoader configLoader = new DBConfigLoader("db.properties");
            DatabaseManagerFactory factory = new DatabaseManagerFactory(configLoader);

            // Obtenir le gestionnaire de base de données par défaut
            DatabaseManager dbManager = factory.createDatabaseManager("oracle");
            System.out.println("Connexion à la base de données établie.");

            // Créer une table de test si elle n'existe pas
            createTestTable(dbManager);

            // Insérer des données de test
            insertTestData(dbManager);

            // Afficher toutes les données
            displayAllData(dbManager);

            // Mettre à jour des données
            updateData(dbManager);

            // Afficher les données après mise à jour
            displayAllData(dbManager);

            // Supprimer des données
            deleteData(dbManager);

            // Afficher les données après suppression
            displayAllData(dbManager);

            // Test de transaction
            testTransaction(dbManager);

            // Fermer la connexion
            dbManager.disconnect();
            System.out.println("Connexion fermée avec succès.");

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTestTable(DatabaseManager dbManager) throws SQLException {
        // Suppression de la table si elle existe déjà
        dbManager.dropTableIfExists(TABLE_NAME);

        // Création de la table
        String columns = dbManager.getSQLDialect().getAutoIncrementPrimaryKeyColumn("id") + ", " +
                "nom VARCHAR(100), " +
                "age INT, " +
                "email VARCHAR(100)";

        dbManager.createTableIfNotExists(TABLE_NAME, columns);
        System.out.println("Table " + TABLE_NAME + " créée avec succès.");
    }

    private static void insertTestData(DatabaseManager dbManager) throws SQLException {
        String query = "INSERT INTO " + TABLE_NAME + " (nom, age, email) VALUES (?, ?, ?)";

        // Insérer quelques enregistrements
        dbManager.executeUpdate(query, "Ahmed Bennani", 28, "ahmed@mail.com");
        dbManager.executeUpdate(query, "Khadija Alaoui", 34, "khadija@mail.com");
        dbManager.executeUpdate(query, "Mehdi Tazi", 22, "mehdi@mail.com");

        System.out.println("Données insérées avec succès.");
    }

    private static void displayAllData(DatabaseManager dbManager) throws SQLException {
        String query = "SELECT * FROM " + TABLE_NAME;
        List<Map<String, Object>> results = dbManager.executeQuery(query);

        System.out.println("\n--- Données dans la table " + TABLE_NAME + " ---");
        if (results.isEmpty()) {
            System.out.println("Aucune donnée trouvée.");
        } else {
            for (Map<String, Object> row : results) {
                System.out.println("ID: " + row.get("id") +
                        ", Nom: " + row.get("nom") +
                        ", Age: " + row.get("age") +
                        ", Email: " + row.get("email"));
            }
        }
        System.out.println("------------------------------------\n");
    }

    private static void updateData(DatabaseManager dbManager) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET age = ? WHERE nom = ?";
        int rowsAffected = dbManager.executeUpdate(query, 29, "Ahmed Bennani");

        System.out.println(rowsAffected + " ligne(s) mise(s) à jour.");
    }

    private static void deleteData(DatabaseManager dbManager) throws SQLException {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE nom = ?";
        int rowsAffected = dbManager.executeUpdate(query, "Mehdi Tazi");

        System.out.println(rowsAffected + " ligne(s) supprimée(s).");
    }

    private static void testTransaction(DatabaseManager dbManager) throws SQLException {
        try {
            // Démarrer une transaction
            dbManager.beginTransaction();

            // Insérer un nouvel enregistrement
            dbManager.executeUpdate(
                    "INSERT INTO " + TABLE_NAME + " (nom, age, email) VALUES (?, ?, ?)",
                    "Nadia Mansouri", 31, "nadia@mail.com"
            );

            // Mettre à jour un enregistrement existant
            dbManager.executeUpdate(
                    "UPDATE " + TABLE_NAME + " SET email = ? WHERE nom = ?",
                    "khadija.updated@mail.com", "Khadija Alaoui"
            );

            // Valider la transaction
            dbManager.commitTransaction();
            System.out.println("Transaction réussie.");

        } catch (SQLException e) {
            // Annuler la transaction en cas d'erreur
            dbManager.rollbackTransaction();
            System.err.println("Transaction annulée: " + e.getMessage());
            throw e;
        }
    }
}