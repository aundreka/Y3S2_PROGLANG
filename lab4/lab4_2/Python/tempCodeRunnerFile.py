import tkinter as tk

# =========================
# DATA
# =========================

favorite_movies = [
    {"title": "Heathers", "year": 1988, "director": "Michael Lehmann"},
    {"title": "Superman", "year": 2025, "director": "James Gunn"},
    {"title": "Weapons", "year": 2025, "director": "Zach Cregger"},
    {"title": "Miracle in Cell No. 7", "year": 2013, "director": "Lee Hwan-kyung"},
    {"title": "Spirited Away", "year": 2001, "director": "Hayao Miyazaki"},
    {"title": "Knives Out", "year": 2019, "director": "Rian Johnson"},
    {"title": "Wake Up Dead Man", "year": 2025, "director": "Rian Johnson"},
    {"title": "Les Misérables", "year": 2012, "director": "Tom Hooper"},
    {"title": "Harry Potter and the Philosopher's Stone", "year": 2001, "director": "Chris Columbus"},
    {"title": "Harry Potter and the Goblet of Fire", "year": 2005, "director": "Mike Newell"},
    {"title": "Amadeus", "year": 1984, "director": "Miloš Forman"},
    {"title": "Dead Poets Society", "year": 1989, "director": "Peter Weir"},
    {"title": "Pride and Prejudice", "year": 2005, "director": "Joe Wright"},
    {"title": "Little Women", "year": 2019, "director": "Greta Gerwig"},
    {"title": "Coraline", "year": 2009, "director": "Henry Selick"},
    {"title": "Howl's Moving Castle", "year": 2004, "director": "Hayao Miyazaki"},
    {"title": "Interstellar", "year": 2014, "director": "Christopher Nolan"},
    {"title": "The Grand Budapest Hotel", "year": 2014, "director": "Wes Anderson"},
    {"title": "The Imitation Game", "year": 2014, "director": "Morten Tyldum"},
    {"title": "The Perks of Being a Wallflower", "year": 2012, "director": "Stephen Chbosky"},
]

books = {
    "The Blob That Ate Everyone": {"author": "R. L. Stine", "year": 1997},
    "Flowers for Algernon": {"author": "Daniel Keyes", "year": 1966},
    "1984": {"author": "George Orwell", "year": 1949},
    "Jane Eyre": {"author": "Charlotte Brontë", "year": 1847},
    "Wuthering Heights": {"author": "Emily Brontë", "year": 1847},
    "The Secret History": {"author": "Donna Tartt", "year": 1992},
    "The Picture of Dorian Gray": {"author": "Oscar Wilde", "year": 1890},
    "Daughter of the Forest": {"author": "Juliet Marillier", "year": 1999},
    "Miss Benson's Beetle": {"author": "Rachel Joyce", "year": 2020},
    "Frankenstein": {"author": "Mary Shelley", "year": 1818},
}

# =========================
# WINDOW
# =========================

root = tk.Tk()
root.title("Favorites Library")
root.geometry("1200x720")
root.resizable(False, False)
root.configure(bg="#ECEEF1")

BG = "#ECEEF1"
CARD = "#FFFFFF"
TEXT = "#111111"
SUBTEXT = "#6E6E73"
LINE = "#D9D9DE"

# =========================
# HELPER
# =========================

def make_card(parent, x, y, w, h):
    frame = tk.Frame(parent, bg=CARD, highlightbackground=LINE, highlightthickness=1)
    frame.place(x=x, y=y, width=w, height=h)
    return frame

def label(parent, text, font, x, y, fg=TEXT):
    tk.Label(parent, text=text, font=font, fg=fg, bg=parent["bg"]).place(x=x, y=y)

# =========================
# HEADER
# =========================

label(root, "Favorites Library", ("Helvetica", 26, "bold"), 40, 20)
label(root, "Movie list and book dictionary", ("Helvetica", 11), 42, 60, SUBTEXT)

# =========================
# MOVIES PANEL
# =========================

movies_card = make_card(root, 40, 100, 720, 580)

label(movies_card, "Favorite Movies", ("Helvetica", 16, "bold"), 20, 20)
label(movies_card, "Title, Year, Director", ("Helvetica", 10), 20, 45, SUBTEXT)

# headers
label(movies_card, "#", ("Helvetica", 10, "bold"), 20, 80, SUBTEXT)
label(movies_card, "Title", ("Helvetica", 10, "bold"), 60, 80, SUBTEXT)
label(movies_card, "Year", ("Helvetica", 10, "bold"), 470, 80, SUBTEXT)
label(movies_card, "Director", ("Helvetica", 10, "bold"), 540, 80, SUBTEXT)

row_y = 105
row_gap = 22

for i, m in enumerate(favorite_movies, start=1):
    y = row_y + (i-1)*row_gap
    label(movies_card, f"{i}.", ("Helvetica", 9), 20, y, SUBTEXT)
    label(movies_card, m["title"], ("Helvetica", 9), 60, y)
    label(movies_card, str(m["year"]), ("Helvetica", 9), 470, y)
    label(movies_card, m["director"], ("Helvetica", 9), 540, y)

# =========================
# BOOKS PANEL
# =========================

books_card = make_card(root, 790, 100, 370, 580)

label(books_card, "Book Dictionary", ("Helvetica", 16, "bold"), 20, 20)
label(books_card, "Title, Author, Year", ("Helvetica", 10), 20, 45, SUBTEXT)

book_y = 80
book_gap = 48

for i, (title, info) in enumerate(books.items(), start=1):
    y = book_y + (i-1)*book_gap
    label(books_card, f"{i}. {title}", ("Helvetica", 10, "bold"), 20, y)
    label(books_card, f"{info['author']} • {info['year']}", ("Helvetica", 9), 40, y+18, SUBTEXT)

root.mainloop()