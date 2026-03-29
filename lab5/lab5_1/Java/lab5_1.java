import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class lab5_1 extends JFrame {

    // ── Minimal Modern Palette ────────────────────────────────────────────────
    private static final Color BG         = new Color(0x10, 0x10, 0x14);
    private static final Color PANEL      = new Color(0x17, 0x18, 0x1f);
    private static final Color PANEL_2    = new Color(0x1d, 0x1f, 0x27);
    private static final Color BORDER     = new Color(0x2a, 0x2d, 0x37);
    private static final Color ACCENT     = new Color(0x7c, 0x8c, 0xff);
    private static final Color ACCENT_HOV = new Color(0x93, 0xa0, 0xff);
    private static final Color TEXT       = new Color(0xf3, 0xf4, 0xf6);
    private static final Color TEXT_DIM   = new Color(0x9a, 0xa0, 0xad);
    private static final Color SUCCESS    = new Color(0x4a, 0xde, 0x80);
    private static final Color WARN       = new Color(0xfa, 0xcc, 0x15);
    private static final Color ERROR      = new Color(0xf8, 0x71, 0x71);
    private static final Color ENTRY_BG   = new Color(0x11, 0x13, 0x19);

    private static final int WIN_W = 420;
    private static final int WIN_H_NORMAL = 430;
    private static final int WIN_H_RESULT = 560;

    private String mode = "A";

    private JLabel inputLabel;
    private JTextField entryField;
    private JLabel errLabel;

    private JPanel resultFrame;
    private JLabel statusLabel;
    private JLabel resultValueLabel;
    private JLabel resultUnitLabel;
    private JLabel detailLabel;
    private JButton resetBtn;

    private SegmentedToggle toggle;

    public lab5_1() {
        setTitle("Temperature Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        buildUI();
        setWindowHeight(WIN_H_NORMAL);
        centerWindow();
        setVisible(true);
    }

    private void buildUI() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(outer);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        outer.add(card, BorderLayout.CENTER);

        JLabel title = new JLabel("Temperature Converter");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(22, 24, 4, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Convert values instantly with a clean minimal interface.");
        subtitle.setForeground(TEXT_DIM);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        subtitle.setBorder(new EmptyBorder(0, 24, 18, 24));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subtitle);

        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(0, 1));
        divider.setBackground(BORDER);
        card.add(wrap(divider, PANEL, 0, 24, 18, 24));

        JLabel conversionLabel = new JLabel("Conversion");
        conversionLabel.setForeground(TEXT_DIM);
        conversionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        conversionLabel.setBorder(new EmptyBorder(0, 24, 0, 24));
        conversionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(conversionLabel);

        toggle = new SegmentedToggle(this::onModeChanged);
        toggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(wrap(toggle, PANEL, 8, 24, 18, 24));

        inputLabel = new JLabel("Temperature (°C)");
        inputLabel.setForeground(TEXT_DIM);
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        inputLabel.setBorder(new EmptyBorder(0, 24, 0, 24));
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(inputLabel);

        JPanel entryShell = new JPanel(new BorderLayout());
        entryShell.setBackground(ENTRY_BG);
        entryShell.setBorder(new LineBorder(BORDER, 1));
        entryShell.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        entryField = new JTextField();
        entryField.setBackground(ENTRY_BG);
        entryField.setForeground(TEXT);
        entryField.setCaretColor(TEXT);
        entryField.setBorder(new EmptyBorder(14, 14, 14, 14));
        entryField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        entryField.addActionListener(e -> convert());

        entryField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                entryShell.setBorder(new LineBorder(ACCENT, 1));
            }

            @Override
            public void focusLost(FocusEvent e) {
                entryShell.setBorder(new LineBorder(BORDER, 1));
            }
        });

        entryShell.add(entryField, BorderLayout.CENTER);
        card.add(wrap(entryShell, PANEL, 8, 24, 8, 24));

        errLabel = new JLabel(" ");
        errLabel.setForeground(ERROR);
        errLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        errLabel.setBorder(new EmptyBorder(0, 24, 12, 24));
        errLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(errLabel);

        JButton convertBtn = createButton("Convert", true, this::convert);
        card.add(wrap(convertBtn, PANEL, 0, 24, 18, 24));

        resultFrame = new JPanel();
        resultFrame.setLayout(new BoxLayout(resultFrame, BoxLayout.Y_AXIS));
        resultFrame.setBackground(PANEL_2);
        resultFrame.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));
        resultFrame.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel resultText = new JLabel("Result");
        resultText.setForeground(TEXT_DIM);
        resultText.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        topRow.add(resultText, BorderLayout.WEST);

        statusLabel = new JLabel("");
        statusLabel.setForeground(WARN);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        topRow.add(statusLabel, BorderLayout.EAST);

        resultFrame.add(topRow);
        resultFrame.add(Box.createVerticalStrut(6));

        JPanel valueRow = new JPanel();
        valueRow.setLayout(new BoxLayout(valueRow, BoxLayout.X_AXIS));
        valueRow.setOpaque(false);

        resultValueLabel = new JLabel("");
        resultValueLabel.setForeground(TEXT);
        resultValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueRow.add(resultValueLabel);

        valueRow.add(Box.createHorizontalStrut(8));

        resultUnitLabel = new JLabel("");
        resultUnitLabel.setForeground(ACCENT);
        resultUnitLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueRow.add(resultUnitLabel);

        resultFrame.add(valueRow);
        resultFrame.add(Box.createVerticalStrut(6));

        detailLabel = new JLabel("");
        detailLabel.setForeground(TEXT_DIM);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        resultFrame.add(detailLabel);

        resultFrame.setVisible(false);
        card.add(wrap(resultFrame, PANEL, 0, 24, 12, 24));

        resetBtn = createButton("Reset", false, this::reset);
        resetBtn.setVisible(false);
        card.add(wrap(resetBtn, PANEL, 0, 24, 22, 24));
    }

    private JPanel wrap(JComponent component, Color bg, int top, int left, int bottom, int right) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(bg);
        wrapper.setBorder(new EmptyBorder(top, left, bottom, right));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createButton(String text, boolean primary, Runnable action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 44));

        if (primary) {
            button.setBackground(ACCENT);
            button.setForeground(Color.WHITE);
            button.setBorder(new LineBorder(ACCENT, 1));
        } else {
            button.setBackground(PANEL_2);
            button.setForeground(TEXT);
            button.setBorder(new LineBorder(BORDER, 1));
        }

        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(primary ? ACCENT_HOV : new Color(0x25, 0x28, 0x33));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(primary ? ACCENT : PANEL_2);
            }
        });

        button.addActionListener(e -> action.run());
        return button;
    }

    private void onModeChanged(String newMode) {
        mode = newMode;

        if (errLabel != null) {
            errLabel.setText(" ");
        }

        hideResult();

        if (inputLabel != null) {
            if ("A".equals(mode)) {
                inputLabel.setText("Temperature (°C)");
            } else {
                inputLabel.setText("Temperature (°F)");
            }
        }
    }

    private void convert() {
        String raw = entryField.getText().trim();

        if (raw.isEmpty()) {
            errLabel.setText("Enter a temperature value.");
            hideResult();
            return;
        }

        double value;
        try {
            value = Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            errLabel.setText("Please enter a valid number.");
            hideResult();
            return;
        }

        errLabel.setText(" ");

        double converted;
        String outUnit;
        String detail;

        if ("A".equals(mode)) {
            converted = (value * 9.0 / 5.0) + 32.0;
            outUnit = "°F";
            detail = String.format("%.2f °C converted to Fahrenheit", value);
        } else {
            converted = (value - 32.0) * 5.0 / 9.0;
            outUnit = "°C";
            detail = String.format("%.2f °F converted to Celsius", value);
        }

        if (converted > 100) {
            statusLabel.setText("Too High");
            statusLabel.setForeground(WARN);
        } else {
            statusLabel.setText("Normal");
            statusLabel.setForeground(SUCCESS);
        }

        resultValueLabel.setText(String.format("%.2f", converted));
        resultUnitLabel.setText(outUnit);
        detailLabel.setText(detail);

        showResult();
    }

    private void showResult() {
        resultFrame.setVisible(true);
        resetBtn.setVisible(true);
        setWindowHeight(WIN_H_RESULT);
        centerWindow();
        revalidate();
        repaint();
    }

    private void hideResult() {
        if (resultFrame != null) {
            resultFrame.setVisible(false);
        }
        if (resetBtn != null) {
            resetBtn.setVisible(false);
        }
        setWindowHeight(WIN_H_NORMAL);
        centerWindow();
        revalidate();
        repaint();
    }

    private void reset() {
        entryField.setText("");
        errLabel.setText(" ");
        resultValueLabel.setText("");
        resultUnitLabel.setText("");
        detailLabel.setText("");
        statusLabel.setText("");
        entryField.requestFocusInWindow();
        hideResult();
    }

    private void setWindowHeight(int height) {
        setSize(WIN_W, height);
    }

    private void centerWindow() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - getWidth()) / 2;
        int y = (screen.height - getHeight()) / 2;
        setLocation(x, y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(lab5_1::new);
    }

    private static class SegmentedToggle extends JPanel {
        private final JLabel btnA;
        private final JLabel btnB;
        private final ToggleCallback callback;
        private String mode = "A";

        SegmentedToggle(ToggleCallback callback) {
            this.callback = callback;

            setLayout(new GridLayout(1, 2));
            setBackground(PANEL);
            setBorder(new LineBorder(BORDER, 1));

            btnA = createSegment("°C → °F");
            btnB = createSegment("°F → °C");

            add(btnA);
            add(btnB);

            setModeSilently("A");

            btnA.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setMode("A");
                }
            });

            btnB.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setMode("B");
                }
            });
        }

        private JLabel createSegment(String text) {
            JLabel label = new JLabel(text, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setBorder(new EmptyBorder(10, 24, 10, 24));
            return label;
        }

        private void applyModeStyles() {
            if ("A".equals(mode)) {
                btnA.setBackground(ACCENT);
                btnA.setForeground(Color.WHITE);
                btnB.setBackground(PANEL);
                btnB.setForeground(TEXT_DIM);
            } else {
                btnA.setBackground(PANEL);
                btnA.setForeground(TEXT_DIM);
                btnB.setBackground(ACCENT);
                btnB.setForeground(Color.WHITE);
            }
        }

        private void setModeSilently(String newMode) {
            mode = newMode;
            applyModeStyles();
        }

        private void setMode(String newMode) {
            mode = newMode;
            applyModeStyles();

            if (callback != null) {
                callback.onToggle(mode);
            }
        }
    }

    @FunctionalInterface
    private interface ToggleCallback {
        void onToggle(String mode);
    }
}