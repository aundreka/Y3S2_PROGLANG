import tkinter as tk
from tkinter import messagebox

BG = "#f5f7fb"
CARD_BG = "#ffffff"
TEXT = "#202124"
MUTED = "#5f6368"
BORDER = "#dadce0"
PRIMARY = "#1a73e8"
PRIMARY_HOVER = "#1666d3"
DANGER = "#d93025"
OK = "#188038"

FONT = ("Segoe UI", 11)
FONT_B = ("Segoe UI", 11, "bold")
TITLE_FONT = ("Segoe UI", 16, "bold")


def _is_int_token(tok: str) -> bool:
    if tok.startswith("-"):
        return tok[1:].isdigit() and len(tok) > 1
    return tok.isdigit()


def format_int_with_commas(tok: str) -> str:
    raw = tok.replace(",", "")
    if not _is_int_token(raw):
        return tok  
    n = int(raw)
    return f"{n:,}"


def parse_two_ints(s: str):
    """
    Parse exactly 2 integers from a string like: "12 6" or "1,000 -2,500"
    Returns (a, b) as ints or raises ValueError with a friendly message.
    """
    parts = s.strip().split()
    if len(parts) != 2:
        raise ValueError("Please enter exactly TWO numbers separated by space.")
    p0 = parts[0].replace(",", "")
    p1 = parts[1].replace(",", "")
    if not _is_int_token(p0) or not _is_int_token(p1):
        raise ValueError("Numbers must be integers (e.g., 12 6).")
    return int(p0), int(p1)


