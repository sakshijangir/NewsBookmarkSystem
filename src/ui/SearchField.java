package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Rounded search input with a leading magnifier icon, placeholder text
 * and a focus ring. Typing fires the listener, so results filter live.
 */
public class SearchField extends RoundPanel {

    /** Callback fired whenever the text changes. */
    public interface Listener {
        void onChange(String text);
    }

    private final PlaceholderField field;
    private boolean focused;

    public SearchField(String placeholder, int width, final Listener listener) {
        super(new BorderLayout(9, 0));
        field = new PlaceholderField(placeholder);

        radius(UIUtil.RADIUS_CONTROL);
        fill(UIUtil.SURFACE_ALT);
        setBorder(BorderFactory.createEmptyBorder(0, 13, 0, 12));
        setPreferredSize(new Dimension(width, 38));
        setMaximumSize(new Dimension(width, 38));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                fire();
            }

            public void removeUpdate(DocumentEvent e) {
                fire();
            }

            public void changedUpdate(DocumentEvent e) {
                fire();
            }

            private void fire() {
                if (listener != null) {
                    listener.onChange(field.getText());
                }
            }
        });

        add(new JLabel(Icons.of("search", 16, UIUtil.MUTED)), BorderLayout.WEST);
        add(field, BorderLayout.CENTER);
    }

    public String getText() {
        return field.getText();
    }

    public void setText(String text) {
        field.setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        setFillColor(focused ? UIUtil.SURFACE : UIUtil.SURFACE_ALT);
        borderColor(focused ? UIUtil.PRIMARY : UIUtil.BORDER);
        super.paintComponent(g);
    }

    /** Text field that draws its own placeholder and no background. */
    private static class PlaceholderField extends JTextField {

        private final String placeholder;

        PlaceholderField(String placeholder) {
            this.placeholder = placeholder;
            setBorder(null);
            setOpaque(false);
            setBackground(new Color(0, 0, 0, 0));
            setFont(UIUtil.BODY_FONT);
            setForeground(UIUtil.INK);
            setCaretColor(UIUtil.PRIMARY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!getText().isEmpty()) {
                return;
            }
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            g2.setFont(UIUtil.BODY_FONT);
            g2.setColor(UIUtil.MUTED);
            int y = getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 1;
            g2.drawString(placeholder, 1, y);
            g2.dispose();
        }
    }
}
