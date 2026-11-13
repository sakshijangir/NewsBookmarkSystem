# News & Bookmark Management System — GUI Prototype (Java Swing)

Mid Term–I submission: **Project GUI Prototype (Java Swing) — 20 Marks**

The application runs against **MySQL** when the database is reachable, and
falls back to built-in **sample data** when it is not — so it always starts,
with or without a database. See `DATABASE_SETUP.md` for the four steps that
switch it to MySQL.

---

## Screens

| # | Screen | File |
|---|--------|------|
| 1 | Login | `src/ui/LoginFrame.java` |
| 2 | Dashboard (menu bar, sidebar, summary cards, recent news) | `src/ui/DashboardFrame.java`, `src/ui/HomePanel.java` |
| 3 | News Feed (search, filter, add/edit/delete, bookmark) | `src/ui/NewsPanel.java` |
| 4 | Bookmarks (search, filter, add/edit/delete) | `src/ui/BookmarkPanel.java` |
| 5 | Add / Edit News form | `src/ui/NewsDialog.java` |
| 6 | Add / Edit Bookmark form | `src/ui/BookmarkDialog.java` |

Screenshots of each screen are in the `screenshots/` folder.

---

## Demo login

| Username | Password |
|----------|----------|
| `admin`  | `admin123` |

---

## How to run

**macOS / Linux**

```bash
cd "news and bookmark management system"
./run.sh
```

**Windows**

```bat
run.bat
```

**Manually (any OS)**

```bash
javac -d bin src/Main.java src/model/*.java src/data/*.java src/ui/*.java
java -cp bin Main
```

Requires JDK 8 or newer (`java -version` to check). No external libraries.

---

## Project structure

```
src/
  Main.java                 entry point and base look-and-feel defaults
  model/
    User.java               user account
    News.java               news article
    Bookmark.java           saved link
  data/
    DBConnection.java       opens and reuses the JDBC connection
    PasswordUtil.java       SHA-256 password hashing
    UserDAO.java            login query
    CategoryDAO.java        category list and name-to-id lookup
    NewsDAO.java            CRUD statements for the news table
    BookmarkDAO.java        CRUD statements for the bookmarks table
    DataStore.java          picks database or sample mode, forwards every call
  ui/
    UIUtil.java             design system: colours, type scale, painting helpers
    Icons.java              line icons drawn with Java2D (no image assets)
    RoundPanel.java         rounded surface with hairline border and soft shadow
    ModernButton.java       flat button: primary / secondary / ghost / danger
    SearchField.java        rounded search input with placeholder and focus ring
    FilterBar.java          clickable category pills used as the filter control
    WrapLayout.java         layout that wraps the pills on narrow windows
    FormField.java          labelled input used by the dialogs, with error state
    Chip.java               category pill shown on cards and rows
    NewsCard.java           one article card in the feed
    BookmarkCard.java       one saved link card
    Dialogs.java            styled confirm / message / empty-state components
    LoginFrame.java         login screen
    DashboardFrame.java     app shell: navigation rail, CardLayout, toasts, menus
    HomePanel.java          dashboard: stat tiles, latest articles, category bars
    NewsPanel.java          news feed with reading pane
    BookmarkPanel.java      bookmark manager
    NewsDialog.java         add / edit news form
    BookmarkDialog.java     add / edit bookmark form
database/
  schema.sql                CREATE TABLE statements and seed data
screenshots/                PNG screenshots of every screen
DATABASE_SETUP.md           how to connect the project to MySQL
```

---

## Visual design

The interface follows a small design system defined in `ui/UIUtil.java`, so
colours, fonts and corner radii are changed in one place.

| Token | Value | Used for |
|---|---|---|
| Background | `#F4F5F8` | app canvas behind the cards |
| Surface | `#FFFFFF` | cards, inputs, dialogs |
| Navigation | `#141A2A` | left navigation rail |
| Brand | `#3B5BF5` | primary buttons, active nav item, links |
| Accent | `#FF6B45` | bookmark actions and the logo tile |
| Ink / Body / Muted | `#0E1320` / `#3F4757` / `#8A93A6` | text hierarchy |
| Radius | 14 px cards, 10 px controls, pill chips | shape language |

Each category (Technology, Sports, Business, Science, Health, Entertainment)
has its own tint, used consistently on chips, tiles and the dashboard bars so
an item's category is recognisable at a glance.

Notable interface decisions:

- Card list instead of a plain table, so each article shows its source tile,
  category, date and summary together.
- Reading pane beside the feed, so selecting an article never hides the list.
- Category pills instead of a drop-down, making the current filter visible.
- Inline validation on the forms: the field itself turns red with a message,
  rather than interrupting with a pop-up.
- Toast confirmations in the corner after add, edit, delete and save actions.

---

## Swing features used

- `JFrame`, `JDialog` (modal forms), `JPanel`, `JComponent`
- Layouts: `BorderLayout`, `BoxLayout`, `GridLayout`, `GridBagLayout`,
  `FlowLayout`, `CardLayout` and a custom `WrapLayout`
- `JMenuBar` / `JMenu` / `JMenuItem` with accelerators (Ctrl+D, Ctrl+N, Ctrl+B, Ctrl+Q)
- Custom painting with `Graphics2D`: rounded rectangles, gradients, soft
  shadows, antialiased vector icons
- Custom `Icon`, `LayoutManager` and `ScrollBarUI` implementations
- `JTextField`, `JPasswordField`, `JTextArea`, `JComboBox` (restyled with a
  custom `BasicComboBoxUI`), `JCheckBox` with a custom icon, `JScrollPane`
- `DocumentListener` for live search, `MouseAdapter` for hover states,
  `Timer` for the toast messages
- Form validation with inline error states and `SwingUtilities.invokeLater`
  for correct event-dispatch-thread startup

---

## Database

Four tables in `news_bookmark_db`: `users`, `categories`, `news`, `bookmarks`.
The full schema, foreign keys and seed data are in `database/schema.sql`, and
`DATABASE_SETUP.md` explains how to load it and attach the JDBC driver.

Passwords are stored as SHA-256 hashes, never plain text. Every query uses a
`PreparedStatement` with bound parameters, so user input cannot be injected
into SQL.

---

## Next phase

1. Per-user roles in the interface (hide news editing from non-admin users).
2. Fetch live articles from an RSS or news API instead of manual entry.
3. Export the bookmark list to CSV.
