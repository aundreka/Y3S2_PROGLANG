import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class lab1_2 extends JFrame {

    // --- UI colors (Google-ish) ---
    private static final Color BG = new Color(0xF6, 0xF7, 0xFB);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(0xD1, 0xD5, 0xDB);
    private static final Color BORDER_FOCUS = new Color(0x25, 0x63, 0xEB);
    private static final Color BORDER_ERROR = new Color(0xEF, 0x44, 0x44);
    private static final Color TEXT_MAIN = new Color(0x11, 0x18, 0x27);
    private static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);

    // --- inputs ---
    private JTextField pair1Field;
    private JTextField pair2Field;
    private JLabel pair1Error;
    private JLabel pair2Error;

    // --- output ---
    private JLabel badgeAvg;
    private JLabel badgeDiff1;
    private JLabel badgeDiff2;
    private JTextArea detailsArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new lab1_2().setVisible(true));
    }

    public lab1_2() {
        super("Difference & Average Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 540);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        // Root wrapper
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 22, 22, 22));
        setContentPane(root);

        // Card
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE7, 0xE9, 0xF1), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        root.add(card, gbc);

        // Header
        JLabel title = new JLabel("Compute Differences & Average");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_MAIN);

        JLabel subtitle = new JLabel("Enter two numbers per line (separated by space). Commas are allowed (e.g., 12,000 6).");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(14));

        // Inputs
        pair1Field = new JTextField();
        pair2Field = new JTextField();
        pair1Error = errorLabel();
        pair2Error = errorLabel();

        JPanel input1 = labeledInput("Input 1st two numbers", pair1Field, pair1Error);
        JPanel input2 = labeledInput("Input last two numbers", pair2Field, pair2Error);

        card.add(input1);
        card.add(Box.createVerticalStrut(10));
        card.add(input2);
        card.add(Box.createVerticalStrut(10));

        JLabel tip = new JLabel("Tip: Use space between numbers (e.g., -3.5 20000).");
        tip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tip.setForeground(TEXT_MUTED);
        card.add(tip);
        card.add(Box.createVerticalStrut(12));

        // Buttons row
        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBtns.setOpaque(false);

        JButton computeBtn = primaryButton("Compute");
        JButton clearBtn = secondaryButton("Clear");
        JButton sampleBtn = tertiaryButton("Fill Sample");

        leftBtns.add(computeBtn);
        leftBtns.add(clearBtn);

        btnRow.add(leftBtns, BorderLayout.WEST);
        btnRow.add(sampleBtn, BorderLayout.EAST);

        card.add(btnRow);
        card.add(Box.createVerticalStrut(14));

        // Output section
        JLabel resultsLbl = new JLabel("Results");
        resultsLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultsLbl.setForeground(TEXT_MAIN);
        card.add(resultsLbl);
        card.add(Box.createVerticalStrut(8));

        JPanel outCard = new JPanel();
        outCard.setBackground(new Color(0xF9, 0xFA, 0xFB));
        outCard.setLayout(new BoxLayout(outCard, BoxLayout.Y_AXIS));
        outCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5, 0xE7, 0xEB), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badgeRow.setOpaque(false);

        badgeAvg = badge("AVG: —", new Color(0xEE, 0xF2, 0xFF), new Color(0x37, 0x30, 0xA3));
        badgeDiff1 = badge("Diff #1: —", new Color(0xEC, 0xFE, 0xFF), new Color(0x15, 0x5E, 0x75));
        badgeDiff2 = badge("Diff #2: —", new Color(0xF0, 0xFD, 0xF4), new Color(0x16, 0x65, 0x34));

        badgeRow.add(badgeAvg);
        badgeRow.add(badgeDiff1);
        badgeRow.add(badgeDiff2);

        outCard.add(badgeRow);
        outCard.add(Box.createVerticalStrut(10));
        outCard.add(new JSeparator());
        outCard.add(Box.createVerticalStrut(10));

        detailsArea = new JTextArea(7, 1);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsArea.setForeground(TEXT_MAIN);
        detailsArea.setBackground(new Color(0xF9, 0xFA, 0xFB));
        detailsArea.setBorder(null);
        detailsArea.setText("Enter values then click Compute.");

        // No scrollbars requested -> add directly (it can grow, but layout stays fixed)
        outCard.add(detailsArea);

        card.add(outCard);

        // Live validation
        installLiveValidation(pair1Field, pair1Error);
        installLiveValidation(pair2Field, pair2Error);

        // On focus lost, auto-format if valid
        pair1Field.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { formatFieldIfValid(pair1Field, pair1Error); }
            @Override public void focusGained(FocusEvent e) { if (!hasError(pair1Error)) setFieldBorder(pair1Field, BORDER_FOCUS); }
        });
        pair2Field.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { formatFieldIfValid(pair2Field, pair2Error); }
            @Override public void focusGained(FocusEvent e) { if (!hasError(pair2Error)) setFieldBorder(pair2Field, BORDER_FOCUS); }
        });

        // Button actions
        computeBtn.addActionListener(_e -> compute());
        clearBtn.addActionListener(_e -> clear());
        sampleBtn.addActionListener(_e -> fillSample());

        // Initial validate (show required)
        validateField(pair1Field, pair1Error);
        validateField(pair2Field, pair2Error);
        setFieldBorder(pair1Field, BORDER);
        setFieldBorder(pair2Field, BORDER);
    }

    // ----------------- UI helpers -----------------
    private JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(BORDER_ERROR);
        return l;
    }

    private JPanel labeledInput(String label, JTextField field, JLabel err) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(0x37, 0x41, 0x51));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT_MAIN);
        field.setCaretColor(TEXT_MAIN);

        // "Inner padding": use margin (works in Swing)
        field.setMargin(new Insets(10, 12, 10, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(field);
        p.add(Box.createVerticalStrut(4));
        p.add(err);
        return p;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0x25, 0x63, 0xEB));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setForeground(TEXT_MAIN);
        b.setBackground(Color.WHITE);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
        return b;
    }

    private JButton tertiaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setForeground(new Color(0x25, 0x63, 0xEB));
        b.setBackground(CARD);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(10, 10, 10, 10));
        return b;
    }

    private JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setBorder(new EmptyBorder(8, 12, 8, 12));
        return l;
    }

    private void setFieldBorder(JTextField field, Color borderColor) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
    }

    private boolean hasError(JLabel errLabel) {
        return errLabel.getText() != null && !errLabel.getText().trim().isEmpty() && !errLabel.getText().trim().equals(" ");
    }

    // ----------------- live validation -----------------
    private void installLiveValidation(JTextField field, JLabel errLabel) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validateField(field, errLabel); }
            @Override public void removeUpdate(DocumentEvent e) { validateField(field, errLabel); }
            @Override public void changedUpdate(DocumentEvent e) { validateField(field, errLabel); }
        });
    }

    private void validateField(JTextField field, JLabel errLabel) {
        String t = field.getText().trim();

        Validation v = validateTwoNumbersLive(t);

        if (v.ok) {
            errLabel.setText(" ");
            // if focused -> blue border; else gray border
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == field) {
                setFieldBorder(field, BORDER_FOCUS);
            } else {
                setFieldBorder(field, BORDER);
            }
        } else {
            errLabel.setText(v.message);
            setFieldBorder(field, BORDER_ERROR);
        }
    }

    private void formatFieldIfValid(JTextField field, JLabel errLabel) {
        Validation v = validateTwoNumbersLive(field.getText().trim());
        if (v.ok) {
            try {
                double[] ab = parseTwoNumbersStrict(field.getText());
                field.setText(formatWithCommas(ab[0]) + " " + formatWithCommas(ab[1]));
            } catch (Exception ignored) {
                // if formatting fails, leave as-is
            }
        }
        // after formatting, reset border (not focused)
        validateField(field, errLabel);
    }

    // ----------------- compute -----------------
    private void compute() {
        // validate both first
        Validation v1 = validateTwoNumbersLive(pair1Field.getText().trim());
        Validation v2 = validateTwoNumbersLive(pair2Field.getText().trim());

        validateField(pair1Field, pair1Error);
        validateField(pair2Field, pair2Error);

        if (!v1.ok) {
            JOptionPane.showMessageDialog(this, "Fix Input 1st two numbers first.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            pair1Field.requestFocusInWindow();
            return;
        }
        if (!v2.ok) {
            JOptionPane.showMessageDialog(this, "Fix Input last two numbers first.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            pair2Field.requestFocusInWindow();
            return;
        }

        double a, b, c, d;
        try {
            double[] ab = parseTwoNumbersStrict(pair1Field.getText());
            double[] cd = parseTwoNumbersStrict(pair2Field.getText());
            a = ab[0]; b = ab[1]; c = cd[0]; d = cd[1];
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not parse numbers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double diff1 = a - b;
        double diff2 = c - d;
        double avg = (a + b + c + d) / 4.0;

        // update badges
        badgeAvg.setText("AVG: " + formatWithCommas(avg));
        badgeDiff1.setText("Diff #1: " + formatWithCommas(diff1));
        badgeDiff2.setText("Diff #2: " + formatWithCommas(diff2));

        // pretty details
        String details =
                "Inputs\n" +
                "  • First pair:  " + formatWithCommas(a) + "  and  " + formatWithCommas(b) + "\n" +
                "  • Second pair: " + formatWithCommas(c) + "  and  " + formatWithCommas(d) + "\n\n" +
                "Results\n" +
                "  • Difference of 1st 2 numbers: " + formatWithCommas(diff1) + "\n" +
                "  • Difference of 2nd 2 numbers: " + formatWithCommas(diff2) + "\n" +
                "  • Average of four numbers:     " + formatWithCommas(avg);

        detailsArea.setText(details);

        // auto-format fields
        formatFieldIfValid(pair1Field, pair1Error);
        formatFieldIfValid(pair2Field, pair2Error);
    }

    private void clear() {
        pair1Field.setText("");
        pair2Field.setText("");
        pair1Error.setText(" ");
        pair2Error.setText(" ");
        setFieldBorder(pair1Field, BORDER);
        setFieldBorder(pair2Field, BORDER);

        badgeAvg.setText("AVG: —");
        badgeDiff1.setText("Diff #1: —");
        badgeDiff2.setText("Diff #2: —");
        detailsArea.setText("Enter values then click Compute.");

        pair1Field.requestFocusInWindow();
        validateField(pair1Field, pair1Error);
        validateField(pair2Field, pair2Error);
    }

    private void fillSample() {
        pair1Field.setText("12 6");
        pair2Field.setText("7 9");
        compute();
    }

    // ----------------- parsing + formatting -----------------
    private static double[] parseTwoNumbersStrict(String text) {
        String[] parts = text.trim().split("\\s+");
        if (parts.length != 2) throw new IllegalArgumentException("Enter exactly 2 numbers separated by space.");
        double a = Double.parseDouble(parts[0].replace(",", ""));
        double b = Double.parseDouble(parts[1].replace(",", ""));
        return new double[]{a, b};
    }

    private static String formatWithCommas(double n) {
        // If integer-ish, format as integer with commas
        if (Math.abs(n - Math.rint(n)) < 1e-12) {
            long v = Math.round(n);
            return String.format("%,d", v);
        }
        // Otherwise, format with commas + trimmed decimals
        DecimalFormat df = new DecimalFormat("#,##0.##########");
        return df.format(n);
    }

    // ----------------- validation logic -----------------
    private static class Validation {
        boolean ok;
        String message;
        Validation(boolean ok, String message) { this.ok = ok; this.message = message; }
    }

    private static boolean isPartialNumber(String s) {
        // allow partial typing: "", "-", ".", "-."
        if (s.isEmpty()) return true;
        if (s.equals("-") || s.equals(".") || s.equals("-.")) return true;
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Validation validateTwoNumbersLive(String text) {
        String t = text.trim();
        if (t.isEmpty()) return new Validation(false, "Required. Enter 2 numbers (e.g., 12 6).");

        String[] parts = t.split("\\s+");
        if (parts.length == 1) {
            String token = parts[0].replace(",", "");
            if (!isPartialNumber(token)) return new Validation(false, "First value must be a number.");
            return new Validation(false, "Add the second number (e.g., 12 6).");
        }
        if (parts.length > 2) {
            return new Validation(false, "Too many values. Use exactly 2 numbers.");
        }

        String aS = parts[0].replace(",", "");
        String bS = parts[1].replace(",", "");

        if (!isPartialNumber(aS)) return new Validation(false, "First value must be a valid number.");
        if (!isPartialNumber(bS)) return new Validation(false, "Second value must be a valid number.");

        // must be fully parseable (not just "-", ".", "-.")
        try { Double.parseDouble(aS); } catch (Exception e) { return new Validation(false, "Finish typing the first number."); }
        try { Double.parseDouble(bS); } catch (Exception e) { return new Validation(false, "Finish typing the second number."); }

        return new Validation(true, "");
    }
}