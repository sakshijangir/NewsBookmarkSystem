import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.LoginFrame;
import ui.UIUtil;

/**
 * Application entry point for the News & Bookmark Management System.
 *
 * This build is the GUI prototype: every screen is complete and all data
 * comes from an in-memory sample store. MySQL is connected in the next phase.
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("apple.awt.application.appearance", "system");

        applyLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }

    /**
     * The interface paints its own components, so the base look and feel
     * only needs to supply neutral defaults for menus, dialogs and
     * text input.
     */
    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to whatever the platform provides
        }

        UIManager.put("Panel.background", UIUtil.BG);
        UIManager.put("OptionPane.background", UIUtil.SURFACE);
        UIManager.put("MenuBar.background", UIUtil.SURFACE);
        UIManager.put("Menu.background", UIUtil.SURFACE);
        UIManager.put("MenuItem.background", UIUtil.SURFACE);
        UIManager.put("Menu.foreground", UIUtil.BODY);
        UIManager.put("MenuItem.foreground", UIUtil.BODY);
        UIManager.put("MenuItem.selectionBackground", UIUtil.PRIMARY_SOFT);
        UIManager.put("MenuItem.selectionForeground", UIUtil.INK);
        UIManager.put("Menu.selectionBackground", UIUtil.PRIMARY_SOFT);
        UIManager.put("Menu.selectionForeground", UIUtil.INK);
        UIManager.put("MenuBar.borderColor", UIUtil.BORDER);
        UIManager.put("PopupMenu.background", UIUtil.SURFACE);
        UIManager.put("ToolTip.background", UIUtil.INK);
        UIManager.put("ToolTip.foreground", java.awt.Color.WHITE);
        UIManager.put("ToolTip.font", UIUtil.MICRO);
        UIManager.put("ComboBox.background", UIUtil.SURFACE_ALT);
        UIManager.put("ComboBox.foreground", UIUtil.INK);
        UIManager.put("ComboBox.selectionBackground", UIUtil.PRIMARY_SOFT);
        UIManager.put("ComboBox.selectionForeground", UIUtil.INK);
        UIManager.put("TextField.caretForeground", UIUtil.PRIMARY);
        UIManager.put("CheckBox.background", UIUtil.SURFACE);
        UIManager.put("CheckBox.focus", new java.awt.Color(0, 0, 0, 0));
    }
}
