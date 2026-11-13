package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Bookmark;

/**
 * One saved link in the bookmark list, drawn as a card with the link
 * icon, title, address, category chip and the user's note.
 */
public class BookmarkCard extends RoundPanel {

    public interface Handler {
        void onSelect(Bookmark bookmark);

        void onEdit(Bookmark bookmark);

        void onDelete(Bookmark bookmark);
    }

    private final Bookmark bookmark;
    private boolean hover;
    private boolean selected;

    public BookmarkCard(final Bookmark bookmark, final Handler handler) {
        super(new BorderLayout(14, 0));
        this.bookmark = bookmark;

        radius(UIUtil.RADIUS_CARD);
        fill(UIUtil.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 14));
        setPreferredSize(new Dimension(10, 92));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(new LinkTile(bookmark.getCategory()), BorderLayout.WEST);
        add(buildText(bookmark), BorderLayout.CENTER);
        add(buildActions(bookmark, handler), BorderLayout.EAST);

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
                handler.onSelect(bookmark);
            }
        };
        attach(this, mouse);
    }

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

    private JPanel buildText(Bookmark bookmark) {
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(bookmark.getTitle());
        title.setFont(UIUtil.H2);
        title.setForeground(UIUtil.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel url = new JLabel(bookmark.getUrl());
        url.setFont(UIUtil.SMALL);
        url.setForeground(UIUtil.PRIMARY);
        url.setAlignmentX(LEFT_ALIGNMENT);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        meta.setOpaque(false);
        meta.setAlignmentX(LEFT_ALIGNMENT);
        meta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        meta.add(new Chip(bookmark.getCategory()));

        JLabel saved = new JLabel("Saved " + bookmark.getDateAdded()
                + (bookmark.getNotes().isEmpty() ? "" : "  ·  " + bookmark.getNotes()));
        saved.setFont(UIUtil.SMALL);
        saved.setForeground(UIUtil.MUTED);
        meta.add(saved);

        text.add(Box.createVerticalGlue());
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(url);
        text.add(Box.createVerticalStrut(6));
        text.add(meta);
        text.add(Box.createVerticalGlue());
        return text;
    }

    private JPanel buildActions(final Bookmark bookmark, final Handler handler) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);

        ModernButton edit = ModernButton.iconOnly(Icons.of("edit", 16, UIUtil.BODY),
                ModernButton.Variant.GHOST, "Edit bookmark");
        edit.addActionListener(e -> handler.onEdit(bookmark));

        ModernButton delete = ModernButton.iconOnly(Icons.of("trash", 16, UIUtil.DANGER),
                ModernButton.Variant.DANGER, "Delete bookmark");
        delete.addActionListener(e -> handler.onDelete(bookmark));

        actions.add(edit);
        actions.add(delete);
        return actions;
    }

    public Bookmark getBookmark() {
        return bookmark;
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
    }

    /** Rounded tile with a link glyph, tinted by the bookmark category. */
    private static class LinkTile extends JComponent {

        private final String category;

        LinkTile(String category) {
            this.category = category;
            setPreferredSize(new Dimension(46, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            java.awt.Graphics2D g2 = UIUtil.smooth((java.awt.Graphics2D) g.create());
            int size = 46;
            int y = (getHeight() - size) / 2;
            g2.setColor(UIUtil.categoryTint(category));
            g2.fill(UIUtil.roundRect(0, y, size, size, 12));
            Icons.of("bookmark-filled", 20, UIUtil.categoryInk(category))
                    .paintIcon(this, g2, 13, y + 13);
            g2.dispose();
        }
    }
}
