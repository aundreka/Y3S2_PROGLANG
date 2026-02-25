"""
CSCN09C Grade Collector (Tkinter • Google-Forms-ish UI)

What it does:
- Collects Instructor First/Last Name
- Collects 10 students (Last Name + Grade)
- Validates inputs (required fields, grade 0..100, name characters)
- Shows a pretty summary (instructor last name + lowest grade)

UI details:
- “Google Forms” vibe: clean header, cards, spacing, subtle colors
- No visible scrollbars
- Mousewheel scrolling works (Windows/macOS/Linux)
"""

import tkinter as tk
from tkinter import ttk
import re

STUDENTS = 10

NAME_RE = re.compile(r"^[A-Za-z][A-Za-z\s'\-]*$")  # letters + spaces + ' + -

def is_valid_name(s: str) -> bool:
    s = s.strip()
    if not s:
        return False
    # Must contain at least one letter; only allow letters/spaces/'/-
    return bool(NAME_RE.match(s)) and any(c.isalpha() for c in s)

def is_valid_grade(s: str) -> bool:
    s = s.strip()
    if not s.isdigit():
        return False
    v = int(s)
    return 0 <= v <= 100

class NoScrollbarScrollFrame(ttk.Frame):
    """
    Scrollable frame using a Canvas, but with NO visible scrollbars.
    Mousewheel scroll is enabled and robust across platforms.
    """
    def __init__(self, master, **kwargs):
        super().__init__(master, **kwargs)

        self.canvas = tk.Canvas(self, highlightthickness=0, bd=0)
        self.canvas.pack(side="left", fill="both", expand=True)

        self.inner = ttk.Frame(self.canvas)
        self.window_id = self.canvas.create_window((0, 0), window=self.inner, anchor="nw")

        self.inner.bind("<Configure>", self._on_inner_configure)
        self.canvas.bind("<Configure>", self._on_canvas_configure)

        # Mousewheel binds (Windows/macOS)
        self.canvas.bind_all("<MouseWheel>", self._on_mousewheel, add="+")
        # Linux (X11)
        self.canvas.bind_all("<Button-4>", self._on_mousewheel_linux, add="+")
        self.canvas.bind_all("<Button-5>", self._on_mousewheel_linux, add="+")
        # Trackpad (some macOS setups)
        self.canvas.bind_all("<Shift-MouseWheel>", self._on_mousewheel, add="+")

    def _on_inner_configure(self, _event=None):
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))

    def _on_canvas_configure(self, event):
        # Keep inner frame width synced with canvas width (so it looks like a form column)
        self.canvas.itemconfigure(self.window_id, width=event.width)

    def _on_mousewheel(self, event):
        # Only scroll if cursor is inside this widget (prevents affecting other windows)
        if not self._mouse_is_over_canvas():
            return

        delta = event.delta
        if delta == 0:
            return
        # Normalize
        step = -1 if delta > 0 else 1
        self.canvas.yview_scroll(step * 3, "units")

    def _on_mousewheel_linux(self, event):
        if not self._mouse_is_over_canvas():
            return
        if event.num == 4:
            self.canvas.yview_scroll(-3, "units")
        elif event.num == 5:
            self.canvas.yview_scroll(3, "units")

    def _mouse_is_over_canvas(self) -> bool:
        # Check if current mouse pointer is over the canvas area
        x_root, y_root = self.winfo_pointerx(), self.winfo_pointery()
        x = self.canvas.winfo_rootx()
        y = self.canvas.winfo_rooty()
        return (x <= x_root <= x + self.canvas.winfo_width() and
                y <= y_root <= y + self.canvas.winfo_height())

