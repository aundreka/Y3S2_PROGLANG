import tkinter as tk
from tkinter import ttk


class DataTypesVisualizer:
    def __init__(self, root):
        self.root = root
        self.root.title("Python Data Types Visualizer")
        self.root.geometry("1250x760")
        self.root.configure(bg="#0f172a")

        self.setup_styles()
        self.build_ui()
        self.show_welcome()

    def setup_styles(self):
        style = ttk.Style()
        style.theme_use("clam")

        style.configure("TFrame", background="#0f172a")
        style.configure("Sidebar.TFrame", background="#111827")
        style.configure("Card.TFrame", background="#1e293b")
        style.configure("Panel.TFrame", background="#0f172a")

        style.configure(
            "Title.TLabel",
            background="#0f172a",
            foreground="white",
            font=("Segoe UI", 24, "bold")
        )

        style.configure(
            "Subtitle.TLabel",
            background="#0f172a",
            foreground="#94a3b8",
            font=("Segoe UI", 11)
        )

        style.configure(
            "SidebarTitle.TLabel",
            background="#111827",
            foreground="white",
            font=("Segoe UI", 14, "bold")
        )

        style.configure(
            "CardTitle.TLabel",
            background="#1e293b",
            foreground="white",
            font=("Segoe UI", 14, "bold")
        )

        style.configure(
            "CardText.TLabel",
            background="#1e293b",
            foreground="#cbd5e1",
            font=("Segoe UI", 11)
        )

        style.configure(
            "Badge.TLabel",
            background="#334155",
            foreground="#e2e8f0",
            font=("Segoe UI", 10, "bold"),
            padding=(10, 4)
        )

        style.configure(
            "Modern.TButton",
            font=("Segoe UI", 10, "bold"),
            padding=12,
            background="#2563eb",
            foreground="white",
            borderwidth=0
        )

        style.map(
            "Modern.TButton",
            background=[("active", "#3b82f6")]
        )

    def build_ui(self):
        self.main = ttk.Frame(self.root, style="Panel.TFrame", padding=20)
        self.main.pack(fill="both", expand=True)

        header_frame = ttk.Frame(self.main, style="Panel.TFrame")
        header_frame.pack(fill="x", pady=(0, 15))

        ttk.Label(
            header_frame,
            text="Python Data Types Visualizer",
            style="Title.TLabel"
        ).pack(anchor="w")

        ttk.Label(
            header_frame,
            text="Interactive, visual, and modern Tkinter demonstration of Python data types.",
            style="Subtitle.TLabel"
        ).pack(anchor="w", pady=(4, 0))

        body = ttk.Frame(self.main, style="Panel.TFrame")
        body.pack(fill="both", expand=True)

        self.sidebar = ttk.Frame(body, style="Sidebar.TFrame", padding=15)
        self.sidebar.pack(side="left", fill="y", padx=(0, 20))

        ttk.Label(
            self.sidebar,
            text="Data Types",
            style="SidebarTitle.TLabel"
        ).pack(anchor="w", pady=(0, 10))

        button_data = [
            ("Integers and Floats", self.show_numeric),
            ("Strings", self.show_string),
            ("Lists", self.show_list),
            ("Tuples", self.show_tuple),
            ("Sets", self.show_set),
            ("Dictionaries", self.show_dict),
            ("Complex", self.show_complex),
            ("Range", self.show_range),
            ("Boolean", self.show_bool),
            ("Bytes", self.show_bytes),
            ("Bytearray", self.show_bytearray),
            ("Memoryview", self.show_memoryview),
            ("Show All", self.show_all)
        ]

        for text, command in button_data:
            ttk.Button(
                self.sidebar,
                text=text,
                command=command,
                style="Modern.TButton"
            ).pack(fill="x", pady=5)

        self.content = ttk.Frame(body, style="Panel.TFrame")
        self.content.pack(side="left", fill="both", expand=True)

        self.hero = ttk.Frame(self.content, style="Card.TFrame", padding=20)
        self.hero.pack(fill="x", pady=(0, 15))

        self.hero_title = ttk.Label(self.hero, text="", style="CardTitle.TLabel")
        self.hero_title.pack(anchor="w")

        self.hero_desc = ttk.Label(self.hero, text="", style="CardText.TLabel", wraplength=820)
        self.hero_desc.pack(anchor="w", pady=(8, 0))

        self.cards_area = ttk.Frame(self.content, style="Panel.TFrame")
        self.cards_area.pack(fill="both", expand=True)

    def clear_cards(self):
        for widget in self.cards_area.winfo_children():
            widget.destroy()

    def clear_hero(self):
        self.hero_title.config(text="")
        self.hero_desc.config(text="")

    def make_card(self, parent, title, rows, width=380):
        card = tk.Frame(parent, bg="#1e293b", bd=0, highlightthickness=0)
        card.pack(side="left", fill="both", expand=True, padx=8, pady=8)

        title_lbl = tk.Label(
            card,
            text=title,
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold"),
            anchor="w"
        )
        title_lbl.pack(fill="x", padx=16, pady=(16, 10))

        for label, value in rows:
            row = tk.Frame(card, bg="#1e293b")
            row.pack(fill="x", padx=16, pady=4)

            tk.Label(
                row,
                text=label,
                bg="#1e293b",
                fg="#94a3b8",
                font=("Segoe UI", 10, "bold"),
                anchor="w",
                width=16
            ).pack(side="left")

            tk.Label(
                row,
                text=value,
                bg="#1e293b",
                fg="#e2e8f0",
                font=("Consolas", 10),
                anchor="w",
                justify="left",
                wraplength=250
            ).pack(side="left", fill="x", expand=True)

        return card

    def make_type_badge(self, parent, text):
        badge = tk.Label(
            parent,
            text=text,
            bg="#334155",
            fg="#e2e8f0",
            font=("Segoe UI", 10, "bold"),
            padx=10,
            pady=4
        )
        badge.pack(anchor="w", padx=16, pady=(0, 10))

    def make_visual_list(self, parent, items, title="Visualization"):
        card = tk.Frame(parent, bg="#1e293b", bd=0)
        card.pack(fill="x", padx=8, pady=8)

        tk.Label(
            card,
            text=title,
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 10))

        container = tk.Frame(card, bg="#1e293b")
        container.pack(anchor="w", padx=16, pady=(0, 16))

        for item in items:
            box = tk.Label(
                container,
                text=str(item),
                bg="#2563eb",
                fg="white",
                font=("Segoe UI", 11, "bold"),
                padx=14,
                pady=10
            )
            box.pack(side="left", padx=6)

    def make_dict_visual(self, parent, dictionary):
        card = tk.Frame(parent, bg="#1e293b", bd=0)
        card.pack(fill="x", padx=8, pady=8)

        tk.Label(
            card,
            text="Visualization",
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 10))

        body = tk.Frame(card, bg="#1e293b")
        body.pack(fill="x", padx=16, pady=(0, 16))

        for key, value in dictionary.items():
            row = tk.Frame(body, bg="#0f172a")
            row.pack(fill="x", pady=5)

            tk.Label(
                row,
                text=str(key),
                bg="#0f172a",
                fg="#93c5fd",
                font=("Segoe UI", 11, "bold"),
                width=14,
                anchor="w",
                padx=10,
                pady=8
            ).pack(side="left")

            tk.Label(
                row,
                text=str(value),
                bg="#1d4ed8",
                fg="white",
                font=("Segoe UI", 11),
                anchor="w",
                padx=10,
                pady=8
            ).pack(side="left", fill="x", expand=True)

    def make_set_visual(self, parent, set1, set2, union_set, intersection_set):
        card = tk.Frame(parent, bg="#1e293b", bd=0)
        card.pack(fill="x", padx=8, pady=8)

        tk.Label(
            card,
            text="Visualization",
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 10))

        section = tk.Frame(card, bg="#1e293b")
        section.pack(anchor="w", padx=16, pady=(0, 16))

        groups = [
            ("Set 1", set1, "#2563eb"),
            ("Set 2", set2, "#7c3aed"),
            ("Union", union_set, "#059669"),
            ("Intersection", intersection_set, "#dc2626")
        ]

        for title, values, color in groups:
            line = tk.Frame(section, bg="#1e293b")
            line.pack(anchor="w", pady=4)

            tk.Label(
                line,
                text=title,
                bg="#1e293b",
                fg="#cbd5e1",
                font=("Segoe UI", 10, "bold"),
                width=12,
                anchor="w"
            ).pack(side="left")

            for value in values:
                tk.Label(
                    line,
                    text=str(value),
                    bg=color,
                    fg="white",
                    font=("Segoe UI", 10, "bold"),
                    padx=10,
                    pady=6
                ).pack(side="left", padx=4)

    def set_hero(self, title, desc):
        self.hero_title.config(text=title)
        self.hero_desc.config(text=desc)

    def show_welcome(self):
        self.clear_cards()
        self.set_hero(
            "Welcome",
            "Choose a Python data type from the left sidebar to see its real value, real type, operations, and visual representation."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "What this app does", [
            ("Purpose", "Demonstrates Python data types visually"),
            ("UI Style", "Modern dashboard layout"),
            ("Output", "Actual values and actual types"),
            ("Visualization", "Lists, sets, dictionaries, bytes, and more")
        ])

        self.make_card(row, "Included data types", [
            ("Numeric", "int, float, complex"),
            ("Sequence", "str, list, tuple, range"),
            ("Set types", "set"),
            ("Mapping", "dict"),
            ("Other", "bool, bytes, bytearray, memoryview")
        ])

    def show_numeric(self):
        self.clear_cards()

        a = 10
        b = 3.5

        self.set_hero(
            "Integers and Floats",
            "Numeric types are used for mathematical operations. Integers store whole numbers, while floats store decimal values."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        card1 = self.make_card(row, "Variables", [
            ("a", f"{a}"),
            ("type(a)", f"{type(a)}"),
            ("b", f"{b}"),
            ("type(b)", f"{type(b)}"),
        ])

        card2 = self.make_card(row, "Operations", [
            ("a + b", f"{a + b}"),
            ("a - b", f"{a - b}"),
            ("a * b", f"{a * b}"),
            ("a / b", f"{a / b}"),
        ])

        visual = tk.Frame(self.cards_area, bg="#1e293b")
        visual.pack(fill="x", padx=8, pady=8)

        tk.Label(
            visual,
            text="Visualization",
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 10))

        for label, value in [("a", a), ("b", b), ("a+b", a + b)]:
            line = tk.Frame(visual, bg="#1e293b")
            line.pack(fill="x", padx=16, pady=6)

            tk.Label(
                line,
                text=label,
                bg="#1e293b",
                fg="#cbd5e1",
                font=("Segoe UI", 11, "bold"),
                width=6,
                anchor="w"
            ).pack(side="left")

            bar = tk.Frame(line, bg="#3b82f6", height=28, width=int(float(value) * 18))
            bar.pack(side="left", padx=10)
            bar.pack_propagate(False)

            tk.Label(
                bar,
                text=str(value),
                bg="#3b82f6",
                fg="white",
                font=("Segoe UI", 10, "bold")
            ).pack(expand=True)

    def show_string(self):
        self.clear_cards()

        name = "Alice"
        greeting = "Hello, " + name + "!"

        self.set_hero(
            "Strings",
            "Strings store text data. They can be combined, transformed to uppercase or lowercase, and indexed character by character."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "String Values", [
            ("name", repr(name)),
            ("type(name)", f"{type(name)}"),
            ("greeting", repr(greeting)),
            ("length", str(len(name))),
        ])

        self.make_card(row, "Operations", [
            ("upper()", name.upper()),
            ("lower()", name.lower()),
            ("first char", name[0]),
            ("last char", name[-1]),
        ])

        self.make_visual_list(self.cards_area, list(name), "Character Visualization")

    def show_list(self):
        self.clear_cards()

        fruits = ["apple", "banana", "cherry"]
        fruits.append("date")

        self.set_hero(
            "Lists",
            "Lists are ordered, mutable collections. You can access items by index, add new items, and modify contents."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "List Data", [
            ("value", str(fruits)),
            ("type", f"{type(fruits)}"),
            ("first item", fruits[0]),
            ("length", str(len(fruits))),
        ])

        self.make_card(row, "Examples", [
            ("append()", "Added 'date'"),
            ("index 1", fruits[1]),
            ("last item", fruits[-1]),
            ("mutable?", "Yes"),
        ])

        self.make_visual_list(self.cards_area, fruits, "List Visualization")

    def show_tuple(self):
        self.clear_cards()

        coordinates = (10, 20)

        self.set_hero(
            "Tuples",
            "Tuples are ordered collections like lists, but they are immutable. They are useful for fixed data such as coordinates."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Tuple Data", [
            ("value", str(coordinates)),
            ("type", f"{type(coordinates)}"),
            ("x", str(coordinates[0])),
            ("y", str(coordinates[1])),
        ])

        self.make_card(row, "Properties", [
            ("ordered?", "Yes"),
            ("mutable?", "No"),
            ("length", str(len(coordinates))),
            ("common use", "Fixed grouped values"),
        ])

        self.make_visual_list(self.cards_area, coordinates, "Tuple Visualization")

    def show_set(self):
        self.clear_cards()

        set1 = {1, 2, 3}
        set2 = {3, 4, 5}
        union_set = set1.union(set2)
        intersection_set = set1.intersection(set2)

        self.set_hero(
            "Sets",
            "Sets store unique values only. They are useful for union, intersection, and removing duplicates."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Set Data", [
            ("set1", str(set1)),
            ("set2", str(set2)),
            ("type", f"{type(set1)}"),
            ("unique values", "Yes"),
        ])

        self.make_card(row, "Operations", [
            ("union", str(union_set)),
            ("intersection", str(intersection_set)),
            ("set1 - set2", str(set1 - set2)),
            ("duplicates?", "Not allowed"),
        ])

        self.make_set_visual(self.cards_area, set1, set2, union_set, intersection_set)

    def show_dict(self):
        self.clear_cards()

        student = {"name": "Bob", "age": 22, "major": "Computer Science"}

        self.set_hero(
            "Dictionaries",
            "Dictionaries store data in key-value pairs. They are useful when you want to label values clearly."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Dictionary Data", [
            ("value", str(student)),
            ("type", f"{type(student)}"),
            ("name", student["name"]),
            ("age", str(student["age"])),
        ])

        self.make_card(row, "Properties", [
            ("major", student["major"]),
            ("keys", str(list(student.keys()))),
            ("values", str(list(student.values()))),
            ("mutable?", "Yes"),
        ])

        self.make_dict_visual(self.cards_area, student)

    def show_complex(self):
        self.clear_cards()

        c = 1 + 2j
        d = 3 + 4j

        self.set_hero(
            "Complex Numbers",
            "Complex numbers contain a real part and an imaginary part. They are often used in engineering, signal processing, and advanced mathematics."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Complex Values", [
            ("c", str(c)),
            ("d", str(d)),
            ("type(c)", f"{type(c)}"),
            ("c + d", str(c + d)),
        ])

        self.make_card(row, "Components", [
            ("c.real", str(c.real)),
            ("c.imag", str(c.imag)),
            ("d.real", str(d.real)),
            ("d.imag", str(d.imag)),
        ])

        visual = tk.Frame(self.cards_area, bg="#1e293b")
        visual.pack(fill="x", padx=8, pady=8)

        tk.Label(
            visual,
            text="Visualization",
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 10))

        for label, real, imag in [("c", c.real, c.imag), ("d", d.real, d.imag)]:
            rowv = tk.Frame(visual, bg="#1e293b")
            rowv.pack(anchor="w", padx=16, pady=8)

            tk.Label(
                rowv,
                text=f"{label} = {real} + {imag}j",
                bg="#1e293b",
                fg="#e2e8f0",
                font=("Segoe UI", 11, "bold"),
                width=16,
                anchor="w"
            ).pack(side="left")

            tk.Label(
                rowv,
                text=f"Real: {real}",
                bg="#2563eb",
                fg="white",
                font=("Segoe UI", 10, "bold"),
                padx=10,
                pady=6
            ).pack(side="left", padx=4)

            tk.Label(
                rowv,
                text=f"Imaginary: {imag}",
                bg="#7c3aed",
                fg="white",
                font=("Segoe UI", 10, "bold"),
                padx=10,
                pady=6
            ).pack(side="left", padx=4)

    def show_range(self):
        self.clear_cards()

        r = range(5)
        values = list(r)

        self.set_hero(
            "Range",
            "A range represents a sequence of numbers and is commonly used in loops."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Range Data", [
            ("value", str(r)),
            ("type", f"{type(r)}"),
            ("as list", str(values)),
            ("length", str(len(values))),
        ])

        self.make_card(row, "Examples", [
            ("start", "0"),
            ("stop", "5"),
            ("step", "1"),
            ("common use", "for loops"),
        ])

        self.make_visual_list(self.cards_area, values, "Range Visualization")

    def show_bool(self):
        self.clear_cards()

        x = 10
        y = 5
        comparison = x > y

        self.set_hero(
            "Boolean",
            "Booleans represent truth values: True or False. They are used in conditions and comparisons."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Boolean Data", [
            ("x", str(x)),
            ("y", str(y)),
            ("x > y", str(comparison)),
            ("type", f"{type(comparison)}"),
        ])

        visual = tk.Frame(self.cards_area, bg="#1e293b")
        visual.pack(fill="x", padx=8, pady=8)

        tk.Label(
            visual,
            text="Visualization",
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 10))

        color = "#16a34a" if comparison else "#dc2626"
        tk.Label(
            visual,
            text=str(comparison),
            bg=color,
            fg="white",
            font=("Segoe UI", 20, "bold"),
            padx=30,
            pady=20
        ).pack(anchor="w", padx=16, pady=(0, 16))

    def show_bytes(self):
        self.clear_cards()

        data = b"hello"

        self.set_hero(
            "Bytes",
            "Bytes are immutable binary data. Each character is stored as a numeric byte value."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Bytes Data", [
            ("value", str(data)),
            ("type", f"{type(data)}"),
            ("length", str(len(data))),
            ("first byte", str(data[0])),
        ])

        self.make_visual_list(self.cards_area, list(data), "Byte Values")

    def show_bytearray(self):
        self.clear_cards()

        data = bytearray(b"hello")
        before = bytes(data)
        data[0] = 72

        self.set_hero(
            "Bytearray",
            "Bytearray is similar to bytes, but mutable. This means its contents can be changed after creation."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Bytearray Data", [
            ("before", str(before)),
            ("after", str(data)),
            ("decoded", data.decode()),
            ("type", f"{type(data)}"),
        ])

        self.make_visual_list(self.cards_area, list(data), "Mutable Byte Values")

    def show_memoryview(self):
        self.clear_cards()

        data = b"hello"
        view = memoryview(data)

        self.set_hero(
            "Memoryview",
            "A memoryview lets Python access binary data without copying it. It is useful for efficient handling of large binary objects."
        )

        row = ttk.Frame(self.cards_area, style="Panel.TFrame")
        row.pack(fill="x")

        self.make_card(row, "Memoryview Data", [
            ("source", str(data)),
            ("view", str(view)),
            ("type", f"{type(view)}"),
            ("as list", str(list(view))),
        ])

        self.make_visual_list(self.cards_area, list(view), "Memoryview Byte Access")

    def show_all(self):
        self.show_welcome()

        summary = tk.Frame(self.cards_area, bg="#1e293b")
        summary.pack(fill="x", padx=8, pady=8)

        tk.Label(
            summary,
            text="Quick Summary",
            bg="#1e293b",
            fg="white",
            font=("Segoe UI", 14, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 10))

        entries = [
            ("int", "Whole numbers"),
            ("float", "Decimal numbers"),
            ("complex", "Real + imaginary"),
            ("str", "Text"),
            ("list", "Ordered mutable collection"),
            ("tuple", "Ordered immutable collection"),
            ("range", "Sequence of numbers"),
            ("set", "Unique unordered values"),
            ("dict", "Key-value pairs"),
            ("bool", "True or False"),
            ("bytes", "Immutable binary data"),
            ("bytearray", "Mutable binary data"),
            ("memoryview", "View binary data without copying")
        ]

        for dtype, meaning in entries:
            row = tk.Frame(summary, bg="#1e293b")
            row.pack(fill="x", padx=16, pady=4)

            tk.Label(
                row,
                text=dtype,
                bg="#2563eb",
                fg="white",
                font=("Segoe UI", 10, "bold"),
                width=12,
                pady=6
            ).pack(side="left")

            tk.Label(
                row,
                text=meaning,
                bg="#1e293b",
                fg="#e2e8f0",
                font=("Segoe UI", 10),
                anchor="w"
            ).pack(side="left", padx=10)


if __name__ == "__main__":
    root = tk.Tk()
    app = DataTypesVisualizer(root)
    root.mainloop()