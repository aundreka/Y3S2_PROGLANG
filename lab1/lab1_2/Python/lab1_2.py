import tkinter as tk
from tkinter import messagebox

def is_partial_number(s: str) -> bool:
    """
    Allow partial typing states:
    "", "-", ".", "-.", "12", "12.", ".5", "-.5", "12.34"
    """
    if s == "":
        return True
    if s in {"-", ".", "-."}:
        return True
    try:
        float(s)
        return True
    except ValueError:
        return False

def parse_two_numbers_strict(text: str):
    parts = text.strip().split()
    if len(parts) != 2:
        raise ValueError("Enter exactly 2 numbers separated by ONE space (e.g., 12 6).")
    a = float(parts[0].replace(",", ""))
    b = float(parts[1].replace(",", ""))
    return a, b

def validate_two_numbers_live(text: str):
    """
    Returns (ok, message).
    ok=True means it is valid (or empty allowed? -> here: empty is NOT ok for compute,
    but ok for "live typing" we'll treat empty as not-ok with a friendly message.)
    """
    t = text.strip()
    if t == "":
        return False, "Required. Enter 2 numbers (e.g., 12 6)."

    parts = t.split()
    if len(parts) == 1:
        # could be still typing first or missing second
        # If first token is not even a partial number, mark invalid
        token = parts[0].replace(",", "")
        if not is_partial_number(token):
            return False, "First value must be a number."
        return False, "Add the second number (e.g., 12 6)."

    if len(parts) > 2:
        return False, "Too many values. Use exactly 2 numbers."

    a_s = parts[0].replace(",", "")
    b_s = parts[1].replace(",", "")

    if not is_partial_number(a_s):
        return False, "First value must be a valid number."
    if not is_partial_number(b_s):
        return False, "Second value must be a valid number."

    # For "valid" state while typing, require that both are fully convertible (not just '-', '.', etc.)
    try:
        float(a_s)
    except ValueError:
        return False, "Finish typing the first number."
    try:
        float(b_s)
    except ValueError:
        return False, "Finish typing the second number."

    return True, ""

def format_with_commas(n: float) -> str:
    """
    Adds commas; keeps decimals if needed.
    Examples:
      12000 -> 12,000
      12000.5 -> 12,000.5
      12000.1234 -> 12,000.1234 (trim trailing zeros)
    """
    if abs(n - round(n)) < 1e-12:
        return f"{int(round(n)):,}"

    # keep up to 10 dp, trim trailing zeros
    s = f"{n:,.10f}".rstrip("0").rstrip(".")
    return s

def format_pair_entry_text(text: str):
    """
    If it contains exactly 2 valid numbers, returns formatted "a b" with commas.
    Otherwise, returns original text unchanged.
    """
    try:
        a, b = parse_two_numbers_strict(text)
    except Exception:
        return text
    return f"{format_with_commas(a)} {format_with_commas(b)}"

