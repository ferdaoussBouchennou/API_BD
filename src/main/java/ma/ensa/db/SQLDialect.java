package ma.ensa.db;

/* Interface qui définit les dialectes SQL spécifiques à chaque SGBD */
public interface SQLDialect {
    /*Retourne la requête SQL pour créer une table si elle n'existe pas*/
    String createTableIfNotExists(String tableName, String columns);

    /*Retourne la requête SQL pour supprimer une table si elle existe*/
    String dropTableIfExists(String tableName);

    /*Retourne la requête SQL pour compter le nombre d'enregistrements*/
    String countAll(String tableName);

    /*Retourne la déclaration d'une colonne auto-incrémentée pour une clé primaire*/
    String getAutoIncrementPrimaryKeyColumn(String columnName);

    /**
     * Indique si le SGBD met les noms de colonnes en majuscules par défaut

     */
    default boolean useUpperCaseColumnNames() {
        return false;
    }
}