package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.News;

/**
 * One article in the news feed, drawn as a card: a coloured source tile,
 * the headline, the source and date, a category chip and the row actions.
 */
public class NewsCard extends RoundPanel {

    /** Actions the feed reacts to when a card is used. */
    public interface Handler {
        void onSelect(News news);

        void onBookmark(News news);

        void onEdit(News news);

        void onDelete(News news);
    }

    private final News news;
    private boolean hover;
    private boolean selected;

    public NewsCard(final News news, final Handler handler, boolean alreadySaved) {
        super(new BorderLayout(14, 0));
        this.news = news;

        radius(UIUtil.RADIUS_CARD);
        fill(UIUtil.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 14));
        setPreferredSize(new Dimension(10, 96));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(new SourceTile(news), BorderLayout.WEST);
        add(buildText(news), BorderLayout.CENTER);
        add(buildActions(news, handler, alreadySaved), BorderLayout.EAST);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!contains(e.getPoint())) {
                    hover = false;
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handler.onSelect(news);
            }
        };
        attach(this, mouse);
    }

    /** Adds the card mouse handler to every non-interactive child too. */
    private void attach(Container container, MouseAdapter mouse) {
        container.addMouseListener(mouse);
        for (Component child : container.getComponents()) {
            if (child instanceof ModernButton) {
                continue;
            }
            if (child instanceof Container) {
                attach((Container) child, mouse);
            } else {
                child.addMouseListener(mouse);
            }
        }
    }

    private JPanel buildText(News news) {
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(news.getTitle());
        title.setFont(UIUtil.H2);
        title.setForeground(UIUtil.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        meta.setOpaque(false);
        meta.setAlignmentX(LEFT_ALIGNMENT);
        meta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        Chip chip = new Chip(news.getCategory());
        JLabel source = new JLabel(news.getSource() + "  ·  " + news.getDate());
        source.setFont(UIUtil.SMALL);
        source.setForeground(UIUtil.MUTED);

        meta.add(chip);
        meta.add(source);

        JLabel summary = new JLabel(news.getSummary());
        summary.setFont(UIUtil.SMALL);
        summary.setForeground(UIUtil.MUTED);
        summary.setAlignmentX(LEFT_ALIGNMENT);

        text.add(Box.createVerticalGlue());
        text.add(title);
        text.add(Box.createVerticalStrut(6));
        text.add(meta);
        text.add(Box.createVerticalStrut(6));
        text.add(summary);
        text.add(Box.createVerticalGlue());
        return text;
    }

    private JPanel buildActions(final News news, final Handler handler, boolean alreadySaved) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);

        ModernButton bookmark = ModernButton.iconOnly(
                Icons.of(alreadySaved ? "bookmark-filled" : "bookmark", 17,
                        alreadySaved ? UIUtil.ACCENT : UIUtil.BODY),
                ModernButton.Variant.GHOST,
                alreadySaved ? "Already bookmarked" : "Save to bookmarks");
        bookmark.addActionListener(e -> handler.onBookmark(news));

        ModernButton edit = ModernButton.iconOnly(Icons.of("edit", 16, UIUtil.BODY),
                ModernButton.Variant.GHOST, "Edit article");
        edit.addActionListener(e -> handler.onEdit(news));

        ModernButton delete = ModernButton.iconOnly(Icons.of("trash", 16, UIUtil.DANGER),
                ModernButton.Variant.DANGER, "Delete article");
        delete.addActionListener(e -> handler.onDelete(news));

        actions.add(bookmark);
        actions.add(edit);
        actions.add(delete);
        return actions;
    }

    public News getNews() {
        return news;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        borderColor(selected ? UIUtil.PRIMARY : hover ? new Color(0xD3D9E6) : UIUtil.BORDER);
        setFillColor(selected ? new Color(0xFBFCFF) : UIUtil.SURFACE);
        super.paintComponent(g);

        if (selected) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            g2.setColor(UIUtil.PRIMARY);
            g2.fill(UIUtil.roundRect(0, 18, 3.5, getHeight() - 36.0, 2));
            g2.dispose();
        }
    }

    /** Rounded tile showing the first letter of the news source. */
    private static class SourceTile extends JComponent {

        private final String letter;
        private final Color ink;
        private final Color tint;

        SourceTile(News news) {
            String source = news.getSource();
            this.letter = source == null || source.isEmpty()
                    ? "?" : source.substring(0, 1).toUpperCase();
            this.ink = UIUtil.categoryInk(news.getCategory());
            this.tint = UIUtil.categoryTint(news.getCategory());
            setPreferredSize(new Dimension(46, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            int size = 46;
            int y = (getHeight() - size) / 2;
            g2.setColor(tint);
            g2.fill(UIUtil.roundRect(0, y, size, size, 12));
            g2.setColor(ink);
            g2.setFont(UIUtil.H1);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(letter, (size - fm.stringWidth(letter)) / 2,
                    y + (size - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
