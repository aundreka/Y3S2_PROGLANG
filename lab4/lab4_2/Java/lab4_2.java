import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class lab4_2 {

    // =========================
    // DATA
    // =========================

    static class Movie {
        String title;
        int year;
        String director;

        Movie(String title, int year, String director) {
            this.title = title;
            this.year = year;
            this.director = director;
        }
    }

    static class BookInfo {
        String author;
        int year;

        BookInfo(String author, int year) {
            this.author = author;
            this.year = year;
        }
    }

    static Movie[] favoriteMovies = {
        new Movie("Heathers", 1988, "Michael Lehmann"),
        new Movie("Superman", 2025, "James Gunn"),
        new Movie("Weapons", 2025, "Zach Cregger"),
        new Movie("Miracle in Cell No. 7", 2013, "Lee Hwan-kyung"),
        new Movie("Spirited Away", 2001, "Hayao Miyazaki"),
        new Movie("Knives Out", 2019, "Rian Johnson"),
        new Movie("Wake Up Dead Man", 2025, "Rian Johnson"),
        new Movie("Les Misérables", 2012, "Tom Hooper"),
        new Movie("Harry Potter and the Philosopher's Stone", 2001, "Chris Columbus"),
        new Movie("Harry Potter and the Goblet of Fire", 2005, "Mike Newell"),
        new Movie("Amadeus", 1984, "Miloš Forman"),
        new Movie("Dead Poets Society", 1989, "Peter Weir"),
        new Movie("Pride and Prejudice", 2005, "Joe Wright"),
        new Movie("Little Women", 2019, "Greta Gerwig"),
        new Movie("Coraline", 2009, "Henry Selick"),
        new Movie("Howl's Moving Castle", 2004, "Hayao Miyazaki"),
        new Movie("Interstellar", 2014, "Christopher Nolan"),
        new Movie("The Grand Budapest Hotel", 2014, "Wes Anderson"),
        new Movie("The Imitation Game", 2014, "Morten Tyldum"),
        new Movie("The Perks of Being a Wallflower", 2012, "Stephen Chbosky")
    };

    static LinkedHashMap<String, BookInfo> books = new LinkedHashMap<>();

    static {
        books.put("The Blob That Ate Everyone", new BookInfo("R. L. Stine", 1997));
        books.put("Flowers for Algernon", new BookInfo("Daniel Keyes", 1966));
        books.put("1984", new BookInfo("George Orwell", 1949));
        books.put("Jane Eyre", new BookInfo("Charlotte Brontë", 1847));
        books.put("Wuthering Heights", new BookInfo("Emily Brontë", 1847));
        books.put("The Secret History", new BookInfo("Donna Tartt", 1992));
        books.put("The Picture of Dorian Gray", new BookInfo("Oscar Wilde", 1890));
        books.put("Daughter of the Forest", new BookInfo("Juliet Marillier", 1999));
        books.put("Miss Benson's Beetle", new BookInfo("Rachel Joyce", 2020));
        books.put("Frankenstein", new BookInfo("Mary Shelley", 1818));
    }

    // =========================
    // COLORS
    // =========================

    static final Color BG = new Color(0xECEEF1);
    static final Color CARD = Color.WHITE;
    static final Color TEXT = new Color(0x111111);
    static final Color SUBTEXT = new Color(0x6E6E73);
    static final Color LINE = new Color(0xD9D9DE);

    // =========================
    // HELPER
    // =========================

    static JLabel createLabel(Container parent, String text, Font font, int x, int y, Color fg) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(fg);
        label.setBounds(x, y, 1000, 30);
        parent.add(label);
        return label;
    }

    static JPanel makeCard(Container parent, int x, int y, int w, int h) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(CARD);
        panel.setBorder(new LineBorder(LINE, 1));
        panel.setBounds(x, y, w, h);
        parent.add(panel);
        return panel;
    }

    // =========================
    // UI
    // =========================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Favorites Library");
            frame.setSize(1200, 720);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLayout(null);
            frame.getContentPane().setBackground(BG);

            Font titleFont = new Font("Helvetica", Font.BOLD, 26);
            Font subtitleFont = new Font("Helvetica", Font.PLAIN, 11);
            Font sectionFont = new Font("Helvetica", Font.BOLD, 16);
            Font smallBold = new Font("Helvetica", Font.BOLD, 10);
            Font normal9 = new Font("Helvetica", Font.PLAIN, 9);
            Font bold10 = new Font("Helvetica", Font.BOLD, 10);

            // =========================
            // HEADER
            // =========================

            createLabel(frame, "Favorites Library", titleFont, 40, 20, TEXT);
            createLabel(frame, "Movie list and book dictionary", subtitleFont, 42, 60, SUBTEXT);

            // =========================
            // MOVIES PANEL
            // =========================

            JPanel moviesCard = makeCard(frame, 40, 100, 720, 580);

            createLabel(moviesCard, "Favorite Movies", sectionFont, 20, 20, TEXT);
            createLabel(moviesCard, "Title, Year, Director", subtitleFont, 20, 45, SUBTEXT);

            createLabel(moviesCard, "#", smallBold, 20, 80, SUBTEXT);
            createLabel(moviesCard, "Title", smallBold, 60, 80, SUBTEXT);
            createLabel(moviesCard, "Year", smallBold, 470, 80, SUBTEXT);
            createLabel(moviesCard, "Director", smallBold, 540, 80, SUBTEXT);

            int rowY = 105;
            int rowGap = 22;

            for (int i = 0; i < favoriteMovies.length; i++) {
                int y = rowY + (i * rowGap);
                Movie m = favoriteMovies[i];

                createLabel(moviesCard, (i + 1) + ".", normal9, 20, y, SUBTEXT);
                createLabel(moviesCard, m.title, normal9, 60, y, TEXT);
                createLabel(moviesCard, String.valueOf(m.year), normal9, 470, y, TEXT);
                createLabel(moviesCard, m.director, normal9, 540, y, TEXT);
            }

            // =========================
            // BOOKS PANEL
            // =========================

            JPanel booksCard = makeCard(frame, 790, 100, 370, 580);

            createLabel(booksCard, "Book Dictionary", sectionFont, 20, 20, TEXT);
            createLabel(booksCard, "Title, Author, Year", subtitleFont, 20, 45, SUBTEXT);

            int bookY = 80;
            int bookGap = 48;
            int index = 1;

            for (Map.Entry<String, BookInfo> entry : books.entrySet()) {
                int y = bookY + ((index - 1) * bookGap);
                String title = entry.getKey();
                BookInfo info = entry.getValue();

                createLabel(booksCard, index + ". " + title, bold10, 20, y, TEXT);
                createLabel(booksCard, info.author + " • " + info.year, normal9, 40, y + 18, SUBTEXT);
                index++;
            }

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}