package ma.ensa.test;

import ma.ensa.db.DatabaseManager;
import ma.ensa.util.CSVDataLoader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Classe de test abstraite pour les gestionnaires de base de données
 * Les classes de test spécifiques pour chaque SGBD étendront cette classe
 */
public abstract class AbstractDatabaseManagerTest {

    protected DatabaseManager dbManager;
    protected CSVDataLoader dataLoader;
    protected static final String TEST_TABLE = "test_users";

    /**
     * Méthode à implémenter par les sous-classes pour fournir le bon gestionnaire de DB
     * @return Une instance de DatabaseManager pour le type spécifique à tester
     */
    protected abstract DatabaseManager createDatabaseManager() throws Exception;

    @BeforeEach
    public void setUp() throws Exception {
        // Créer le gestionnaire de base de données approprié
        dbManager = createDatabaseManager();

        // Charger les données de test
        dataLoader = new CSVDataLoader("test_data.csv");

        // Préparer la base de données pour les tests
        prepareDatabase();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // S'assurer que toutes les connexions sont fermées
        if (dbManager != null) {
            dbManager.disconnect();
        }
    }

    /**
     * Prépare la base de données pour les tests (création de table, insertion de données)
     */
    private void prepareDatabase() throws SQLException, IOException {
        dbManager.connect();

        // Supprimer la table si elle existe et la recréer
        dbManager.executeUpdate(dbManager.getSQLDialect().dropTableIfExists(TEST_TABLE));

        // Création de la table avec une structure adaptée pour les tests
        String columnDefinitions =
                dbManager.getSQLDialect().getAutoIncrementPrimaryKeyColumn("id") + ", " +
                        "name VARCHAR(100), " +
                        "age INT, " +
                        "email VARCHAR(100)";

        dbManager.executeUpdate(dbManager.getSQLDialect().createTableIfNotExists(TEST_TABLE, columnDefinitions));

        // Insérer les données de test depuis le CSV
        List<Map<String, String>> testData = dataLoader.loadData();
        for (Map<String, String> row : testData) {
            dbManager.executeUpdate(
                    "INSERT INTO " + TEST_TABLE + " (name, age, email) VALUES (?, ?, ?)",
                    row.get("name"),
                    Integer.parseInt(row.get("age")),
                    row.get("email")
            );
        }
    }

    @Test
    public void testConnection() throws SQLException {
        // Tester que la connexion fonctionne
        assertNotNull(dbManager.connect());
    }

    @Test
    public void testSelect() throws SQLException {
        // Tester une requête SELECT
        List<Map<String, Object>> results = dbManager.executeQuery(
                "SELECT * FROM " + TEST_TABLE + " WHERE age > ?",
                25
        );

        // Vérifier que les résultats sont corrects
        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Vérifier que tous les utilisateurs ont un âge > 25
        for (Map<String, Object> row : results) {
            int age = ((Number)row.get("age")).intValue();
            assertTrue(age > 25);
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        // Modification d'un utilisateur
        String newName = "Updated Name";
        int id = 1;

        int rowsAffected = dbManager.executeUpdate(
                "UPDATE " + TEST_TABLE + " SET name = ? WHERE id = ?",
                newName,
                id
        );

        // Vérifier que la mise à jour a réussi
        assertEquals(1, rowsAffected);

        // Vérifier que le nom a bien été mis à jour
        List<Map<String, Object>> results = dbManager.executeQuery(
                "SELECT name FROM " + TEST_TABLE + " WHERE id = ?",
                id
        );

        assertFalse(results.isEmpty());
        assertEquals(newName, results.get(0).get("name"));
    }

    @Test
    public void testTransaction() throws SQLException {
        try {
            dbManager.beginTransaction();

            // Ajouter un utilisateur
            dbManager.executeUpdate(
                    "INSERT INTO " + TEST_TABLE + " (name, age, email) VALUES (?, ?, ?)",
                    "Test Transaction",
                    30,
                    "transaction@test.com"
            );

            // Mettre à jour un utilisateur
            dbManager.executeUpdate(
                    "UPDATE " + TEST_TABLE + " SET email = ? WHERE id = ?",
                    "updated@test.com",
                    1
            );

            dbManager.commitTransaction();

            // Vérifier que les modifications ont été appliquées
            List<Map<String, Object>> results = dbManager.executeQuery(
                    "SELECT * FROM " + TEST_TABLE + " WHERE name = ?",
                    "Test Transaction"
            );

            assertFalse(results.isEmpty());

        } catch (SQLException e) {
            dbManager.rollbackTransaction();
            throw e;
        }
    }

    @Test
    public void testDelete() throws SQLException {
        // Compter le nombre d'utilisateurs au départ
        List<Map<String, Object>> initialResults = dbManager.executeQuery(
                dbManager.getSQLDialect().countAll(TEST_TABLE)
        );
        int initialCount = ((Number)initialResults.get(0).get("count")).intValue();

        // Supprimer un utilisateur
        int id = 1;
        int rowsAffected = dbManager.executeUpdate("DELETE FROM " + TEST_TABLE + " WHERE id = ?", id);

        // Vérifier que la suppression a réussi
        assertEquals(1, rowsAffected);

        // Vérifier que le nombre d'utilisateurs a diminué
        List<Map<String, Object>> finalResults = dbManager.executeQuery(
                dbManager.getSQLDialect().countAll(TEST_TABLE)
        );
        int finalCount = ((Number)finalResults.get(0).get("count")).intValue();

        assertEquals(initialCount - 1, finalCount);

        // Vérifier que l'utilisateur n'existe plus
        List<Map<String, Object>> results = dbManager.executeQuery("SELECT * FROM " + TEST_TABLE + " WHERE id = ?", id);
        assertTrue(results.isEmpty());
    }
}