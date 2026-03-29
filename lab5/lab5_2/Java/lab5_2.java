import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;

public class lab5_2 extends JFrame {

    // ── Colours ─────────────────────────────────────────────────────────────
    private static final Color BG       = Color.decode("#0f0f13");
    private static final Color GLASS_BG = Color.decode("#1c1c26");
    private static final Color GLASS_BD = Color.decode("#2e2e42");
    private static final Color ACCENT   = Color.decode("#7c6aff");
    private static final Color ACCENT2  = Color.decode("#a78bfa");
    private static final Color TEXT_PRI = Color.decode("#f0f0ff");
    private static final Color TEXT_SEC = Color.decode("#8888aa");
    private static final Color ENTRY_BG = Color.decode("#14141e");
    private static final Color BTN_ACT  = Color.decode("#6c5ce7");
    private static final Color ERROR    = Color.decode("#f87171");
    private static final Color SUCCESS  = Color.decode("#4ade80");

    // ── State ───────────────────────────────────────────────────────────────
    private String mode = "M"; // M = hour → min, H = min → hour

    private JTextField inputField;
    private JLabel resultLabel;
    private JLabel statusLabel;
    private JLabel inputLabel;
    private JButton btnM;
    private JButton btnH;

    public lab5_2() {
        setTitle("Time Converter");
        setSize(420, 560);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(null);
        setLocationRelativeTo(null);

        buildUI();
        refreshToggle();
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
    }