class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Difference & Average Calculator")
        self.minsize(780, 500)
        self.configure(bg="#f6f7fb")

        self.font_title = ("Segoe UI", 16, "bold")
        self.font_label = ("Segoe UI", 10)
        self.font_input = ("Segoe UI", 11)
        self.font_button = ("Segoe UI", 10, "bold")

        # No visible scrollbars; wheel scroll if needed
        self.canvas = tk.Canvas(self, bg="#f6f7fb", highlightthickness=0)
        self.canvas.pack(fill="both", expand=True)
        self.content = tk.Frame(self.canvas, bg="#f6f7fb")
        self.window_id = self.canvas.create_window((0, 0), window=self.content, anchor="nw")

        self.content.bind("<Configure>", self._on_content_configure)
        self.canvas.bind("<Configure>", self._on_canvas_configure)
        self._bind_mousewheel(self.canvas)

        self._build_ui()

    # ---------- scrolling ----------
    def _on_content_configure(self, _event=None):
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))

    def _on_canvas_configure(self, event):
        self.canvas.itemconfigure(self.window_id, width=event.width)

    def _bind_mousewheel(self, widget):
        widget.bind_all("<MouseWheel>", self._on_mousewheel)  # Win/mac
        widget.bind_all("<Button-4>", self._on_mousewheel)    # Linux
        widget.bind_all("<Button-5>", self._on_mousewheel)

    def _on_mousewheel(self, event):
        bbox = self.canvas.bbox("all")
        if not bbox:
            return
        content_h = bbox[3] - bbox[1]
        if content_h <= self.canvas.winfo_height():
            return

        if event.num == 4:
            self.canvas.yview_scroll(-2, "units")
        elif event.num == 5:
            self.canvas.yview_scroll(2, "units")
        else:
            delta = int(-1 * (event.delta / 120))
            self.canvas.yview_scroll(delta * 2, "units")

    # ---------- UI builders ----------
    def _build_ui(self):
        wrapper = tk.Frame(self.content, bg="#f6f7fb")
        wrapper.pack(fill="both", expand=True, padx=22, pady=22)

        card = tk.Frame(wrapper, bg="white", bd=0, highlightthickness=1, highlightbackground="#e7e9f1")
        card.pack(fill="x")

        header = tk.Frame(card, bg="white")
        header.pack(fill="x", padx=18, pady=(16, 6))

        tk.Label(header, text="Compute Differences & Average", font=self.font_title, bg="white", fg="#111827").pack(anchor="w")
        tk.Label(
            header,
            text="Enter two numbers per line (separated by space). Commas are allowed (e.g., 12,000 6).",
            font=self.font_label,
            bg="white",
            fg="#6b7280"
        ).pack(anchor="w", pady=(4, 0))

        form = tk.Frame(card, bg="white")
        form.pack(fill="x", padx=18, pady=(10, 10))

        self.in1_var = tk.StringVar(value="")
        self.in2_var = tk.StringVar(value="")

        self.in1 = self._validated_entry(
            form,
            label="Input 1st two numbers",
            var=self.in1_var
        )
        self.in2 = self._validated_entry(
            form,
            label="Input last two numbers",
            var=self.in2_var
        )

        tip = tk.Label(form, text="Tip: Use space between numbers (e.g., -3.5 20000).",
                       font=self.font_label, bg="white", fg="#6b7280")
        tip.pack(anchor="w", pady=(2, 10))

        btn_row = tk.Frame(form, bg="white")
        btn_row.pack(fill="x", pady=(2, 0))

        self.calc_btn = self._primary_button(btn_row, "Compute", self.compute)
        self.calc_btn.pack(side="left")

        self.clear_btn = self._secondary_button(btn_row, "Clear", self.clear)
        self.clear_btn.pack(side="left", padx=(10, 0))

        self.example_btn = self._tertiary_button(btn_row, "Fill Sample", self.fill_sample)
        self.example_btn.pack(side="right")

        # Output card
        out_wrap = tk.Frame(card, bg="white")
        out_wrap.pack(fill="both", padx=18, pady=(14, 18))

        tk.Label(out_wrap, text="Results", font=("Segoe UI", 11, "bold"), bg="white", fg="#111827").pack(anchor="w")

        self.out_card = tk.Frame(out_wrap, bg="#f9fafb", highlightthickness=1, highlightbackground="#e5e7eb")
        self.out_card.pack(fill="x", pady=(8, 0))

        top_row = tk.Frame(self.out_card, bg="#f9fafb")
        top_row.pack(fill="x", padx=14, pady=(12, 6))

        self.badge_avg = self._badge(top_row, "AVG: —", bg="#eef2ff", fg="#3730a3")
        self.badge_avg.pack(side="left")

        self.badge_diff1 = self._badge(top_row, "Diff #1: —", bg="#ecfeff", fg="#155e75")
        self.badge_diff1.pack(side="left", padx=(10, 0))

        self.badge_diff2 = self._badge(top_row, "Diff #2: —", bg="#f0fdf4", fg="#166534")
        self.badge_diff2.pack(side="left", padx=(10, 0))

        tk.Frame(self.out_card, bg="#e5e7eb", height=1).pack(fill="x", padx=14, pady=(8, 0))

        self.output = tk.Text(
            self.out_card,
            height=7,
            font=("Segoe UI", 10),
            bg="#f9fafb",
            fg="#111827",
            relief="flat",
            bd=0,
            highlightthickness=0,
            padx=14,
            pady=10,
            wrap="word"
        )
        self.output.pack(fill="x")
        self.output.configure(state="disabled")
        self._set_output("Enter values then click Compute.")

        # Live validation hooks
        self.in1_var.trace_add("write", lambda *_: self._live_validate(self.in1))
        self.in2_var.trace_add("write", lambda *_: self._live_validate(self.in2))
        self._live_validate(self.in1)
        self._live_validate(self.in2)

        self.bind("<Return>", lambda _e: self.compute())
        self.in1["entry"].focus_set()

    def _badge(self, parent, text, bg, fg):
        return tk.Label(parent, text=text, font=("Segoe UI", 11, "bold"), bg=bg, fg=fg, padx=12, pady=8)

    def _primary_button(self, parent, text, cmd):
        btn = tk.Label(parent, text=text, font=self.font_button, bg="#2563eb", fg="white",
                       padx=18, pady=10, cursor="hand2")
        btn.bind("<Button-1>", lambda _e: cmd())
        btn.bind("<Enter>", lambda _e: btn.configure(bg="#1d4ed8"))
        btn.bind("<Leave>", lambda _e: btn.configure(bg="#2563eb"))
        return btn

    def _secondary_button(self, parent, text, cmd):
        btn = tk.Label(parent, text=text, font=self.font_button, bg="#ffffff", fg="#111827",
                       padx=18, pady=10, cursor="hand2",
                       highlightthickness=1, highlightbackground="#d1d5db")
        btn.bind("<Button-1>", lambda _e: cmd())
        btn.bind("<Enter>", lambda _e: btn.configure(bg="#f3f4f6"))
        btn.bind("<Leave>", lambda _e: btn.configure(bg="#ffffff"))
        return btn

    def _tertiary_button(self, parent, text, cmd):
        btn = tk.Label(parent, text=text, font=self.font_button, bg="white", fg="#2563eb",
                       padx=10, pady=10, cursor="hand2")
        btn.bind("<Button-1>", lambda _e: cmd())
        btn.bind("<Enter>", lambda _e: btn.configure(bg="#eff6ff"))
        btn.bind("<Leave>", lambda _e: btn.configure(bg="white"))
        return btn

    def _validated_entry(self, parent, label, var):
        container = tk.Frame(parent, bg="white")
        container.pack(fill="x", pady=(0, 10))

        tk.Label(container, text=label, font=self.font_label, bg="white", fg="#374151").pack(anchor="w", pady=(0, 6))

        # Input shell for "Google-ish" border + true padding
        shell = tk.Frame(
            container,
            bg="#ffffff",
            highlightthickness=2,
            highlightbackground="#d1d5db",
            highlightcolor="#2563eb",
        )
        shell.pack(fill="x")

        pad = tk.Frame(shell, bg="#ffffff")
        pad.pack(fill="x", padx=12, pady=10)  # <-- inner padding

        entry = tk.Entry(
            pad,
            textvariable=var,
            font=self.font_input,
            bg="#ffffff",
            fg="#111827",
            relief="flat",
            bd=0,
            highlightthickness=0,
            insertbackground="#111827",
        )
        entry.pack(fill="x")

        # Error/help text under field
        help_lbl = tk.Label(container, text="", font=("Segoe UI", 9), bg="white", fg="#ef4444")
        help_lbl.pack(anchor="w", pady=(4, 0))

        def on_focus_in(_e):
            # only show blue focus border if not currently invalid
            if shell.cget("highlightbackground") != "#ef4444":
                shell.configure(highlightbackground="#2563eb")

        def on_focus_out(_e):
            # revert border based on current validity
            self._live_validate({"shell": shell, "help": help_lbl, "entry": entry, "var": var}, format_on_valid=True)

        entry.bind("<FocusIn>", on_focus_in)
        entry.bind("<FocusOut>", on_focus_out)

        return {"shell": shell, "help": help_lbl, "entry": entry, "var": var}

    # ---------- output ----------
    def _set_output(self, text):
        self.output.configure(state="normal")
        self.output.delete("1.0", "end")
        self.output.insert("end", text)
        self.output.configure(state="disabled")

    # ---------- validation ----------
    def _live_validate(self, field, format_on_valid=False):
        text = field["var"].get()
        ok, msg = validate_two_numbers_live(text)

        if ok:
            field["help"].configure(text="")
            # If focused, keep blue; else gray
            if self.focus_get() == field["entry"]:
                field["shell"].configure(highlightbackground="#2563eb")
            else:
                field["shell"].configure(highlightbackground="#d1d5db")

            if format_on_valid:
                formatted = format_pair_entry_text(text)
                if formatted != text:
                    # avoid recursion glitch by setting after idle
                    self.after_idle(lambda: field["var"].set(formatted))
        else:
            field["shell"].configure(highlightbackground="#ef4444")
            field["help"].configure(text=msg)

        return ok

    def _show_compute_error(self, field, message):
        messagebox.showerror("Invalid input", message)
        field["entry"].focus_set()

    # ---------- actions ----------
    def compute(self):
        ok1 = self._live_validate(self.in1, format_on_valid=True)
        ok2 = self._live_validate(self.in2, format_on_valid=True)
        if not ok1:
            self._show_compute_error(self.in1, "Fix Input 1st two numbers first.")
            return
        if not ok2:
            self._show_compute_error(self.in2, "Fix Input last two numbers first.")
            return

        # Strict parse (strip commas)
        try:
            a, b = parse_two_numbers_strict(self.in1_var.get())
        except Exception as e:
            self._show_compute_error(self.in1, f"Input 1st two numbers: {e}")
            return

        try:
            c, d = parse_two_numbers_strict(self.in2_var.get())
        except Exception as e:
            self._show_compute_error(self.in2, f"Input last two numbers: {e}")
            return

        diff1 = a - b
        diff2 = c - d
        avg = (a + b + c + d) / 4.0

        # Update badges with commas
        self.badge_avg.configure(text=f"AVG: {format_with_commas(avg)}")
        self.badge_diff1.configure(text=f"Diff #1: {format_with_commas(diff1)}")
        self.badge_diff2.configure(text=f"Diff #2: {format_with_commas(diff2)}")

        details = (
            f"Inputs\n"
            f"  • First pair:  {format_with_commas(a)}  and  {format_with_commas(b)}\n"
            f"  • Second pair: {format_with_commas(c)}  and  {format_with_commas(d)}\n\n"
            f"Results\n"
            f"  • Difference of 1st 2 numbers: {format_with_commas(diff1)}\n"
            f"  • Difference of 2nd 2 numbers: {format_with_commas(diff2)}\n"
            f"  • Average of four numbers:     {format_with_commas(avg)}"
        )
        self._set_output(details)

    def clear(self):
        self.in1_var.set("")
        self.in2_var.set("")
        self.badge_avg.configure(text="AVG: —")
        self.badge_diff1.configure(text="Diff #1: —")
        self.badge_diff2.configure(text="Diff #2: —")
        self._set_output("Enter values then click Compute.")
        self._live_validate(self.in1)
        self._live_validate(self.in2)
        self.in1["entry"].focus_set()

    def fill_sample(self):
        self.in1_var.set("12 6")
        self.in2_var.set("7 9")
        self.compute()

if __name__ == "__main__":
    App().mainloop()