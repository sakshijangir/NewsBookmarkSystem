package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import javax.swing.Icon;

/**
 * Line icons drawn with Java2D.
 *
 * Using vector shapes instead of image files keeps the project free of
 * external assets and lets every icon take the colour and size it needs.
 * All shapes are designed on a 24 x 24 grid and scaled when painted.
 */
public class Icons implements Icon {

    private final String name;
    private final int size;
    private final Color color;
    private final float weight;

    private Icons(String name, int size, Color color, float weight) {
        this.name = name;
        this.size = size;
        this.color = color;
        this.weight = weight;
    }

    public static Icons of(String name, int size, Color color) {
        return new Icons(name, size, color, 1.7f);
    }

    public static Icons bold(String name, int size, Color color) {
        return new Icons(name, size, color, 2.1f);
    }

    public Icons withColor(Color newColor) {
        return new Icons(name, size, newColor, weight);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
        double scale = size / 24.0;
        g2.translate(x, y);
        g2.scale(scale, scale);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        draw(g2);
        g2.dispose();
    }

    private void draw(Graphics2D g) {
        if ("home".equals(name)) {
            GeneralPath p = path(3, 10, 12, 3, 21, 10);
            g.draw(p);
            g.draw(path(5.5, 10, 5.5, 20, 18.5, 20, 18.5, 10));
            g.draw(path(10, 20, 10, 14.5, 14, 14.5, 14, 20));
        } else if ("news".equals(name)) {
            g.draw(UIUtil.roundRect(3, 5, 14, 15, 2));
            g.draw(path(17, 9, 21, 9, 21, 17.5));
            g.draw(path(21, 17.5, 20, 20, 4.5, 20));
            g.draw(path(6.5, 9, 13.5, 9));
            g.draw(path(6.5, 12.5, 13.5, 12.5));
            g.draw(path(6.5, 16, 11, 16));
        } else if ("bookmark".equals(name)) {
            g.draw(path(6, 4, 18, 4, 18, 21, 12, 16.5, 6, 21, 6, 4));
        } else if ("bookmark-filled".equals(name)) {
            GeneralPath p = closed(6, 4, 18, 4, 18, 21, 12, 16.5, 6, 21);
            g.fill(p);
        } else if ("search".equals(name)) {
            g.drawOval(4, 4, 12, 12);
            g.draw(path(15.5, 15.5, 20, 20));
        } else if ("plus".equals(name)) {
            g.draw(path(12, 5, 12, 19));
            g.draw(path(5, 12, 19, 12));
        } else if ("edit".equals(name)) {
            g.draw(path(4, 20, 4.5, 16, 16, 4.5, 19.5, 8, 8, 19.5, 4, 20));
            g.draw(path(14, 6.5, 17.5, 10));
        } else if ("trash".equals(name)) {
            g.draw(path(4, 6.5, 20, 6.5));
            g.draw(path(9.5, 6.5, 9.5, 4, 14.5, 4, 14.5, 6.5));
            g.draw(path(6, 6.5, 7, 20, 17, 20, 18, 6.5));
            g.draw(path(10, 10, 10, 17));
            g.draw(path(14, 10, 14, 17));
        } else if ("logout".equals(name)) {
            g.draw(path(14, 4, 5, 4, 5, 20, 14, 20));
            g.draw(path(11, 12, 21, 12));
            g.draw(path(17.5, 8.5, 21, 12, 17.5, 15.5));
        } else if ("user".equals(name)) {
            g.drawOval(8, 3, 8, 8);
            g.draw(path(4, 21, 4.5, 17, 8, 14.5, 16, 14.5, 19.5, 17, 20, 21));
        } else if ("link".equals(name)) {
            g.draw(path(10, 14, 14, 10));
            g.draw(path(13, 6.5, 15.5, 4, 20, 8.5, 17.5, 11));
            g.draw(path(11, 17.5, 8.5, 20, 4, 15.5, 6.5, 13));
        } else if ("calendar".equals(name)) {
            g.draw(UIUtil.roundRect(4, 5.5, 16, 15, 2));
            g.draw(path(4, 10, 20, 10));
            g.draw(path(8.5, 3, 8.5, 7));
            g.draw(path(15.5, 3, 15.5, 7));
        } else if ("tag".equals(name)) {
            g.draw(path(3.5, 11.5, 11.5, 3.5, 20.5, 3.5, 20.5, 12.5, 12.5, 20.5, 3.5, 11.5));
            g.fillOval(15, 7, 3, 3);
        } else if ("layers".equals(name)) {
            g.draw(closed(12, 3, 21, 8, 12, 13, 3, 8));
            g.draw(path(3.5, 12, 12, 17, 20.5, 12));
            g.draw(path(3.5, 16, 12, 21, 20.5, 16));
        } else if ("chevron".equals(name)) {
            g.draw(path(9.5, 5, 16, 12, 9.5, 19));
        } else if ("chevron-down".equals(name)) {
            g.draw(path(5.5, 9.5, 12, 16, 18.5, 9.5));
        } else if ("close".equals(name)) {
            g.draw(path(6, 6, 18, 18));
            g.draw(path(18, 6, 6, 18));
        } else if ("check".equals(name)) {
            g.draw(path(5, 12.5, 10, 17.5, 19, 7));
        } else if ("sparkle".equals(name)) {
            g.draw(path(12, 3, 14, 10, 21, 12, 14, 14, 12, 21, 10, 14, 3, 12, 10, 10, 12, 3));
        } else if ("filter".equals(name)) {
            g.draw(path(4, 6.5, 20, 6.5));
            g.draw(path(7, 12, 17, 12));
            g.draw(path(10, 17.5, 14, 17.5));
        } else if ("source".equals(name)) {
            g.draw(UIUtil.roundRect(3.5, 6, 17, 12, 2));
            g.draw(path(6.5, 10, 12, 10));
            g.draw(path(6.5, 14, 15, 14));
        }
    }

    /** Builds an open poly-line from x,y pairs. */
    private static GeneralPath path(double... xy) {
        GeneralPath p = new GeneralPath(Path2D.WIND_NON_ZERO);
        p.moveTo(xy[0], xy[1]);
        for (int i = 2; i < xy.length; i += 2) {
            p.lineTo(xy[i], xy[i + 1]);
        }
        return p;
    }

    /** Builds a closed polygon from x,y pairs. */
    private static GeneralPath closed(double... xy) {
        GeneralPath p = path(xy);
        p.closePath();
        return p;
    }
}
