import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.*;

public class lab1_1 extends JFrame {

    // Colors (Google-ish)
    private static final Color BG = new Color(0xF5F7FB);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(0x202124);
    private static final Color MUTED = new Color(0x5F6368);
    private static final Color BORDER = new Color(0xDADCE0);
    private static final Color PRIMARY = new Color(0x1A73E8);
    private static final Color DANGER = new Color(0xD93025);
    private static final Color OK = new Color(0x188038);

    private final JTextField in1 = new JTextField();
    private final JTextField in2 = new JTextField();

    private final JLabel msg1 = new JLabel(" ");
    private final JLabel msg2 = new JLabel(" ");

    private final JLabel out1 = new JLabel("—");
    private final JLabel out2 = new JLabel("—");
    private final JLabel out3 = new JLabel("—");
    private final JLabel status = new JLabel(" ");

    private final Border normalBorder = new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 12, 10, 12)
    );

    private final Border errorBorder = new CompoundBorder(
            new LineBorder(DANGER, 1, true),
            new EmptyBorder(10, 12, 10, 12)
    );

    private boolean formattingInProgress = false;

    public lab1_1() {
        super("Product & Average Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Use a modern-looking font if available
        setUIFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(16, 16, 16, 16)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Compute Product & Average");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        card.add(title, c);

        c.gridy++;
        JLabel subtitle = new JLabel("Enter two numbers per line (space-separated).");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.insets = new Insets(6, 0, 14, 0);
        card.add(subtitle, c);
        c.insets = new Insets(0, 0, 0, 0);

        // Input 1
        c.gridy++;
        card.add(fieldLabel("1st two numbers"), c);

        c.gridy++;
        setupTextField(in1, "e.g., 12 6");
        card.add(in1, c);

        c.gridy++;
        setupMsgLabel(msg1);
        card.add(msg1, c);

        // Input 2
        c.gridy++;
        c.insets = new Insets(8, 0, 0, 0);
        card.add(fieldLabel("Last two numbers"), c);
        c.insets = new Insets(0, 0, 0, 0);

        c.gridy++;
        setupTextField(in2, "e.g., 7 9");
        card.add(in2, c);

        c.gridy++;
        setupMsgLabel(msg2);
        card.add(msg2, c);

        
        // Buttons
        c.gridy++;
        c.insets = new Insets(6, 0, 10, 0);
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setBackground(CARD_BG);

        JButton computeBtn = makeButton("Compute", true);
        JButton clearBtn = makeButton("Clear", false);

        btnRow.add(computeBtn);
        btnRow.add(clearBtn);
        card.add(btnRow, c);
        c.insets = new Insets(0, 0, 0, 0);

        // Results box
        c.gridy++;
        JPanel results = new JPanel(new GridBagLayout());
        results.setBackground(CARD_BG);
        results.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(10, 12, 10, 12)));

        GridBagConstraints r = new GridBagConstraints();
        r.gridx = 0; r.gridy = 0; r.weightx = 1; r.fill = GridBagConstraints.HORIZONTAL;

        results.add(resultRow("The product of 1st 2 numbers is:", out1), r);
        r.gridy++;
        r.insets = new Insets(6, 0, 0, 0);
        results.add(resultRow("The product of 2nd 2 numbers is:", out2), r);
        r.gridy++;
        results.add(resultRow("The average of four numbers is:", out3), r);

        card.add(results, c);

        // Status
        c.gridy++;
        c.insets = new Insets(10, 0, 0, 0);
        status.setForeground(MUTED);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(status, c);

        // Root pack
        GridBagConstraints rc = new GridBagConstraints();
        rc.insets = new Insets(20, 20, 20, 20);
        root.add(card, rc);

        setContentPane(root);

        // Wire actions
        computeBtn.addActionListener(e -> compute());
        clearBtn.addActionListener(e -> clear());

        // Enter triggers compute from either field
        Action computeAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { compute(); }
        };
        in1.addActionListener(computeAction);
        in2.addActionListener(computeAction);

        // Live validation + auto-format
        wireLiveValidation(in1, msg1);
        wireLiveValidation(in2, msg2);

        pack();
        setLocationRelativeTo(null);
        in1.requestFocusInWindow();
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        return l;
    }

    private void setupMsgLabel(JLabel l) {
        l.setForeground(DANGER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void setupTextField(JTextField tf, String placeholder) {
        tf.setBorder(normalBorder);
        tf.setForeground(TEXT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(Color.WHITE);
        tf.setToolTipText(placeholder);
    }

    private JButton makeButton(String text, boolean primary) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new CompoundBorder(
                new LineBorder(primary ? PRIMARY : BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        b.setOpaque(true);
        b.setBackground(primary ? PRIMARY : new Color(0xEEF3FD));
        b.setForeground(primary ? Color.WHITE : PRIMARY);

        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (primary) b.setBackground(new Color(0x1666D3));
                else b.setBackground(new Color(0xE3ECFF));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(primary ? PRIMARY : new Color(0xEEF3FD));
            }
        });
        return b;
    }

    private JPanel resultRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(CARD_BG);

        JLabel l = new JLabel(label);
        l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        value.setForeground(TEXT);
        value.setFont(new Font("Segoe UI", Font.BOLD, 13));
        value.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(l, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    // ---------------- Validation + Formatting ----------------

    private static class ValidationResult {
        boolean ok;
        String reason;
        ValidationResult(boolean ok, String reason) { this.ok = ok; this.reason = reason; }
    }

    private ValidationResult validateLine(String text) {
        if (text == null) return new ValidationResult(false, "Enter two integers separated by a space.");
        String t = text.trim();
        if (t.isEmpty()) return new ValidationResult(false, "Enter two integers separated by a space.");

        String[] parts = t.split("\\s+");
        if (parts.length < 2) return new ValidationResult(false, "Need 2 numbers (example: 12 6).");
        if (parts.length > 2) return new ValidationResult(false, "Only 2 numbers are allowed on this line.");

        String a = parts[0].replace(",", "");
        String b = parts[1].replace(",", "");

        if (!isIntToken(a) || !isIntToken(b)) return new ValidationResult(false, "Integers only (no letters/decimals).");
        return new ValidationResult(true, "");
    }

    private static boolean isIntToken(String s) {
        if (s == null || s.isEmpty()) return false;
        if (s.startsWith("-")) return s.length() > 1 && s.substring(1).matches("\\d+");
        return s.matches("\\d+");
    }

    private static long parseLongToken(String tok) {
        String raw = tok.replace(",", "");
        return Long.parseLong(raw);
    }

    private static String formatWithCommas(String tok) {
        String raw = tok.replace(",", "");
        if (!isIntToken(raw)) return tok;
        long n = Long.parseLong(raw);
        return NumberFormat.getInstance(Locale.US).format(n);
    }

    private void wireLiveValidation(JTextField field, JLabel reasonLabel) {
        Document doc = field.getDocument();

        doc.addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onChange(); }
            @Override public void removeUpdate(DocumentEvent e) { onChange(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange(); }

            private void onChange() {
                if (formattingInProgress) return;

                String text = field.getText();
                if (text == null) text = "";

                // Auto-format first two tokens with commas.
                // Keep it simple and stable: cursor goes to end after format.
                String formatted = autoFormatLine(text);
                if (!formatted.equals(text)) {
                    formattingInProgress = true;
                    field.setText(formatted);
                    field.setCaretPosition(field.getText().length());
                    formattingInProgress = false;
                }

                // Validate
                String t = field.getText().trim();
                if (t.isEmpty()) {
                    field.setBorder(normalBorder);
                    reasonLabel.setText(" ");
                    return;
                }

                ValidationResult vr = validateLine(field.getText());
                if (vr.ok) {
                    field.setBorder(normalBorder);
                    reasonLabel.setText(" ");
                } else {
                    field.setBorder(errorBorder);
                    reasonLabel.setText(vr.reason);
                }
            }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                // On blur, re-run validation and formatting once.
                if (formattingInProgress) return;
                String text = field.getText();
                String formatted = autoFormatLine(text);
                if (!formatted.equals(text)) {
                    formattingInProgress = true;
                    field.setText(formatted);
                    formattingInProgress = false;
                }
                String t = field.getText().trim();
                if (t.isEmpty()) {
                    field.setBorder(normalBorder);
                    reasonLabel.setText(" ");
                    return;
                }
                ValidationResult vr = validateLine(field.getText());
                field.setBorder(vr.ok ? normalBorder : errorBorder);
                reasonLabel.setText(vr.ok ? " " : vr.reason);
            }
        });
    }

    private String autoFormatLine(String text) {
        String t = text.trim();
        if (t.isEmpty()) return text;

        String[] parts = t.split("\\s+");
        // Format first two tokens; keep extras as-is (validation will catch extras)
        if (parts.length >= 1) parts[0] = formatWithCommas(parts[0]);
        if (parts.length >= 2) parts[1] = formatWithCommas(parts[1]);

        String joined = String.join(" ", parts);
        return joined;
    }

    // ---------------- Actions ----------------

    private void compute() {
        status.setForeground(MUTED);
        status.setText(" ");

        String s1 = in1.getText().trim();
        String s2 = in2.getText().trim();

        ValidationResult v1 = validateLine(s1);
        ValidationResult v2 = validateLine(s2);

        if (s1.isEmpty()) v1 = new ValidationResult(false, "First input is required.");
        if (s2.isEmpty()) v2 = new ValidationResult(false, "Second input is required.");

        msg1.setText(v1.ok ? " " : v1.reason);
        msg2.setText(v2.ok ? " " : v2.reason);
        in1.setBorder(v1.ok ? normalBorder : errorBorder);
        in2.setBorder(v2.ok ? normalBorder : errorBorder);

        if (!v1.ok || !v2.ok) {
            JOptionPane.showMessageDialog(this, (!v1.ok ? v1.reason : v2.reason),
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            status.setForeground(DANGER);
            status.setText("Fix the highlighted input(s).");
            return;
        }

        try {
            String[] p1 = s1.split("\\s+");
            String[] p2 = s2.split("\\s+");

            long a = parseLongToken(p1[0]);
            long b = parseLongToken(p1[1]);
            long c = parseLongToken(p2[0]);
            long d = parseLongToken(p2[1]);

            long prod1 = a * b;
            long prod2 = c * d;

            double avg = (a + b + c + d) / 4.0;

            out1.setForeground(OK);
            out2.setForeground(OK);
            out3.setForeground(OK);

            out1.setText(NumberFormat.getInstance(Locale.US).format(prod1));
            out2.setText(NumberFormat.getInstance(Locale.US).format(prod2));

            // Average: if whole number, show as integer with commas; else show decimal.
            if (Math.abs(avg - Math.rint(avg)) < 1e-12) {
                long avgInt = (long) Math.rint(avg);
                out3.setText(NumberFormat.getInstance(Locale.US).format(avgInt));
            } else {
                out3.setText(String.valueOf(avg));
            }

            status.setForeground(OK);
            status.setText("Computed successfully ✔");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numbers are too large or invalid.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            status.setForeground(DANGER);
            status.setText("Invalid number format.");
        }
    }

    private void clear() {
        in1.setText("");
        in2.setText("");

        in1.setBorder(normalBorder);
        in2.setBorder(normalBorder);

        msg1.setText(" ");
        msg2.setText(" ");

        out1.setForeground(TEXT);
        out2.setForeground(TEXT);
        out3.setForeground(TEXT);

        out1.setText("—");
        out2.setText("—");
        out3.setText("—");

        status.setForeground(MUTED);
        status.setText(" ");

        in1.requestFocusInWindow();
    }

    // ---------------- Utility ----------------

    private static void setUIFont(Font f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(f));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Native feel
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new lab1_1().setVisible(true);
        });
    }
}