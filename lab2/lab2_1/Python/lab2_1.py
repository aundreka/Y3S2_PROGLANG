import tkinter as tk
from tkinter import ttk, messagebox
from tkinter.font import Font

MAX_CHILDREN = 50


def bind_mousewheel(widget, on_scroll):
    """
    Bind mouse wheel scrolling to a widget (Windows/macOS + Linux).
    on_scroll(delta, platform) should scroll appropriately.
    """
    def _win_mac(e):
        # e.delta: Windows=120 steps, mac can be smaller
        on_scroll(e.delta, "winmac")
        return "break"

    def _linux_up(e):
        on_scroll(1, "linux_up")
        return "break"

    def _linux_down(e):
        on_scroll(-1, "linux_down")
        return "break"

    widget.bind("<MouseWheel>", _win_mac)     # Windows/macOS
    widget.bind("<Button-4>", _linux_up)      # Linux
    widget.bind("<Button-5>", _linux_down)    # Linux


class ModernFamilyApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Personal Information System")
        self.geometry("980x640")
        self.minsize(860, 560)

        style = ttk.Style(self)
        for theme in ("clam", "vista", "xpnative", "aqua", "alt", "default"):
            if theme in style.theme_names():
                style.theme_use(theme)
                break

        # --- Modern light theme ---
        self.bg = "#f5f7fb"
        self.card = "#ffffff"
        self.text = "#0f172a"
        self.muted = "#64748b"
        self.accent = "#2563eb"
        self.err = "#ef4444"
        self.border = "#e2e8f0"
        self.row_a = "#ffffff"
        self.row_b = "#f8fafc"

        self.configure(bg=self.bg)

        style.configure("App.TFrame", background=self.bg)
        style.configure("Card.TFrame", background=self.card)
        style.configure("Title.TLabel", background=self.bg, foreground=self.text, font=("Segoe UI", 18, "bold"))
        style.configure("Sub.TLabel", background=self.bg, foreground=self.muted, font=("Segoe UI", 10))
        style.configure("CardTitle.TLabel", background=self.card, foreground=self.text, font=("Segoe UI", 12, "bold"))
        style.configure("CardText.TLabel", background=self.card, foreground=self.muted, font=("Segoe UI", 10))
        style.configure(
            "Primary.TButton",
            font=("Segoe UI", 10, "bold"),
            padding=(16, 11),
            background=self.accent,
            foreground="#ffffff",
            borderwidth=0,
            relief="flat",
        )
        style.map(
            "Primary.TButton",
            background=[("active", "#1d4ed8"), ("pressed", "#1e40af")],
            foreground=[("disabled", "#cbd5e1")],
        )
        style.configure(
            "Ghost.TButton",
            font=("Segoe UI", 10, "bold"),
            padding=(12, 10),
            background="#eef2ff",
            foreground="#1e3a8a",
            borderwidth=0,
            relief="flat",
        )
        style.map(
            "Ghost.TButton",
            background=[("active", "#e0e7ff"), ("pressed", "#c7d2fe")],
            foreground=[("disabled", "#94a3b8")],
        )

        # Header
        top = ttk.Frame(self, style="App.TFrame")
        top.pack(fill="x", padx=18, pady=(16, 10))
        ttk.Label(top, text="Personal Information System", style="Title.TLabel").pack(anchor="w")
        ttk.Label(
            top,
            text="Modern responsive layout • scroll with mouse wheel (no visible scrollbars)",
            style="Sub.TLabel",
        ).pack(anchor="w", pady=(4, 0))

        # Paned layout: LEFT bigger than RIGHT
        paned = ttk.Panedwindow(self, orient="horizontal")
        paned.pack(fill="both", expand=True, padx=18, pady=(0, 16))

        self.left_wrap = ttk.Frame(paned, style="App.TFrame")
        self.right_wrap = ttk.Frame(paned, style="App.TFrame")
        paned.add(self.left_wrap, weight=3)
        paned.add(self.right_wrap, weight=2)

        # LEFT: whole form scrollable via mouse wheel (no scrollbar shown)
        self.form_canvas = tk.Canvas(self.left_wrap, bg=self.bg, highlightthickness=0)
        self.form_canvas.pack(fill="both", expand=True)

        self.form_inner = ttk.Frame(self.form_canvas, style="App.TFrame")
        self.form_window = self.form_canvas.create_window((0, 0), window=self.form_inner, anchor="nw")

        self.form_inner.bind("<Configure>", lambda e: self.form_canvas.configure(scrollregion=self.form_canvas.bbox("all")))
        self.form_canvas.bind("<Configure>", lambda e: self.form_canvas.itemconfigure(self.form_window, width=e.width))

        bind_mousewheel(self.form_canvas, self._scroll_form_canvas)
        bind_mousewheel(self.form_inner, self._scroll_form_canvas)

        # RIGHT: output card
        self._build_output_card(self.right_wrap)

        # Build form cards
        self._build_parent_card(self.form_inner)
        self._build_children_card(self.form_inner)

        # Start with 3 children rows
        for _ in range(3):
            self.add_child_row()

        # Global shortcuts
        self.bind("<Return>", self._global_enter)
        self.bind("<Shift-Return>", lambda e: self.add_child_row())
        self.bind("<Control-Return>", lambda e: self.compute())

        self.validate_all()
        self._clear_output()  # no instructions in output

    # ---------------- scrolling (no scrollbars visible) ----------------
    def _scroll_form_canvas(self, delta, platform):
        # Only scroll the form if mouse is over the left area
        x, y = self.winfo_pointerxy()
        widget_under_mouse = self.winfo_containing(x, y)
        if widget_under_mouse is None:
            return
        if not str(widget_under_mouse).startswith(str(self.left_wrap)):
            return

        if platform == "winmac":
            self.form_canvas.yview_scroll(int(-1 * (delta / 120)), "units")
        elif platform == "linux_up":
            self.form_canvas.yview_scroll(-3, "units")
        elif platform == "linux_down":
            self.form_canvas.yview_scroll(3, "units")

    def _scroll_children_canvas(self, delta, platform):
        # Only scroll children grid if mouse is over children canvas
        x, y = self.winfo_pointerxy()
        widget_under_mouse = self.winfo_containing(x, y)
        if widget_under_mouse is None:
            return
        if not str(widget_under_mouse).startswith(str(self.children_container)):
            return

        if platform == "winmac":
            self.child_canvas.yview_scroll(int(-1 * (delta / 120)), "units")
        elif platform == "linux_up":
            self.child_canvas.yview_scroll(-3, "units")
        elif platform == "linux_down":
            self.child_canvas.yview_scroll(3, "units")

    def _scroll_output_text(self, delta, platform):
        # Scroll output text without visible scrollbar
        x, y = self.winfo_pointerxy()
        widget_under_mouse = self.winfo_containing(x, y)
        if widget_under_mouse is None:
            return
        if widget_under_mouse != self.output and not str(widget_under_mouse).startswith(str(self.output)):
            return

        if platform == "winmac":
            self.output.yview_scroll(int(-1 * (delta / 120)), "units")
        elif platform == "linux_up":
            self.output.yview_scroll(-3, "units")
        elif platform == "linux_down":
            self.output.yview_scroll(3, "units")

    # ---------------- UI helpers ----------------
    def _card(self, parent, title):
        card = ttk.Frame(parent, style="Card.TFrame")
        card.pack(fill="x", pady=(0, 14))
        ttk.Label(card, text=title, style="CardTitle.TLabel").pack(anchor="w", padx=18, pady=(16, 10))
        return card

    def _make_entry(self, parent):
        return tk.Entry(
            parent,
            bg=self.card, fg=self.text, insertbackground=self.text,
            relief="flat",
            highlightthickness=2,
            highlightbackground=self.border,
            highlightcolor=self.accent,
            font=("Segoe UI", 11),
        )

    def _make_error_label(self, parent, bg=None):
        return tk.Label(parent, text="", fg=self.err, bg=(bg if bg else self.card), font=("Segoe UI", 9))

    def _set_entry_error(self, entry: tk.Entry, err_label: tk.Label | None, msg: str | None):
        if msg:
            entry.configure(highlightbackground=self.err)
            if err_label:
                err_label.configure(text=msg)
        else:
            entry.configure(highlightbackground=self.border)
            if err_label:
                err_label.configure(text="")

    # ---------------- Parent card ----------------
    def _build_parent_card(self, parent):
        card = self._card(parent, "Parent Information")

        grid = tk.Frame(card, bg=self.card)
        grid.pack(fill="x", padx=18, pady=(0, 16))
        grid.columnconfigure(0, weight=1)
        grid.columnconfigure(1, weight=1)

        tk.Label(grid, text="First Name", bg=self.card, fg=self.muted, font=("Segoe UI", 10)).grid(row=0, column=0, sticky="w")
        self.parent_first = self._make_entry(grid)
        self.parent_first.grid(row=1, column=0, sticky="ew", padx=(0, 12), pady=(6, 0))
        self.parent_first_err = self._make_error_label(grid)
        self.parent_first_err.grid(row=2, column=0, sticky="w", pady=(4, 0))

        tk.Label(grid, text="Last Name", bg=self.card, fg=self.muted, font=("Segoe UI", 10)).grid(row=0, column=1, sticky="w")
        self.parent_last = self._make_entry(grid)
        self.parent_last.grid(row=1, column=1, sticky="ew", pady=(6, 0))
        self.parent_last_err = self._make_error_label(grid)
        self.parent_last_err.grid(row=2, column=1, sticky="w", pady=(4, 0))

        for w in (self.parent_first, self.parent_last):
            w.bind("<KeyRelease>", lambda _e: self.validate_all())
            w.bind("<FocusOut>", lambda _e: self.validate_all())

        self.parent_first.bind("<Return>", lambda e: (self.parent_last.focus_set(), "break")[-1])
        self.parent_last.bind("<Return>", lambda e: (self._focus_first_child_or_add(), "break")[-1])

    def _focus_first_child_or_add(self):
        if self.child_rows:
            self.child_rows[0]["name"].focus_set()
        else:
            self.add_child_row()
            self.child_rows[0]["name"].focus_set()

    # ---------------- Children card ----------------
    def _build_children_card(self, parent):
        card = self._card(parent, "Children Information")

        bar = tk.Frame(card, bg=self.card)
        bar.pack(fill="x", padx=18, pady=(0, 10))
        tk.Label(bar, text="Tip: Press enter to see output",
                 bg=self.card, fg=self.muted, font=("Segoe UI", 10)).pack(side="left")

        btns = tk.Frame(bar, bg=self.card)
        btns.pack(side="right")
        ttk.Button(btns, text="+ Add Child", style="Ghost.TButton", command=self.add_child_row).pack(side="left", padx=(0, 8))
        ttk.Button(btns, text="Clear", style="Ghost.TButton", command=self.clear_children).pack(side="left")

        header = tk.Frame(card, bg=self.card)
        header.pack(fill="x", padx=18, pady=(8, 6))
        header.columnconfigure(0, weight=0)
        header.columnconfigure(1, weight=3)
        header.columnconfigure(2, weight=1)
        header.columnconfigure(3, weight=0)

        tk.Label(header, text="#", bg=self.card, fg=self.muted, font=("Segoe UI", 10, "bold")).grid(row=0, column=0, sticky="w", padx=(8, 12))
        tk.Label(header, text="Child First Name", bg=self.card, fg=self.muted, font=("Segoe UI", 10, "bold")).grid(row=0, column=1, sticky="w")
        tk.Label(header, text="Age", bg=self.card, fg=self.muted, font=("Segoe UI", 10, "bold")).grid(row=0, column=2, sticky="w", padx=(10, 0))

        # Children rows canvas (no visible scrollbar)
        self.children_container = tk.Frame(card, bg=self.card)
        self.children_container.pack(fill="both", expand=True, padx=18, pady=(0, 12))
        self.children_container.rowconfigure(0, weight=1)
        self.children_container.columnconfigure(0, weight=1)

        self.child_canvas = tk.Canvas(self.children_container, bg=self.card, highlightthickness=0)
        self.child_canvas.grid(row=0, column=0, sticky="nsew")

        self.rows_frame = tk.Frame(self.child_canvas, bg=self.card)
        self.rows_window = self.child_canvas.create_window((0, 0), window=self.rows_frame, anchor="nw")

        self.rows_frame.bind("<Configure>", lambda e: self.child_canvas.configure(scrollregion=self.child_canvas.bbox("all")))
        self.child_canvas.bind("<Configure>", lambda e: self.child_canvas.itemconfigure(self.rows_window, width=e.width))

        bind_mousewheel(self.child_canvas, self._scroll_children_canvas)
        bind_mousewheel(self.rows_frame, self._scroll_children_canvas)

        footer = tk.Frame(card, bg=self.card)
        footer.pack(fill="x", padx=18, pady=(0, 16))
        ttk.Button(footer, text="Compute Eldest", style="Primary.TButton", command=self.compute).pack(side="left")
        ttk.Button(footer, text="Reset All", style="Ghost.TButton", command=self.reset_all).pack(side="left", padx=(10, 0))

        self.child_rows = []

    def add_child_row(self):
        if len(self.child_rows) >= MAX_CHILDREN:
            messagebox.showwarning("Limit reached", f"Maximum children allowed is {MAX_CHILDREN}.")
            return

        i = len(self.child_rows)
        stripe_bg = self.row_a if i % 2 == 0 else self.row_b

        row = tk.Frame(self.rows_frame, bg=stripe_bg)
        row.grid(row=i, column=0, sticky="ew", pady=4)
        row.columnconfigure(0, weight=0)
        row.columnconfigure(1, weight=3)
        row.columnconfigure(2, weight=1)
        row.columnconfigure(3, weight=0)

        idx = tk.Label(row, text=str(i + 1), fg=self.muted, bg=stripe_bg, font=("Segoe UI", 10))
        idx.grid(row=0, column=0, sticky="w", padx=(8, 12), pady=(8, 0))

        name_entry = self._make_entry(row)
        name_entry.grid(row=0, column=1, sticky="ew", padx=(0, 10), pady=(6, 0))
        name_err = self._make_error_label(row, bg=stripe_bg)
        name_err.grid(row=1, column=1, sticky="w", padx=(0, 10), pady=(4, 8))

        age_entry = self._make_entry(row)
        age_entry.grid(row=0, column=2, sticky="ew", padx=(0, 10), pady=(6, 0))
        age_err = self._make_error_label(row, bg=stripe_bg)
        age_err.grid(row=1, column=2, sticky="w", padx=(0, 10), pady=(4, 8))

        btn = ttk.Button(row, text="Remove", style="Ghost.TButton", command=lambda idx=i: self.remove_child_row(idx))
        btn.grid(row=0, column=3, sticky="e", padx=(0, 8), pady=(6, 0))
        tk.Label(row, text="", bg=stripe_bg).grid(row=1, column=3, pady=(4, 8))

        row_obj = {
            "frame": row, "idx": idx,
            "name": name_entry, "age": age_entry,
            "name_err": name_err, "age_err": age_err,
            "btn": btn
        }
        self.child_rows.append(row_obj)

        for w in (name_entry, age_entry):
            w.bind("<KeyRelease>", lambda _e: self.validate_all())
            w.bind("<FocusOut>", lambda _e: self.validate_all())

        # Enter navigation
        name_entry.bind("<Return>", lambda e, r=i: self._return_nav_child(e, r, "name"))
        age_entry.bind("<Return>", lambda e, r=i: self._return_nav_child(e, r, "age"))

        # Arrow navigation
        for keysym in ("Up", "Down", "Left", "Right"):
            name_entry.bind(f"<{keysym}>", lambda e, r=i: self._arrow_nav(e, r, "name"))
            age_entry.bind(f"<{keysym}>", lambda e, r=i: self._arrow_nav(e, r, "age"))

        self.validate_all()

    def remove_child_row(self, index):
        if not (0 <= index < len(self.child_rows)):
            return
        self.child_rows[index]["frame"].destroy()
        self.child_rows.pop(index)
        self._reindex_children()
        self.validate_all()

    def _reindex_children(self):
        for i, row in enumerate(self.child_rows):
            stripe_bg = self.row_a if i % 2 == 0 else self.row_b
            row["frame"].grid_configure(row=i)
            row["frame"].configure(bg=stripe_bg)
            row["idx"].configure(text=str(i + 1), bg=stripe_bg)
            row["name_err"].configure(bg=stripe_bg)
            row["age_err"].configure(bg=stripe_bg)
            row["btn"].configure(command=lambda idx=i: self.remove_child_row(idx))

            row["name"].unbind("<Return>")
            row["age"].unbind("<Return>")
            row["name"].bind("<Return>", lambda e, r=i: self._return_nav_child(e, r, "name"))
            row["age"].bind("<Return>", lambda e, r=i: self._return_nav_child(e, r, "age"))

            for keysym in ("Up", "Down", "Left", "Right"):
                row["name"].unbind(f"<{keysym}>")
                row["age"].unbind(f"<{keysym}>")
                row["name"].bind(f"<{keysym}>", lambda e, r=i: self._arrow_nav(e, r, "name"))
                row["age"].bind(f"<{keysym}>", lambda e, r=i: self._arrow_nav(e, r, "age"))

    def clear_children(self):
        while self.child_rows:
            self.child_rows[0]["frame"].destroy()
            self.child_rows.pop(0)
        self.validate_all()

    def reset_all(self):
        self.parent_first.delete(0, "end")
        self.parent_last.delete(0, "end")
        self.clear_children()
        for _ in range(3):
            self.add_child_row()
        self._clear_output()
        self.validate_all()
        self.parent_first.focus_set()

    # ---------------- Output (prettier, no instructions) ----------------
    def _build_output_card(self, parent):
        parent.rowconfigure(0, weight=1)
        parent.columnconfigure(0, weight=1)

        card = ttk.Frame(parent, style="Card.TFrame")
        card.grid(row=0, column=0, sticky="nsew")

        header = tk.Frame(card, bg=self.card)
        header.pack(fill="x", padx=18, pady=(16, 10))

        tk.Label(header, text="Output", bg=self.card, fg=self.text, font=("Segoe UI", 12, "bold")).pack(side="left")

        # A small "pill" badge that updates (e.g., Valid / Needs Fix)
        self.status_pill = tk.Label(
            header, text="",
            bg="#e2e8f0", fg=self.muted,
            font=("Segoe UI", 9, "bold"),
            padx=10, pady=4
        )
        self.status_pill.pack(side="right")

        # Output text box (no scrollbar; mouse wheel still works)
        self.output = tk.Text(
            card,
            wrap="word",
            font=("Consolas", 11),
            bg=self.card,
            fg=self.text,
            relief="flat",
            highlightthickness=1,
            highlightbackground=self.border,
            padx=14, pady=12
        )
        self.output.pack(fill="both", expand=True, padx=18, pady=(0, 16))
        self.output.configure(state="disabled")

        # Tags for prettier formatting
        self.output.tag_configure("h1", font=("Segoe UI", 14, "bold"), foreground=self.text)
        self.output.tag_configure("label", font=("Segoe UI", 10, "bold"), foreground=self.muted)
        self.output.tag_configure("mono", font=("Consolas", 11), foreground=self.text)
        self.output.tag_configure("muted", font=("Consolas", 10), foreground=self.muted)
        self.output.tag_configure("good", font=("Segoe UI", 12, "bold"), foreground=self.accent)

        bind_mousewheel(self.output, self._scroll_output_text)

    def _clear_output(self):
        self.output.configure(state="normal")
        self.output.delete("1.0", "end")
        self.output.configure(state="disabled")
        self._set_status("Waiting", ok=None)

    def _set_status(self, text, ok=None):
        if ok is True:
            self.status_pill.configure(text=text, bg="#dcfce7", fg="#166534")  # green-ish
        elif ok is False:
            self.status_pill.configure(text=text, bg="#fee2e2", fg="#991b1b")  # red-ish
        else:
            self.status_pill.configure(text=text, bg="#e2e8f0", fg=self.muted)

    def _set_output_pretty(self, parent_first, parent_last, children, eldest_name, eldest_age):
        self.output.configure(state="normal")
        self.output.delete("1.0", "end")

        # Title line
        self.output.insert("end", "Summary\n", "h1")
        self.output.insert("end", "\n", "mono")

        # Parent block
        self.output.insert("end", "Parent\n", "label")
        self.output.insert("end", f"First Name: {parent_first}\n", "mono")
        self.output.insert("end", f"Last Name:  {parent_last}\n", "mono")
        self.output.insert("end", "\n", "mono")

        # Children block
        self.output.insert("end", "Children\n", "label")
        for i, (name, age) in enumerate(children, start=1):
            self.output.insert("end", f"{i}. {name}  ", "mono")
            self.output.insert("end", f"(Age {age})\n", "muted")

        self.output.insert("end", "\n", "mono")
        self.output.insert("end", "—" * 38 + "\n\n", "muted")

        # Result
        self.output.insert("end", f"Hi {parent_first}! ", "mono")
        self.output.insert("end", f"Eldest child: {eldest_age} ({eldest_name})\n", "good")

        self.output.configure(state="disabled")

    # ---------------- Navigation / shortcuts ----------------
    def _global_enter(self, _event):
        w = self.focus_get()
        if isinstance(w, tk.Entry):
            return  # let field-specific Return handlers run
        self.compute()

    def _move_focus_child(self, row_index: int, col: str):
        if 0 <= row_index < len(self.child_rows):
            self.child_rows[row_index][col].focus_set()

    def _arrow_nav(self, event, row_index: int, col: str):
        if event.keysym == "Up":
            self._move_focus_child(max(0, row_index - 1), col)
            return "break"
        if event.keysym == "Down":
            self._move_focus_child(min(len(self.child_rows) - 1, row_index + 1), col)
            return "break"
        if event.keysym == "Left":
            self._move_focus_child(row_index, "name")
            return "break"
        if event.keysym == "Right":
            self._move_focus_child(row_index, "age")
            return "break"
        return None

    def _return_nav_child(self, _event, row_index: int, col: str):
        self.validate_all()

        if col == "name":
            self._move_focus_child(row_index, "age")
            return "break"

        # col == "age"
        if row_index == len(self.child_rows) - 1:
            if self.validate_all():
                self.compute()
                return "break"
            self.add_child_row()
            self._move_focus_child(row_index + 1, "name")
            return "break"
        else:
            self._move_focus_child(row_index + 1, "name")
            return "break"

    # ---------------- Validation + compute ----------------
    def validate_all(self) -> bool:
        ok = True

        pf = self.parent_first.get().strip()
        pl = self.parent_last.get().strip()
        self._set_entry_error(self.parent_first, self.parent_first_err, None if pf else "Required")
        self._set_entry_error(self.parent_last, self.parent_last_err, None if pl else "Required")
        if not pf or not pl:
            ok = False

        any_child = False
        for row in self.child_rows:
            cname = row["name"].get().strip()
            cage_raw = row["age"].get().strip()

            # allow blank row
            if cname == "" and cage_raw == "":
                self._set_entry_error(row["name"], row["name_err"], None)
                self._set_entry_error(row["age"], row["age_err"], None)
                continue

            any_child = True

            self._set_entry_error(row["name"], row["name_err"], None if cname else "Name required")
            if not cname:
                ok = False

            age_msg = None
            if cage_raw == "":
                age_msg = "Age required"
            else:
                try:
                    age_val = int(cage_raw)
                    if age_val < 1 or age_val > 130:
                        age_msg = "1–130 only"
                except ValueError:
                    age_msg = "Whole number"

            self._set_entry_error(row["age"], row["age_err"], age_msg)
            if age_msg:
                ok = False

        if not any_child:
            ok = False

        # Update pill status live (nice UI touch)
        if ok:
            self._set_status("Ready", ok=True)
        else:
            self._set_status("Needs Fix", ok=False)

        return ok

    def compute(self):
        if not self.validate_all():
            messagebox.showerror("Fix inputs", "Please fix the highlighted fields (red border) before computing.")
            return

        parent_first = self.parent_first.get().strip()
        parent_last = self.parent_last.get().strip()

        children = []
        for row in self.child_rows:
            cname = row["name"].get().strip()
            cage_raw = row["age"].get().strip()
            if cname == "" and cage_raw == "":
                continue
            children.append((cname, int(cage_raw)))

        if not children:
            messagebox.showerror("Input Error", "Please enter at least one child (name + age).")
            return

        eldest_name, eldest_age = max(children, key=lambda x: x[1])
        self._set_output_pretty(parent_first, parent_last, children, eldest_name, eldest_age)
        self._set_status("Computed", ok=True)


if __name__ == "__main__":
    app = ModernFamilyApp()
    app.mainloop()
