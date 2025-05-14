package ma.ensa.db;
/*Implémentation du dialecte SQL pour Oracle*/
public class OracleDialect implements SQLDialect {
    @Override
    public String createTableIfNotExists(String tableName, String columns) {
        return "BEGIN " +
                "EXECUTE IMMEDIATE 'CREATE TABLE " + tableName + " (" + columns + ")'; " +
                "EXCEPTION " +
                "WHEN OTHERS THEN " +
                "IF SQLCODE = -955 THEN NULL; ELSE RAISE; END IF; " +
                "END;";
    }
    @Override
    public String dropTableIfExists(String tableName) {
        return "BEGIN " +
                "EXECUTE IMMEDIATE 'DROP TABLE " + tableName + "'; " +
                "EXCEPTION " +
                "WHEN OTHERS THEN " +
                "IF SQLCODE != -942 THEN RAISE; END IF; " +
                "END;";
    }
    @Override
    public String countAll(String tableName) {
        // Oracle renvoie COUNT(*) et non "count"
        return "SELECT COUNT(*) as \"count\" FROM " + tableName;
    }
    @Override
    public String getAutoIncrementPrimaryKeyColumn(String columnName) {
        return columnName + " NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY";
    }
    /*Indique si le SGBD met les noms de colonnes en majuscules par défaut*/
    @Override
    public boolean useUpperCaseColumnNames() {
        return true;
    }
}