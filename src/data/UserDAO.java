package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

/**
 * Database access for the users table.
 */
public class UserDAO {

    private static final String FIND_BY_USERNAME =
            "SELECT user_id, username, password, full_name FROM users WHERE username = ?";

    /**
     * Returns the user when the username exists and the password matches
     * the stored SHA-256 hash, otherwise null.
     */
    public User login(String username, String plainPassword) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            statement = connection.prepareStatement(FIND_BY_USERNAME);
            statement.setString(1, username);
            rows = statement.executeQuery();

            if (rows.next()) {
                String storedHash = rows.getString("password");
                if (PasswordUtil.matches(plainPassword, storedHash)) {
                    return new User(
                            rows.getInt("user_id"),
                            rows.getString("username"),
                            storedHash,
                            rows.getString("full_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.login failed: " + e.getMessage());
        } finally {
            close(rows, statement);
        }
        return null;
    }

    static void close(ResultSet rows, PreparedStatement statement) {
        try {
            if (rows != null) {
                rows.close();
            }
        } catch (SQLException ignored) {
            // nothing useful to do
        }
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ignored) {
            // nothing useful to do
        }
    }
}
