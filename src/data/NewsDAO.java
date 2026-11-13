package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.News;

/**
 * Database access for the news table.
 *
 * Every statement is a PreparedStatement, so values are bound safely
 * and the application is not open to SQL injection.
 */
public class NewsDAO {

    private final CategoryDAO categories = new CategoryDAO();

    private static final String SELECT_BASE =
            "SELECT n.news_id, n.title, n.source, n.news_date, n.url, n.summary, "
                    + "       COALESCE(c.category_name, 'Technology') AS category_name "
                    + "  FROM news n "
                    + "  LEFT JOIN categories c ON c.category_id = n.category_id ";

    private static final String SELECT_ALL =
            SELECT_BASE + "ORDER BY n.news_date DESC, n.news_id DESC";

    private static final String SEARCH =
            SELECT_BASE
                    + " WHERE (? = '' OR n.title LIKE ? OR n.summary LIKE ? OR n.source LIKE ?) "
                    + "   AND (? = 'All Categories' OR c.category_name = ?) "
                    + " ORDER BY n.news_date DESC, n.news_id DESC";

    private static final String INSERT =
            "INSERT INTO news (title, source, category_id, news_date, url, summary) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE news SET title = ?, source = ?, category_id = ?, news_date = ?, "
                    + "url = ?, summary = ? WHERE news_id = ?";

    private static final String DELETE = "DELETE FROM news WHERE news_id = ?";

    public List<News> findAll() {
        return query(SELECT_ALL, null, null);
    }

    /** Keyword and category filter, matching the in-memory behaviour. */
    public List<News> search(String keyword, String category) {
        return query(SEARCH, keyword, category);
    }

    private List<News> query(String sql, String keyword, String category) {
        List<News> result = new ArrayList<News>();
        Connection connection = DBConnection.get();
        if (connection == null) {
            return result;
        }

        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            statement = connection.prepareStatement(sql);
            if (keyword != null || category != null) {
                String key = keyword == null ? "" : keyword.trim();
                String like = "%" + key + "%";
                String cat = category == null ? "All Categories" : category;

                statement.setString(1, key);
                statement.setString(2, like);
                statement.setString(3, like);
                statement.setString(4, like);
                statement.setString(5, cat);
                statement.setString(6, cat);
            }
            rows = statement.executeQuery();
            while (rows.next()) {
                result.add(read(rows));
            }
        } catch (SQLException e) {
            System.err.println("NewsDAO query failed: " + e.getMessage());
        } finally {
            UserDAO.close(rows, statement);
        }
        return result;
    }

    private News read(ResultSet rows) throws SQLException {
        java.sql.Date date = rows.getDate("news_date");
        return new News(
                rows.getInt("news_id"),
                rows.getString("title"),
                rows.getString("source"),
                rows.getString("category_name"),
                date == null ? "" : date.toString(),
                rows.getString("url") == null ? "" : rows.getString("url"),
                rows.getString("summary") == null ? "" : rows.getString("summary"));
    }

    /** Inserts the article and writes the generated id back into it. */
    public boolean insert(News news) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            bind(statement, news);
            statement.executeUpdate();

            keys = statement.getGeneratedKeys();
            if (keys.next()) {
                news.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.err.println("NewsDAO.insert failed: " + e.getMessage());
            return false;
        } finally {
            UserDAO.close(keys, statement);
        }
    }

    public boolean update(News news) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(UPDATE);
            bind(statement, news);
            statement.setInt(7, news.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("NewsDAO.update failed: " + e.getMessage());
            return false;
        } finally {
            UserDAO.close(null, statement);
        }
    }

    public boolean delete(int newsId) {
        Connection connection = DBConnection.get();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(DELETE);
            statement.setInt(1, newsId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("NewsDAO.delete failed: " + e.getMessage());
            return false;
        } finally {
            UserDAO.close(null, statement);
        }
    }

    /** Fills the six shared parameters of the insert and update statements. */
    private void bind(PreparedStatement statement, News news) throws SQLException {
        statement.setString(1, news.getTitle());
        statement.setString(2, news.getSource());

        int categoryId = categories.findId(news.getCategory());
        if (categoryId > 0) {
            statement.setInt(3, categoryId);
        } else {
            statement.setNull(3, java.sql.Types.INTEGER);
        }

        statement.setDate(4, toSqlDate(news.getDate()));
        statement.setString(5, news.getUrl());
        statement.setString(6, news.getSummary());
    }

    /** Converts a yyyy-MM-dd string into a SQL date, or null when invalid. */
    static java.sql.Date toSqlDate(String text) {
        try {
            return java.sql.Date.valueOf(text);
        } catch (Exception e) {
            return new java.sql.Date(System.currentTimeMillis());
        }
    }
}
