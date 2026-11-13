package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database access for the categories table.
 *
 * The application works with category names, so this class also
 * translates a name into the category_id used by the other tables.
 */
public class CategoryDAO {

    private static final String SELECT_ALL =
            "SELECT category_name FROM categories ORDER BY category_id";

    private static final String SELECT_ID =
            "SELECT category_id FROM categories WHERE category_name = ?";

    public List<String> findAll() {
        List<String> names = new ArrayList<String>();
        Connection connection = DBConnection.get();
        if (connection == null) {
            return names;
        }

        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            statement = connection.prepareStatement(SELECT_ALL);
            rows = statement.executeQuery();
            while (rows.next()) {
                names.add(rows.getString("category_name"));
            }
        } catch (SQLException e) {
            System.err.println("CategoryDAO.findAll failed: " + e.getMessage());
        } finally {
            UserDAO.close(rows, statement);
        }
        return names;
    }

    /** Returns the id of a category name, or 0 when it is not found. */
    public int findId(String categoryName) {
        Connection connection = DBConnection.get();
        if (connection == null || categoryName == null) {
            return 0;
        }

        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            statement = connection.prepareStatement(SELECT_ID);
            statement.setString(1, categoryName);
            rows = statement.executeQuery();
            if (rows.next()) {
                return rows.getInt("category_id");
            }
        } catch (SQLException e) {
            System.err.println("CategoryDAO.findId failed: " + e.getMessage());
        } finally {
            UserDAO.close(rows, statement);
        }
        return 0;
    }
}
