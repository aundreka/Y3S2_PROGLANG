package lab2.lab2_1.Java;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class lab2_1 extends JFrame {

    // ---- Theme ----
    private final Color BG = new Color(0xF5F7FB);
    private final Color CARD = Color.WHITE;
    private final Color TEXT = new Color(0x0F172A);
    private final Color MUTED = new Color(0x64748B);
    private final Color ACCENT = new Color(0x2563EB);
    private final Color ERR = new Color(0xEF4444);
    private final Color BORDER = new Color(0xE2E8F0);
    private final Color ROW_A = Color.WHITE;
    private final Color ROW_B = new Color(0xF8FAFC);

    // ---- Parent fields ----
    private JTextField parentFirst;
    private JTextField parentLast;
    private JLabel parentFirstErr;
    private JLabel parentLastErr;

    // ---- Children rows ----
    private final List<ChildRow> childRows = new ArrayList<>();
    private JPanel childrenTablePanel;
    private JScrollPane childrenScroll;

    // ---- Output ----
    private JTextArea output;
    private JLabel statusPill;

    private static final int MAX_CHILDREN = 50;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new lab2_1().setVisible(true));
    }

    public lab2_1() {
        super("Personal Information System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(860, 580));
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        setUIFont(new Font("Segoe UI", Font.PLAIN, 13));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);

        for (int i = 0; i < 3; i++) addChildRow();

        validateAll();
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG);
        top.setBorder(new EmptyBorder(16, 18, 10, 18));

        JLabel title = new JLabel("Personal Information System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Modern Swing UI • live validation • mouse-wheel scroll (no visible scrollbars)");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(MUTED);

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setBackground(BG);
        stack.add(title);
        stack.add(Box.createVerticalStrut(6));
        stack.add(sub);

        top.add(stack, BorderLayout.WEST);
        return top;
    }

    private JComponent buildMain() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.62);
        split.setBorder(new EmptyBorder(0, 18, 16, 18));
        split.setDividerSize(10);
        split.setBackground(BG);

        JPanel formStack = new JPanel();
        formStack.setBackground(BG);
        formStack.setLayout(new BoxLayout(formStack, BoxLayout.Y_AXIS));
        formStack.add(buildParentCard());
        formStack.add(Box.createVerticalStrut(14));
        formStack.add(buildChildrenCard());
        formStack.add(Box.createVerticalGlue());

        JScrollPane formScroll = new JScrollPane(formStack);
        makeScrollInvisibleButWheelWorks(formScroll);

        JPanel outputCard = buildOutputCard();

        split.setLeftComponent(formScroll);
        split.setRightComponent(outputCard);

        return split;
    }

    private JPanel buildParentCard() {
        JPanel card = card("Parent Information");
        card.setLayout(new BorderLayout());

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(CARD);
        grid.setBorder(new EmptyBorder(6, 18, 16, 18));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 0, 0, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JLabel fnLabel = labelMuted("First Name");
        JLabel lnLabel = labelMuted("Last Name");

        parentFirst = textField();
        parentLast = textField();

        parentFirstErr = errorLabel();
        parentLastErr = errorLabel();

        c.gridx = 0; c.gridy = 0;
        grid.add(fnLabel, c);
        c.gridx = 1; c.gridy = 0; c.insets = new Insets(4, 0, 0, 0);
        grid.add(lnLabel, c);

        c.insets = new Insets(6, 0, 0, 12);
        c.gridx = 0; c.gridy = 1;
        grid.add(parentFirst, c);
        c.gridx = 1; c.gridy = 1; c.insets = new Insets(6, 0, 0, 0);
        grid.add(parentLast, c);

        c.insets = new Insets(4, 0, 0, 12);
        c.gridx = 0; c.gridy = 2;
        grid.add(parentFirstErr, c);
        c.gridx = 1; c.gridy = 2; c.insets = new Insets(4, 0, 0, 0);
        grid.add(parentLastErr, c);

        addLiveValidation(parentFirst);
        addLiveValidation(parentLast);

        parentFirst.addActionListener(e -> parentLast.requestFocusInWindow());
        parentLast.addActionListener(e -> {
            if (!childRows.isEmpty()) childRows.get(0).name.requestFocusInWindow();
            else addChildRow();
        });

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildChildrenCard() {
        JPanel card = card("Children Information");
        card.setLayout(new BorderLayout());

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD);
        bar.setBorder(new EmptyBorder(0, 18, 10, 18));

        JLabel tip = new JLabel("Tip: Enter moves forward • Arrow keys move between rows");
        tip.setForeground(MUTED);
        tip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bar.add(tip, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(CARD);

        JButton addBtn = ghostButton("+ Add Child");
        addBtn.addActionListener(e -> addChildRow());

        JButton clearBtn = ghostButton("Clear");
        clearBtn.addActionListener(e -> clearChildren());

        btns.add(addBtn);
        btns.add(clearBtn);
        bar.add(btns, BorderLayout.EAST);

        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(CARD);
        header.setBorder(new EmptyBorder(0, 18, 6, 18));

        GridBagConstraints hc = new GridBagConstraints();
        hc.fill = GridBagConstraints.HORIZONTAL;
        hc.insets = new Insets(0, 0, 0, 12);

        JLabel col1 = labelMutedBold("#");
        JLabel col2 = labelMutedBold("Child First Name");
        JLabel col3 = labelMutedBold("Age");

        hc.gridx = 0; hc.weightx = 0; header.add(col1, hc);
        hc.gridx = 1; hc.weightx = 1; header.add(col2, hc);
        hc.gridx = 2; hc.weightx = 0; hc.insets = new Insets(0, 10, 0, 0); header.add(col3, hc);

        childrenTablePanel = new JPanel();
        childrenTablePanel.setBackground(CARD);
        childrenTablePanel.setLayout(new BoxLayout(childrenTablePanel, BoxLayout.Y_AXIS));
        childrenTablePanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        childrenScroll = new JScrollPane(childrenTablePanel);
        makeScrollInvisibleButWheelWorks(childrenScroll);
        childrenScroll.setBorder(new EmptyBorder(0, 18, 12, 18));
        childrenScroll.getViewport().setBackground(CARD);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        footer.setBackground(CARD);
        footer.setBorder(new EmptyBorder(0, 18, 16, 18));

        JButton computeBtn = primaryButton("Compute Eldest");
        computeBtn.addActionListener(e -> compute());

        JButton resetBtn = ghostButton("Reset All");
        resetBtn.addActionListener(e -> resetAll());

        footer.add(computeBtn);
        footer.add(resetBtn);

        // Shortcuts
        InputMap im = card.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = card.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK), "addChild");
        am.put("addChild", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { addChildRow(); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "computeAlways");
        am.put("computeAlways", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { compute(); }
        });

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(CARD);
        center.add(header, BorderLayout.NORTH);
        center.add(childrenScroll, BorderLayout.CENTER);

        card.add(bar, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildOutputCard() {
        JPanel card = card("Output");
        card.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new EmptyBorder(0, 18, 10, 18));

        JLabel title = new JLabel("Output");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));

        statusPill = new JLabel("Waiting");
        statusPill.setOpaque(true);
        statusPill.setBackground(new Color(0xE2E8F0));
        statusPill.setForeground(MUTED);
        statusPill.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusPill.setFont(new Font("Segoe UI", Font.BOLD, 11));

        header.add(title, BorderLayout.WEST);
        header.add(statusPill, BorderLayout.EAST);

        output = new JTextArea();
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font("Consolas", Font.PLAIN, 12));
        output.setForeground(TEXT);
        output.setBackground(CARD);
        output.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JScrollPane outScroll = new JScrollPane(output);
        makeScrollInvisibleButWheelWorks(outScroll);
        outScroll.setBorder(new EmptyBorder(0, 18, 16, 18));
        outScroll.getViewport().setBackground(CARD);

        output.setText(""); // no instructions

        card.add(header, BorderLayout.NORTH);
        card.add(outScroll, BorderLayout.CENTER);

        setStatus("Waiting", null);
        return card;
    }

    // ---------------- CHILD ROW ----------------

    private void addChildRow() {
        if (childRows.size() >= MAX_CHILDREN) {
            JOptionPane.showMessageDialog(this, "Maximum children allowed is " + MAX_CHILDREN + ".", "Limit reached", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int index = childRows.size();
        ChildRow row = new ChildRow(index);
        childRows.add(row);
        childrenTablePanel.add(row.container);

        restripeAndReindex();
        validateAll();

        SwingUtilities.invokeLater(() -> row.name.requestFocusInWindow());
    }

    private void removeChildRow(int index) {
        if (index < 0 || index >= childRows.size()) return;

        ChildRow row = childRows.remove(index);
        childrenTablePanel.remove(row.container);

        restripeAndReindex();
        validateAll();
    }

    private void clearChildren() {
        childRows.clear();
        childrenTablePanel.removeAll();
        restripeAndReindex();
        validateAll();
    }

    private void resetAll() {
        parentFirst.setText("");
        parentLast.setText("");
        clearChildren();
        for (int i = 0; i < 3; i++) addChildRow();
        output.setText("");
        setStatus("Waiting", null);
        parentFirst.requestFocusInWindow();
    }

    private void restripeAndReindex() {
        for (int i = 0; i < childRows.size(); i++) {
            ChildRow r = childRows.get(i);
            r.index = i;
            r.idxLabel.setText(String.valueOf(i + 1));
            Color bg = (i % 2 == 0) ? ROW_A : ROW_B;
            r.setStripe(bg);
            r.removeBtn.setActionCommand(String.valueOf(i));
        }

        childrenTablePanel.revalidate();
        childrenTablePanel.repaint();
    }

    // ---------------- VALIDATION ----------------

    private void addLiveValidation(JTextField field) {
        field.getDocument().addDocumentListener(SimpleDocumentListener.onChange(this::validateAll));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { validateAll(); }
        });
    }

    private boolean validateAll() {
        boolean ok = true;

        String pf = parentFirst.getText().trim();
        String pl = parentLast.getText().trim();

        setFieldError(parentFirst, parentFirstErr, pf.isEmpty() ? "Required" : null);
        setFieldError(parentLast, parentLastErr, pl.isEmpty() ? "Required" : null);

        if (pf.isEmpty() || pl.isEmpty()) ok = false;

        boolean anyChild = false;

        for (ChildRow r : childRows) {
            String name = r.name.getText().trim();
            String ageRaw = r.age.getText().trim();

            if (name.isEmpty() && ageRaw.isEmpty()) {
                setFieldError(r.name, r.nameErr, null);
                setFieldError(r.age, r.ageErr, null);
                continue;
            }

            anyChild = true;

            setFieldError(r.name, r.nameErr, name.isEmpty() ? "Name required" : null);
            if (name.isEmpty()) ok = false;

            String ageMsg = null;
            if (ageRaw.isEmpty()) {
                ageMsg = "Age required";
            } else {
                try {
                    int age = Integer.parseInt(ageRaw);
                    if (age < 1 || age > 130) ageMsg = "1–130 only";
                } catch (NumberFormatException ex) {
                    ageMsg = "Whole number";
                }
            }

            setFieldError(r.age, r.ageErr, ageMsg);
            if (ageMsg != null) ok = false;
        }

        if (!anyChild) ok = false;

        if (ok) setStatus("Ready", true);
        else setStatus("Needs Fix", false);

        return ok;
    }

    private void setFieldError(JTextField field, JLabel errLabel, String msg) {
        if (msg != null) {
            field.setBorder(errorBorder());
            errLabel.setText(msg);
        } else {
            field.setBorder(normalBorder());
            errLabel.setText(" ");
        }
    }

    // ---------------- COMPUTE + OUTPUT ----------------

    private void compute() {
        if (!validateAll()) {
            JOptionPane.showMessageDialog(this, "Please fix the highlighted fields before computing.", "Fix inputs", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String pf = parentFirst.getText().trim();
        String pl = parentLast.getText().trim();

        List<String> childNames = new ArrayList<>();
        List<Integer> childAges = new ArrayList<>();

        for (ChildRow r : childRows) {
            String name = r.name.getText().trim();
            String ageRaw = r.age.getText().trim();
            if (name.isEmpty() && ageRaw.isEmpty()) continue;

            childNames.add(name);
            childAges.add(Integer.parseInt(ageRaw));
        }

        if (childNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one child (name + age).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int eldestIdx = 0;
        for (int i = 1; i < childAges.size(); i++) {
            if (childAges.get(i) > childAges.get(eldestIdx)) eldestIdx = i;
        }

        String eldestName = childNames.get(eldestIdx);
        int eldestAge = childAges.get(eldestIdx);

        StringBuilder sb = new StringBuilder();
        sb.append("Summary\n\n");
        sb.append("Parent\n");
        sb.append("First Name: ").append(pf).append("\n");
        sb.append("Last Name:  ").append(pl).append("\n\n");

        sb.append("Children\n");
        for (int i = 0; i < childNames.size(); i++) {
            sb.append(i + 1).append(". ").append(childNames.get(i))
              .append("  (Age ").append(childAges.get(i)).append(")\n");
        }

        sb.append("\n--------------------------------------\n\n");
        sb.append("Hi ").append(pf).append("! Eldest child: ").append(eldestAge)
          .append(" (").append(eldestName).append(")\n");

        output.setText(sb.toString());
        output.setCaretPosition(0);
        setStatus("Computed", true);
    }

    private void setStatus(String text, Boolean ok) {
        statusPill.setText(text);
        if (ok == null) {
            statusPill.setBackground(new Color(0xE2E8F0));
            statusPill.setForeground(MUTED);
        } else if (ok) {
            statusPill.setBackground(new Color(0xDCFCE7));
            statusPill.setForeground(new Color(0x166534));
        } else {
            statusPill.setBackground(new Color(0xFEE2E2));
            statusPill.setForeground(new Color(0x991B1B));
        }
    }

    // ---------------- Components ----------------

    private JPanel card(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 0, 0, 0)
        ));

        JLabel t = new JLabel(title);
        t.setForeground(TEXT);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setBorder(new EmptyBorder(0, 18, 10, 18));
        card.setLayout(new BorderLayout());
        card.add(t, BorderLayout.NORTH);
        return card;
    }

    private JTextField textField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT);
        tf.setBackground(CARD);
        tf.setCaretColor(TEXT);
        tf.setBorder(normalBorder());
        tf.setMargin(new Insets(8, 10, 8, 10));
        return tf;
    }

    private JLabel labelMuted(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JLabel labelMutedBold(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setForeground(ERR);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return l;
    }

    private Border normalBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER, 2, true),
                new EmptyBorder(6, 8, 6, 8)
        );
    }

    private Border errorBorder() {
        return new CompoundBorder(
                new LineBorder(ERR, 2, true),
                new EmptyBorder(6, 8, 6, 8)
        );
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(ACCENT);
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(hover(b, ACCENT.darker(), ACCENT));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(TEXT);
        b.setBackground(new Color(0xF1F5F9));
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(hover(b, new Color(0xE2E8F0), new Color(0xF1F5F9)));
        return b;
    }

    private MouseAdapter hover(JButton b, Color hover, Color normal) {
        return new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(normal); }
        };
    }

    private void makeScrollInvisibleButWheelWorks(JScrollPane sp) {
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);

        sp.addMouseWheelListener(e -> {
            JScrollBar bar = sp.getVerticalScrollBar();
            int amount = e.getUnitsToScroll() * bar.getUnitIncrement();
            bar.setValue(bar.getValue() + amount);
        });
    }

    private void setUIFont(Font f) {
        for (var k : UIManager.getDefaults().keySet()) {
            Object v = UIManager.get(k);
            if (v instanceof Font) UIManager.put(k, f);
        }
    }

    // ---------------- ChildRow ----------------

    private class ChildRow {
        int index;

        JPanel container;
        JLabel idxLabel;

        JTextField name;
        JTextField age;

        JLabel nameErr;
        JLabel ageErr;

        JButton removeBtn;

        ChildRow(int idx) {
            this.index = idx;

            container = new JPanel(new GridBagLayout());
            setStripe((idx % 2 == 0) ? ROW_A : ROW_B);
            container.setBorder(new EmptyBorder(6, 8, 6, 8));

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(0, 0, 0, 12);

            idxLabel = new JLabel(String.valueOf(idx + 1));
            idxLabel.setForeground(MUTED);
            idxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            name = textField();
            age = textField();

            nameErr = new JLabel(" ");
            nameErr.setForeground(ERR);
            nameErr.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            ageErr = new JLabel(" ");
            ageErr.setForeground(ERR);
            ageErr.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            removeBtn = ghostButton("Remove");
            removeBtn.setActionCommand(String.valueOf(idx));
            removeBtn.addActionListener(e -> removeChildRow(Integer.parseInt(removeBtn.getActionCommand())));

            c.gridx = 0; c.gridy = 0; c.weightx = 0;
            container.add(idxLabel, c);

            c.gridx = 1; c.gridy = 0; c.weightx = 1;
            container.add(name, c);

            c.gridx = 2; c.gridy = 0; c.weightx = 0; c.insets = new Insets(0, 10, 0, 12);
            age.setPreferredSize(new Dimension(110, age.getPreferredSize().height));
            container.add(age, c);

            c.gridx = 3; c.gridy = 0; c.weightx = 0; c.insets = new Insets(0, 0, 0, 0);
            container.add(removeBtn, c);

            c.insets = new Insets(4, 0, 0, 12);
            c.gridx = 1; c.gridy = 1; c.weightx = 1;
            container.add(nameErr, c);

            c.gridx = 2; c.gridy = 1; c.weightx = 0; c.insets = new Insets(4, 10, 0, 12);
            container.add(ageErr, c);

            name.getDocument().addDocumentListener(SimpleDocumentListener.onChange(lab2_1.this::validateAll));
            age.getDocument().addDocumentListener(SimpleDocumentListener.onChange(lab2_1.this::validateAll));

            name.addActionListener(e -> age.requestFocusInWindow());
            age.addActionListener(e -> {
                if (index == childRows.size() - 1) {
                    if (validateAll()) compute();
                    else {
                        addChildRow();
                        childRows.get(index + 1).name.requestFocusInWindow();
                    }
                } else {
                    childRows.get(index + 1).name.requestFocusInWindow();
                }
            });

            installArrowNav(name, "name");
            installArrowNav(age, "age");
        }

        void setStripe(Color bg) {
            container.setBackground(bg);
        }

        void installArrowNav(JTextField field, String col) {
            InputMap im = field.getInputMap(JComponent.WHEN_FOCUSED);
            ActionMap am = field.getActionMap();

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "upNav");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "downNav");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "leftNav");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "rightNav");

            am.put("upNav", new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    int target = Math.max(0, index - 1);
                    childRows.get(target).focusCol(col);
                }
            });
            am.put("downNav", new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    int target = Math.min(childRows.size() - 1, index + 1);
                    childRows.get(target).focusCol(col);
                }
            });
            am.put("leftNav", new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    focusCol("name");
                }
            });
            am.put("rightNav", new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    focusCol("age");
                }
            });
        }

        void focusCol(String col) {
            if ("name".equals(col)) name.requestFocusInWindow();
            else age.requestFocusInWindow();
        }
    }

    // ---------------- Document Listener helper ----------------
    @FunctionalInterface
    interface ChangeHandler { void handle(); }

    static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final ChangeHandler h;
        private SimpleDocumentListener(ChangeHandler h) { this.h = h; }
        public static SimpleDocumentListener onChange(ChangeHandler h) { return new SimpleDocumentListener(h); }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { h.handle(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { h.handle(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { h.handle(); }
    }
}