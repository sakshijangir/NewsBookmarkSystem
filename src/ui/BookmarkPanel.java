package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.DataStore;
import model.Bookmark;

/**
 * Bookmarks screen: every saved link as a card, with the same search
 * and category filter used by the news feed.
 */
public class BookmarkPanel extends JPanel implements BookmarkCard.Handler {

    private final DashboardFrame parent;

    private SearchField search;
    private FilterBar filters;
    private final JPanel list = new JPanel();
    private final JLabel countLabel = new JLabel();

    private List<Bookmark> visible = new ArrayList<Bookmark>();
    private Bookmark selected;

    public BookmarkPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtil.BG);
        setBorder(BorderFactory.createEmptyBorder(26, 30, 24, 30));

        add(buildHeader(), BorderLayout.NORTH);

        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        add(UIUtil.scroll(Dialogs.top(list)), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 16));
        header.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Bookmarks");
        title.setFont(UIUtil.DISPLAY);
        title.setForeground(UIUtil.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        countLabel.setFont(UIUtil.BODY_FONT);
        countLabel.setForeground(UIUtil.MUTED);
        countLabel.setAlignmentX(LEFT_ALIGNMENT);

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(countLabel);

        ModernButton add = new ModernButton("Add bookmark", ModernButton.Variant.PRIMARY,
                Icons.bold("plus", 16, Color.WHITE));
        add.addActionListener(e -> addBookmark());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(add);

        titleRow.add(titles, BorderLayout.WEST);
        titleRow.add(actions, BorderLayout.EAST);

        search = new SearchField("Search titles, links or notes…", 340, text -> refresh());

        String[] categories = DataStore.getCategories();
        String[] withAll = new String[categories.length + 1];
        withAll[0] = "All";
        System.arraycopy(categories, 0, withAll, 1, categories.length);
        filters = new FilterBar(withAll, category -> refresh());

        JPanel toolbar = new JPanel();
        toolbar.setOpaque(false);
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new BorderLayout());
        searchRow.setOpaque(false);
        searchRow.setAlignmentX(LEFT_ALIGNMENT);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchRow.add(search, BorderLayout.WEST);

        JPanel filterRow = new JPanel(new BorderLayout());
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(LEFT_ALIGNMENT);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        filterRow.add(filters, BorderLayout.CENTER);

        toolbar.add(searchRow);
        toolbar.add(Box.createVerticalStrut(12));
        toolbar.add(filterRow);

        header.add(titleRow, BorderLayout.NORTH);
        header.add(toolbar, BorderLayout.CENTER);
        return header;
    }

    /** Rebuilds the card list from the current search text and filter. */
    public void refresh() {
        String keyword = search == null ? "" : search.getText();
        String category = filters == null ? "All" : filters.getSelected();
        String storeCategory = "All".equals(category) ? "All Categories" : category;

        visible = DataStore.searchBookmarks(keyword, storeCategory);
        list.removeAll();

        for (Bookmark bookmark : visible) {
            BookmarkCard card = new BookmarkCard(bookmark, this);
            card.setSelected(bookmark == selected);
            list.add(card);
            list.add(Box.createVerticalStrut(10));
        }
        if (visible.isEmpty()) {
            list.add(Dialogs.emptyState("No bookmarks here yet",
                    "Save an article from the feed, or add a link yourself.", "bookmark"));
        }

        countLabel.setText(visible.size() + (visible.size() == 1 ? " link" : " links")
                + ("All".equals(category) ? " saved" : " in " + category));

        list.revalidate();
        list.repaint();
    }

    /* ------------------------------------------------------------------
     * Card actions
     * ------------------------------------------------------------------ */

    public void onSelect(Bookmark bookmark) {
        selected = bookmark;
        for (java.awt.Component component : list.getComponents()) {
            if (component instanceof BookmarkCard) {
                BookmarkCard card = (BookmarkCard) component;
                card.setSelected(card.getBookmark() == bookmark);
            }
        }
    }

    public void onEdit(Bookmark bookmark) {
        BookmarkDialog dialog = new BookmarkDialog(parent, bookmark);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            DataStore.updateBookmark(dialog.getBookmark());
            parent.showSuccess("Bookmark updated");
            refresh();
        }
    }

    public void onDelete(Bookmark bookmark) {
        boolean yes = Dialogs.confirm(parent, "Remove this bookmark?",
                "\"" + bookmark.getTitle() + "\" will be deleted from your list.", "Remove");
        if (yes) {
            DataStore.deleteBookmark(bookmark);
            if (selected == bookmark) {
                selected = null;
            }
            parent.showSuccess("Bookmark removed");
            refresh();
        }
    }

    private void addBookmark() {
        BookmarkDialog dialog = new BookmarkDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            DataStore.addBookmark(dialog.getBookmark());
            parent.showSuccess("Bookmark added");
            refresh();
        }
    }
}
