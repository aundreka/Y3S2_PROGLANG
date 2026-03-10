import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class lab4_1 extends JFrame {

    private final Color BG = new Color(15, 23, 42);
    private final Color SIDEBAR = new Color(17, 24, 39);
    private final Color CARD = new Color(30, 41, 59);
    private final Color MUTED = new Color(148, 163, 184);
    private final Color TEXT = Color.WHITE;
    private final Color SUBTEXT = new Color(203, 213, 225);
    private final Color BLUE = new Color(37, 99, 235);

    private JLabel heroTitle;
    private JLabel heroDesc;
    private JPanel cardsArea;

    public lab4_1() {
        setTitle("Python Data Types Visualizer");
        setSize(1250, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        buildUI();
        showWelcome();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(main, BorderLayout.CENTER);

        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Python Data Types Visualizer");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitle = new JLabel("Interactive, visual, and modern Java demonstration of Python data types.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        main.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(BG);
        main.add(body, BorderLayout.CENTER);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(15, 15, 15, 15));
        sidebar.setPreferredSize(new Dimension(250, 0));

        JLabel sidebarTitle = new JLabel("Data Types");
        sidebarTitle.setForeground(TEXT);
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sidebarTitle);
        sidebar.add(Box.createVerticalStrut(10));

        addSidebarButton(sidebar, "Integers and Floats", this::showNumeric);
        addSidebarButton(sidebar, "Strings", this::showString);
        addSidebarButton(sidebar, "Lists", this::showList);
        addSidebarButton(sidebar, "Tuples", this::showTuple);
        addSidebarButton(sidebar, "Sets", this::showSet);
        addSidebarButton(sidebar, "Dictionaries", this::showDict);
        addSidebarButton(sidebar, "Complex", this::showComplex);
        addSidebarButton(sidebar, "Range", this::showRange);
        addSidebarButton(sidebar, "Boolean", this::showBool);
        addSidebarButton(sidebar, "Bytes", this::showBytes);
        addSidebarButton(sidebar, "Bytearray", this::showBytearray);
        addSidebarButton(sidebar, "Memoryview", this::showMemoryview);
        addSidebarButton(sidebar, "Show All", this::showAll);

        body.add(sidebar, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(BG);
        body.add(content, BorderLayout.CENTER);

        JPanel hero = new JPanel();
        hero.setBackground(CARD);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(20, 20, 20, 20));

        heroTitle = new JLabel();
        heroTitle.setForeground(TEXT);
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        heroDesc = new JLabel();
        heroDesc.setForeground(SUBTEXT);
        heroDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        hero.add(heroTitle);
        hero.add(Box.createVerticalStrut(8));
        hero.add(heroDesc);

        content.add(hero, BorderLayout.NORTH);

        cardsArea = new JPanel();
        cardsArea.setBackground(BG);
        cardsArea.setLayout(new BoxLayout(cardsArea, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(cardsArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        content.add(scrollPane, BorderLayout.CENTER);
    }

    private void addSidebarButton(JPanel parent, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(BLUE);
        btn.setForeground(TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        parent.add(btn);
        parent.add(Box.createVerticalStrut(5));
    }

    private void clearCards() {
        cardsArea.removeAll();
        cardsArea.revalidate();
        cardsArea.repaint();
    }

    private void setHero(String title, String desc) {
        heroTitle.setText(title);
        heroDesc.setText("<html><div style='width:820px'>" + desc + "</div></html>");
    }

    private JPanel createRowPanel() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setBackground(BG);
        row.setBorder(new EmptyBorder(0, 0, 8, 0));
        return row;
    }

    private JPanel makeCard(String title, String[][] rows) {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(TEXT);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(10));

        for (String[] row : rows) {
            JPanel line = new JPanel(new BorderLayout(10, 0));
            line.setBackground(CARD);

            JLabel left = new JLabel(row[0]);
            left.setForeground(MUTED);
            left.setFont(new Font("Segoe UI", Font.BOLD, 10));
            left.setPreferredSize(new Dimension(120, 20));

            JLabel right = new JLabel("<html>" + row[1] + "</html>");
            right.setForeground(SUBTEXT);
            right.setFont(new Font("Consolas", Font.PLAIN, 10));

            line.add(left, BorderLayout.WEST);
            line.add(right, BorderLayout.CENTER);
            card.add(line);
            card.add(Box.createVerticalStrut(4));
        }

        return card;
    }

    private JPanel makeVisualList(String title, Object[] items, Color color) {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel(title);
        lbl.setForeground(TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(lbl);
        card.add(Box.createVerticalStrut(10));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        container.setBackground(CARD);

        for (Object item : items) {
            JLabel box = new JLabel(String.valueOf(item));
            box.setOpaque(true);
            box.setBackground(color);
            box.setForeground(TEXT);
            box.setFont(new Font("Segoe UI", Font.BOLD, 11));
            box.setBorder(new EmptyBorder(10, 14, 10, 14));
            container.add(box);
        }

        card.add(container);
        return card;
    }

    private JPanel makeDictVisual(String title, LinkedHashMap<String, Object> map) {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel(title);
        lbl.setForeground(TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(lbl);
        card.add(Box.createVerticalStrut(10));

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            JPanel row = new JPanel(new BorderLayout(0, 0));
            row.setBackground(BG);
            row.setBorder(new EmptyBorder(0, 0, 5, 0));

            JLabel key = new JLabel(entry.getKey());
            key.setOpaque(true);
            key.setBackground(BG);
            key.setForeground(new Color(147, 197, 253));
            key.setFont(new Font("Segoe UI", Font.BOLD, 11));
            key.setBorder(new EmptyBorder(8, 10, 8, 10));
            key.setPreferredSize(new Dimension(140, 36));

            JLabel value = new JLabel(String.valueOf(entry.getValue()));
            value.setOpaque(true);
            value.setBackground(new Color(29, 78, 216));
            value.setForeground(TEXT);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            value.setBorder(new EmptyBorder(8, 10, 8, 10));

            row.add(key, BorderLayout.WEST);
            row.add(value, BorderLayout.CENTER);
            card.add(row);
        }

        return card;
    }

    private JPanel makeSetVisual(Set<Integer> set1, Set<Integer> set2, Set<Integer> union, Set<Integer> intersection) {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel("Visualization");
        lbl.setForeground(TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(lbl);
        card.add(Box.createVerticalStrut(10));

        addSetLine(card, "Set 1", set1.toArray(), new Color(37, 99, 235));
        addSetLine(card, "Set 2", set2.toArray(), new Color(124, 58, 237));
        addSetLine(card, "Union", union.toArray(), new Color(5, 150, 105));
        addSetLine(card, "Intersection", intersection.toArray(), new Color(220, 38, 38));

        return card;
    }

    private void addSetLine(JPanel parent, String title, Object[] values, Color color) {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        line.setBackground(CARD);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(SUBTEXT);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setPreferredSize(new Dimension(90, 25));
        line.add(titleLbl);

        for (Object value : values) {
            JLabel box = new JLabel(String.valueOf(value));
            box.setOpaque(true);
            box.setBackground(color);
            box.setForeground(TEXT);
            box.setFont(new Font("Segoe UI", Font.BOLD, 10));
            box.setBorder(new EmptyBorder(6, 10, 6, 10));
            line.add(box);
        }

        parent.add(line);
    }

    private JPanel makeBarVisualization(Object[][] data) {
        JPanel visual = new JPanel();
        visual.setBackground(CARD);
        visual.setLayout(new BoxLayout(visual, BoxLayout.Y_AXIS));
        visual.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Visualization");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        visual.add(title);
        visual.add(Box.createVerticalStrut(10));

        for (Object[] item : data) {
            String label = String.valueOf(item[0]);
            double value = Double.parseDouble(String.valueOf(item[1]));

            JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            line.setBackground(CARD);

            JLabel lbl = new JLabel(label);
            lbl.setForeground(SUBTEXT);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setPreferredSize(new Dimension(50, 28));

            JPanel bar = new JPanel(new BorderLayout());
            bar.setBackground(new Color(59, 130, 246));
            bar.setPreferredSize(new Dimension((int) (value * 18), 28));

            JLabel barText = new JLabel(String.valueOf(value), SwingConstants.CENTER);
            barText.setForeground(TEXT);
            barText.setFont(new Font("Segoe UI", Font.BOLD, 10));
            bar.add(barText, BorderLayout.CENTER);

            line.add(lbl);
            line.add(bar);
            visual.add(line);
        }

        return visual;
    }

    private void showWelcome() {
        clearCards();
        setHero(
                "Welcome",
                "Choose a Python data type from the left sidebar to see its real value, real type, operations, and visual representation."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("What this app does", new String[][]{
                {"Purpose", "Demonstrates Python data types visually"},
                {"UI Style", "Modern dashboard layout"},
                {"Output", "Actual values and actual types"},
                {"Visualization", "Lists, sets, dictionaries, bytes, and more"}
        }));
        row.add(makeCard("Included data types", new String[][]{
                {"Numeric", "int, float, complex"},
                {"Sequence", "str, list, tuple, range"},
                {"Set types", "set"},
                {"Mapping", "dict"},
                {"Other", "bool, bytes, bytearray, memoryview"}
        }));
        cardsArea.add(row);

        refreshCards();
    }

    private void showNumeric() {
        clearCards();

        int a = 10;
        double b = 3.5;

        setHero(
                "Integers and Floats",
                "Numeric types are used for mathematical operations. Integers store whole numbers, while floats store decimal values."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Variables", new String[][]{
                {"a", String.valueOf(a)},
                {"type(a)", "int"},
                {"b", String.valueOf(b)},
                {"type(b)", "double"}
        }));
        row.add(makeCard("Operations", new String[][]{
                {"a + b", String.valueOf(a + b)},
                {"a - b", String.valueOf(a - b)},
                {"a * b", String.valueOf(a * b)},
                {"a / b", String.valueOf(a / b)}
        }));
        cardsArea.add(row);

        cardsArea.add(makeBarVisualization(new Object[][]{
                {"a", a},
                {"b", b},
                {"a+b", a + b}
        }));

        refreshCards();
    }

    private void showString() {
        clearCards();

        String name = "Alice";
        String greeting = "Hello, " + name + "!";

        setHero(
                "Strings",
                "Strings store text data. They can be combined, transformed to uppercase or lowercase, and indexed character by character."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("String Values", new String[][]{
                {"name", "\"" + name + "\""},
                {"type(name)", "String"},
                {"greeting", "\"" + greeting + "\""},
                {"length", String.valueOf(name.length())}
        }));
        row.add(makeCard("Operations", new String[][]{
                {"upper()", name.toUpperCase()},
                {"lower()", name.toLowerCase()},
                {"first char", String.valueOf(name.charAt(0))},
                {"last char", String.valueOf(name.charAt(name.length() - 1))}
        }));
        cardsArea.add(row);

        Character[] chars = name.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
        cardsArea.add(makeVisualList("Character Visualization", chars, BLUE));

        refreshCards();
    }

    private void showList() {
        clearCards();

        java.util.List<String> fruits = new java.util.ArrayList<>();
        fruits.add("apple");
        fruits.add("banana");
        fruits.add("cherry");
        fruits.add("date");

        setHero(
                "Lists",
                "Lists are ordered, mutable collections. You can access items by index, add new items, and modify contents."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("List Data", new String[][]{
                {"value", fruits.toString()},
                {"type", "ArrayList<String>"},
                {"first item", fruits.get(0)},
                {"length", String.valueOf(fruits.size())}
        }));
        row.add(makeCard("Examples", new String[][]{
                {"append()", "Added 'date'"},
                {"index 1", fruits.get(1)},
                {"last item", fruits.get(fruits.size() - 1)},
                {"mutable?", "Yes"}
        }));
        cardsArea.add(row);

        cardsArea.add(makeVisualList("List Visualization", fruits.toArray(), BLUE));

        refreshCards();
    }

    private void showTuple() {
        clearCards();

        int[] coordinates = {10, 20};

        setHero(
                "Tuples",
                "Tuples are ordered collections like lists, but they are immutable. They are useful for fixed data such as coordinates."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Tuple Data", new String[][]{
                {"value", "(10, 20)"},
                {"type", "fixed pair / int[] demo"},
                {"x", String.valueOf(coordinates[0])},
                {"y", String.valueOf(coordinates[1])}
        }));
        row.add(makeCard("Properties", new String[][]{
                {"ordered?", "Yes"},
                {"mutable?", "No (conceptually)"},
                {"length", "2"},
                {"common use", "Fixed grouped values"}
        }));
        cardsArea.add(row);

        cardsArea.add(makeVisualList("Tuple Visualization", new Object[]{10, 20}, BLUE));

        refreshCards();
    }

    private void showSet() {
        clearCards();

        java.util.Set<Integer> set1 = new java.util.LinkedHashSet<>(java.util.Arrays.asList(1, 2, 3));
        java.util.Set<Integer> set2 = new java.util.LinkedHashSet<>(java.util.Arrays.asList(3, 4, 5));
        java.util.Set<Integer> union = new java.util.LinkedHashSet<>(set1);
        union.addAll(set2);

        java.util.Set<Integer> intersection = new java.util.LinkedHashSet<>(set1);
        intersection.retainAll(set2);

        java.util.Set<Integer> difference = new java.util.LinkedHashSet<>(set1);
        difference.removeAll(set2);

        setHero(
                "Sets",
                "Sets store unique values only. They are useful for union, intersection, and removing duplicates."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Set Data", new String[][]{
                {"set1", set1.toString()},
                {"set2", set2.toString()},
                {"type", "Set<Integer>"},
                {"unique values", "Yes"}
        }));
        row.add(makeCard("Operations", new String[][]{
                {"union", union.toString()},
                {"intersection", intersection.toString()},
                {"set1 - set2", difference.toString()},
                {"duplicates?", "Not allowed"}
        }));
        cardsArea.add(row);

        cardsArea.add(makeSetVisual(set1, set2, union, intersection));

        refreshCards();
    }

    private void showDict() {
        clearCards();

        LinkedHashMap<String, Object> student = new LinkedHashMap<>();
        student.put("name", "Bob");
        student.put("age", 22);
        student.put("major", "Computer Science");

        setHero(
                "Dictionaries",
                "Dictionaries store data in key-value pairs. They are useful when you want to label values clearly."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Dictionary Data", new String[][]{
                {"value", student.toString()},
                {"type", "Map<String, Object>"},
                {"name", String.valueOf(student.get("name"))},
                {"age", String.valueOf(student.get("age"))}
        }));
        row.add(makeCard("Properties", new String[][]{
                {"major", String.valueOf(student.get("major"))},
                {"keys", student.keySet().toString()},
                {"values", student.values().toString()},
                {"mutable?", "Yes"}
        }));
        cardsArea.add(row);

        cardsArea.add(makeDictVisual("Visualization", student));

        refreshCards();
    }

    private void showComplex() {
        clearCards();

        double cReal = 1, cImag = 2;
        double dReal = 3, dImag = 4;

        setHero(
                "Complex Numbers",
                "Complex numbers contain a real part and an imaginary part. They are often used in engineering, signal processing, and advanced mathematics."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Complex Values", new String[][]{
                {"c", "1.0 + 2.0j"},
                {"d", "3.0 + 4.0j"},
                {"type(c)", "custom complex representation"},
                {"c + d", "4.0 + 6.0j"}
        }));
        row.add(makeCard("Components", new String[][]{
                {"c.real", String.valueOf(cReal)},
                {"c.imag", String.valueOf(cImag)},
                {"d.real", String.valueOf(dReal)},
                {"d.imag", String.valueOf(dImag)}
        }));
        cardsArea.add(row);

        JPanel visual = new JPanel();
        visual.setBackground(CARD);
        visual.setLayout(new BoxLayout(visual, BoxLayout.Y_AXIS));
        visual.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Visualization");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        visual.add(title);
        visual.add(Box.createVerticalStrut(10));

        addComplexLine(visual, "c = 1.0 + 2.0j", "Real: 1.0", "Imaginary: 2.0");
        addComplexLine(visual, "d = 3.0 + 4.0j", "Real: 3.0", "Imaginary: 4.0");

        cardsArea.add(visual);

        refreshCards();
    }

    private void addComplexLine(JPanel parent, String main, String real, String imag) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 8));
        row.setBackground(CARD);

        JLabel mainLbl = new JLabel(main);
        mainLbl.setForeground(SUBTEXT);
        mainLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        mainLbl.setPreferredSize(new Dimension(180, 30));

        JLabel realLbl = new JLabel(real);
        realLbl.setOpaque(true);
        realLbl.setBackground(new Color(37, 99, 235));
        realLbl.setForeground(TEXT);
        realLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        realLbl.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel imagLbl = new JLabel(imag);
        imagLbl.setOpaque(true);
        imagLbl.setBackground(new Color(124, 58, 237));
        imagLbl.setForeground(TEXT);
        imagLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        imagLbl.setBorder(new EmptyBorder(6, 10, 6, 10));

        row.add(mainLbl);
        row.add(realLbl);
        row.add(imagLbl);
        parent.add(row);
    }

    private void showRange() {
        clearCards();

        Integer[] values = {0, 1, 2, 3, 4};

        setHero(
                "Range",
                "A range represents a sequence of numbers and is commonly used in loops."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Range Data", new String[][]{
                {"value", "0..4"},
                {"type", "int sequence"},
                {"as list", java.util.Arrays.toString(values)},
                {"length", String.valueOf(values.length)}
        }));
        row.add(makeCard("Examples", new String[][]{
                {"start", "0"},
                {"stop", "5"},
                {"step", "1"},
                {"common use", "for loops"}
        }));
        cardsArea.add(row);

        cardsArea.add(makeVisualList("Range Visualization", values, BLUE));

        refreshCards();
    }

    private void showBool() {
        clearCards();

        int x = 10;
        int y = 5;
        boolean comparison = x > y;

        setHero(
                "Boolean",
                "Booleans represent truth values: True or False. They are used in conditions and comparisons."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Boolean Data", new String[][]{
                {"x", String.valueOf(x)},
                {"y", String.valueOf(y)},
                {"x > y", String.valueOf(comparison)},
                {"type", "boolean"}
        }));
        row.add(new JPanel());
        cardsArea.add(row);

        JPanel visual = new JPanel();
        visual.setBackground(CARD);
        visual.setLayout(new BoxLayout(visual, BoxLayout.Y_AXIS));
        visual.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Visualization");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        visual.add(title);
        visual.add(Box.createVerticalStrut(10));

        JLabel result = new JLabel(String.valueOf(comparison));
        result.setOpaque(true);
        result.setBackground(comparison ? new Color(22, 163, 74) : new Color(220, 38, 38));
        result.setForeground(TEXT);
        result.setFont(new Font("Segoe UI", Font.BOLD, 20));
        result.setBorder(new EmptyBorder(20, 30, 20, 30));
        result.setAlignmentX(Component.LEFT_ALIGNMENT);

        visual.add(result);
        cardsArea.add(visual);

        refreshCards();
    }

    private void showBytes() {
        clearCards();

        byte[] data = "hello".getBytes();

        setHero(
                "Bytes",
                "Bytes are immutable binary data. Each character is stored as a numeric byte value."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Bytes Data", new String[][]{
                {"value", "\"hello\".getBytes()"},
                {"type", "byte[]"},
                {"length", String.valueOf(data.length)},
                {"first byte", String.valueOf(data[0])}
        }));
        row.add(new JPanel());
        cardsArea.add(row);

        Integer[] values = new Integer[data.length];
        for (int i = 0; i < data.length; i++) values[i] = (int) data[i];
        cardsArea.add(makeVisualList("Byte Values", values, BLUE));

        refreshCards();
    }

    private void showBytearray() {
        clearCards();

        byte[] data = "hello".getBytes();
        String before = new String(data);
        data[0] = 72;

        setHero(
                "Bytearray",
                "Bytearray is similar to bytes, but mutable. This means its contents can be changed after creation."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Bytearray Data", new String[][]{
                {"before", before},
                {"after", new String(data)},
                {"decoded", new String(data)},
                {"type", "byte[] (mutable demo)"}
        }));
        row.add(new JPanel());
        cardsArea.add(row);

        Integer[] values = new Integer[data.length];
        for (int i = 0; i < data.length; i++) values[i] = (int) data[i];
        cardsArea.add(makeVisualList("Mutable Byte Values", values, BLUE));

        refreshCards();
    }

    private void showMemoryview() {
        clearCards();

        byte[] data = "hello".getBytes();

        setHero(
                "Memoryview",
                "A memoryview lets Python access binary data without copying it. It is useful for efficient handling of large binary objects."
        );

        JPanel row = createRowPanel();
        row.add(makeCard("Memoryview Data", new String[][]{
                {"source", "\"hello\".getBytes()"},
                {"view", "ByteBuffer / direct byte access equivalent"},
                {"type", "byte[] view demo"},
                {"as list", java.util.Arrays.toString(data)}
        }));
        row.add(new JPanel());
        cardsArea.add(row);

        Integer[] values = new Integer[data.length];
        for (int i = 0; i < data.length; i++) values[i] = (int) data[i];
        cardsArea.add(makeVisualList("Memoryview Byte Access", values, BLUE));

        refreshCards();
    }

    private void showAll() {
        showWelcome();

        JPanel summary = new JPanel();
        summary.setBackground(CARD);
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Quick Summary");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summary.add(title);
        summary.add(Box.createVerticalStrut(10));

        String[][] entries = {
                {"int", "Whole numbers"},
                {"float", "Decimal numbers"},
                {"complex", "Real + imaginary"},
                {"str", "Text"},
                {"list", "Ordered mutable collection"},
                {"tuple", "Ordered immutable collection"},
                {"range", "Sequence of numbers"},
                {"set", "Unique unordered values"},
                {"dict", "Key-value pairs"},
                {"bool", "True or False"},
                {"bytes", "Immutable binary data"},
                {"bytearray", "Mutable binary data"},
                {"memoryview", "View binary data without copying"}
        };

        for (String[] entry : entries) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            row.setBackground(CARD);

            JLabel dtype = new JLabel(entry[0]);
            dtype.setOpaque(true);
            dtype.setBackground(BLUE);
            dtype.setForeground(TEXT);
            dtype.setFont(new Font("Segoe UI", Font.BOLD, 10));
            dtype.setBorder(new EmptyBorder(6, 12, 6, 12));
            dtype.setPreferredSize(new Dimension(100, 28));

            JLabel meaning = new JLabel(entry[1]);
            meaning.setForeground(SUBTEXT);
            meaning.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            row.add(dtype);
            row.add(meaning);
            summary.add(row);
        }

        cardsArea.add(summary);
        refreshCards();
    }

    private void refreshCards() {
        cardsArea.revalidate();
        cardsArea.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new lab4_1().setVisible(true));
    }
}