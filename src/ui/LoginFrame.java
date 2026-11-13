package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import data.DataStore;
import model.User;

/**
 * Login screen: a branded gradient panel on the left and a clean
 * sign-in card on the right.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private RoundPanel userBox;
    private RoundPanel passBox;

    public LoginFrame() {
        setTitle("News & Bookmark Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(940, 580);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtil.SURFACE);

        add(new BrandPanel(), BorderLayout.WEST);
        add(buildForm(), BorderLayout.CENTER);
    }

    /* ------------------------------------------------------------------
     * Left: brand panel
     * ------------------------------------------------------------------ */

    private static class BrandPanel extends JPanel {

        private static final int WIDTH = 430;
        private static final int INNER = WIDTH - 88;

        BrandPanel() {
            setPreferredSize(new Dimension(WIDTH, 0));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(0, 44, 0, 44));
            setOpaque(false);

            JLabel mark = new JLabel("NB");
            mark.setFont(new Font(UIUtil.H1.getFamily(), Font.BOLD, 20));
            mark.setForeground(Color.WHITE);
            mark.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            mark.setAlignmentX(LEFT_ALIGNMENT);

            JLabel headline = new JLabel("Everything worth");
            JLabel headline2 = new JLabel("reading, in one place.");
            for (JLabel label : new JLabel[] {headline, headline2}) {
                label.setFont(new Font(UIUtil.DISPLAY.getFamily(), Font.BOLD, 27));
                label.setForeground(Color.WHITE);
                label.setAlignmentX(LEFT_ALIGNMENT);
                label.setMaximumSize(new Dimension(INNER, 40));
            }

            JPanel copy = UIUtil.textBlock(
                    "Browse the feed, save what matters, and find it again in one "
                            + "search. Everything stays organised by category.",
                    UIUtil.BODY_FONT, new Color(0xB9C4E4), INNER - 4, 4);
            copy.setAlignmentX(LEFT_ALIGNMENT);

            add(Box.createVerticalGlue());
            add(logoRow());
            add(Box.createVerticalStrut(34));
            add(headline);
            add(Box.createVerticalStrut(4));
            add(headline2);
            add(Box.createVerticalStrut(16));
            add(copy);
            add(Box.createVerticalStrut(34));
            add(feature("Save an article in one click"));
            add(Box.createVerticalStrut(12));
            add(feature("Filter by category instantly"));
            add(Box.createVerticalStrut(12));
            add(feature("Keep private notes on every link"));
            add(Box.createVerticalGlue());
        }

        private JPanel logoRow() {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

            JPanel tile = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                    g2.setColor(UIUtil.ACCENT);
                    g2.fill(UIUtil.roundRect(0, 0, 38, 38, 11));
                    Icons.bold("bookmark-filled", 20, Color.WHITE).paintIcon(this, g2, 9, 9);
                    g2.dispose();
                }
            };
            tile.setOpaque(false);
            tile.setPreferredSize(new Dimension(38, 38));

            JLabel name = new JLabel("News & Bookmarks");
            name.setFont(UIUtil.H2);
            name.setForeground(Color.WHITE);

            row.add(tile);
            row.add(name);
            return row;
        }

        private JPanel feature(String text) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

            JLabel tick = new JLabel(Icons.bold("check", 15, UIUtil.ACCENT));
            JLabel label = new JLabel(text);
            label.setFont(UIUtil.SMALL);
            label.setForeground(new Color(0xD3DBF0));

            row.add(tick);
            row.add(label);
            return row;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            g2.setPaint(new GradientPaint(0, 0, new Color(0x1B2340),
                    0, getHeight(), new Color(0x0E1424)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // decorative soft circles
            g2.setColor(new Color(0x3B, 0x5B, 0xF5, 38));
            g2.fillOval(-90, -70, 260, 260);
            g2.setColor(new Color(0xFF, 0x6B, 0x45, 20));
            g2.fillOval(getWidth() - 110, getHeight() - 150, 260, 260);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /* ------------------------------------------------------------------
     * Right: sign-in form
     * ------------------------------------------------------------------ */

    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new java.awt.GridBagLayout());
        wrapper.setBackground(UIUtil.SURFACE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(340, 470));

        JLabel title = new JLabel("Welcome back");
        title.setFont(UIUtil.DISPLAY);
        title.setForeground(UIUtil.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to open your reading list");
        subtitle.setFont(UIUtil.BODY_FONT);
        subtitle.setForeground(UIUtil.MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        userBox = inputBox("Username", usernameField);
        passBox = inputBox("Password", passwordField);

        final JCheckBox show = new JCheckBox("Show password");
        show.setFont(UIUtil.SMALL);
        show.setForeground(UIUtil.BODY);
        show.setOpaque(false);
        show.setFocusPainted(false);
        show.setAlignmentX(LEFT_ALIGNMENT);
        show.setIconTextGap(9);
        show.setIcon(new javax.swing.Icon() {
            public int getIconWidth() {
                return 17;
            }

            public int getIconHeight() {
                return 17;
            }

            public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
                Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                boolean on = show.isSelected();
                g2.setColor(on ? UIUtil.PRIMARY : UIUtil.SURFACE_ALT);
                g2.fill(UIUtil.roundRect(x, y, 17, 17, 5));
                g2.setColor(on ? UIUtil.PRIMARY : UIUtil.BORDER);
                g2.draw(UIUtil.roundRect(x + 0.5, y + 0.5, 16, 16, 5));
                if (on) {
                    Icons.bold("check", 13, Color.WHITE).paintIcon(c, g2, x + 2, y + 2);
                }
                g2.dispose();
            }
        });
        show.addActionListener(e ->
                passwordField.setEchoChar(show.isSelected() ? (char) 0 : '•'));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtil.SMALL);
        errorLabel.setForeground(UIUtil.DANGER);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);

        ModernButton signIn = new ModernButton("Sign in", ModernButton.Variant.PRIMARY);
        signIn.size(44);
        signIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        signIn.setAlignmentX(LEFT_ALIGNMENT);
        signIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        RoundPanel hint = new RoundPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        hint.radius(UIUtil.RADIUS_CONTROL).fill(UIUtil.PRIMARY_SOFT).noBorder();
        hint.setAlignmentX(LEFT_ALIGNMENT);
        hint.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel hintLabel = new JLabel("Demo account   admin  ·  admin123");
        hintLabel.setFont(UIUtil.SMALL);
        hintLabel.setForeground(UIUtil.PRIMARY_HOVER);
        hint.add(new JLabel(Icons.of("sparkle", 15, UIUtil.PRIMARY)));
        hint.add(hintLabel);

        KeyAdapter enter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        usernameField.addKeyListener(enter);
        passwordField.addKeyListener(enter);

        form.add(Box.createVerticalGlue());
        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(28));
        form.add(fieldLabel("USERNAME"));
        form.add(userBox);
        form.add(Box.createVerticalStrut(16));
        form.add(fieldLabel("PASSWORD"));
        form.add(passBox);
        form.add(Box.createVerticalStrut(12));
        form.add(show);
        form.add(Box.createVerticalStrut(6));
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(10));
        form.add(signIn);
        form.add(Box.createVerticalStrut(18));
        form.add(hint);
        form.add(Box.createVerticalGlue());

        wrapper.add(form);
        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIUtil.MICRO_BOLD);
        label.setForeground(UIUtil.MUTED);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));
        return label;
    }

    /** Wraps a text field in a rounded box that highlights on focus. */
    private RoundPanel inputBox(String placeholder, final JTextField field) {
        final RoundPanel box = new RoundPanel(new BorderLayout());
        box.radius(UIUtil.RADIUS_CONTROL).fill(UIUtil.SURFACE_ALT);
        box.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        box.setAlignmentX(LEFT_ALIGNMENT);
        box.setPreferredSize(new Dimension(340, 44));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        field.setBorder(null);
        field.setOpaque(false);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setFont(UIUtil.BODY_FONT);
        field.setForeground(UIUtil.INK);
        field.setCaretColor(UIUtil.PRIMARY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                box.fill(UIUtil.SURFACE).borderColor(UIUtil.PRIMARY);
                box.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                box.fill(UIUtil.SURFACE_ALT).borderColor(UIUtil.BORDER);
                box.repaint();
            }
        });

        box.add(field, BorderLayout.CENTER);
        return box;
    }

    /* ------------------------------------------------------------------
     * Actions
     * ------------------------------------------------------------------ */

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Enter your username and password to continue.");
            return;
        }

        User user = DataStore.login(username, password);
        if (user == null) {
            showError("That username and password do not match.");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }

        errorLabel.setText(" ");
        dispose();
        new DashboardFrame().setVisible(true);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        userBox.borderColor(UIUtil.DANGER);
        passBox.borderColor(UIUtil.DANGER);
        userBox.repaint();
        passBox.repaint();
    }

    /** Called by the dashboard when the user logs out. */
    public static void showLogin() {
        DataStore.logout();
        new LoginFrame().setVisible(true);
    }
}