    private void buildUI() {
        JPanel outer = new JPanel();
        outer.setLayout(null);
        outer.setBackground(BG);
        outer.setBounds(25, 25, 370, 510);
        add(outer);

        JLabel title = new JLabel("Time Converter", SwingConstants.CENTER);
        title.setFont(font("Helvetica Neue", Font.BOLD, 22));
        title.setForeground(TEXT_PRI);
        title.setBackground(BG);
        title.setOpaque(true);
        title.setBounds(0, 0, 370, 30);
        outer.add(title);

        JLabel subtitle = new JLabel("Hours  ↔  Minutes", SwingConstants.CENTER);
        subtitle.setFont(font("Helvetica Neue", Font.PLAIN, 9));
        subtitle.setForeground(TEXT_SEC);
        subtitle.setBackground(BG);
        subtitle.setOpaque(true);
        subtitle.setBounds(0, 34, 370, 16);
        outer.add(subtitle);

        JPanel card = panel(GLASS_BG, GLASS_BD);
        card.setLayout(null);
        card.setBounds(0, 72, 370, 215);
        outer.add(card);

        JPanel tog = new JPanel(new GridLayout(1, 2, 0, 0));
        tog.setBackground(GLASS_BD);
        tog.setBounds(28, 28, 314, 42);
        card.add(tog);

        btnM = createToggleButton("Hour → Min", e -> setMode("M"));
        btnH = createToggleButton("Min → Hour", e -> setMode("H"));
        tog.add(btnM);
        tog.add(btnH);

        inputLabel = new JLabel("Hours");
        inputLabel.setFont(font("Helvetica Neue", Font.PLAIN, 11));
        inputLabel.setForeground(TEXT_SEC);
        inputLabel.setBackground(GLASS_BG);
        inputLabel.setOpaque(true);
        inputLabel.setBounds(28, 82, 314, 18);
        card.add(inputLabel);

        JPanel entryFrame = panel(ENTRY_BG, GLASS_BD);
        entryFrame.setLayout(new BorderLayout());
        entryFrame.setBounds(28, 106, 314, 46);
        card.add(entryFrame);

        inputField = new JTextField();
        inputField.setFont(font("Helvetica Neue", Font.PLAIN, 15));
        inputField.setBackground(ENTRY_BG);
        inputField.setForeground(TEXT_PRI);
        inputField.setCaretColor(ACCENT2);
        inputField.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        ((AbstractDocument) inputField.getDocument()).setDocumentFilter(new NumericFilter());
        inputField.addActionListener(e -> convert());
        entryFrame.add(inputField, BorderLayout.CENTER);

        JButton convertBtn = new JButton("Convert");
        convertBtn.setFont(font("Helvetica Neue", Font.BOLD, 11));
        convertBtn.setBackground(ACCENT);
        convertBtn.setForeground(TEXT_PRI);
        convertBtn.setFocusPainted(false);
        convertBtn.setBorderPainted(false);
        convertBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        convertBtn.setBounds(28, 170, 314, 42);
        convertBtn.addActionListener(e -> convert());
        convertBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                convertBtn.setBackground(BTN_ACT);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                convertBtn.setBackground(ACCENT);
            }
        });
        card.add(convertBtn);

        JPanel resultCard = panel(GLASS_BG, GLASS_BD);
        resultCard.setLayout(null);
        resultCard.setBounds(0, 299, 370, 120);
        outer.add(resultCard);

        JLabel resultText = new JLabel("Result");
        resultText.setFont(font("Helvetica Neue", Font.PLAIN, 11));
        resultText.setForeground(TEXT_SEC);
        resultText.setBackground(GLASS_BG);
        resultText.setOpaque(true);
        resultText.setBounds(28, 20, 314, 18);
        resultCard.add(resultText);

        resultLabel = new JLabel("—");
        resultLabel.setFont(font("Helvetica Neue", Font.BOLD, 28));
        resultLabel.setForeground(TEXT_PRI);
        resultLabel.setBackground(GLASS_BG);
        resultLabel.setOpaque(true);
        resultLabel.setBounds(28, 40, 314, 40);
        resultCard.add(resultLabel);

        statusLabel = new JLabel("");
        statusLabel.setFont(font("Helvetica Neue", Font.PLAIN, 9));
        statusLabel.setForeground(SUCCESS);
        statusLabel.setBackground(GLASS_BG);
        statusLabel.setOpaque(true);
        statusLabel.setBounds(28, 84, 314, 14);
        resultCard.add(statusLabel);
    }

    private JPanel panel(Color bg, Color border) {
        JPanel p = new JPanel();
        p.setBackground(bg);
        p.setBorder(new LineBorder(border, 1));
        return p;
    }

    private JButton createToggleButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        btn.setFont(font("Helvetica Neue", Font.BOLD, 11));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Font font(String name, int style, int size) {
        return new Font(name, style, size);
    }

    private void setMode(String m) {
        mode = m;
        inputField.setText("");
        resultLabel.setText("—");
        statusLabel.setText("");
        inputLabel.setText(m.equals("M") ? "Hours" : "Minutes");
        refreshToggle();
        inputField.requestFocusInWindow();
    }

    private void refreshToggle() {
        JButton[] buttons = {btnM, btnH};
        String[] keys = {"M", "H"};

        for (int i = 0; i < buttons.length; i++) {
            boolean active = mode.equals(keys[i]);
            buttons[i].setBackground(active ? ACCENT : GLASS_BD);
            buttons[i].setForeground(active ? TEXT_PRI : TEXT_SEC);
        }
    }

    private void convert() {
        String raw = inputField.getText().trim();

        if (raw.isEmpty()) {
            flash("Enter a value first", ERROR);
            return;
        }

        double value;
        try {
            value = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            flash("Numbers only, please", ERROR);
            return;
        }

        if (value < 0) {
            flash("Negative time? Really?", ERROR);
            return;
        }

        double hoursEq;

        if (mode.equals("M")) { // hours → minutes
            double minutes = value * 60;
            hoursEq = value;
            resultLabel.setText(formatGeneral(minutes) + " min");
        } else if (mode.equals("H")) { // minutes → hours
            hoursEq = value / 60;
            resultLabel.setText(formatFiveSig(hoursEq) + " hr");
        } else {
            flash("Invalid Option", ERROR);
            resultLabel.setText("—");
            return;
        }

        if (hoursEq > 24) {
            flash("Too long!", ACCENT2);
        } else {
            flash("Less than a day processing.", SUCCESS);
        }

        blink();
    }

    private void blink() {
        resultLabel.setForeground(ACCENT2);
        Timer timer = new Timer(150, e -> resultLabel.setForeground(TEXT_PRI));
        timer.setRepeats(false);
        timer.start();
    }

    private void flash(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private String formatGeneral(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        }
        return String.format("%s", stripTrailingZeros(value));
    }

    private String formatFiveSig(double value) {
        return stripTrailingZeros(Double.parseDouble(String.format("%.5g", value)));
    }

    private String stripTrailingZeros(double value) {
        String s = Double.toString(value);
        if (s.contains("E") || s.contains("e")) return s;
        if (s.indexOf('.') >= 0) {
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return s;
    }

    // Only allow digits and a single decimal point
    static class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            String newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()))
                    .insert(offset, string).toString();
            if (isValid(newText)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = current.substring(0, offset) + (text == null ? "" : text) + current.substring(offset + length);
            if (isValid(newText)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            super.remove(fb, offset, length);
        }

        private boolean isValid(String text) {
            if (text.isEmpty()) return true;
            return text.matches("\\d*\\.?\\d*") && text.chars().filter(ch -> ch == '.').count() <= 1;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new lab5_2().setVisible(true);
        });
    }
}