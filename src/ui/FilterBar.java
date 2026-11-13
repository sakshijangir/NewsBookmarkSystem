package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A row of clickable category pills used instead of a drop-down filter.
 * The selected pill is filled with the category colour, so the current
 * filter is visible at a glance.
 */
public class FilterBar extends JPanel {

    public interface Listener {
        void onSelect(String category);
    }

    private final List<Pill> pills = new ArrayList<Pill>();
    private final Listener listener;
    private String selected;

    public FilterBar(String[] categories, Listener listener) {
        super(new WrapLayout(6, 8));
        this.listener = listener;
        this.selected = categories.length > 0 ? categories[0] : null;
        setOpaque(false);

        for (String category : categories) {
            Pill pill = new Pill(category);
            pills.add(pill);
            add(pill);
        }

        // The wrap layout depends on the width, so re-measure on resize.
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                java.awt.Container parent = getParent();
                if (parent != null) {
                    parent.revalidate();
                }
            }
        });
    }

    public String getSelected() {
        return selected;
    }

    public void select(String category) {
        this.selected = category;
        for (Pill pill : pills) {
            pill.repaint();
        }
        if (listener != null) {
            listener.onSelect(category);
        }
    }

    /** One clickable pill inside the bar. */
    private class Pill extends JComponent {

        private final String label;
        private boolean hover;

        Pill(String label) {
            this.label = label;
            setFont(UIUtil.MICRO_BOLD);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                public void mouseClicked(MouseEvent e) {
                    select(Pill.this.label);
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(label) + 20, 30);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            boolean active = label.equals(selected);
            boolean all = "All".equals(label);

            Color ink = all ? UIUtil.BODY : UIUtil.categoryInk(label);
            Color fill;
            Color text;

            if (active) {
                fill = all ? UIUtil.INK : ink;
                text = Color.WHITE;
            } else {
                fill = hover ? new Color(0xEDEFF5) : UIUtil.SURFACE;
                text = hover ? UIUtil.INK : UIUtil.BODY;
            }

            g2.setColor(fill);
            g2.fill(UIUtil.roundRect(0, 0, getWidth(), getHeight(), getHeight() / 2));
            if (!active) {
                g2.setColor(UIUtil.BORDER);
                g2.draw(UIUtil.roundRect(0.5, 0.5, getWidth() - 1.0, getHeight() - 1.0,
                        getHeight() / 2));
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(text);
            g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
