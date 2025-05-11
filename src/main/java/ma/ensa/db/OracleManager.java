package ma.ensa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implémentation de DatabaseManager pour Oracle
 */
public class OracleManager extends AbstractDatabaseManager {
    private final String driver;

    public OracleManager(String driver, String url, String username, String password) {
        super(url, username, password);
        this.driver = driver;
    }

    @Override
    public Connection connect() throws SQLException {
        try {
            // Charger le driver JDBC
            Class.forName(driver);

            // Établir la connexion
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion établie avec Oracle.");
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver Oracle non trouvé: " + e.getMessage());
        }
    }
}