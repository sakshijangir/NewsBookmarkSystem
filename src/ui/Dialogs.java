package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Replacement for JOptionPane so that confirmations, warnings and the
 * About box match the rest of the interface.
 */
public final class Dialogs {

    private Dialogs() {
        // static helpers only
    }

    /** Asks a yes / no question. Returns true when the user confirms. */
    public static boolean confirm(Window parent, String title, String message,
                                  String confirmText) {
        final boolean[] result = {false};
        final JDialog dialog = base(parent, title);

        JPanel body = content(title, message, "trash", UIUtil.DANGER_SOFT, UIUtil.DANGER);

        ModernButton cancel = new ModernButton("Cancel", ModernButton.Variant.SECONDARY);
        cancel.addActionListener(e -> dialog.dispose());

        ModernButton confirm = new ModernButton(confirmText, ModernButton.Variant.PRIMARY);
        confirm.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        body.add(Box.createVerticalStrut(22));
        body.add(buttonRow(cancel, confirm));

        dialog.add(body);
        dialog.pack();
        dialog.setSize(Math.max(420, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }

    /** Shows a single-button information or warning message. */
    public static void message(Window parent, String title, String message) {
        final JDialog dialog = base(parent, title);
        JPanel body = content(title, message, "sparkle", UIUtil.PRIMARY_SOFT, UIUtil.PRIMARY);

        ModernButton ok = new ModernButton("Got it", ModernButton.Variant.PRIMARY);
        ok.addActionListener(e -> dialog.dispose());

        body.add(Box.createVerticalStrut(22));
        body.add(buttonRow(ok));

        dialog.add(body);
        dialog.pack();
        dialog.setSize(Math.max(420, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /** About box shown from the Help menu. */
    public static void about(Frame parent) {
        message(parent, "News & Bookmark Management System",
                "Desktop application built with Java Swing and MySQL for the "
                        + "Mini Project Lab.  Data source: "
                        + data.DataStore.getDataSourceLabel() + ".");
    }

    /* ------------------------------------------------------------------
     * Building blocks
     * ------------------------------------------------------------------ */

    private static JDialog base(Window parent, String title) {
        JDialog dialog = new JDialog(parent, title, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(UIUtil.BORDER));
        dialog.getContentPane().setBackground(UIUtil.SURFACE);
        return dialog;
    }

    private static JPanel content(String title, String message, String icon,
                                  Color tint, Color ink) {
        JPanel body = new JPanel();
        body.setBackground(UIUtil.SURFACE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(24, 26, 22, 26));

        JComponent badge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                g2.setColor(tint);
                g2.fill(UIUtil.roundRect(0, 0, 42, 42, 12));
                Icons.of(icon, 20, ink).paintIcon(this, g2, 11, 11);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(42, 42));
        badge.setMaximumSize(new Dimension(42, 42));
        badge.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        JLabel heading = new JLabel(title);
        heading.setFont(UIUtil.H1);
        heading.setForeground(UIUtil.INK);
        heading.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        JPanel text = UIUtil.textBlock(message, UIUtil.BODY_FONT, UIUtil.BODY, 356, 4);
        text.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        body.add(badge);
        body.add(Box.createVerticalStrut(16));
        body.add(heading);
        body.add(Box.createVerticalStrut(8));
        body.add(text);
        return body;
    }

    private static JPanel buttonRow(ModernButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        row.setOpaque(false);
        row.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        for (ModernButton button : buttons) {
            row.add(button);
        }
        return row;
    }

    /** Utility for dialogs that need a footer separator line. */
    public static JPanel divider() {
        JPanel line = new JPanel();
        line.setBackground(UIUtil.BORDER);
        line.setPreferredSize(new Dimension(10, 1));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setBorder(BorderFactory.createEmptyBorder());
        return line;
    }

    /** Convenience wrapper used by panels that only have a component. */
    public static Window windowOf(JComponent component) {
        return javax.swing.SwingUtilities.getWindowAncestor(component);
    }

    /** Empty-state block shown when a list has no results. */
    public static JPanel emptyState(String title, String message, String icon) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        JComponent badge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                g2.setColor(new Color(0xEDF0F7));
                g2.fillOval(0, 0, 56, 56);
                Icons.of(icon, 24, UIUtil.MUTED).paintIcon(this, g2, 16, 16);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(56, 56));
        badge.setMaximumSize(new Dimension(56, 56));
        badge.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JLabel heading = new JLabel(title);
        heading.setFont(UIUtil.H2);
        heading.setForeground(UIUtil.INK);
        heading.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JLabel copy = new JLabel(message);
        copy.setFont(UIUtil.SMALL);
        copy.setForeground(UIUtil.MUTED);
        copy.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        panel.add(badge);
        panel.add(Box.createVerticalStrut(14));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(6));
        panel.add(copy);
        return panel;
    }

    /** Wraps a panel so BorderLayout.CENTER keeps it at the top. */
    public static JPanel top(JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(component, BorderLayout.NORTH);
        return wrapper;
    }
}
