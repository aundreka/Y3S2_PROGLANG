import tkinter as tk

# ── Minimal Modern Palette ────────────────────────────────────────────────────
BG          = "#101014"
PANEL       = "#17181f"
PANEL_2     = "#1d1f27"
BORDER      = "#2a2d37"
ACCENT      = "#7c8cff"
ACCENT_HOV  = "#93a0ff"
TEXT        = "#f3f4f6"
TEXT_DIM    = "#9aa0ad"
SUCCESS     = "#4ade80"
WARN        = "#facc15"
ERROR       = "#f87171"
ENTRY_BG    = "#111319"


class SegmentedToggle(tk.Frame):
    def __init__(self, parent, callback=None, **kwargs):
        super().__init__(parent, bg=PANEL, **kwargs)
        self.callback = callback
        self.mode = "A"

        self.configure(highlightthickness=1, highlightbackground=BORDER)

        self.btn_a = tk.Label(
            self, text="°C → °F",
            bg=ACCENT, fg="white",
            font=("Segoe UI", 10, "bold"),
            padx=24, pady=10,
            cursor="hand2"
        )
        self.btn_b = tk.Label(
            self, text="°F → °C",
            bg=PANEL, fg=TEXT_DIM,
            font=("Segoe UI", 10, "bold"),
            padx=24, pady=10,
            cursor="hand2"
        )

        self.btn_a.pack(side="left", expand=True, fill="both")
        self.btn_b.pack(side="left", expand=True, fill="both")

        self.btn_a.bind("<Button-1>", lambda e: self.set_mode("A"))
        self.btn_b.bind("<Button-1>", lambda e: self.set_mode("B"))

    def set_mode(self, mode):
        self.mode = mode
        if mode == "A":
            self.btn_a.config(bg=ACCENT, fg="white")
            self.btn_b.config(bg=PANEL, fg=TEXT_DIM)
        else:
            self.btn_a.config(bg=PANEL, fg=TEXT_DIM)
            self.btn_b.config(bg=ACCENT, fg="white")

        if self.callback:
            self.callback(mode)


class ModernButton(tk.Label):
    def __init__(self, parent, text, command=None, primary=True, **kwargs):
        super().__init__(
            parent,
            text=text,
            font=("Segoe UI", 10, "bold"),
            padx=16,
            pady=12,
            cursor="hand2",
            **kwargs
        )
        self.command = command
        self.primary = primary

        if primary:
            self.default_bg = ACCENT
            self.hover_bg = ACCENT_HOV
            self.default_fg = "white"
            self.default_border = ACCENT
        else:
            self.default_bg = PANEL_2
            self.hover_bg = "#252833"
            self.default_fg = TEXT
            self.default_border = BORDER

        self.configure(
            bg=self.default_bg,
            fg=self.default_fg,
            highlightthickness=1,
            highlightbackground=self.default_border
        )

        self.bind("<Enter>", self._on_enter)
        self.bind("<Leave>", self._on_leave)
        self.bind("<Button-1>", self._on_click)

    def _on_enter(self, _):
        self.config(bg=self.hover_bg)

    def _on_leave(self, _):
        self.config(bg=self.default_bg)

    def _on_click(self, _):
        if self.command:
            self.command()


