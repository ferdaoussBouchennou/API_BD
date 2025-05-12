package ma.ensa.db;

/**
 * Interface qui définit les dialectes SQL spécifiques à chaque SGBD
 */
public interface SQLDialect {
    /**
     * Retourne la requête SQL pour créer une table si elle n'existe pas
     * @param tableName Nom de la table
     * @param columns Définition des colonnes
     * @return Requête SQL adaptée au SGBD
     */
    String createTableIfNotExists(String tableName, String columns);

    /**
     * Retourne la requête SQL pour supprimer une table si elle existe
     * @param tableName Nom de la table
     * @return Requête SQL adaptée au SGBD
     */
    String dropTableIfExists(String tableName);

    /**
     * Retourne la requête SQL pour compter le nombre d'enregistrements
     * @param tableName Nom de la table
     * @return Requête SQL adaptée au SGBD
     */
    String countAll(String tableName);

    /**
     * Retourne la déclaration d'une colonne auto-incrémentée pour une clé primaire
     * @param columnName Nom de la colonne
     * @return Déclaration SQL adaptée au SGBD
     */
    String getAutoIncrementPrimaryKeyColumn(String columnName);
}