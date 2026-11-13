package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.DataStore;
import model.Bookmark;

/**
 * Form used to add a new bookmark or edit an existing one.
 * Pass null as the bookmark argument to open the dialog in "add" mode.
 */
public class BookmarkDialog extends JDialog {

    private final FormField titleField = FormField.text("Title", true);
    private final FormField urlField = FormField.text("Link", true);
    private final FormField categoryField =
            FormField.select("Category", DataStore.getCategories());
    private final FormField dateField = FormField.text("Saved on", false);
    private final FormField notesField = FormField.area("Notes", 96);
    private final JLabel error = new JLabel(" ");

    private final Bookmark existing;
    private Bookmark bookmark;
    private boolean saved;

    public BookmarkDialog(Frame parent, Bookmark existing) {
        super(parent, existing == null ? "Add bookmark" : "Edit bookmark", true);
        this.existing = existing;

        setSize(520, 610);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtil.SURFACE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        if (existing != null) {
            titleField.setValue(existing.getTitle());
            urlField.setValue(existing.getUrl());
            categoryField.setValue(existing.getCategory());
            dateField.setValue(existing.getDateAdded());
            notesField.setValue(existing.getNotes());
        } else {
            dateField.setValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(UIUtil.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtil.BORDER),
                BorderFactory.createEmptyBorder(20, 24, 18, 24)));

        JComponent badge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtil.smooth((Graphics2D) g.create());
                g2.setColor(UIUtil.ACCENT_SOFT);
                g2.fill(UIUtil.roundRect(0, 0, 42, 42, 12));
                Icons.of("bookmark-filled", 20, UIUtil.ACCENT).paintIcon(this, g2, 11, 11);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(42, 42));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(existing == null ? "Add bookmark" : "Edit bookmark");
        title.setFont(UIUtil.H1);
        title.setForeground(UIUtil.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Save any link and find it again by category.");
        subtitle.setFont(UIUtil.SMALL);
        subtitle.setForeground(UIUtil.MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        text.add(Box.createVerticalGlue());
        text.add(title);
        text.add(Box.createVerticalStrut(2));
        text.add(subtitle);
        text.add(Box.createVerticalGlue());

        header.add(badge, BorderLayout.WEST);
        header.add(text, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setBackground(UIUtil.SURFACE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 4, 24));

        form.add(titleField);
        form.add(urlField);

        JPanel row = new JPanel(new java.awt.GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        row.add(categoryField);
        row.add(dateField);
        form.add(row);

        form.add(notesField);

        error.setFont(UIUtil.SMALL);
        error.setForeground(UIUtil.DANGER);
        error.setAlignmentX(LEFT_ALIGNMENT);
        form.add(error);
        return form;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setBackground(UIUtil.SURFACE_ALT);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtil.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        ModernButton cancel = new ModernButton("Cancel", ModernButton.Variant.SECONDARY);
        cancel.addActionListener(e -> {
            saved = false;
            dispose();
        });

        ModernButton save = new ModernButton(
                existing == null ? "Add bookmark" : "Save changes",
                ModernButton.Variant.PRIMARY);
        save.addActionListener(e -> handleSave());

        footer.add(cancel);
        footer.add(save);
        return footer;
    }

    /** Checks the required fields and the link format. */
    private void handleSave() {
        titleField.markInvalid(false);
        urlField.markInvalid(false);
        error.setText(" ");

        if (titleField.getValue().isEmpty()) {
            titleField.markInvalid(true);
            error.setText("Give the bookmark a title.");
            titleField.focus();
            return;
        }

        String url = urlField.getValue();
        if (url.isEmpty()) {
            urlField.markInvalid(true);
            error.setText("A bookmark needs a link.");
            urlField.focus();
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            urlField.markInvalid(true);
            error.setText("The link should start with http:// or https://");
            urlField.focus();
            return;
        }

        String date = dateField.getValue();
        if (date.isEmpty()) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }

        bookmark = new Bookmark(existing == null ? 0 : existing.getId(),
                titleField.getValue(), url, categoryField.getValue(),
                date, notesField.getValue());

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }
}
