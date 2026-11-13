package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * A labelled input used by the add / edit dialogs: a small bold caption
 * above a rounded input box that highlights when it has focus.
 */
public class FormField extends JPanel {

    private final RoundPanel box;
    private JTextComponent input;
    private JComboBox<String> combo;

    private FormField(String label, Component editor, int height, boolean required) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(LEFT_ALIGNMENT);

        JLabel caption = new JLabel(required ? label + " *" : label);
        caption.setFont(UIUtil.MICRO_BOLD);
        caption.setForeground(UIUtil.BODY);
        caption.setAlignmentX(LEFT_ALIGNMENT);
        caption.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));

        box = new RoundPanel(new BorderLayout());
        box.radius(UIUtil.RADIUS_CONTROL).fill(UIUtil.SURFACE_ALT);
        box.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
        box.setAlignmentX(LEFT_ALIGNMENT);
        box.setPreferredSize(new Dimension(10, height));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        box.add(editor, BorderLayout.CENTER);

        add(caption);
        add(box);
        add(Box.createVerticalStrut(14));
    }

    /** Single line text input. */
    public static FormField text(String label, boolean required) {
        JTextField field = new JTextField();
        style(field);
        FormField form = new FormField(label, field, 38, required);
        form.input = field;
        form.watchFocus(field);
        return form;
    }

    /** Multi line text input. */
    public static FormField area(String label, int height) {
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        style(area);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(UIUtil.scroll(area), BorderLayout.CENTER);

        FormField form = new FormField(label, wrapper, height, false);
        form.input = area;
        form.watchFocus(area);
        return form;
    }

    /** Drop-down input, restyled so it matches the other fields. */
    public static FormField select(String label, String[] options) {
        final JComboBox<String> combo = new JComboBox<String>(options);
        combo.setFont(UIUtil.BODY_FONT);
        combo.setForeground(UIUtil.INK);
        combo.setOpaque(false);
        combo.setBorder(BorderFactory.createEmptyBorder());
        combo.setFocusable(false);

        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected javax.swing.JButton createArrowButton() {
                javax.swing.JButton arrow = new javax.swing.JButton(
                        Icons.of("chevron-down", 15, UIUtil.MUTED));
                arrow.setBorder(BorderFactory.createEmptyBorder());
                arrow.setContentAreaFilled(false);
                arrow.setFocusPainted(false);
                arrow.setOpaque(false);
                return arrow;
            }

            @Override
            public void paintCurrentValueBackground(java.awt.Graphics g,
                                                    java.awt.Rectangle bounds,
                                                    boolean hasFocus) {
                // the rounded container behind the combo supplies the background
            }
        });

        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                setFont(UIUtil.BODY_FONT);
                setForeground(UIUtil.INK);
                if (index < 0) {
                    // the closed drop-down: let the rounded field show through
                    setOpaque(false);
                    setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
                } else {
                    setOpaque(true);
                    setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                    setBackground(isSelected ? UIUtil.PRIMARY_SOFT : UIUtil.SURFACE);
                }
                return c;
            }
        });

        FormField form = new FormField(label, combo, 38, false);
        form.combo = combo;
        return form;
    }

    private static void style(JTextComponent component) {
        component.setFont(UIUtil.BODY_FONT);
        component.setForeground(UIUtil.INK);
        component.setCaretColor(UIUtil.PRIMARY);
        component.setOpaque(false);
        component.setBackground(new Color(0, 0, 0, 0));
        if (component instanceof JTextField) {
            component.setBorder(null);
        }
    }

    private void watchFocus(Component component) {
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                box.fill(UIUtil.SURFACE).borderColor(UIUtil.PRIMARY);
                box.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                box.fill(UIUtil.SURFACE_ALT).borderColor(UIUtil.BORDER);
                box.repaint();
            }
        });
    }

    /** Marks the field red so the user can see which input failed validation. */
    public void markInvalid(boolean invalid) {
        box.borderColor(invalid ? UIUtil.DANGER : UIUtil.BORDER);
        box.fill(invalid ? new Color(0xFFF6F6) : UIUtil.SURFACE_ALT);
        box.repaint();
    }

    public String getValue() {
        if (combo != null) {
            return (String) combo.getSelectedItem();
        }
        return input.getText().trim();
    }

    public void setValue(String value) {
        if (combo != null) {
            combo.setSelectedItem(value);
        } else {
            input.setText(value);
        }
    }

    public void focus() {
        if (input != null) {
            input.requestFocusInWindow();
        }
    }
}
