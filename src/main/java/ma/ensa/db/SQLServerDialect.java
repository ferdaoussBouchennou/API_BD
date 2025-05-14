package ma.ensa.db;

/*Implémentation du dialecte SQL pour SQL Server*/
public class SQLServerDialect implements SQLDialect {

    @Override
    public String createTableIfNotExists(String tableName, String columns) {
        return "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = '" + tableName + "') " +
                "BEGIN " +
                "CREATE TABLE " + tableName + " (" + columns + ") " +
                "END";
    }
    @Override
    public String dropTableIfExists(String tableName) {
        return "IF OBJECT_ID('" + tableName + "', 'U') IS NOT NULL DROP TABLE " + tableName;
    }

    @Override
    public String countAll(String tableName) {
        return "SELECT COUNT(*) as count FROM " + tableName;
    }
    @Override
    public String getAutoIncrementPrimaryKeyColumn(String columnName) {
        return columnName + " INT IDENTITY(1,1) PRIMARY KEY";
    }
}