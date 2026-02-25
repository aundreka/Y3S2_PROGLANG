import tkinter as tk
from tkinter import ttk, messagebox

MAX_CHILDREN = 50


class ModernFamilyApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Personal Information System")
        self.geometry("820x560")
        self.minsize(760, 520)

        # Use a nicer built-in ttk theme if available
        style = ttk.Style(self)
        for theme in ("clam", "vista", "xpnative", "aqua", "alt", "default"):
            if theme in style.theme_names():
                style.theme_use(theme)
                break

        # ---- Styling (modern-ish) ----
        self.bg = "#0f172a"       # slate-900
        self.card = "#111827"     # gray-900
        self.text = "#e5e7eb"     # gray-200
        self.muted = "#94a3b8"    # slate-400
        self.accent = "#38bdf8"   # sky-400

        self.configure(bg=self.bg)

        style.configure("App.TFrame", background=self.bg)
        style.configure("Card.TFrame", background=self.card)
        style.configure("Title.TLabel", background=self.bg, foreground=self.text, font=("Segoe UI", 18, "bold"))
        style.configure("Sub.TLabel", background=self.bg, foreground=self.muted, font=("Segoe UI", 10))
        style.configure("CardTitle.TLabel", background=self.card, foreground=self.text, font=("Segoe UI", 12, "bold"))
        style.configure("CardText.TLabel", background=self.card, foreground=self.muted, font=("Segoe UI", 10))
        style.configure("Out.TLabel", background=self.card, foreground=self.text, font=("Segoe UI", 11))

        style.configure("Accent.TButton", font=("Segoe UI", 10, "bold"), padding=(12, 8))
        style.map("Accent.TButton",
                  foreground=[("active", "#0b1220")],
                  background=[("active", self.accent)])

        style.configure("Ghost.TButton", font=("Segoe UI", 10), padding=(10, 7))
        style.configure("TEntry", padding=(8, 7))
        style.configure("TCombobox", padding=(8, 7))

        # ---- Layout ----
        root = ttk.Frame(self, style="App.TFrame")
        root.pack(fill="both", expand=True, padx=18, pady=16)

        # Header
        header = ttk.Frame(root, style="App.TFrame")
        header.pack(fill="x")
        ttk.Label(header, text="Personal Information System", style="Title.TLabel").pack(anchor="w")
        ttk.Label(header, text="Enter parent info, add children, then compute the eldest child's age.",
                  style="Sub.TLabel").pack(anchor="w", pady=(4, 0))

        content = ttk.Frame(root, style="App.TFrame")
        content.pack(fill="both", expand=True, pady=(14, 0))

        content.columnconfigure(0, weight=1)
        content.columnconfigure(1, weight=1)
        content.rowconfigure(0, weight=1)

        # Left column cards: Parent + Children
        left = ttk.Frame(content, style="App.TFrame")
        left.grid(row=0, column=0, sticky="nsew", padx=(0, 10))
        left.rowconfigure(1, weight=1)

        self._build_parent_card(left)
        self._build_children_card(left)

        # Right column card: Output
        right = ttk.Frame(content, style="App.TFrame")
        right.grid(row=0, column=1, sticky="nsew", padx=(10, 0))
        right.rowconfigure(0, weight=1)
        self._build_output_card(right)

        # Start with 3 children rows (like your original requirement)
        for _ in range(3):
            self.add_child_row()

    def _build_parent_card(self, parent):
        card = ttk.Frame(parent, style="Card.TFrame")
        card.grid(row=0, column=0, sticky="ew")
        card.columnconfigure(1, weight=1)

        ttk.Label(card, text="Parent Information", style="CardTitle.TLabel").grid(
            row=0, column=0, columnspan=2, sticky="w", padx=16, pady=(14, 10)
        )

        ttk.Label(card, text="First Name", style="CardText.TLabel").grid(row=1, column=0, sticky="w", padx=16)
        self.parent_first = ttk.Entry(card)
        self.parent_first.grid(row=1, column=1, sticky="ew", padx=16, pady=6)

        ttk.Label(card, text="Last Name", style="CardText.TLabel").grid(row=2, column=0, sticky="w", padx=16)
        self.parent_last = ttk.Entry(card)
        self.parent_last.grid(row=2, column=1, sticky="ew", padx=16, pady=(0, 14))

    def _build_children_card(self, parent):
        card = ttk.Frame(parent, style="Card.TFrame")
        card.grid(row=1, column=0, sticky="nsew", pady=(12, 0))
        card.columnconfigure(0, weight=1)
        card.rowconfigure(2, weight=1)

        top = ttk.Frame(card, style="Card.TFrame")
        top.grid(row=0, column=0, sticky="ew", padx=16, pady=(14, 6))
        top.columnconfigure(0, weight=1)

        ttk.Label(top, text="Children Information", style="CardTitle.TLabel").grid(row=0, column=0, sticky="w")
        btns = ttk.Frame(top, style="Card.TFrame")
        btns.grid(row=0, column=1, sticky="e")

        ttk.Button(btns, text="+ Add Child", style="Ghost.TButton", command=self.add_child_row).pack(side="left", padx=(0, 8))
        ttk.Button(btns, text="Clear", style="Ghost.TButton", command=self.clear_children).pack(side="left")

        # Column headers
        hdr = ttk.Frame(card, style="Card.TFrame")
        hdr.grid(row=1, column=0, sticky="ew", padx=16, pady=(0, 6))
        hdr.columnconfigure(0, weight=3)
        hdr.columnconfigure(1, weight=1)
        hdr.columnconfigure(2, weight=0)

        ttk.Label(hdr, text="Child First Name", style="CardText.TLabel").grid(row=0, column=0, sticky="w")
        ttk.Label(hdr, text="Age", style="CardText.TLabel").grid(row=0, column=1, sticky="w")

        # Scrollable rows area
        container = ttk.Frame(card, style="Card.TFrame")
        container.grid(row=2, column=0, sticky="nsew", padx=16, pady=(0, 10))
        container.rowconfigure(0, weight=1)
        container.columnconfigure(0, weight=1)

        self.canvas = tk.Canvas(container, bg=self.card, highlightthickness=0)
        self.canvas.grid(row=0, column=0, sticky="nsew")
        self.vsb = ttk.Scrollbar(container, orient="vertical", command=self.canvas.yview)
        self.vsb.grid(row=0, column=1, sticky="ns")
        self.canvas.configure(yscrollcommand=self.vsb.set)

        self.rows_frame = ttk.Frame(self.canvas, style="Card.TFrame")
        self.rows_frame.columnconfigure(0, weight=3)
        self.rows_frame.columnconfigure(1, weight=1)
        self.rows_frame.columnconfigure(2, weight=0)

        self.window_id = self.canvas.create_window((0, 0), window=self.rows_frame, anchor="nw")

        self.rows_frame.bind("<Configure>", self._on_rows_configure)
        self.canvas.bind("<Configure>", self._on_canvas_configure)

        # Footer actions
        footer = ttk.Frame(card, style="Card.TFrame")
        footer.grid(row=3, column=0, sticky="ew", padx=16, pady=(0, 14))
        footer.columnconfigure(0, weight=1)

        ttk.Button(footer, text="Compute Eldest", style="Accent.TButton", command=self.compute).pack(side="left")
        ttk.Button(footer, text="Reset All", style="Ghost.TButton", command=self.reset_all).pack(side="left", padx=(10, 0))

        self.child_rows = []  # list of dicts: {"name": Entry, "age": Entry, "btn": Button, "row": int}

    def _build_output_card(self, parent):
        card = ttk.Frame(parent, style="Card.TFrame")
        card.grid(row=0, column=0, sticky="nsew")
        card.columnconfigure(0, weight=1)
        card.rowconfigure(2, weight=1)

        ttk.Label(card, text="Output", style="CardTitle.TLabel").grid(
            row=0, column=0, sticky="w", padx=16, pady=(14, 8)
        )
        ttk.Label(card, text="Your formatted result will appear here.", style="CardText.TLabel").grid(
            row=1, column=0, sticky="w", padx=16, pady=(0, 10)
        )

        self.output = tk.Text(card, height=18, wrap="word", bg=self.card, fg=self.text,
                              insertbackground=self.text, relief="flat",
                              font=("Consolas", 11))
        self.output.grid(row=2, column=0, sticky="nsew", padx=16, pady=(0, 14))

        # Make selection readable
        self.output.configure(selectbackground=self.accent, selectforeground="#0b1220")

        # Default text
        self._set_output("Fill in the form on the left and click “Compute Eldest”.")

    def _set_output(self, text):
        self.output.configure(state="normal")
        self.output.delete("1.0", "end")
        self.output.insert("1.0", text)
        self.output.configure(state="disabled")

    # ---- Scroll handling ----
    def _on_rows_configure(self, _event):
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))

    def _on_canvas_configure(self, event):
        self.canvas.itemconfigure(self.window_id, width=event.width)

    # ---- Children row management ----
    def add_child_row(self):
        if len(self.child_rows) >= MAX_CHILDREN:
            messagebox.showwarning("Limit reached", f"Maximum children allowed is {MAX_CHILDREN}.")
            return

        r = len(self.child_rows)
        name_entry = ttk.Entry(self.rows_frame)
        age_entry = ttk.Entry(self.rows_frame)

        name_entry.grid(row=r, column=0, sticky="ew", pady=6, padx=(0, 10))
        age_entry.grid(row=r, column=1, sticky="ew", pady=6, padx=(0, 10))

        btn = ttk.Button(self.rows_frame, text="Remove", style="Ghost.TButton",
                         command=lambda idx=r: self.remove_child_row(idx))
        btn.grid(row=r, column=2, sticky="e", pady=6)

        self.child_rows.append({"name": name_entry, "age": age_entry, "btn": btn})

        # Autofocus first empty field
        if r == 0:
            name_entry.focus_set()

    def remove_child_row(self, index):
        if index < 0 or index >= len(self.child_rows):
            return

        row = self.child_rows[index]
        row["name"].destroy()
        row["age"].destroy()
        row["btn"].destroy()

        self.child_rows.pop(index)

        # Re-grid remaining rows (to keep layout tight)
        for i, row in enumerate(self.child_rows):
            row["name"].grid_configure(row=i)
            row["age"].grid_configure(row=i)
            row["btn"].grid_configure(row=i)
            row["btn"].configure(command=lambda idx=i: self.remove_child_row(idx))

    def clear_children(self):
        while self.child_rows:
            self.remove_child_row(0)

    def reset_all(self):
        self.parent_first.delete(0, "end")
        self.parent_last.delete(0, "end")
        self.clear_children()
        for _ in range(3):
            self.add_child_row()
        self._set_output("Fill in the form on the left and click “Compute Eldest”.")

    # ---- Validation + compute ----
    def compute(self):
        parent_first = self.parent_first.get().strip()
        parent_last = self.parent_last.get().strip()

        if not parent_first:
            messagebox.showerror("Input Error", "Parent First Name cannot be empty.")
            self.parent_first.focus_set()
            return
        if not parent_last:
            messagebox.showerror("Input Error", "Parent Last Name cannot be empty.")
            self.parent_last.focus_set()
            return

        children = []
        for i, row in enumerate(self.child_rows, start=1):
            cname = row["name"].get().strip()
            cage_raw = row["age"].get().strip()

            if not cname and not cage_raw:
                # allow completely blank row (user may have extra rows)
                continue

            if not cname:
                messagebox.showerror("Input Error", f"Child #{i} name cannot be empty.")
                row["name"].focus_set()
                return

            # Age validation
            try:
                cage = int(cage_raw)
            except ValueError:
                messagebox.showerror("Input Error", f"Child #{i} age must be a whole number.")
                row["age"].focus_set()
                return

            if cage <= 0 or cage > 130:
                messagebox.showerror("Input Error", f"Child #{i} age must be between 1 and 130.")
                row["age"].focus_set()
                return

            children.append((cname, cage))

        if not children:
            messagebox.showerror("Input Error", "Please enter at least one child (name + age).")
            return

        eldest_name, eldest_age = max(children, key=lambda x: x[1])

        # Build output similar to your sample, but supports any # of children
        lines = []
        lines.append(f"First Name: {parent_first}")
        lines.append(f"Last Name: {parent_last}")

        for idx, (cname, cage) in enumerate(children, start=1):
            lines.append(f"{idx} Entry - First Name of Child :  {cname}")
            lines.append(f"Age of Child: {cage}")

        lines.append("-" * 45)
        lines.append(f"Hi {parent_first}!")
        lines.append(f"The age of your eldest child is {eldest_age} ({eldest_name}).")

        self._set_output("\n".join(lines))


if __name__ == "__main__":
    app = ModernFamilyApp()
    app.mainloop()