package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * A panel with rounded corners, an optional hairline border and an
 * optional soft shadow. This is the base surface used for every card,
 * toolbar and input container in the application.
 */
public class RoundPanel extends JPanel {

    private int radius = UIUtil.RADIUS_CARD;
    private Color fill = UIUtil.SURFACE;
    private Color borderColor = UIUtil.BORDER;
    private boolean shadow;
    private int shadowSpread = 6;
    private int shadowAlpha = 26;

    public RoundPanel() {
        this(null);
    }

    public RoundPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public RoundPanel radius(int r) {
        this.radius = r;
        return this;
    }

    public RoundPanel fill(Color color) {
        this.fill = color;
        return this;
    }

    public RoundPanel borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    public RoundPanel noBorder() {
        this.borderColor = null;
        return this;
    }

    public RoundPanel shadow(boolean on) {
        this.shadow = on;
        return this;
    }

    public RoundPanel shadow(int spread, int alpha) {
        this.shadow = true;
        this.shadowSpread = spread;
        this.shadowAlpha = alpha;
        return this;
    }

    protected void setFillColor(Color color) {
        this.fill = color;
    }

    protected Color fillColor() {
        return fill;
    }

    protected int radius() {
        return radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
        int pad = shadow ? shadowSpread : 0;
        int w = getWidth() - pad * 2;
        int h = getHeight() - pad * 2;

        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }
        if (shadow) {
            UIUtil.shadow(g2, pad, pad, w, h, radius, shadowSpread, shadowAlpha);
        }
        if (fill != null) {
            g2.setColor(fill);
            g2.fill(UIUtil.roundRect(pad, pad, w, h, radius));
        }
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.draw(UIUtil.roundRect(pad + 0.5, pad + 0.5, w - 1.0, h - 1.0, radius));
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
