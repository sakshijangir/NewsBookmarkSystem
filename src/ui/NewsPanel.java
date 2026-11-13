package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.DataStore;
import model.Bookmark;
import model.News;

/**
 * News feed screen: a searchable, filterable list of article cards on
 * the left and a reading pane showing the selected article on the right.
 */
public class NewsPanel extends JPanel implements NewsCard.Handler {

    private final DashboardFrame parent;

    private SearchField search;
    private FilterBar filters;
    private final JPanel list = new JPanel();
    private final ReadingPane reader = new ReadingPane();
    private final JLabel countLabel = new JLabel();

    private List<News> visible = new ArrayList<News>();
    private News selected;

    public NewsPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(18, 0));
        setBackground(UIUtil.BG);
        setBorder(BorderFactory.createEmptyBorder(26, 30, 24, 30));

        JPanel left = new JPanel(new BorderLayout(0, 16));
        left.setOpaque(false);
        left.add(buildHeader(), BorderLayout.NORTH);

        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        left.add(UIUtil.scroll(Dialogs.top(list)), BorderLayout.CENTER);

        add(left, BorderLayout.CENTER);
        add(reader, BorderLayout.EAST);
    }

    /* ------------------------------------------------------------------
     * Header and toolbar
     * ------------------------------------------------------------------ */

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 16));
        header.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("News feed");
        title.setFont(UIUtil.DISPLAY);
        title.setForeground(UIUtil.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        countLabel.setFont(UIUtil.BODY_FONT);
        countLabel.setForeground(UIUtil.MUTED);
        countLabel.setAlignmentX(LEFT_ALIGNMENT);

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(countLabel);

        ModernButton add = new ModernButton("Add article", ModernButton.Variant.PRIMARY,
                Icons.bold("plus", 16, Color.WHITE));
        add.addActionListener(e -> addNews());

        titleRow.add(titles, BorderLayout.WEST);
        titleRow.add(rightAligned(add), BorderLayout.EAST);

        search = new SearchField("Search headlines, sources or summaries…", 340,
                text -> refresh());

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

    private JPanel rightAligned(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);
        panel.add(component);
        return panel;
    }

    /* ------------------------------------------------------------------
     * Data
     * ------------------------------------------------------------------ */

    /** Rebuilds the card list from the current search text and filter. */
    public void refresh() {
        String keyword = search == null ? "" : search.getText();
        String category = filters == null ? "All" : filters.getSelected();
        String storeCategory = "All".equals(category) ? "All Categories" : category;

        visible = DataStore.searchNews(keyword, storeCategory);
        list.removeAll();

        for (News news : visible) {
            NewsCard card = new NewsCard(news, this, DataStore.isBookmarked(news.getUrl()));
            card.setSelected(news == selected);
            list.add(card);
            list.add(Box.createVerticalStrut(10));
        }
        if (visible.isEmpty()) {
            list.add(Dialogs.emptyState("Nothing matches that search",
                    "Try another keyword or pick a different category.", "search"));
        }

        countLabel.setText(visible.size() + (visible.size() == 1 ? " article" : " articles")
                + ("All".equals(category) ? " in the feed" : " in " + category));

        if (selected != null && !visible.contains(selected)) {
            selected = null;
        }
        reader.show(selected);

        list.revalidate();
        list.repaint();
    }

    /* ------------------------------------------------------------------
     * Card actions
     * ------------------------------------------------------------------ */

    public void onSelect(News news) {
        selected = news;
        for (java.awt.Component component : list.getComponents()) {
            if (component instanceof NewsCard) {
                NewsCard card = (NewsCard) component;
                card.setSelected(card.getNews() == news);
            }
        }
        reader.show(news);
    }

    public void onBookmark(News news) {
        if (DataStore.isBookmarked(news.getUrl())) {
            parent.showToast("Already in your bookmarks");
            return;
        }
        DataStore.addBookmark(new Bookmark(0, news.getTitle(), news.getUrl(),
                news.getCategory(), today(), "Saved from the news feed."));
        parent.showSuccess("Saved to bookmarks");
        refresh();
    }

    public void onEdit(News news) {
        NewsDialog dialog = new NewsDialog(parent, news);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            DataStore.updateNews(dialog.getNews());
            selected = dialog.getNews();
            parent.showSuccess("Article updated");
            refresh();
        }
    }

    public void onDelete(News news) {
        boolean yes = Dialogs.confirm(parent, "Delete this article?",
                "\"" + news.getTitle() + "\" will be removed from the feed.", "Delete");
        if (yes) {
            DataStore.deleteNews(news);
            if (selected == news) {
                selected = null;
            }
            parent.showSuccess("Article deleted");
            refresh();
        }
    }

    private void addNews() {
        NewsDialog dialog = new NewsDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            DataStore.addNews(dialog.getNews());
            selected = dialog.getNews();
            parent.showSuccess("Article added");
            refresh();
        }
    }

    private static String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /* ------------------------------------------------------------------
     * Reading pane
     * ------------------------------------------------------------------ */

    /** Right hand column showing the full details of the selected article. */
    private class ReadingPane extends RoundPanel {

        private final JPanel content = new JPanel();

        static final int TEXT_WIDTH = 278;

        ReadingPane() {
            super(new BorderLayout());
            setPreferredSize(new Dimension(344, 10));
            setBorder(BorderFactory.createEmptyBorder(22, 22, 20, 18));

            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            add(UIUtil.scroll(Dialogs.top(content)), BorderLayout.CENTER);
        }

        void show(News news) {
            content.removeAll();

            if (news == null) {
                JPanel empty = Dialogs.emptyState("Nothing selected",
                        "Pick an article to read it here.", "news");
                empty.setAlignmentX(LEFT_ALIGNMENT);
                content.add(empty);
                content.revalidate();
                content.repaint();
                return;
            }

            JLabel eyebrow = new JLabel("READING");
            eyebrow.setFont(UIUtil.MICRO_BOLD);
            eyebrow.setForeground(UIUtil.MUTED);
            eyebrow.setAlignmentX(LEFT_ALIGNMENT);

            JPanel title = UIUtil.textBlock(news.getTitle(), UIUtil.H1,
                    UIUtil.INK, TEXT_WIDTH, 2);

            JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            meta.setOpaque(false);
            meta.setAlignmentX(LEFT_ALIGNMENT);
            meta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            meta.add(new Chip(news.getCategory()));
            JLabel date = new JLabel(news.getDate());
            date.setFont(UIUtil.SMALL);
            date.setForeground(UIUtil.MUTED);
            meta.add(date);

            JPanel source = detailRow("source", news.getSource());
            JPanel link = detailRow("link", news.getUrl());

            JPanel summary = UIUtil.textBlock(news.getSummary(), UIUtil.BODY_FONT,
                    UIUtil.BODY, TEXT_WIDTH, 3);

            boolean saved = DataStore.isBookmarked(news.getUrl());
            ModernButton save = new ModernButton(
                    saved ? "Saved to bookmarks" : "Save to bookmarks",
                    saved ? ModernButton.Variant.SECONDARY : ModernButton.Variant.PRIMARY,
                    Icons.of(saved ? "bookmark-filled" : "bookmark", 16,
                            saved ? UIUtil.ACCENT : Color.WHITE));
            save.size(42);
            save.setAlignmentX(LEFT_ALIGNMENT);
            save.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            save.addActionListener(e -> onBookmark(news));

            ModernButton edit = new ModernButton("Edit article", ModernButton.Variant.SECONDARY,
                    Icons.of("edit", 16, UIUtil.INK));
            edit.size(42);
            edit.setAlignmentX(LEFT_ALIGNMENT);
            edit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            edit.addActionListener(e -> onEdit(news));

            content.add(eyebrow);
            content.add(Box.createVerticalStrut(10));
            content.add(title);
            content.add(Box.createVerticalStrut(12));
            content.add(meta);
            content.add(Box.createVerticalStrut(16));
            content.add(new Divider());
            content.add(Box.createVerticalStrut(14));
            content.add(source);
            content.add(Box.createVerticalStrut(10));
            content.add(link);
            content.add(Box.createVerticalStrut(16));
            content.add(summary);
            content.add(Box.createVerticalStrut(22));
            content.add(save);
            content.add(Box.createVerticalStrut(8));
            content.add(edit);

            content.revalidate();
            content.repaint();
        }

        private JPanel detailRow(String icon, String value) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

            JLabel iconLabel = new JLabel(Icons.of(icon, 15, UIUtil.MUTED));
            JLabel text = new JLabel(value);
            text.setFont(UIUtil.SMALL);
            text.setForeground("link".equals(icon) ? UIUtil.PRIMARY : UIUtil.BODY);

            row.add(iconLabel, BorderLayout.WEST);
            row.add(text, BorderLayout.CENTER);
            return row;
        }
    }

    /** Hairline separator used inside the reading pane. */
    private static class Divider extends JComponent {

        Divider() {
            setPreferredSize(new Dimension(10, 1));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(UIUtil.BORDER);
            g2.drawLine(0, 0, getWidth(), 0);
            g2.dispose();
        }
    }
}
