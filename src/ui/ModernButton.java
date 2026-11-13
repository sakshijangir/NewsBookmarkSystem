package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Flat button with rounded corners, an optional leading icon and
 * hover / pressed states.
 *
 * Four variants cover every action in the application:
 * PRIMARY (filled), SECONDARY (outlined), GHOST (text only) and
 * DANGER (destructive actions).
 */
public class ModernButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, GHOST, DANGER }

    private final Variant variant;
    private Icons icon;
    private boolean hover;
    private boolean pressed;
    private int radius = UIUtil.RADIUS_CONTROL;
    private int padX = 16;
    private int height = 38;
    private boolean iconOnly;

    public ModernButton(String text, Variant variant) {
        this(text, variant, null);
    }

    public ModernButton(String text, Variant variant, Icons icon) {
        super(text);
        this.variant = variant;
        this.icon = icon;

        setFont(UIUtil.SMALL_BOLD);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(null);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(textColor());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    /** Square icon-only button, used for row actions. */
    public static ModernButton iconOnly(Icons icon, Variant variant, String tooltip) {
        ModernButton button = new ModernButton("", variant, icon);
        button.iconOnly = true;
        button.height = 32;
        button.padX = 0;
        button.radius = 9;
        button.setToolTipText(tooltip);
        return button;
    }

    public ModernButton size(int h) {
        this.height = h;
        return this;
    }

    public ModernButton pill() {
        this.radius = UIUtil.RADIUS_PILL;
        return this;
    }

    private Color textColor() {
        switch (variant) {
            case PRIMARY:
                return Color.WHITE;
            case DANGER:
                return UIUtil.DANGER;
            case SECONDARY:
                return UIUtil.INK;
            default:
                return UIUtil.BODY;
        }
    }

    private Color background() {
        switch (variant) {
            case PRIMARY:
                return pressed ? UIUtil.PRIMARY_HOVER
                        : hover ? UIUtil.mix(UIUtil.PRIMARY, UIUtil.PRIMARY_HOVER, 0.5)
                        : UIUtil.PRIMARY;
            case SECONDARY:
                return pressed ? new Color(0xEFF1F6) : hover ? UIUtil.SURFACE_ALT : UIUtil.SURFACE;
            case DANGER:
                return hover ? UIUtil.DANGER_SOFT : null;
            default:
                return hover ? new Color(0xEFF1F6) : null;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        if (iconOnly) {
            return new Dimension(height, height);
        }
        FontMetrics fm = getFontMetrics(getFont());
        int width = fm.stringWidth(getText()) + padX * 2 + (icon != null ? 24 : 0);
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize() {
        return isMaximumSizeSet() ? super.getMaximumSize() : getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return isMinimumSizeSet() ? super.getMinimumSize() : getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
        int w = getWidth();
        int h = getHeight();

        Color bg = background();
        if (bg != null) {
            g2.setColor(bg);
            g2.fill(UIUtil.roundRect(0, 0, w, h, radius));
        }
        if (variant == Variant.SECONDARY) {
            g2.setColor(hover ? new Color(0xD3D8E4) : UIUtil.BORDER);
            g2.draw(UIUtil.roundRect(0.5, 0.5, w - 1.0, h - 1.0, radius));
        }

        Color ink = textColor();
        if (!isEnabled()) {
            ink = UIUtil.MUTED;
        }

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int iconSize = icon == null ? 0 : icon.getIconWidth();
        int textWidth = text == null || text.isEmpty() ? 0 : fm.stringWidth(text);
        int gap = (icon != null && textWidth > 0) ? 7 : 0;
        int totalWidth = textWidth + gap + (icon != null ? iconSize : 0);
        int x = (w - totalWidth) / 2;

        if (icon != null) {
            icon.withColor(ink).paintIcon(this, g2, x, (h - iconSize) / 2);
            x += iconSize + gap;
        }
        if (textWidth > 0) {
            g2.setColor(ink);
            g2.drawString(text, x, (h - fm.getHeight()) / 2 + fm.getAscent());
        }
        g2.dispose();
    }
}
