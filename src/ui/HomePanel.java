package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import model.User;

/**
 * Dashboard screen: a greeting, three summary tiles, the latest
 * articles and a breakdown of saved items by category.
 */
public class HomePanel extends JPanel {

    private final DashboardFrame parent;

    private final StatCard newsStat = new StatCard("Articles in feed", "news", UIUtil.PRIMARY);
    private final StatCard bookmarkStat = new StatCard("Saved bookmarks", "bookmark", UIUtil.ACCENT);
    private final StatCard categoryStat = new StatCard("Categories", "tag", UIUtil.SUCCESS);

    private final JPanel recentList = new JPanel();
    private final JPanel categoryList = new JPanel();
    private JLabel greeting;

    public HomePanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG);
        setBorder(BorderFactory.createEmptyBorder(26, 30, 24, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    /* ------------------------------------------------------------------
     * Header
     * ------------------------------------------------------------------ */

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        greeting = new JLabel("Dashboard");
        greeting.setFont(UIUtil.DISPLAY);
        greeting.setForeground(UIUtil.INK);
        greeting.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("A quick look at your feed and saved links.");
        subtitle.setFont(UIUtil.BODY_FONT);
        subtitle.setForeground(UIUtil.MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        titles.add(greeting);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        ModernButton browse = new ModernButton("Browse feed", ModernButton.Variant.SECONDARY,
                Icons.of("news", 16, UIUtil.INK));
        browse.addActionListener(e -> parent.showCard(DashboardFrame.CARD_NEWS));

        ModernButton bookmarks = new ModernButton("Open bookmarks", ModernButton.Variant.PRIMARY,
                Icons.of("bookmark", 16, Color.WHITE));
        bookmarks.addActionListener(e -> parent.showCard(DashboardFrame.CARD_BOOKMARKS));

        actions.add(browse);
        actions.add(bookmarks);

        header.add(titles, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    /* ------------------------------------------------------------------
     * Body
     * ------------------------------------------------------------------ */

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 3, 18, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(10, 116));
        stats.add(newsStat);
        stats.add(bookmarkStat);
        stats.add(categoryStat);

        JPanel columns = new JPanel(new BorderLayout(18, 0));
        columns.setOpaque(false);
        columns.add(buildRecentCard(), BorderLayout.CENTER);
        columns.add(buildCategoryCard(), BorderLayout.EAST);

        body.add(stats, BorderLayout.NORTH);
        body.add(columns, BorderLayout.CENTER);
        return body;
    }

    private RoundPanel buildRecentCard() {
        RoundPanel card = new RoundPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 16));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 4));

        JLabel title = new JLabel("Latest articles");
        title.setFont(UIUtil.H2);
        title.setForeground(UIUtil.INK);

        ModernButton all = new ModernButton("View all", ModernButton.Variant.GHOST,
                Icons.of("chevron", 14, UIUtil.PRIMARY));
        all.setForeground(UIUtil.PRIMARY);
        all.addActionListener(e -> parent.showCard(DashboardFrame.CARD_NEWS));

        head.add(title, BorderLayout.WEST);
        head.add(all, BorderLayout.EAST);

        recentList.setOpaque(false);
        recentList.setLayout(new BoxLayout(recentList, BoxLayout.Y_AXIS));

        card.add(head, BorderLayout.NORTH);
        card.add(Dialogs.top(recentList), BorderLayout.CENTER);
        return card;
    }

    private RoundPanel buildCategoryCard() {
        RoundPanel card = new RoundPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));
        card.setPreferredSize(new Dimension(348, 10));

        JLabel title = new JLabel("Saved by category");
        title.setFont(UIUtil.H2);
        title.setForeground(UIUtil.INK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        categoryList.setOpaque(false);
        categoryList.setLayout(new BoxLayout(categoryList, BoxLayout.Y_AXIS));

        card.add(title, BorderLayout.NORTH);
        card.add(Dialogs.top(categoryList), BorderLayout.CENTER);
        return card;
    }

    /* ------------------------------------------------------------------
     * Data
     * ------------------------------------------------------------------ */

    /** Reloads the counters, the latest articles and the category bars. */
    public void refresh() {
        User user = DataStore.getCurrentUser();
        greeting.setText("Welcome back, "
                + (user == null ? "there" : user.getFullName().split(" ")[0]));

        List<News> news = DataStore.getAllNews();
        List<Bookmark> bookmarks = DataStore.getAllBookmarks();

        newsStat.setValue(String.valueOf(news.size()), "across every category");
        bookmarkStat.setValue(String.valueOf(bookmarks.size()), "links kept for later");
        categoryStat.setValue(String.valueOf(DataStore.getCategories().length),
                "used to organise items");

        recentList.removeAll();
        int limit = Math.min(5, news.size());
        for (int i = 0; i < limit; i++) {
            recentList.add(new RecentRow(news.get(i), i == limit - 1));
        }
        if (news.isEmpty()) {
            recentList.add(Dialogs.emptyState("No articles yet",
                    "Add the first article from the news feed.", "news"));
        }

        categoryList.removeAll();
        int max = 1;
        for (String category : DataStore.getCategories()) {
            max = Math.max(max, DataStore.searchBookmarks("", category).size());
        }
        for (String category : DataStore.getCategories()) {
            int count = DataStore.searchBookmarks("", category).size();
            categoryList.add(new CategoryBar(category, count, max));
            categoryList.add(Box.createVerticalStrut(12));
        }

        revalidate();
        repaint();
    }

    /* ------------------------------------------------------------------
     * Sub components
     * ------------------------------------------------------------------ */

    /** Large number tile at the top of the dashboard. */
    private static class StatCard extends RoundPanel {

        private final JLabel value = new JLabel("0");
        private final JLabel caption = new JLabel(" ");

        StatCard(String label, final String icon, final Color accent) {
            super(new BorderLayout(14, 0));
            setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

            JComponent badge = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                    int y = (getHeight() - 44) / 2;
                    g2.setColor(UIUtil.mix(accent, Color.WHITE, 0.88));
                    g2.fill(UIUtil.roundRect(0, y, 44, 44, 13));
                    Icons.of(icon, 20, accent).paintIcon(this, g2, 12, y + 12);
                    g2.dispose();
                }
            };
            badge.setPreferredSize(new Dimension(44, 44));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(label);
            name.setFont(UIUtil.SMALL);
            name.setForeground(UIUtil.MUTED);
            name.setAlignmentX(LEFT_ALIGNMENT);

            value.setFont(UIUtil.NUMERAL);
            value.setForeground(UIUtil.INK);
            value.setAlignmentX(LEFT_ALIGNMENT);

            caption.setFont(UIUtil.MICRO);
            caption.setForeground(UIUtil.MUTED);
            caption.setAlignmentX(LEFT_ALIGNMENT);

            text.add(Box.createVerticalGlue());
            text.add(name);
            text.add(Box.createVerticalStrut(2));
            text.add(value);
            text.add(Box.createVerticalStrut(2));
            text.add(caption);
            text.add(Box.createVerticalGlue());

            add(badge, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        void setValue(String number, String note) {
            value.setText(number);
            caption.setText(note);
        }
    }

    /** One row in the "Latest articles" list. */
    private class RecentRow extends JPanel {

        private boolean hover;

        RecentRow(final News news, boolean last) {
            setLayout(new BorderLayout(12, 0));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(11, 4, 11, 6));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            JLabel title = new JLabel(news.getTitle());
            title.setFont(UIUtil.H3);
            title.setForeground(UIUtil.INK);
            title.setAlignmentX(LEFT_ALIGNMENT);

            JLabel meta = new JLabel(news.getSource() + "  ·  " + news.getDate());
            meta.setFont(UIUtil.MICRO);
            meta.setForeground(UIUtil.MUTED);
            meta.setAlignmentX(LEFT_ALIGNMENT);

            text.add(title);
            text.add(Box.createVerticalStrut(3));
            text.add(meta);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
            right.setOpaque(false);
            right.add(new Chip(news.getCategory()));

            add(text, BorderLayout.CENTER);
            add(right, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    parent.showCard(DashboardFrame.CARD_NEWS);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            if (hover) {
                g2.setColor(UIUtil.SURFACE_ALT);
                g2.fill(UIUtil.roundRect(-4, 2, getWidth() + 8.0, getHeight() - 4.0, 10));
            }
            g2.setColor(UIUtil.BORDER);
            g2.drawLine(4, getHeight() - 1, getWidth() - 6, getHeight() - 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Category name, count and a proportional bar. */
    private static class CategoryBar extends JComponent {

        private final String category;
        private final int count;
        private final int max;

        CategoryBar(String category, int count, int max) {
            this.category = category;
            this.count = count;
            this.max = Math.max(1, max);
            setPreferredSize(new Dimension(10, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            Color ink = UIUtil.categoryInk(category);

            g2.setFont(UIUtil.SMALL_BOLD);
            g2.setColor(UIUtil.BODY);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(category, 0, fm.getAscent());

            String number = String.valueOf(count);
            g2.setColor(UIUtil.MUTED);
            g2.drawString(number, getWidth() - fm.stringWidth(number), fm.getAscent());

            int barY = 24;
            g2.setColor(new Color(0xEDEFF5));
            g2.fill(UIUtil.roundRect(0, barY, getWidth(), 7, 4));

            int width = (int) (getWidth() * (count / (double) max));
            if (count > 0) {
                g2.setColor(ink);
                g2.fill(UIUtil.roundRect(0, barY, Math.max(10, width), 7, 4));
            }
            g2.dispose();
        }
    }
}
