package ma.ensa.db;

/*Implémentation du dialecte SQL pour MySQL*/
public class MySQLDialect implements SQLDialect {

    @Override
    public String createTableIfNotExists(String tableName, String columns) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
    }

    @Override
    public String dropTableIfExists(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    @Override
    public String countAll(String tableName) {
        return "SELECT COUNT(*) as count FROM " + tableName;
    }

    @Override
    public String getAutoIncrementPrimaryKeyColumn(String columnName) {
        return columnName + " INT PRIMARY KEY AUTO_INCREMENT";
    }
}