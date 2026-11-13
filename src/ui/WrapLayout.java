package ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Left aligned layout that moves to the next line when the components
 * no longer fit the width of the container. Used for the category
 * filter pills so they stay visible on narrow windows.
 */
public class WrapLayout implements LayoutManager {

    private final int hgap;
    private final int vgap;

    public WrapLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    public void addLayoutComponent(String name, Component component) {
        // nothing to store
    }

    public void removeLayoutComponent(Component component) {
        // nothing to store
    }

    public Dimension preferredLayoutSize(Container parent) {
        return layoutSize(parent, availableWidth(parent), false);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    public void layoutContainer(Container parent) {
        layoutSize(parent, availableWidth(parent), true);
    }

    private int availableWidth(Container parent) {
        int width = parent.getWidth();
        if (width <= 0) {
            width = parent.getParent() == null ? 0 : parent.getParent().getWidth();
        }
        return width <= 0 ? Integer.MAX_VALUE : width;
    }

    /** Places the components, or just measures them when apply is false. */
    private Dimension layoutSize(Container parent, int maxWidth, boolean apply) {
        Insets insets = parent.getInsets();
        int limit = maxWidth - insets.left - insets.right;
        int x = insets.left;
        int y = insets.top;
        int rowHeight = 0;
        int widest = 0;

        for (Component component : parent.getComponents()) {
            if (!component.isVisible()) {
                continue;
            }
            Dimension size = component.getPreferredSize();

            if (x > insets.left && x - insets.left + size.width > limit) {
                x = insets.left;
                y += rowHeight + vgap;
                rowHeight = 0;
            }
            if (apply) {
                component.setBounds(x, y, size.width, size.height);
            }
            x += size.width + hgap;
            rowHeight = Math.max(rowHeight, size.height);
            widest = Math.max(widest, x - insets.left);
        }

        return new Dimension(Math.min(widest, limit) + insets.left + insets.right,
                y + rowHeight + insets.bottom);
    }
}
