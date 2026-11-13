package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import data.DataStore;
import model.User;

/**
 * Main application window: a dark navigation rail on the left and the
 * active screen on the right, swapped with a CardLayout.
 */
public class DashboardFrame extends JFrame {

    public static final String CARD_HOME = "home";
    public static final String CARD_NEWS = "news";
    public static final String CARD_BOOKMARKS = "bookmarks";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final List<NavItem> navItems = new ArrayList<NavItem>();

    private HomePanel homePanel;
    private NewsPanel newsPanel;
    private BookmarkPanel bookmarkPanel;

    private Toast toast;

    public DashboardFrame() {
        setTitle("News & Bookmark Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtil.BG);

        setJMenuBar(buildMenuBar());

        homePanel = new HomePanel(this);
        newsPanel = new NewsPanel(this);
        bookmarkPanel = new BookmarkPanel(this);

        content.setBackground(UIUtil.BG);
        content.add(homePanel, CARD_HOME);
        content.add(newsPanel, CARD_NEWS);
        content.add(bookmarkPanel, CARD_BOOKMARKS);

        add(buildSidebar(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        toast = new Toast();
        getLayeredPane().add(toast, JLayeredPane.POPUP_LAYER);

        showCard(CARD_HOME);
    }

    /* ------------------------------------------------------------------
     * Navigation rail
     * ------------------------------------------------------------------ */

    private JPanel buildSidebar() {
        JPanel rail = new JPanel();
        rail.setLayout(new BoxLayout(rail, BoxLayout.Y_AXIS));
        rail.setBackground(UIUtil.NAV);
        rail.setPreferredSize(new Dimension(238, 0));
        rail.setBorder(BorderFactory.createEmptyBorder(22, 16, 18, 16));

        rail.add(logo());
        rail.add(Box.createVerticalStrut(26));
        rail.add(sectionLabel("MENU"));
        rail.add(Box.createVerticalStrut(8));
        rail.add(navItem("Dashboard", "home", CARD_HOME));
        rail.add(Box.createVerticalStrut(4));
        rail.add(navItem("News Feed", "news", CARD_NEWS));
        rail.add(Box.createVerticalStrut(4));
        rail.add(navItem("Bookmarks", "bookmark", CARD_BOOKMARKS));
        rail.add(Box.createVerticalGlue());
        rail.add(userBlock());

        return rail;
    }

    private JPanel logo() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 11, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JComponent tile = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                g2.setColor(UIUtil.ACCENT);
                g2.fill(UIUtil.roundRect(0, 0, 34, 34, 10));
                Icons.bold("bookmark-filled", 18, Color.WHITE).paintIcon(this, g2, 8, 8);
                g2.dispose();
            }
        };
        tile.setPreferredSize(new Dimension(34, 34));

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("News & Bookmarks");
        name.setFont(UIUtil.H3);
        name.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Reading manager");
        sub.setFont(UIUtil.MICRO);
        sub.setForeground(UIUtil.NAV_TEXT);
        names.add(name);
        names.add(sub);

        row.add(tile);
        row.add(names);
        return row;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIUtil.MICRO_BOLD);
        label.setForeground(new Color(0x5F6B87));
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        return label;
    }

    private NavItem navItem(String text, String icon, String card) {
        NavItem item = new NavItem(text, icon, card);
        navItems.add(item);
        return item;
    }

    /** One row in the navigation rail. */
    private class NavItem extends JComponent {

        private final String label;
        private final String iconName;
        private final String card;
        private boolean hover;

        NavItem(String label, String iconName, String card) {
            this.label = label;
            this.iconName = iconName;
            this.card = card;
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(206, 42));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(UIUtil.SMALL_BOLD);
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
                public void mousePressed(MouseEvent e) {
                    showCard(NavItem.this.card);
                }
            });
        }

        boolean isActive() {
            return card.equals(currentCard);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            boolean active = isActive();

            if (active) {
                g2.setColor(UIUtil.PRIMARY);
                g2.fill(UIUtil.roundRect(0, 0, getWidth(), getHeight(), 11));
            } else if (hover) {
                g2.setColor(UIUtil.NAV_ALT);
                g2.fill(UIUtil.roundRect(0, 0, getWidth(), getHeight(), 11));
            }

            Color ink = active ? Color.WHITE : hover ? new Color(0xE3E8F4) : UIUtil.NAV_TEXT;
            Icons.of(iconName, 18, ink).paintIcon(this, g2, 13, (getHeight() - 18) / 2);

            g2.setFont(getFont());
            g2.setColor(ink);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, 43, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /** Signed-in user block at the bottom of the rail. */
    private JPanel userBlock() {
        User user = DataStore.getCurrentUser();
        final String name = user == null ? "Guest" : user.getFullName();
        final String initials = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();

        JPanel block = new JPanel(new BorderLayout(10, 0));
        block.setOpaque(false);
        block.setAlignmentX(LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        block.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x242E48)),
                BorderFactory.createEmptyBorder(12, 6, 0, 0)));

        JComponent avatar = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                g2.setColor(UIUtil.PRIMARY);
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(Color.WHITE);
                g2.setFont(UIUtil.SMALL_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, (32 - fm.stringWidth(initials)) / 2,
                        (32 - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(32, 32));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel who = new JLabel(name);
        who.setFont(UIUtil.SMALL_BOLD);
        who.setForeground(Color.WHITE);
        JLabel role = new JLabel(user == null ? "" : "Signed in");
        role.setFont(UIUtil.MICRO);
        role.setForeground(UIUtil.NAV_TEXT);
        text.add(who);
        text.add(role);

        ModernButton logout = ModernButton.iconOnly(
                Icons.of("logout", 17, UIUtil.NAV_TEXT), ModernButton.Variant.GHOST, "Logout");
        logout.addActionListener(e -> handleLogout());

        block.add(avatar, BorderLayout.WEST);
        block.add(text, BorderLayout.CENTER);
        block.add(logout, BorderLayout.EAST);
        return block;
    }

    /* ------------------------------------------------------------------
     * Menu bar
     * ------------------------------------------------------------------ */

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(UIUtil.SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtil.BORDER));

        JMenu file = menu("File", 'F');
        file.add(item("Dashboard", "control D", e -> showCard(CARD_HOME)));
        file.addSeparator();
        file.add(item("Logout", null, e -> handleLogout()));
        file.add(item("Exit", "control Q", e -> System.exit(0)));

        JMenu manage = menu("Manage", 'M');
        manage.add(item("News Feed", "control N", e -> showCard(CARD_NEWS)));
        manage.add(item("Bookmarks", "control B", e -> showCard(CARD_BOOKMARKS)));

        JMenu help = menu("Help", 'H');
        help.add(item("About", null, e -> Dialogs.about(this)));

        bar.add(file);
        bar.add(manage);
        bar.add(help);
        return bar;
    }

    private JMenu menu(String text, char mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        menu.setFont(UIUtil.SMALL);
        return menu;
    }

    private JMenuItem item(String text, String accelerator, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(UIUtil.SMALL);
        if (accelerator != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        }
        menuItem.addActionListener(action);
        return menuItem;
    }

    /* ------------------------------------------------------------------
     * Navigation and feedback
     * ------------------------------------------------------------------ */

    private String currentCard = CARD_HOME;

    public void showCard(String card) {
        currentCard = card;
        cardLayout.show(content, card);

        for (NavItem item : navItems) {
            item.repaint();
        }
        if (CARD_HOME.equals(card)) {
            homePanel.refresh();
        } else if (CARD_NEWS.equals(card)) {
            newsPanel.refresh();
        } else if (CARD_BOOKMARKS.equals(card)) {
            bookmarkPanel.refresh();
        }
    }

    /** Shows a short confirmation message in the bottom right corner. */
    public void showToast(String message) {
        toast.show(message, UIUtil.PRIMARY);
    }

    public void showSuccess(String message) {
        toast.show(message, UIUtil.SUCCESS);
    }

    private void handleLogout() {
        if (Dialogs.confirm(this, "Log out?",
                "You will be returned to the sign-in screen.", "Log out")) {
            dispose();
            LoginFrame.showLogin();
        }
    }

    /** Small floating message shown after an action succeeds. */
    private class Toast extends JComponent {

        private String message = "";
        private Color accent = UIUtil.INK;
        private final Timer timer;

        Toast() {
            setVisible(false);
            timer = new Timer(2400, e -> {
                setVisible(false);
                ((Timer) e.getSource()).stop();
            });
            timer.setRepeats(false);
        }

        void show(String message, Color accent) {
            this.message = message;
            this.accent = accent;

            FontMetrics fm = getFontMetrics(UIUtil.SMALL_BOLD);
            int width = fm.stringWidth(message) + 62;
            int height = 46;
            java.awt.Rectangle bounds = getRootPane().getBounds();
            setBounds(bounds.width - width - 28, bounds.height - height - 24, width, height);

            setVisible(true);
            repaint();
            timer.restart();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
            UIUtil.shadow(g2, 4, 4, getWidth() - 8, getHeight() - 8, 12, 5, 40);
            g2.setColor(new Color(0x16, 0x1C, 0x2E, 250));
            g2.fill(UIUtil.roundRect(4, 4, getWidth() - 8.0, getHeight() - 8.0, 12));

            Icons.bold("check", 16, accent == UIUtil.INK ? UIUtil.PRIMARY : accent)
                    .paintIcon(this, g2, 18, getHeight() / 2 - 8);

            g2.setFont(UIUtil.SMALL_BOLD);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(message, 42, getHeight() / 2 + fm.getAscent() / 2 - 2);
            g2.dispose();
        }
    }
}
