# Connecting the project to MySQL

The application runs in two modes. Out of the box it uses built-in sample
data, so it always starts. Once you complete the four steps below it reads
and writes a real MySQL database instead — without a single change to the
interface code.

Check which mode is active any time: **Help → About**, or the line printed
in the IntelliJ console when the app starts.

---

## Step 1 — Create the database

Make sure MySQL Server is running, then load the schema:

**Terminal**

```bash
mysql -u root -p < database/schema.sql
```

**Or MySQL Workbench**

File → Open SQL Script → `database/schema.sql` → click the ⚡ lightning bolt.

This creates the `news_bookmark_db` database, the four tables
(`users`, `categories`, `news`, `bookmarks`) and inserts the same
sample records the prototype shows.

Verify it worked:

```sql
USE news_bookmark_db;
SELECT COUNT(*) FROM news;       -- 6
SELECT COUNT(*) FROM bookmarks;  -- 4
```

---

## Step 2 — Add the MySQL driver to the project

Java needs the MySQL JDBC driver on the classpath. In IntelliJ:

1. **File → Project Structure** (`⌘ ;`)
2. **Libraries** → **+** → **From Maven…**
3. Type `com.mysql:mysql-connector-j:9.1.0` → **OK**
4. Tick "Download to project libraries", **OK → Apply → OK**

IntelliJ downloads the jar and attaches it. No manual download needed.

*Not using IntelliJ?* Download `mysql-connector-j-9.1.0.jar` from
dev.mysql.com and compile/run with it on the classpath:

```bash
javac -cp lib/mysql-connector-j-9.1.0.jar -d bin src/Main.java src/model/*.java src/data/*.java src/ui/*.java
java -cp bin:lib/mysql-connector-j-9.1.0.jar Main        # use ; instead of : on Windows
```

---

## Step 3 — Set your MySQL username and password

Open `src/data/DBConnection.java` and edit these two lines to match your
own MySQL account:

```java
private static final String USER = "root";
private static final String PASSWORD = "root";
```

The host, port and database name are just above them if you need to change
those too.

---

## Step 4 — Run

Run `Main`. The console prints one of:

```
DataStore: connected to MySQL (news_bookmark_db).
DataStore: running on sample data. Could not connect to ... 
```

If it says sample data, the message after it names the cause — wrong
password, MySQL not started, driver missing, or database not created.

Log in with `admin` / `admin123`. Add a bookmark, then check it landed:

```sql
SELECT title, url, date_added FROM bookmarks ORDER BY bookmark_id DESC LIMIT 1;
```

---

## How the code is organised

```
database/
  schema.sql              CREATE TABLE statements + seed data

src/data/
  DBConnection.java       opens and reuses the JDBC connection
  PasswordUtil.java       SHA-256 hashing so passwords are never plain text
  UserDAO.java            login query
  CategoryDAO.java        category list, name → id lookup
  NewsDAO.java            SELECT / INSERT / UPDATE / DELETE on news
  BookmarkDAO.java        the same for bookmarks, filtered by user
  DataStore.java          chooses database or sample mode and forwards calls
```

`DataStore` is the only class the interface talks to. Every screen calls
`DataStore.searchNews(...)`, `DataStore.addBookmark(...)` and so on, so
switching between MySQL and sample data changes nothing above that line.

Every SQL statement uses a `PreparedStatement` with bound parameters, so
user input cannot be injected into a query.

---

## Table relationships

| Table | Key columns | Relationship |
|---|---|---|
| `users` | `user_id` PK, `username` unique | one user has many bookmarks |
| `categories` | `category_id` PK, `category_name` unique | used by news and bookmarks |
| `news` | `news_id` PK, `category_id` FK | one article belongs to one category |
| `bookmarks` | `bookmark_id` PK, `user_id` FK, `news_id` FK, `category_id` FK | belongs to one user; `news_id` is set when saved from the feed and NULL when typed in manually |

`bookmarks` also has a unique key on `(user_id, url)`, so the same user
cannot save the same link twice — the database enforces the duplicate
check that the interface performs.