class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Product & Average Calculator")
        self.configure(bg=BG)
        self.resizable(False, False)

        self.card = tk.Frame(self, bg=CARD_BG, highlightthickness=1, highlightbackground=BORDER)
        self.card.pack(padx=20, pady=20)

        tk.Label(self.card, text="Compute Product & Average", bg=CARD_BG, fg=TEXT, font=TITLE_FONT)\
            .grid(row=0, column=0, columnspan=2, sticky="w", padx=18, pady=(16, 2))

        tk.Label(self.card, text="Enter two numbers per line (space-separated).",
                 bg=CARD_BG, fg=MUTED, font=FONT)\
            .grid(row=1, column=0, columnspan=2, sticky="w", padx=18, pady=(0, 14))

        # Input 1
        self._field_label("1st two numbers", row=2)
        self.in1, self.in1_placeholder = self._entry(row=3, placeholder="e.g., 12 6")
        self.msg1 = tk.Label(self.card, text="", bg=CARD_BG, fg=DANGER, font=("Segoe UI", 10))
        self.msg1.grid(row=4, column=0, columnspan=2, sticky="w", padx=18, pady=(0, 8))

        # Input 2
        self._field_label("Last two numbers", row=5)
        self.in2, self.in2_placeholder = self._entry(row=6, placeholder="e.g., 7 9")
        self.msg2 = tk.Label(self.card, text="", bg=CARD_BG, fg=DANGER, font=("Segoe UI", 10))
        self.msg2.grid(row=7, column=0, columnspan=2, sticky="w", padx=18, pady=(0, 8))


        # Buttons
        btn_row = tk.Frame(self.card, bg=CARD_BG)
        btn_row.grid(row=9, column=0, columnspan=2, sticky="ew", padx=18, pady=(6, 10))
        btn_row.grid_columnconfigure(0, weight=1)
        btn_row.grid_columnconfigure(1, weight=1)

        self.compute_btn = self._button(btn_row, "Compute", self.compute, primary=True)
        self.compute_btn.grid(row=0, column=0, sticky="ew", padx=(0, 8))

        self.clear_btn = self._button(btn_row, "Clear", self.clear, primary=False)
        self.clear_btn.grid(row=0, column=1, sticky="ew", padx=(8, 0))

        # Results
        self.results = tk.Frame(self.card, bg=CARD_BG, highlightthickness=1, highlightbackground=BORDER)
        self.results.grid(row=10, column=0, columnspan=2, sticky="ew", padx=18, pady=(6, 16))

        self.out1 = self._result_line("The product of 1st 2 numbers is:", 0)
        self.out2 = self._result_line("The product of 2nd 2 numbers is:", 1)
        self.out3 = self._result_line("The average of four numbers is:", 2)

        self.status = tk.Label(self.card, text="", bg=CARD_BG, fg=MUTED, font=("Segoe UI", 10))
        self.status.grid(row=11, column=0, columnspan=2, sticky="w", padx=18, pady=(0, 14))

        # Enter triggers compute
        self.bind("<Return>", lambda e: self.compute())

        # Live validation + auto-format
        self._wire_live_validation(self.in1, self.msg1, self.in1_placeholder)
        self._wire_live_validation(self.in2, self.msg2, self.in2_placeholder)

        self.in1.focus_set()

    # ---------------- UI helpers ----------------
    def _field_label(self, text, row):
        tk.Label(self.card, text=text, bg=CARD_BG, fg=TEXT, font=FONT_B)\
            .grid(row=row, column=0, columnspan=2, sticky="w", padx=18, pady=(0, 6))

    def _entry(self, row, placeholder=""):
        container = tk.Frame(self.card, bg=CARD_BG)
        container.grid(row=row, column=0, columnspan=2, sticky="ew", padx=18, pady=(0, 6))

        e = tk.Entry(
            container,
            bd=0,
            bg="#ffffff",
            fg=TEXT,
            font=FONT,
            insertbackground=TEXT,
            relief="flat",
            highlightthickness=1,
            highlightbackground=BORDER,
            highlightcolor=PRIMARY
        )
        # INNER padding so text isn't sagad sa border
        e.pack(fill="x", ipady=12, padx=8)

        ph = placeholder
        if ph:
            e.insert(0, ph)
            e.config(fg=MUTED)

            def on_focus_in(_):
                if e.get() == ph and e.cget("fg") == MUTED:
                    e.delete(0, "end")
                    e.config(fg=TEXT)

            def on_focus_out(_):
                if not e.get().strip():
                    e.insert(0, ph)
                    e.config(fg=MUTED)

            e.bind("<FocusIn>", on_focus_in)
            e.bind("<FocusOut>", on_focus_out)

        return e, ph

    def _button(self, parent, text, command, primary=False):
        bg = PRIMARY if primary else "#eef3fd"
        fg = "#ffffff" if primary else PRIMARY
        hover = PRIMARY_HOVER if primary else "#e3ecff"

        wrap = tk.Frame(parent, bg=CARD_BG, highlightthickness=1,
                        highlightbackground=(PRIMARY if primary else BORDER))
        label = tk.Label(wrap, text=text, bg=bg, fg=fg, font=FONT_B, padx=14, pady=10, cursor="hand2")
        label.pack(fill="both", expand=True)

        label.bind("<Enter>", lambda e: label.config(bg=hover))
        label.bind("<Leave>", lambda e: label.config(bg=bg))
        label.bind("<Button-1>", lambda e: command())
        return wrap

    def _result_line(self, text, row):
        line = tk.Frame(self.results, bg=CARD_BG)
        line.grid(row=row, column=0, sticky="ew", padx=12, pady=(10 if row == 0 else 6, 6))
        line.grid_columnconfigure(1, weight=1)

        tk.Label(line, text=text, bg=CARD_BG, fg=MUTED, font=FONT)\
            .grid(row=0, column=0, sticky="w")

        val = tk.Label(line, text="—", bg=CARD_BG, fg=TEXT, font=FONT_B)
        val.grid(row=0, column=1, sticky="e")
        return val

    # ---------------- Live validation + formatting ----------------
    def _get_clean_value(self, entry: tk.Entry):
        if entry.cget("fg") == MUTED:
            return ""
        return entry.get().strip()

    def _set_entry_border(self, entry: tk.Entry, ok: bool):
        entry.config(highlightbackground=(BORDER if ok else DANGER))

    def _validate_text(self, text: str):
        """
        Returns (ok: bool, reason: str).
        Live validation rules:
        - blank => not ok (but we'll show a gentle message only if user typed something)
        - must be exactly 2 tokens split by spaces
        - each token must be an integer (commas allowed)
        """
        if not text.strip():
            return False, "Enter two integers separated by a space."
        parts = text.strip().split()
        if len(parts) < 2:
            return False, "Need 2 numbers (example: 12 6)."
        if len(parts) > 2:
            return False, "Only 2 numbers are allowed on this line."
        p0 = parts[0].replace(",", "")
        p1 = parts[1].replace(",", "")
        if not _is_int_token(p0) or not _is_int_token(p1):
            return False, "Integers only (no letters/decimals)."
        return True, ""

    def _auto_format_entry(self, entry: tk.Entry):
        """
        Auto-format numbers with commas while typing.
        Example: "1000 2500" -> "1,000 2,500"
        Keeps it simple: after format, cursor moves to end (stable + reliable).
        """
        if entry.cget("fg") == MUTED:
            return
        raw = entry.get()
        if not raw.strip():
            return

        parts = raw.split()  # collapses multiple spaces; acceptable for this form
        if not parts:
            return

        # Only format up to first 2 tokens; keep extra tokens as-is (validation will catch it)
        formatted_parts = []
        for i, tok in enumerate(parts):
            if i < 2:
                formatted_parts.append(format_int_with_commas(tok))
            else:
                formatted_parts.append(tok)
        new_text = " ".join(formatted_parts)

        if new_text != raw.strip():
            entry.delete(0, "end")
            entry.insert(0, new_text)
            entry.icursor("end")

    def _wire_live_validation(self, entry: tk.Entry, msg_label: tk.Label, placeholder: str):
        def on_key_release(_):
            # Auto-format first so validation sees formatted text too
            self._auto_format_entry(entry)

            t = self._get_clean_value(entry)
            ok, reason = self._validate_text(t)

            # If placeholder/empty: keep neutral border + no loud message
            if not t:
                self._set_entry_border(entry, True)
                msg_label.config(text="", fg=DANGER)
                return

            self._set_entry_border(entry, ok)
            msg_label.config(text=reason, fg=DANGER)

        def on_focus_out(_):
            # On blur, format again (helps if paste happened)
            self._auto_format_entry(entry)
            t = self._get_clean_value(entry)
            if not t:
                self._set_entry_border(entry, True)
                msg_label.config(text="")
                return
            ok, reason = self._validate_text(t)
            self._set_entry_border(entry, ok)
            msg_label.config(text=reason, fg=DANGER)

        entry.bind("<KeyRelease>", on_key_release)
        entry.bind("<FocusOut>", on_focus_out)

    # ---------------- Actions ----------------
    def compute(self):
        s1 = self._get_clean_value(self.in1)
        s2 = self._get_clean_value(self.in2)

        self.status.config(text="", fg=MUTED)

        # Validate both first (and show borders/messages)
        ok1, r1 = self._validate_text(s1)
        ok2, r2 = self._validate_text(s2)

        if not s1:
            ok1, r1 = False, "First input is required."
        if not s2:
            ok2, r2 = False, "Second input is required."

        self._set_entry_border(self.in1, ok1 if s1 else False)
        self._set_entry_border(self.in2, ok2 if s2 else False)
        self.msg1.config(text=(r1 if not ok1 else ""))
        self.msg2.config(text=(r2 if not ok2 else ""))

        if not ok1 or not ok2:
            messagebox.showerror("Invalid Input", (r1 if not ok1 else r2))
            self.status.config(text="Fix the highlighted input(s).", fg=DANGER)
            return

        try:
            a, b = parse_two_ints(s1)
            c, d = parse_two_ints(s2)

            p1 = a * b
            p2 = c * d
            avg = (a + b + c + d) / 4
            avg_display = int(avg) if avg.is_integer() else avg

            self.out1.config(text=f"{p1:,}", fg=OK)
            self.out2.config(text=f"{p2:,}", fg=OK)
            # average might not be int; format nicely
            if isinstance(avg_display, int):
                self.out3.config(text=f"{avg_display:,}", fg=OK)
            else:
                self.out3.config(text=str(avg_display), fg=OK)

            self.status.config(text="Computed successfully ✔", fg=OK)

        except ValueError as e:
            messagebox.showerror("Invalid Input", str(e))
            self.status.config(text=str(e), fg=DANGER)

    def clear(self):
        for e in (self.in1, self.in2):
            e.delete(0, "end")
            e.event_generate("<FocusOut>")  # restores placeholder
            e.config(highlightbackground=BORDER)

        self.msg1.config(text="")
        self.msg2.config(text="")

        for o in (self.out1, self.out2, self.out3):
            o.config(text="—", fg=TEXT)

        self.status.config(text="", fg=MUTED)
        self.in1.focus_set()


if __name__ == "__main__":
    App().mainloop()