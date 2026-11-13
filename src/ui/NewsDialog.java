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
import model.News;

/**
 * Form used to add a new article or edit an existing one.
 * Pass null as the news argument to open the dialog in "add" mode.
 */
public class NewsDialog extends JDialog {

    private final FormField titleField = FormField.text("Headline", true);
    private final FormField sourceField = FormField.text("Source", true);
    private final FormField categoryField =
            FormField.select("Category", DataStore.getCategories());
    private final FormField dateField = FormField.text("Published on", false);
    private final FormField urlField = FormField.text("Link", false);
    private final FormField summaryField = FormField.area("Summary", 104);

    private final News existing;
    private News news;
    private boolean saved;

    public NewsDialog(Frame parent, News existing) {
        super(parent, existing == null ? "Add article" : "Edit article", true);
        this.existing = existing;

        setSize(520, 660);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtil.SURFACE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        if (existing != null) {
            titleField.setValue(existing.getTitle());
            sourceField.setValue(existing.getSource());
            categoryField.setValue(existing.getCategory());
            dateField.setValue(existing.getDate());
            urlField.setValue(existing.getUrl());
            summaryField.setValue(existing.getSummary());
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
                g2.setColor(UIUtil.PRIMARY_SOFT);
                g2.fill(UIUtil.roundRect(0, 0, 42, 42, 12));
                Icons.of("news", 20, UIUtil.PRIMARY).paintIcon(this, g2, 11, 11);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(42, 42));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(existing == null ? "Add article" : "Edit article");
        title.setFont(UIUtil.H1);
        title.setForeground(UIUtil.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Fields marked with * are required.");
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
        form.add(sourceField);

        JPanel row = new JPanel(new java.awt.GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        row.add(categoryField);
        row.add(dateField);
        form.add(row);

        form.add(urlField);
        form.add(summaryField);
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
                existing == null ? "Add article" : "Save changes",
                ModernButton.Variant.PRIMARY);
        save.addActionListener(e -> handleSave());

        footer.add(cancel);
        footer.add(save);
        return footer;
    }

    /** Validates the required fields before accepting the values. */
    private void handleSave() {
        titleField.markInvalid(false);
        sourceField.markInvalid(false);

        if (titleField.getValue().isEmpty()) {
            titleField.markInvalid(true);
            titleField.focus();
            return;
        }
        if (sourceField.getValue().isEmpty()) {
            sourceField.markInvalid(true);
            sourceField.focus();
            return;
        }

        String date = dateField.getValue();
        if (date.isEmpty()) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }

        news = new News(existing == null ? 0 : existing.getId(),
                titleField.getValue(), sourceField.getValue(), categoryField.getValue(),
                date, urlField.getValue(), summaryField.getValue());

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public News getNews() {
        return news;
    }
}
