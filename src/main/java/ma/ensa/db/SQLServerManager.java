package ma.ensa.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/* Implémentation de DatabaseManager pour SQL Server */
public class SQLServerManager extends AbstractDatabaseManager {
    private final String driver;

    public SQLServerManager(String driver, String url, String username, String password) {
        super(url, username, password);
        this.driver = driver;
        this.sqlDialect = new SQLServerDialect();
    }
    @Override
    public Connection connect() throws SQLException {
        try {
            // Charger le driver JDBC
            Class.forName(driver);

            // Établir la connexion
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion établie avec SQL Server.");
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQL Server non trouvé: " + e.getMessage());
        }
    }
}