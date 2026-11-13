package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Bookmark;

/**
 * Database access for the bookmarks table.
 *
 * Bookmarks belong to a user, so every query is filtered by the id of
 * the user who is signed in.
 */
public class BookmarkDAO {

    private final CategoryDAO categories = new CategoryDAO();

    private static final String SELECT_BASE =
            "SELECT b.bookmark_id, b.title, b.url, b.date_added, b.notes, "
                    + "       COALESCE(c.category_name, 'Technology') AS category_name "
                    + "  FROM bookmarks b "
                    + "  LEFT JOIN categories c ON c.category_id = b.category_id "
                    + " WHERE b.user_id = ? ";

    private static final String SELECT_ALL =
            SELECT_BASE + "ORDER BY b.date_added DESC, b.bookmark_id DESC";

    private static final String SEARCH =
            SELECT_BASE
                    + "   AND (? = '' OR b.title LIKE ? OR b.url LIKE ? OR b.notes LIKE ?) "
                    + "   AND (? = 'All Categories' OR c.category_name = ?) "
                    + " ORDER BY b.date_added DESC, b.bookmark_id DESC";

    private static final String EXISTS =
            "SELECT 1 FROM bookmarks WHERE user_id = ? AND url = ? LIMIT 1";

    private static final String INSERT =
            "INSERT INTO bookmarks (user_id, news_id, title, url, category_id, date_added, notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE bookmarks SET title = ?, url = ?, category_id = ?, date_added = ?, "
                    + "notes = ? WHERE bookmark_id = ? AND user_id = ?";

    private static final String DELETE =
            "DELETE FROM bookmarks WHERE bookmark_id = ? AND user_id = ?";

    public List<Bookmark> findAll(int userId) {
        return query(SELECT_ALL, userId, null, null);
    }

    public List<Bookmark> search(int userId, String keyword, String category) {
        return query(SEARCH, userId, keyword, category);
    }

    private List<Bookmark> query(String sql, int userId, String keyword, String category) {
        List<Bookmark> result = new ArrayList<Bookmark>();
        Connection connection = DBConnection.get();
        if (connection == null) {
            return result;
        }

        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            if (keyword != null || category != null) {
                String key = keyword == null ? "" : keyword.trim();
                String like = "%" + key + "%";
                String cat = category == null ? "All Categories" : category;

                statement.setString(2, key);
                statement.setString(3, like);
                statement.setString(4, like);
                statement.setString(5, like);
                statement.setString(6, cat);
                statement.setString(7, cat);
            }
            rows = statement.executeQuery();
            while (rows.next()) {
                java.sql.Date date = rows.getDate("date_added");
                result.add(new Bookmark(
                        rows.getInt("bookmark_id"),
                        rows.getString("title"),
                        rows.getString("url"),
                        rows.getString("category_name"),
                        date == null ? "" : date.toString(),
                        rows.getString("notes") == null ? "" : rows.getString("notes")));
            }
        } catch (SQLException e) {
            System.err.println("BookmarkDAO query failed: " + e.getMessage());
        } finally {
            UserDAO.close(rows, statement);
        }
        return result;
    }

    /** True when this user has already saved the same link. */
    public boolean exists(int userId, String url) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            statement = connection.prepareStatement(EXISTS);
            statement.setInt(1, userId);
            statement.setString(2, url);
            rows = statement.executeQuery();
            return rows.next();
        } catch (SQLException e) {
            System.err.println("BookmarkDAO.exists failed: " + e.getMessage());
            return false;
        } finally {
            UserDAO.close(rows, statement);
        }
    }

    public boolean insert(int userId, Bookmark bookmark) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setNull(2, java.sql.Types.INTEGER);
            statement.setString(3, bookmark.getTitle());
            statement.setString(4, bookmark.getUrl());

            int categoryId = categories.findId(bookmark.getCategory());
            if (categoryId > 0) {
                statement.setInt(5, categoryId);
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }

            statement.setDate(6, NewsDAO.toSqlDate(bookmark.getDateAdded()));
            statement.setString(7, bookmark.getNotes());
            statement.executeUpdate();

            keys = statement.getGeneratedKeys();
            if (keys.next()) {
                bookmark.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.err.println("BookmarkDAO.insert failed: " + e.getMessage());
            return false;
        } finally {
            UserDAO.close(keys, statement);
        }
    }

    public boolean update(int userId, Bookmark bookmark) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(UPDATE);
            statement.setString(1, bookmark.getTitle());
            statement.setString(2, bookmark.getUrl());

            int categoryId = categories.findId(bookmark.getCategory());
            if (categoryId > 0) {
                statement.setInt(3, categoryId);
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }

            statement.setDate(4, NewsDAO.toSqlDate(bookmark.getDateAdded()));
            statement.setString(5, bookmark.getNotes());
            statement.setInt(6, bookmark.getId());
            statement.setInt(7, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("BookmarkDAO.update failed: " + e.getMessage());
            return false;
        } finally {
            UserDAO.close(null, statement);
        }
    }

    public boolean delete(int userId, int bookmarkId) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(DELETE);
            statement.setInt(1, bookmarkId);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("BookmarkDAO.delete failed: " + e.getMessage());
            return false;
        } finally {
            UserDAO.close(null, statement);
        }
    }
}
