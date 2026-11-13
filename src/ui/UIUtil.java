package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Design system for the application.
 *
 * Every colour, font size, corner radius and painting helper used by the
 * interface is defined here, so the whole product can be restyled from a
 * single file.
 */
public final class UIUtil {

    /* ------------------------------------------------------------------
     * Colour palette
     * ------------------------------------------------------------------ */

    /** Application background behind the content cards. */
    public static final Color BG = new Color(0xF4F5F8);
    /** Card and input background. */
    public static final Color SURFACE = new Color(0xFFFFFF);
    /** Subtle fill for hovered rows and secondary surfaces. */
    public static final Color SURFACE_ALT = new Color(0xF7F8FB);

    /** Dark navigation rail. */
    public static final Color NAV = new Color(0x141A2A);
    public static final Color NAV_ALT = new Color(0x1E2740);
    public static final Color NAV_TEXT = new Color(0x9AA4BC);

    /** Headings and primary text. */
    public static final Color INK = new Color(0x0E1320);
    /** Body copy. */
    public static final Color BODY = new Color(0x3F4757);
    /** Captions, metadata, placeholders. */
    public static final Color MUTED = new Color(0x8A93A6);
    /** Hairline borders. */
    public static final Color BORDER = new Color(0xE5E8EF);

    /** Brand colour, used for primary actions and the active nav item. */
    public static final Color PRIMARY = new Color(0x3B5BF5);
    public static final Color PRIMARY_HOVER = new Color(0x2F4AD8);
    public static final Color PRIMARY_SOFT = new Color(0xEBEFFE);

    /** Secondary accent, used for bookmarks. */
    public static final Color ACCENT = new Color(0xFF6B45);
    public static final Color ACCENT_SOFT = new Color(0xFFEDE7);

    public static final Color SUCCESS = new Color(0x12A57A);
    public static final Color DANGER = new Color(0xE04A4A);
    public static final Color DANGER_SOFT = new Color(0xFDECEC);

    /* ------------------------------------------------------------------
     * Typography
     * ------------------------------------------------------------------ */

    private static final String FAMILY = resolveFamily();

    public static final Font DISPLAY = new Font(FAMILY, Font.BOLD, 24);
    public static final Font H1 = new Font(FAMILY, Font.BOLD, 19);
    public static final Font H2 = new Font(FAMILY, Font.BOLD, 15);
    public static final Font H3 = new Font(FAMILY, Font.BOLD, 13);
    public static final Font BODY_FONT = new Font(FAMILY, Font.PLAIN, 13);
    public static final Font SMALL = new Font(FAMILY, Font.PLAIN, 12);
    public static final Font SMALL_BOLD = new Font(FAMILY, Font.BOLD, 12);
    public static final Font MICRO = new Font(FAMILY, Font.PLAIN, 11);
    public static final Font MICRO_BOLD = new Font(FAMILY, Font.BOLD, 11);
    public static final Font NUMERAL = new Font(FAMILY, Font.BOLD, 30);

    /** Picks the nicest UI font that is actually installed on this machine. */
    private static String resolveFamily() {
        String[] wanted = {"Inter", "SF Pro Text", "Helvetica Neue", "Segoe UI",
                "Roboto", "Noto Sans", "DejaVu Sans", "Arial"};
        Set<String> available = new HashSet<String>();
        try {
            String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();
            for (String n : names) {
                available.add(n);
            }
        } catch (Exception ignored) {
            return Font.SANS_SERIF;
        }
        for (String w : wanted) {
            if (available.contains(w)) {
                return w;
            }
        }
        return Font.SANS_SERIF;
    }

    /* ------------------------------------------------------------------
     * Shape tokens
     * ------------------------------------------------------------------ */

    public static final int RADIUS_CARD = 14;
    public static final int RADIUS_CONTROL = 10;
    public static final int RADIUS_PILL = 999;

    private UIUtil() {
        // design tokens only
    }

    /* ------------------------------------------------------------------
     * Painting helpers
     * ------------------------------------------------------------------ */

    /** Turns on antialiasing for shapes and text. */
    public static Graphics2D smooth(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        return g;
    }

    public static Shape roundRect(double x, double y, double w, double h, int radius) {
        int r = Math.min(radius, (int) Math.min(w, h) / 2);
        return new RoundRectangle2D.Double(x, y, w, h, r * 2.0, r * 2.0);
    }

    /** Paints a soft drop shadow underneath a rounded rectangle. */
    public static void shadow(Graphics2D g, int x, int y, int w, int h,
                             int radius, int spread, int alpha) {
        for (int i = spread; i > 0; i--) {
            g.setColor(new Color(0x14, 0x1A, 0x2A, Math.max(1, alpha / (i + 1))));
            g.fill(roundRect(x - i, y - i + 1, w + i * 2.0, h + i * 2.0, radius + i));
        }
    }