class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("CSCN09C Grade Entry Form")
        self.geometry("900x700")
        self.minsize(820, 640)

        self._setup_style()

        # Background
        self.configure(bg="#F6F8FC")

        # Top header (Google Forms-ish)
        header = ttk.Frame(self, style="Header.TFrame", padding=(24, 18))
        header.pack(fill="x", padx=18, pady=(18, 10))

        ttk.Label(header, text="CSCN09C", style="HeaderTitle.TLabel").pack(anchor="w")
        ttk.Label(
            header,
            text="Grade Entry Form • 10 students required",
            style="HeaderSub.TLabel"
        ).pack(anchor="w", pady=(4, 0))

        # Scroll area without scrollbars
        body = ttk.Frame(self, style="Bg.TFrame")
        body.pack(fill="both", expand=True, padx=18, pady=(0, 18))

        self.scroll = NoScrollbarScrollFrame(body, style="Bg.TFrame")
        self.scroll.pack(fill="both", expand=True)

        # Form column container
        self.column = ttk.Frame(self.scroll.inner, style="Bg.TFrame")
        self.column.pack(fill="x", expand=True, padx=0, pady=0)

        # Build sections/cards
        self.instr_first_var = tk.StringVar()
        self.instr_last_var = tk.StringVar()

        self.student_last_vars = [tk.StringVar() for _ in range(STUDENTS)]
        self.student_grade_vars = [tk.StringVar() for _ in range(STUDENTS)]

        self._build_instructor_card()
        self._build_students_card()
        self._build_actions_card()

        # Error banner (hidden until needed)
        self.error_text = tk.StringVar()
        self.error_banner = ttk.Label(
            self.column,
            textvariable=self.error_text,
            style="Error.TLabel",
            padding=(14, 10)
        )
        self.error_banner.pack(fill="x", pady=(10, 0))
        self.error_banner.pack_forget()

    def _setup_style(self):
        style = ttk.Style(self)
        # Use a modern-ish theme if available
        try:
            style.theme_use("clam")
        except Exception:
            pass

        # Base font sizes
        default_font = ("Segoe UI", 11)
        title_font = ("Segoe UI", 18, "bold")
        subtitle_font = ("Segoe UI", 11)
        card_title_font = ("Segoe UI", 12, "bold")

        self.option_add("*Font", default_font)

        # Frames
        style.configure("Bg.TFrame", background="#F6F8FC")
        style.configure("Header.TFrame", background="#FFFFFF")
        style.configure("Card.TFrame", background="#FFFFFF")
        style.configure("CardInner.TFrame", background="#FFFFFF")

        # Labels
        style.configure("HeaderTitle.TLabel", background="#FFFFFF", foreground="#1A73E8", font=title_font)
        style.configure("HeaderSub.TLabel", background="#FFFFFF", foreground="#5F6368", font=subtitle_font)
        style.configure("CardTitle.TLabel", background="#FFFFFF", foreground="#202124", font=card_title_font)
        style.configure("Label.TLabel", background="#FFFFFF", foreground="#3C4043")
        style.configure("Hint.TLabel", background="#FFFFFF", foreground="#5F6368", font=("Segoe UI", 10))
        style.configure("Error.TLabel", background="#FCE8E6", foreground="#C5221F", font=("Segoe UI", 10, "bold"))

        # Entry
        style.configure("Modern.TEntry", padding=(10, 10))
        style.map("Modern.TEntry",
                  fieldbackground=[("!disabled", "#FFFFFF")],
                  foreground=[("!disabled", "#202124")])

        # Buttons
        style.configure("Primary.TButton", padding=(14, 10), font=("Segoe UI", 11, "bold"))
        style.configure("Ghost.TButton", padding=(14, 10))
        style.map("Primary.TButton",
                  foreground=[("!disabled", "#FFFFFF")],
                  background=[("!disabled", "#1A73E8"), ("active", "#1967D2")])
        style.map("Ghost.TButton",
                  foreground=[("!disabled", "#1A73E8")])

        # Make ttk buttons actually show background in clam theme
        style.configure("Primary.TButton", relief="flat")
        style.configure("Ghost.TButton", relief="flat")

    def _card(self, title: str, subtitle: str | None = None):
        outer = ttk.Frame(self.column, style="Card.TFrame", padding=0)
        outer.pack(fill="x", pady=10)

        # faux “card border” via canvas rectangle shadow-ish using padding frame
        inner = ttk.Frame(outer, style="CardInner.TFrame", padding=(18, 16))
        inner.pack(fill="x")

        ttk.Label(inner, text=title, style="CardTitle.TLabel").pack(anchor="w")
        if subtitle:
            ttk.Label(inner, text=subtitle, style="Hint.TLabel").pack(anchor="w", pady=(4, 0))

        return inner

    def _labeled_entry(self, parent, label, var: tk.StringVar, placeholder="", width=34):
        row = ttk.Frame(parent, style="CardInner.TFrame")
        row.pack(fill="x", pady=(12, 0))

        ttk.Label(row, text=label, style="Label.TLabel").pack(anchor="w")

        entry = ttk.Entry(row, textvariable=var, style="Modern.TEntry", width=width)
        entry.pack(fill="x", pady=(6, 0))

        if placeholder:
            # simple placeholder behavior
            self._apply_placeholder(entry, var, placeholder)

        return entry

    def _apply_placeholder(self, entry: ttk.Entry, var: tk.StringVar, placeholder: str):
        def on_focus_in(_):
            if var.get() == placeholder:
                var.set("")
                entry.configure(foreground="#202124")

        def on_focus_out(_):
            if not var.get().strip():
                var.set(placeholder)
                entry.configure(foreground="#9AA0A6")

        # Init
        if not var.get().strip():
            var.set(placeholder)
            entry.configure(foreground="#9AA0A6")

        entry.bind("<FocusIn>", on_focus_in)
        entry.bind("<FocusOut>", on_focus_out)

    def _build_instructor_card(self):
        card = self._card("Instructor Information", "Required fields • Use letters, spaces, - or '")
        grid = ttk.Frame(card, style="CardInner.TFrame")
        grid.pack(fill="x", pady=(10, 0))

        grid.columnconfigure(0, weight=1)
        grid.columnconfigure(1, weight=1)

        left = ttk.Frame(grid, style="CardInner.TFrame")
        right = ttk.Frame(grid, style="CardInner.TFrame")
        left.grid(row=0, column=0, sticky="ew", padx=(0, 10))
        right.grid(row=0, column=1, sticky="ew", padx=(10, 0))

        self._labeled_entry(left, "Instructor First Name", self.instr_first_var, placeholder="e.g., Maricris")
        self._labeled_entry(right, "Instructor Last Name", self.instr_last_var, placeholder="e.g., Mojica")

    def _build_students_card(self):
        card = self._card("Student Grades", "Enter exactly 10 students • Grades must be 0–100")

        for i in range(STUDENTS):
            block = ttk.Frame(card, style="CardInner.TFrame")
            block.pack(fill="x", pady=(14 if i == 0 else 10, 0))

            ttk.Label(block, text=f"Entry {i+1}", style="Hint.TLabel").pack(anchor="w")

            grid = ttk.Frame(block, style="CardInner.TFrame")
            grid.pack(fill="x", pady=(6, 0))
            grid.columnconfigure(0, weight=3)
            grid.columnconfigure(1, weight=1)

            # Student last name
            last_frame = ttk.Frame(grid, style="CardInner.TFrame")
            last_frame.grid(row=0, column=0, sticky="ew", padx=(0, 10))
            ttk.Label(last_frame, text="Student Last Name", style="Label.TLabel").pack(anchor="w")
            e_last = ttk.Entry(last_frame, textvariable=self.student_last_vars[i], style="Modern.TEntry")
            e_last.pack(fill="x", pady=(6, 0))

            # Grade
            grade_frame = ttk.Frame(grid, style="CardInner.TFrame")
            grade_frame.grid(row=0, column=1, sticky="ew", padx=(10, 0))
            ttk.Label(grade_frame, text="CSCN09C Grade", style="Label.TLabel").pack(anchor="w")
            e_grade = ttk.Entry(grade_frame, textvariable=self.student_grade_vars[i], style="Modern.TEntry", width=8)
            e_grade.pack(fill="x", pady=(6, 0))

            # Live validation hint coloring
            self._attach_live_validation(e_last, self.student_last_vars[i], kind="name")
            self._attach_live_validation(e_grade, self.student_grade_vars[i], kind="grade")

        # Also attach live validation to instructor fields
        # (we do this after card build so we can find those entries easily if needed)
        # We'll just validate on submit for instructor fields, but student fields live is already set.

    def _attach_live_validation(self, entry: ttk.Entry, var: tk.StringVar, kind: str):
        def on_change(*_):
            s = var.get().strip()
            if not s:
                entry.configure(foreground="#202124")
                return
            ok = is_valid_name(s) if kind == "name" else is_valid_grade(s)
            entry.configure(foreground=("#202124" if ok else "#C5221F"))

        var.trace_add("write", on_change)

    def _build_actions_card(self):
        card = self._card("Submit", "Review your entries before submitting.")
        btn_row = ttk.Frame(card, style="CardInner.TFrame")
        btn_row.pack(fill="x", pady=(14, 0))

        submit_btn = ttk.Button(btn_row, text="Submit", style="Primary.TButton", command=self.on_submit)
        submit_btn.pack(side="right")

        clear_btn = ttk.Button(btn_row, text="Clear All", style="Ghost.TButton", command=self.on_clear)
        clear_btn.pack(side="right", padx=(0, 10))

    def show_error(self, msg: str):
        self.error_text.set("⚠ " + msg)
        self.error_banner.pack(fill="x", pady=(10, 0))

        # Scroll to top so the user sees the banner
        self.scroll.canvas.yview_moveto(0)

    def hide_error(self):
        self.error_banner.pack_forget()
        self.error_text.set("")

    def on_clear(self):
        self.hide_error()
        self.instr_first_var.set("")
        self.instr_last_var.set("")
        for i in range(STUDENTS):
            self.student_last_vars[i].set("")
            self.student_grade_vars[i].set("")

    def on_submit(self):
        self.hide_error()

        # Instructor validation (handle placeholder text if user never edited)
        instr_first = self.instr_first_var.get().strip()
        instr_last = self.instr_last_var.get().strip()

        # If placeholders were used, they begin with "e.g.," — treat those as invalid
        if instr_first.lower().startswith("e.g"):
            instr_first = ""
        if instr_last.lower().startswith("e.g"):
            instr_last = ""

        if not is_valid_name(instr_first):
            self.show_error("Instructor First Name is required (letters, spaces, - or ').")
            return
        if not is_valid_name(instr_last):
            self.show_error("Instructor Last Name is required (letters, spaces, - or ').")
            return

        # Student validation
        lowest = None
        lowest_name = None

        for i in range(STUDENTS):
            last = self.student_last_vars[i].get().strip()
            grade_s = self.student_grade_vars[i].get().strip()

            if not is_valid_name(last):
                self.show_error(f"Entry {i+1}: Student Last Name is invalid or missing.")
                return
            if not is_valid_grade(grade_s):
                self.show_error(f"Entry {i+1}: Grade must be a whole number from 0 to 100.")
                return

            grade = int(grade_s)
            if lowest is None or grade < lowest:
                lowest = grade
                lowest_name = last

        # Show pretty output
        self._show_summary(instr_first, instr_last, lowest, lowest_name)

    def _show_summary(self, instr_first: str, instr_last: str, lowest: int, lowest_name: str):
        top = tk.Toplevel(self)
        top.title("Submission Summary")
        top.geometry("700x520")
        top.configure(bg="#F6F8FC")
        top.transient(self)
        top.grab_set()

        # Header
        hdr = ttk.Frame(top, style="Header.TFrame", padding=(24, 18))
        hdr.pack(fill="x", padx=18, pady=(18, 10))

        ttk.Label(hdr, text="Submitted ✅", style="HeaderTitle.TLabel").pack(anchor="w")
        ttk.Label(hdr, text="Here’s a clean summary of your entries.", style="HeaderSub.TLabel").pack(anchor="w", pady=(4, 0))

        # Summary card
        card = ttk.Frame(top, style="Card.TFrame")
        card.pack(fill="both", expand=True, padx=18, pady=(0, 18))

        inner = ttk.Frame(card, style="CardInner.TFrame", padding=(18, 16))
        inner.pack(fill="both", expand=True)

        ttk.Label(inner, text="Instructor", style="CardTitle.TLabel").pack(anchor="w")
        ttk.Label(inner, text=f"{instr_first} {instr_last}", style="Label.TLabel").pack(anchor="w", pady=(6, 0))

        ttk.Separator(inner).pack(fill="x", pady=18)

        ttk.Label(inner, text="Result", style="CardTitle.TLabel").pack(anchor="w")

        # Pretty “result” block
        result = ttk.Frame(inner, style="CardInner.TFrame", padding=(14, 12))
        result.pack(fill="x", pady=(10, 0))

        ttk.Label(result, text=f"Instructor Last Name: {instr_last}", style="Label.TLabel").pack(anchor="w")
        ttk.Label(result, text=f"Lowest Grade: {lowest}", style="Label.TLabel").pack(anchor="w", pady=(6, 0))
        ttk.Label(result, text=f"(Lowest belongs to: {lowest_name})", style="Hint.TLabel").pack(anchor="w", pady=(2, 0))

        ttk.Separator(inner).pack(fill="x", pady=18)

        # “Pretty output” text like your sample
        sample = tk.Text(inner, height=7, bd=0, highlightthickness=0, wrap="word")
        sample.pack(fill="both", expand=True)
        sample.configure(font=("Consolas", 11), background="#FFFFFF", foreground="#202124")
        sample.insert("end", f"Instructor Name: {instr_first} {instr_last}\n")
        sample.insert("end", f"The lowest grade of your students is {lowest}.\n")
        sample.insert("end", "\n(Collected 10 student entries successfully.)")
        sample.configure(state="disabled")

        btns = ttk.Frame(inner, style="CardInner.TFrame")
        btns.pack(fill="x", pady=(14, 0))

        ttk.Button(btns, text="Close", style="Primary.TButton", command=top.destroy).pack(side="right")

def main():
    app = App()
    app.mainloop()

if __name__ == "__main__":
    main()