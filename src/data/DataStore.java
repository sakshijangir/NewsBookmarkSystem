package data;

import java.util.ArrayList;
import java.util.List;

import model.Bookmark;
import model.News;
import model.User;

/**
 * Single access point to the application data.
 *
 * The class works in two modes:
 *
 *  - DATABASE mode, used when MySQL is reachable. Every call is passed
 *    to the matching DAO class, which runs a PreparedStatement.
 *  - SAMPLE mode, used when the database is not available. The same
 *    methods then work on in-memory lists filled with sample records,
 *    so the interface can always be demonstrated.
 *
 * No screen knows which mode is active: the user interface only ever
 * calls the methods below.
 */
public class DataStore {

    /* ------------------------------------------------------------------
     * Mode
     * ------------------------------------------------------------------ */

    private static boolean databaseMode;

    private static final UserDAO USER_DAO = new UserDAO();
    private static final NewsDAO NEWS_DAO = new NewsDAO();
    private static final BookmarkDAO BOOKMARK_DAO = new BookmarkDAO();
    private static final CategoryDAO CATEGORY_DAO = new CategoryDAO();

    /* ------------------------------------------------------------------
     * Sample-mode storage
     * ------------------------------------------------------------------ */

    private static final List<User> USERS = new ArrayList<User>();
    private static final List<News> NEWS = new ArrayList<News>();
    private static final List<Bookmark> BOOKMARKS = new ArrayList<Bookmark>();

    private static final String[] DEFAULT_CATEGORIES = {
            "Technology", "Sports", "Business", "Science", "Health", "Entertainment"
    };

    private static String[] categories = DEFAULT_CATEGORIES.clone();

    private static int newsSequence = 0;
    private static int bookmarkSequence = 0;

    private static User currentUser;

    static {
        databaseMode = DBConnection.isAvailable();

        if (databaseMode) {
            List<String> fromDb = CATEGORY_DAO.findAll();
            if (!fromDb.isEmpty()) {
                categories = fromDb.toArray(new String[0]);
            }
            System.out.println("DataStore: connected to MySQL (news_bookmark_db).");
        } else {
            loadSampleData();
            System.out.println("DataStore: running on sample data. "
                    + DBConnection.getLastError());
        }
    }

    private DataStore() {
        // utility class
    }

    /** True when the application is reading and writing MySQL. */
    public static boolean isDatabaseMode() {
        return databaseMode;
    }

    /** Short label describing where the data comes from. */
    public static String getDataSourceLabel() {
        return databaseMode
                ? "Connected to MySQL"
                : "Sample data - database not connected";
    }

    /* ------------------------------------------------------------------
     * Authentication
     * ------------------------------------------------------------------ */