    public static Color alpha(Color color, int a) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
    }

    /** Mixes two colours; ratio 0 returns a, ratio 1 returns b. */
    public static Color mix(Color a, Color b, double ratio) {
        double r = Math.max(0, Math.min(1, ratio));
        return new Color(
                (int) (a.getRed() + (b.getRed() - a.getRed()) * r),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * r),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * r));
    }

    /* ------------------------------------------------------------------
     * Category colours
     * ------------------------------------------------------------------ */

    /** Text colour of the chip shown for a category. */
    public static Color categoryInk(String category) {
        if (category == null) {
            return MUTED;
        }
        if (category.equals("Technology")) {
            return new Color(0x2F49B8);
        }
        if (category.equals("Sports")) {
            return new Color(0x0E7A55);
        }
        if (category.equals("Business")) {
            return new Color(0xA9560F);
        }
        if (category.equals("Science")) {
            return new Color(0x6B3ECF);
        }
        if (category.equals("Health")) {
            return new Color(0xBE2F58);
        }
        if (category.equals("Entertainment")) {
            return new Color(0x0F6E93);
        }
        return new Color(0x4A5266);
    }

    /** Background colour of the chip shown for a category. */
    public static Color categoryTint(String category) {
        return mix(categoryInk(category), Color.WHITE, 0.88);
    }

    /* ------------------------------------------------------------------
     * Text helpers
     * ------------------------------------------------------------------ */

    /** Shortens text with an ellipsis so it fits a fixed width label. */
    public static String clip(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, Math.max(0, maxChars - 1)).trim() + "…";
    }

    /** Wraps plain text in HTML so a JLabel renders it on several lines. */
    public static String html(String text, int widthPx) {
        String safe = text == null ? "" : text
                .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<html><body style='width:" + widthPx + "px; margin:0'>"
                + safe + "</body></html>";
    }

    /**
     * Builds a block of stacked labels wrapped to an exact pixel width.
     *
     * The HTML renderer inside JLabel is not reliable about honouring a
     * width, so the text is measured and split here instead. This keeps
     * long headlines and summaries inside their card.
     */
    public static javax.swing.JPanel textBlock(String text, Font font, Color color,
                                               int width, int lineGap) {
        javax.swing.JPanel block = new javax.swing.JPanel();
        block.setOpaque(false);
        block.setLayout(new javax.swing.BoxLayout(block, javax.swing.BoxLayout.Y_AXIS));
        block.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        java.awt.FontMetrics fm = new javax.swing.JLabel().getFontMetrics(font);
        StringBuilder line = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<String>();

        for (String word : (text == null ? "" : text).split("\\s+")) {
            if (word.isEmpty()) {
                continue;
            }
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(candidate) > width && line.length() > 0) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        for (int i = 0; i < lines.size(); i++) {
            javax.swing.JLabel label = new javax.swing.JLabel(lines.get(i));
            label.setFont(font);
            label.setForeground(color);
            label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new java.awt.Dimension(width + 2,
                    fm.getHeight() + 2));
            block.add(label);
            if (i < lines.size() - 1 && lineGap > 0) {
                block.add(javax.swing.Box.createVerticalStrut(lineGap));
            }
        }

        int height = lines.size() * fm.getHeight()
                + Math.max(0, lines.size() - 1) * lineGap;
        block.setMaximumSize(new java.awt.Dimension(width + 2, height + 2));
        block.setPreferredSize(new java.awt.Dimension(width, height));
        return block;
    }

    /* ------------------------------------------------------------------
     * Scrolling
     * ------------------------------------------------------------------ */

    /** Wraps a component in a scroll pane with a thin, modern scrollbar. */
    public static JScrollPane scroll(JComponent view) {
        JScrollPane pane = new JScrollPane(view,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        pane.setViewportBorder(null);
        pane.setOpaque(false);
        pane.getViewport().setOpaque(false);
        pane.getVerticalScrollBar().setUnitIncrement(18);
        styleScrollBar(pane.getVerticalScrollBar());
        return pane;
    }

    private static void styleScrollBar(JScrollBar bar) {
        bar.setUI(new ThinScrollBarUI());
        bar.setOpaque(false);
        bar.setPreferredSize(new java.awt.Dimension(10, 10));
        bar.setUnitIncrement(18);
    }

    /** Minimal scrollbar: no arrows, a rounded grey thumb, no track. */
    private static class ThinScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

        @Override
        protected javax.swing.JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected javax.swing.JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private javax.swing.JButton zeroButton() {
            javax.swing.JButton button = new javax.swing.JButton();
            button.setPreferredSize(new java.awt.Dimension(0, 0));
            button.setMinimumSize(new java.awt.Dimension(0, 0));
            button.setMaximumSize(new java.awt.Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(java.awt.Graphics g, JComponent c, java.awt.Rectangle r) {
            // no track
        }

        @Override
        protected void paintThumb(java.awt.Graphics g, JComponent c, java.awt.Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = smooth((Graphics2D) g.create());
            Color color = isDragging ? new Color(0x9AA2B4)
                    : isThumbRollover() ? new Color(0xAEB6C6) : new Color(0xC8CDD9);
            g2.setColor(color);
            g2.fill(roundRect(r.x + 2, r.y + 2, r.width - 4.0, r.height - 4.0, 5));
            g2.dispose();
        }
    }
}