class TemperatureConverter(tk.Tk):
    WIN_W = 420
    WIN_H_NORMAL = 430
    WIN_H_RESULT = 560

    def __init__(self):
        super().__init__()
        self.title("Temperature Converter")
        self.configure(bg=BG)
        self.resizable(False, False)

        self._mode = "A"

        self._build_ui()
        self._center(self.WIN_H_NORMAL)

    def _build_ui(self):
        outer = tk.Frame(self, bg=BG)
        outer.pack(fill="both", expand=True, padx=24, pady=24)

        # Main card
        self.card = tk.Frame(
            outer,
            bg=PANEL,
            highlightthickness=1,
            highlightbackground=BORDER
        )
        self.card.pack(fill="both", expand=True)

        # Header
        tk.Label(
            self.card,
            text="Temperature Converter",
            bg=PANEL,
            fg=TEXT,
            font=("Segoe UI", 20, "bold")
        ).pack(anchor="w", padx=24, pady=(22, 4))

        tk.Label(
            self.card,
            text="Convert values instantly with a clean minimal interface.",
            bg=PANEL,
            fg=TEXT_DIM,
            font=("Segoe UI", 9)
        ).pack(anchor="w", padx=24, pady=(0, 18))

        divider = tk.Frame(self.card, bg=BORDER, height=1)
        divider.pack(fill="x", padx=24, pady=(0, 18))

        # Conversion label
        tk.Label(
            self.card,
            text="Conversion",
            bg=PANEL,
            fg=TEXT_DIM,
            font=("Segoe UI", 9)
        ).pack(anchor="w", padx=24)

        self.toggle = SegmentedToggle(self.card, callback=self._on_mode)
        self.toggle.pack(fill="x", padx=24, pady=(8, 18))

        # Input label
        self.input_label = tk.Label(
            self.card,
            text="Temperature (°C)",
            bg=PANEL,
            fg=TEXT_DIM,
            font=("Segoe UI", 9)
        )
        self.input_label.pack(anchor="w", padx=24)

        # Entry shell
        entry_shell = tk.Frame(
            self.card,
            bg=ENTRY_BG,
            highlightthickness=1,
            highlightbackground=BORDER
        )
        entry_shell.pack(fill="x", padx=24, pady=(8, 8))

        self.var = tk.StringVar()
        self.entry = tk.Entry(
            entry_shell,
            textvariable=self.var,
            bg=ENTRY_BG,
            fg=TEXT,
            insertbackground=TEXT,
            relief="flat",
            bd=0,
            font=("Segoe UI", 16),
            highlightthickness=0
        )
        self.entry.pack(fill="x", padx=14, pady=14)
        self.entry.bind("<Return>", lambda e: self._convert())
        self.entry.bind("<FocusIn>", lambda e: entry_shell.config(highlightbackground=ACCENT))
        self.entry.bind("<FocusOut>", lambda e: entry_shell.config(highlightbackground=BORDER))

        # Error label
        self.err = tk.StringVar()
        self.err_label = tk.Label(
            self.card,
            textvariable=self.err,
            bg=PANEL,
            fg=ERROR,
            font=("Segoe UI", 9)
        )
        self.err_label.pack(anchor="w", padx=24, pady=(0, 12))

        # Convert button
        self.convert_btn = ModernButton(
            self.card,
            text="Convert",
            command=self._convert,
            primary=True
        )
        self.convert_btn.pack(fill="x", padx=24, pady=(0, 18))

        # Result card
        self.result_frame = tk.Frame(
            self.card,
            bg=PANEL_2,
            highlightthickness=1,
            highlightbackground=BORDER
        )

        top_row = tk.Frame(self.result_frame, bg=PANEL_2)
        top_row.pack(fill="x", padx=18, pady=(16, 6))

        tk.Label(
            top_row,
            text="Result",
            bg=PANEL_2,
            fg=TEXT_DIM,
            font=("Segoe UI", 9)
        ).pack(side="left")

        self.status_var = tk.StringVar()
        self.status_lbl = tk.Label(
            top_row,
            textvariable=self.status_var,
            bg=PANEL_2,
            fg=WARN,
            font=("Segoe UI", 9, "bold")
        )
        self.status_lbl.pack(side="right")

        value_row = tk.Frame(self.result_frame, bg=PANEL_2)
        value_row.pack(fill="x", padx=18, pady=(0, 6))

        self.result_val = tk.StringVar()
        self.result_unit = tk.StringVar()

        tk.Label(
            value_row,
            textvariable=self.result_val,
            bg=PANEL_2,
            fg=TEXT,
            font=("Segoe UI", 28, "bold")
        ).pack(side="left")

        tk.Label(
            value_row,
            textvariable=self.result_unit,
            bg=PANEL_2,
            fg=ACCENT,
            font=("Segoe UI", 13, "bold")
        ).pack(side="left", padx=(8, 0), pady=(8, 0))

        self.detail_var = tk.StringVar()
        tk.Label(
            self.result_frame,
            textvariable=self.detail_var,
            bg=PANEL_2,
            fg=TEXT_DIM,
            font=("Segoe UI", 9)
        ).pack(anchor="w", padx=18, pady=(0, 16))

        self.reset_btn = ModernButton(
            self.card,
            text="Reset",
            command=self._reset,
            primary=False
        )

    def _on_mode(self, mode):
        self._mode = mode
        self.err.set("")
        self._hide_result()

        if mode == "A":
            self.input_label.config(text="Temperature (°C)")
        else:
            self.input_label.config(text="Temperature (°F)")

    def _convert(self):
        raw = self.var.get().strip()

        if not raw:
            self.err.set("Enter a temperature value.")
            self._hide_result()
            return

        try:
            value = float(raw)
        except ValueError:
            self.err.set("Please enter a valid number.")
            self._hide_result()
            return

        self.err.set("")

        if self._mode == "A":
            converted = (value * 9 / 5) + 32
            out_unit = "°F"
            detail = f"{value:.2f} °C converted to Fahrenheit"
        else:
            converted = (value - 32) * 5 / 9
            out_unit = "°C"
            detail = f"{value:.2f} °F converted to Celsius"

        if converted > 100:
            status = "Too High"
            status_color = WARN
        else:
            status = "Normal"
            status_color = SUCCESS

        self.result_val.set(f"{converted:.2f}")
        self.result_unit.set(out_unit)
        self.detail_var.set(detail)
        self.status_var.set(status)
        self.status_lbl.config(fg=status_color)

        self._show_result()

    def _show_result(self):
        self._center(self.WIN_H_RESULT)
        self.result_frame.pack(fill="x", padx=24, pady=(0, 12))
        self.reset_btn.pack(fill="x", padx=24, pady=(0, 22))

    def _hide_result(self):
        self._center(self.WIN_H_NORMAL)
        self.result_frame.pack_forget()
        self.reset_btn.pack_forget()

    def _reset(self):
        self.var.set("")
        self.err.set("")
        self.result_val.set("")
        self.result_unit.set("")
        self.detail_var.set("")
        self.status_var.set("")
        self.entry.focus_set()
        self._hide_result()

    def _center(self, height):
        self.geometry(f"{self.WIN_W}x{height}")
        self.update_idletasks()
        x = (self.winfo_screenwidth() - self.WIN_W) // 2
        y = (self.winfo_screenheight() - height) // 2
        self.geometry(f"{self.WIN_W}x{height}+{x}+{y}")


if __name__ == "__main__":
    app = TemperatureConverter()
    app.mainloop()