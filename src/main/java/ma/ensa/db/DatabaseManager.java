package ma.ensa.db;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
/* Interface pour gérer la connexion à différents types de bases de données et exécuter des requêtes SQL */
public interface DatabaseManager extends AutoCloseable {
    Connection connect() throws SQLException;
    void disconnect() throws SQLException;
    /* Execute une requete SQL de type SELECT */
    List<Map<String, Object>> executeQuery(String query, Object... params) throws SQLException;
    /* Execute une requete SQL de type UPDATE, INSERT ou DELETE */
    int executeUpdate(String query, Object... params) throws SQLException;
    /* Commence une transaction */
    void beginTransaction() throws SQLException;
    /* Valide une transaction */
    void commitTransaction() throws SQLException;
    /* Annule une transaction */
    void rollbackTransaction() throws SQLException;

    /*Retourne le dialecte SQL associé à cette implémentation*/
    SQLDialect getSQLDialect();

    /*
     * Crée une table si elle n'existe pas déjà
     * tableName Nom de la table
     * columnDefinitions Définitions des colonnes (sans les parenthèses)
     */
    default int createTableIfNotExists(String tableName, String columnDefinitions) throws SQLException {
        return executeUpdate(getSQLDialect().createTableIfNotExists(tableName, columnDefinitions));
    }
    /*Supprime une table si elle existe*/
    default int dropTableIfExists(String tableName) throws SQLException {
        return executeUpdate(getSQLDialect().dropTableIfExists(tableName));
    }
}