    public static User login(String username, String password) {
        if (databaseMode) {
            currentUser = USER_DAO.login(username, password);
            return currentUser;
        }
        for (User u : USERS) {
            if (u.getUsername().equalsIgnoreCase(username)
                    && u.getPassword().equals(password)) {
                currentUser = u;
                return u;
            }
        }
        return null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    private static int currentUserId() {
        return currentUser == null ? 0 : currentUser.getId();
    }

    /* ------------------------------------------------------------------
     * News
     * ------------------------------------------------------------------ */

    public static List<News> getAllNews() {
        return databaseMode ? NEWS_DAO.findAll() : NEWS;
    }

    /** Returns news filtered by a keyword and/or a category. */
    public static List<News> searchNews(String keyword, String category) {
        if (databaseMode) {
            return NEWS_DAO.search(keyword, category);
        }

        List<News> result = new ArrayList<News>();
        String key = keyword == null ? "" : keyword.trim().toLowerCase();

        for (News n : NEWS) {
            boolean matchesKey = key.isEmpty()
                    || n.getTitle().toLowerCase().contains(key)
                    || n.getSummary().toLowerCase().contains(key)
                    || n.getSource().toLowerCase().contains(key);

            boolean matchesCategory = category == null
                    || "All Categories".equals(category)
                    || n.getCategory().equals(category);

            if (matchesKey && matchesCategory) {
                result.add(n);
            }
        }
        return result;
    }

    public static void addNews(News news) {
        if (databaseMode) {
            NEWS_DAO.insert(news);
            return;
        }
        news.setId(++newsSequence);
        NEWS.add(0, news);
    }

    public static void updateNews(News news) {
        if (databaseMode) {
            NEWS_DAO.update(news);
            return;
        }
        for (int i = 0; i < NEWS.size(); i++) {
            if (NEWS.get(i).getId() == news.getId()) {
                NEWS.set(i, news);
                return;
            }
        }
    }

    public static void deleteNews(News news) {
        if (databaseMode) {
            NEWS_DAO.delete(news.getId());
            return;
        }
        NEWS.remove(news);
    }

    /* ------------------------------------------------------------------
     * Bookmarks
     * ------------------------------------------------------------------ */

    public static List<Bookmark> getAllBookmarks() {
        return databaseMode ? BOOKMARK_DAO.findAll(currentUserId()) : BOOKMARKS;
    }

    public static List<Bookmark> searchBookmarks(String keyword, String category) {
        if (databaseMode) {
            return BOOKMARK_DAO.search(currentUserId(), keyword, category);
        }

        List<Bookmark> result = new ArrayList<Bookmark>();
        String key = keyword == null ? "" : keyword.trim().toLowerCase();

        for (Bookmark b : BOOKMARKS) {
            boolean matchesKey = key.isEmpty()
                    || b.getTitle().toLowerCase().contains(key)
                    || b.getUrl().toLowerCase().contains(key)
                    || b.getNotes().toLowerCase().contains(key);

            boolean matchesCategory = category == null
                    || "All Categories".equals(category)
                    || b.getCategory().equals(category);

            if (matchesKey && matchesCategory) {
                result.add(b);
            }
        }
        return result;
    }

    public static void addBookmark(Bookmark bookmark) {
        if (databaseMode) {
            BOOKMARK_DAO.insert(currentUserId(), bookmark);
            return;
        }
        bookmark.setId(++bookmarkSequence);
        BOOKMARKS.add(0, bookmark);
    }

    public static void updateBookmark(Bookmark bookmark) {
        if (databaseMode) {
            BOOKMARK_DAO.update(currentUserId(), bookmark);
            return;
        }
        for (int i = 0; i < BOOKMARKS.size(); i++) {
            if (BOOKMARKS.get(i).getId() == bookmark.getId()) {
                BOOKMARKS.set(i, bookmark);
                return;
            }
        }
    }

    public static void deleteBookmark(Bookmark bookmark) {
        if (databaseMode) {
            BOOKMARK_DAO.delete(currentUserId(), bookmark.getId());
            return;
        }
        BOOKMARKS.remove(bookmark);
    }

    /** True when a bookmark with the same URL already exists. */
    public static boolean isBookmarked(String url) {
        if (databaseMode) {
            return BOOKMARK_DAO.exists(currentUserId(), url);
        }
        for (Bookmark b : BOOKMARKS) {
            if (b.getUrl().equalsIgnoreCase(url)) {
                return true;
            }
        }
        return false;
    }

    /* ------------------------------------------------------------------
     * Categories
     * ------------------------------------------------------------------ */

    public static String[] getCategories() {
        return categories.clone();
    }

    /** Categories with an extra "All Categories" entry, for filters. */
    public static String[] getCategoriesWithAll() {
        String[] all = new String[categories.length + 1];
        all[0] = "All Categories";
        System.arraycopy(categories, 0, all, 1, categories.length);
        return all;
    }

    /* ------------------------------------------------------------------
     * Sample data, used only when MySQL is not reachable
     * ------------------------------------------------------------------ */

    private static void loadSampleData() {
        USERS.add(new User(1, "admin", "admin123", "Administrator"));
        USERS.add(new User(2, "vijay", "1234", "Vijay Sai"));

        // Added oldest first so that the newest article appears at the top
        // of the feed (addNews inserts each new item at position 0).
        addNews(new News(0, "Streaming platforms invest in regional cinema",
                "Variety", "Entertainment", "2026-08-31",
                "https://example.com/regional-cinema",
                "Regional language originals now account for a growing share of new "
                        + "commissions across major streaming services."));

        addNews(new News(0, "Study links regular walking to better sleep quality",
                "BBC Health", "Health", "2026-09-01",
                "https://example.com/walking-sleep",
                "Researchers followed 4,000 adults for a year and found moderate daily "
                        + "activity improved reported sleep quality."));

        addNews(new News(0, "India wins the series decider in the final over",
                "ESPNcricinfo", "Sports", "2026-09-02",
                "https://example.com/series-decider",
                "A composed finish from the middle order sealed a two-wicket win and "
                        + "the series with one match to spare."));

        addNews(new News(0, "Markets close higher as IT stocks rally",
                "Economic Times", "Business", "2026-09-03",
                "https://example.com/markets-rally",
                "Benchmark indices ended the session in the green, led by gains in "
                        + "information technology and banking counters."));

        addNews(new News(0, "New Java release focuses on startup performance",
                "InfoQ", "Technology", "2026-09-03",
                "https://example.com/java-release",
                "The latest release brings faster JVM warm-up, smaller container images "
                        + "and improved pattern matching for switch."));

        addNews(new News(0, "ISRO successfully tests reusable launch vehicle",
                "The Hindu", "Science", "2026-09-04",
                "https://example.com/isro-rlv",
                "The test flight validated autonomous landing systems and marks a step "
                        + "towards low-cost access to orbit."));

        addBookmark(new Bookmark(0, "India wins the series decider in the final over",
                "https://example.com/series-decider",
                "Sports", "2026-09-02", "Watch highlights later."));

        addBookmark(new Bookmark(0, "MySQL Workbench schema design guide",
                "https://dev.mysql.com/doc/workbench/en/",
                "Technology", "2026-09-02", "Needed for the database design section."));

        addBookmark(new Bookmark(0, "New Java release focuses on startup performance",
                "https://example.com/java-release",
                "Technology", "2026-09-03", "Saved from the news feed."));

        addBookmark(new Bookmark(0, "Java Swing Tutorial - Oracle Docs",
                "https://docs.oracle.com/javase/tutorial/uiswing/",
                "Technology", "2026-09-04", "Reference for layout managers."));
    }
}
