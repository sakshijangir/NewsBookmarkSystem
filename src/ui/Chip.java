package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * A small rounded pill used to show a category. The colours come from
 * the design system so every category keeps the same identity across
 * the news feed, the bookmark list and the dashboard.
 */
public class Chip extends JComponent {

    private String text;
    private Color ink;
    private Color tint;

    public Chip(String category) {
        setCategory(category);
        setFont(UIUtil.MICRO_BOLD);
        setOpaque(false);
    }

    public final void setCategory(String category) {
        this.text = category == null ? "" : category;
        this.ink = UIUtil.categoryInk(category);
        this.tint = UIUtil.categoryTint(category);
        revalidate();
        repaint();
    }

    /** Neutral grey variant, used for counts and secondary labels. */
    public Chip neutral() {
        this.ink = UIUtil.BODY;
        this.tint = new Color(0xEFF1F6);
        repaint();
        return this;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(text) + 20, 21);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
        g2.setColor(tint);
        g2.fill(UIUtil.roundRect(0, 0, getWidth(), getHeight(), getHeight() / 2));
        g2.setColor(ink);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, ty);
        g2.dispose();
    }
}
