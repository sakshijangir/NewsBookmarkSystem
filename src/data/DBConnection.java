package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single place where the MySQL connection is configured and opened.
 *
 * Edit USER and PASSWORD to match the MySQL account on the machine that
 * runs the project. If the database cannot be reached the application
 * still starts: DataStore falls back to its built-in sample data, so
 * the interface can always be demonstrated.
 */
public final class DBConnection {

    /** JDBC driver shipped with mysql-connector-j 8.x and above. */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "news_bookmark_db";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /** Change these two lines to match your MySQL installation. */
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;
    private static String lastError;

    private DBConnection() {
        // static helpers only
    }

    /**
     * Opens the connection the first time it is needed and reuses it
     * afterwards. Returns null when the database is not available.
     */
    public static Connection get() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            lastError = null;
            return connection;
        } catch (ClassNotFoundException e) {
            lastError = "MySQL JDBC driver not found on the classpath "
                    + "(add mysql-connector-j to the project libraries).";
        } catch (SQLException e) {
            lastError = "Could not connect to " + URL + " - " + e.getMessage();
        }
        connection = null;
        return null;
    }

    /** True when a live connection to MySQL could be opened. */
    public static boolean isAvailable() {
        return get() != null;
    }

    /** Message explaining why the last connection attempt failed. */
    public static String getLastError() {
        return lastError;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
            // closing on shutdown, nothing useful to do here
        }
        connection = null;
    }
}
