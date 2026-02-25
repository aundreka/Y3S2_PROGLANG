// File: lab2_2.java
// CSCN09C Grade Collector (Swing • Google-Forms-ish UI)
//
// Features:
// - Modern “Google Forms” vibe (clean header, cards, spacing)
// - Collects Instructor First/Last Name + 10 students (Last Name + Grade)
// - Input validation:
//     * Names: required; letters/spaces/hyphen/apostrophe only
//     * Grades: integer 0..100 only
// - No visible scrollbars
// - Mousewheel scrolling works
// - Pretty summary output (Instructor last name + lowest grade)

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class lab2_2 extends JFrame {

    private static final int STUDENTS = 10;

    private final JTextField instructorFirst = new JTextField();
    private final JTextField instructorLast  = new JTextField();

    private final List<JTextField> studentLast = new ArrayList<>();
    private final List<JTextField> studentGrade = new ArrayList<>();

    private final JLabel errorBanner = new JLabel(" ");
    private final JScrollPane scrollPane;
    private final JPanel formColumn = new JPanel();

    // Theme colors (Google-ish)
    private static final Color BG = new Color(0xF6F8FC);
    private static final Color CARD = Color.WHITE;
    private static final Color BLUE = new Color(0x1A73E8);
    private static final Color TEXT = new Color(0x202124);
    private static final Color MUTED = new Color(0x5F6368);
    private static final Color ERROR_BG = new Color(0xFCE8E6);
    private static final Color ERROR_FG = new Color(0xC5221F);

    public lab2_2() {
        super("CSCN09C Grade Entry Form");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 720);
        setMinimumSize(new Dimension(860, 650));
        setLocationRelativeTo(null);

        // Root
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);

        // Header card
        root.add(buildHeader(), BorderLayout.NORTH);

        // Scrollable content (no scrollbars, but mousewheel works)
        JPanel scrollContent = new JPanel();
        scrollContent.setBackground(BG);
        scrollContent.setLayout(new BorderLayout());
        scrollContent.setBorder(new EmptyBorder(0, 18, 18, 18));

        // Form column
        formColumn.setBackground(BG);
        formColumn.setLayout(new BoxLayout(formColumn, BoxLayout.Y_AXIS));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        wrapper.add(formColumn, gbc);

        // Add bottom glue so column stays top-aligned
        gbc.gridy = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        wrapper.add(Box.createVerticalGlue(), gbc);

        scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG);

        // Hide scrollbars, keep scrolling enabled
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Make mousewheel scroll feel nice even without visible bar
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        scrollContent.add(scrollPane, BorderLayout.CENTER);
        root.add(scrollContent, BorderLayout.CENTER);

        // Build cards/sections
        formColumn.add(buildErrorBanner());
        formColumn.add(Box.createVerticalStrut(10));

        formColumn.add(buildInstructorCard());
        formColumn.add(buildStudentsCard());
        formColumn.add(buildActionsCard());

        // Global font smoothing + UI polish
        applyUIHints();

        setVisible(true);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(CARD);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new CompoundBorder(
                new EmptyBorder(18, 18, 10, 18),
                new MatteBorder(0, 0, 0, 0, CARD)
        ));

        JLabel title = new JLabel("CSCN09C");
        title.setForeground(BLUE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel sub = new JLabel("Grade Entry Form • 10 students required");
        sub.setForeground(MUTED);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(18, 18, 10, 18));
        outer.add(header, BorderLayout.CENTER);

        return outer;
    }

    private JComponent buildErrorBanner() {
        errorBanner.setOpaque(true);
        errorBanner.setBackground(BG);
        errorBanner.setForeground(ERROR_FG);
        errorBanner.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorBanner.setBorder(new EmptyBorder(10, 14, 10, 14));
        errorBanner.setVisible(false);
        return errorBanner;
    }

    private JPanel buildInstructorCard() {
        JPanel card = card("Instructor Information",
                "Required fields • Use letters, spaces, - or '");
        JPanel content = cardContent(card);

        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 0));
        grid.setBackground(CARD);

        JPanel left = fieldBlock("Instructor First Name", instructorFirst, "e.g., Maricris");
        JPanel right = fieldBlock("Instructor Last Name", instructorLast, "e.g., Mojica");

        grid.add(left);
        grid.add(right);

        content.add(grid);
        return card;
    }

    private JPanel buildStudentsCard() {
        JPanel card = card("Student Grades",
                "Enter exactly 10 students • Grades must be 0–100");
        JPanel content = cardContent(card);

        for (int i = 0; i < STUDENTS; i++) {
            JPanel section = new JPanel();
            section.setBackground(CARD);
            section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
            section.setBorder(new EmptyBorder(i == 0 ? 14 : 10, 0, 0, 0));

            JLabel entryLabel = new JLabel("Entry " + (i + 1));
            entryLabel.setForeground(MUTED);
            entryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            section.add(entryLabel);
            section.add(Box.createVerticalStrut(6));

            JTextField last = new JTextField();
            JTextField grade = new JTextField();

            studentLast.add(last);
            studentGrade.add(grade);

            JPanel row = new JPanel(new GridLayout(1, 2, 18, 0));
            row.setBackground(CARD);

            row.add(fieldBlock("Student Last Name", last, ""));
            row.add(fieldBlock("CSCN09C Grade", grade, "0-100"));

            // live validation color hint
            addLiveValidation(last, FieldKind.NAME);
            addLiveValidation(grade, FieldKind.GRADE);

            section.add(row);
            content.add(section);
        }

        return card;
    }

    private JPanel buildActionsCard() {
        JPanel card = card("Submit", "Review your entries before submitting.");
        JPanel content = cardContent(card);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        row.setBackground(CARD);

        JButton clear = new JButton("Clear All");
        JButton submit = new JButton("Submit");

        styleGhostButton(clear);
        stylePrimaryButton(submit);

        clear.addActionListener(e -> clearAll());
        submit.addActionListener(e -> onSubmit());

        row.add(clear);
        row.add(submit);

        JLabel tip = new JLabel("Tip: Mousewheel scrolling is enabled even without scrollbars.");
        tip.setForeground(MUTED);
        tip.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        content.add(row);
        content.add(Box.createVerticalStrut(14));
        content.add(tip);

        return card;
    }

    // ---------- UI helpers ----------

    private JPanel card(String title, String subtitle) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CARD);
        outer.setBorder(new CompoundBorder(
                new EmptyBorder(10, 0, 0, 0),
                new CompoundBorder(
                        new LineBorder(new Color(0xE0E3EB), 1, true),
                        new EmptyBorder(16, 18, 16, 18)
                )
        ));

        JPanel top = new JPanel();
        top.setBackground(CARD);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setForeground(TEXT);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel s = new JLabel(subtitle);
        s.setForeground(MUTED);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        top.add(t);
        top.add(Box.createVerticalStrut(4));
        top.add(s);

        outer.add(top, BorderLayout.NORTH);

        // Space between cards
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(new EmptyBorder(0, 0, 10, 0));
        wrap.add(outer, BorderLayout.CENTER);

        // Attach content panel in CENTER later
        JPanel content = new JPanel();
        content.setBackground(CARD);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 0, 0, 0));

        outer.add(content, BorderLayout.CENTER);

        // return the wrapper but store content on client property
        wrap.putClientProperty("content", content);
        return wrap;
    }

    private JPanel cardContent(JPanel cardWrapper) {
        Object c = cardWrapper.getClientProperty("content");
        return (JPanel) c;
    }

    private JPanel fieldBlock(String label, JTextField field, String placeholder) {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setForeground(new Color(0x3C4043));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xDADCE0), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        if (placeholder != null && !placeholder.isBlank()) {
            installPlaceholder(field, placeholder);
        }

        p.add(l);
        p.add(Box.createVerticalStrut(6));
        p.add(field);

        return p;
    }

    private void installPlaceholder(JTextField field, String placeholder) {
        Color phColor = new Color(0x9AA0A6);
        Color normalColor = TEXT;

        field.setText(placeholder);
        field.setForeground(phColor);

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(normalColor);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(phColor);
                }
            }
        });
    }

    private void stylePrimaryButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(BLUE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleGhostButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(BLUE);
        b.setBackground(CARD);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xDADCE0), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showError(String msg) {
        errorBanner.setText("⚠ " + msg);
        errorBanner.setBackground(ERROR_BG);
        errorBanner.setVisible(true);

        // Scroll to top so user sees it
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));
    }

    private void hideError() {
        errorBanner.setVisible(false);
        errorBanner.setText(" ");
        errorBanner.setBackground(BG);
    }

    private void clearAll() {
        hideError();

        instructorFirst.setText("");
        instructorLast.setText("");

        for (int i = 0; i < STUDENTS; i++) {
            studentLast.get(i).setText("");
            studentGrade.get(i).setText("");
        }

        // Scroll to top
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));
    }

    // ---------- Validation ----------

    private enum FieldKind { NAME, GRADE }

    private void addLiveValidation(JTextField field, FieldKind kind) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                String s = field.getText().trim();
                if (s.isEmpty()) {
                    field.setForeground(TEXT);
                    return;
                }
                boolean ok = (kind == FieldKind.NAME) ? validName(s) : validGrade(s);
                field.setForeground(ok ? TEXT : ERROR_FG);
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private boolean validName(String s) {
        s = s.trim();
        if (s.isEmpty()) return false;
        // letters/spaces/'/-
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(Character.isLetter(c) || c == ' ' || c == '\'' || c == '-')) return false;
        }
        boolean hasLetter = false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i))) { hasLetter = true; break; }
        }
        return hasLetter;
    }

    private boolean validGrade(String s) {
        s = s.trim();
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        try {
            int v = Integer.parseInt(s);
            return v >= 0 && v <= 100;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private String normalizePlaceholder(JTextField field, String placeholder) {
        String s = field.getText().trim();
        if (s.equals(placeholder)) return "";
        return s;
    }

    private void onSubmit() {
        hideError();

        // Handle placeholders for instructor fields
        // (If placeholder is present, normalize to empty)
        String instrFirst = instructorFirst.getText().trim();
        String instrLast  = instructorLast.getText().trim();

        // If user didn't type and placeholder remains (starts with "e.g.")
        if (instrFirst.toLowerCase().startsWith("e.g")) instrFirst = "";
        if (instrLast.toLowerCase().startsWith("e.g")) instrLast = "";

        if (!validName(instrFirst)) {
            showError("Instructor First Name is required (letters, spaces, - or ').");
            return;
        }
        if (!validName(instrLast)) {
            showError("Instructor Last Name is required (letters, spaces, - or ').");
            return;
        }

        Integer lowest = null;
        String lowestStudent = null;

        for (int i = 0; i < STUDENTS; i++) {
            String last = studentLast.get(i).getText().trim();
            String gradeS = studentGrade.get(i).getText().trim();

            if (!validName(last)) {
                showError("Entry " + (i + 1) + ": Student Last Name is invalid or missing.");
                return;
            }
            if (!validGrade(gradeS)) {
                showError("Entry " + (i + 1) + ": Grade must be a whole number from 0 to 100.");
                return;
            }

            int g = Integer.parseInt(gradeS);
            if (lowest == null || g < lowest) {
                lowest = g;
                lowestStudent = last;
            }
        }

        showSummary(instrFirst, instrLast, lowest, lowestStudent);
    }

    private void showSummary(String instrFirst, String instrLast, int lowest, String lowestStudent) {
        JDialog dialog = new JDialog(this, "Submission Summary", true);
        dialog.setSize(720, 540);
        dialog.setMinimumSize(new Dimension(640, 480));
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        dialog.setContentPane(root);

        // Header
        JPanel header = new JPanel();
        header.setBackground(CARD);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Submitted \u2714");
        title.setForeground(BLUE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel sub = new JLabel("Here’s a clean summary of your entries.");
        sub.setForeground(MUTED);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        root.add(header, BorderLayout.NORTH);

        // Summary card
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new EmptyBorder(12, 0, 0, 0),
                new CompoundBorder(
                        new LineBorder(new Color(0xE0E3EB), 1, true),
                        new EmptyBorder(16, 18, 16, 18)
                )
        ));

        JLabel instLabel = new JLabel("Instructor");
        instLabel.setForeground(TEXT);
        instLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel instName = new JLabel(instrFirst + " " + instrLast);
        instName.setForeground(TEXT);
        instName.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JSeparator sep1 = new JSeparator();

        JLabel resultLabel = new JLabel("Result");
        resultLabel.setForeground(TEXT);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel resultBlock = new JPanel();
        resultBlock.setBackground(CARD);
        resultBlock.setLayout(new BoxLayout(resultBlock, BoxLayout.Y_AXIS));
        resultBlock.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xDADCE0), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel l1 = new JLabel("Instructor Last Name: " + instrLast);
        l1.setForeground(TEXT);

        JLabel l2 = new JLabel("Lowest Grade: " + lowest);
        l2.setForeground(TEXT);

        JLabel l3 = new JLabel("(Lowest belongs to: " + lowestStudent + ")");
        l3.setForeground(MUTED);
        l3.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        resultBlock.add(l1);
        resultBlock.add(Box.createVerticalStrut(6));
        resultBlock.add(l2);
        resultBlock.add(Box.createVerticalStrut(2));
        resultBlock.add(l3);

        JSeparator sep2 = new JSeparator();

        JTextArea pretty = new JTextArea();
        pretty.setEditable(false);
        pretty.setLineWrap(true);
        pretty.setWrapStyleWord(true);
        pretty.setFont(new Font("Consolas", Font.PLAIN, 12));
        pretty.setForeground(TEXT);
        pretty.setBackground(CARD);
        pretty.setBorder(null);
        pretty.setText(
                "Instructor Name: " + instrFirst + " " + instrLast + "\n" +
                "The lowest grade of your students is " + lowest + ".\n\n" +
                "(Collected 10 student entries successfully.)"
        );

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setBackground(CARD);

        JButton close = new JButton("Close");
        stylePrimaryButton(close);
        close.addActionListener(e -> dialog.dispose());

        btnRow.add(close);

        card.add(instLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(instName);
        card.add(Box.createVerticalStrut(14));
        card.add(sep1);
        card.add(Box.createVerticalStrut(14));
        card.add(resultLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(resultBlock);
        card.add(Box.createVerticalStrut(14));
        card.add(sep2);
        card.add(Box.createVerticalStrut(14));
        card.add(pretty);
        card.add(Box.createVerticalStrut(16));
        card.add(btnRow);

        root.add(card, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private void applyUIHints() {
        // Improves text rendering in many environments
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(lab2_2::new);
    }
}