package ma.ensa;

import ma.ensa.db.DatabaseManager;
import ma.ensa.db.DatabaseManagerFactory;
import ma.ensa.util.DBConfigLoader;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Classe principale pour tester l'API de gestion de base de données
 */
public class Main {

    public static void main(String[] args) {
        // Charger la configuration
        DBConfigLoader configLoader = new DBConfigLoader("db.properties");
        DatabaseManagerFactory factory = new DatabaseManagerFactory(configLoader);

        // Utiliser la base de données par défaut
        try {
            // Créer le gestionnaire de base de données
            DatabaseManager dbManager = factory.createDefaultDatabaseManager();

            // Test de connexion
            System.out.println("Test de connexion à la base de données...");
            dbManager.connect();

            // Création d'une table de test si elle n'existe pas
            System.out.println("\nCréation de la table de test si elle n'existe pas...");
            dbManager.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "name VARCHAR(100), " +
                            "age INT, " +
                            "email VARCHAR(100))"
            );

            // Insertion de quelques données si nécessaire
            System.out.println("\nInsertion de données de test...");
            dbManager.executeUpdate(
                    "INSERT INTO users (name, age, email) VALUES (?, ?, ?)",
                    "Jean Dupont",
                    35,
                    "jean@example.com"
            );

            dbManager.executeUpdate(
                    "INSERT INTO users (name, age, email) VALUES (?, ?, ?)",
                    "Marie Martin",
                    28,
                    "marie@example.com"
            );

            // Exemple de requête SELECT
            System.out.println("\nExécution d'une requête SELECT...");
            List<Map<String, Object>> results = dbManager.executeQuery("SELECT * FROM users WHERE age > ?", 25);

            // Afficher les résultats
            System.out.println("Résultats:");
            for (Map<String, Object> row : results) {
                System.out.println(row);
            }

            // Exemple de requête UPDATE
            System.out.println("\nExécution d'une requête UPDATE...");
            int rowsAffected = dbManager.executeUpdate("UPDATE users SET name = ? WHERE id = ?", "Nouveau Nom", 1);
            System.out.println("Nombre de lignes mises à jour: " + rowsAffected);

            // Exemple de transaction
            System.out.println("\nTest de transaction...");
            dbManager.beginTransaction();

            try {
                dbManager.executeUpdate("INSERT INTO users (name, age, email) VALUES (?, ?, ?)",
                        "Utilisateur Test",
                        30,
                        "test@example.com"
                );

                dbManager.executeUpdate("UPDATE users SET name = ? WHERE id = ?",
                        "Nom Modifié",
                        2
                );

                // Valider la transaction
                dbManager.commitTransaction();
                System.out.println("Transaction réussie!");
            } catch (SQLException e) {
                // Annuler la transaction en cas d'erreur
                dbManager.rollbackTransaction();
                System.err.println("Transaction annulée: " + e.getMessage());
            }

            // Vérifier les modifications après la transaction
            System.out.println("\nVérification des données après la transaction:");
            results = dbManager.executeQuery("SELECT * FROM users");
            for (Map<String, Object> row : results) {
                System.out.println(row);
            }

            // Fermer la connexion
            dbManager.disconnect();

        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}