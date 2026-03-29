import tkinter as tk

# ── Colours ──────────────────────────────────────────────────────────────────
BG       = "#0f0f13"
GLASS_BG = "#1c1c26"
GLASS_BD = "#2e2e42"
ACCENT   = "#7c6aff"
ACCENT2  = "#a78bfa"
TEXT_PRI = "#f0f0ff"
TEXT_SEC = "#8888aa"
ENTRY_BG = "#14141e"
BTN_ACT  = "#6c5ce7"
ERROR    = "#f87171"
SUCCESS  = "#4ade80"

# ── Root window ───────────────────────────────────────────────────────────────
root = tk.Tk()
root.title("Time Converter")
root.geometry("420x560")
root.resizable(False, False)
root.configure(bg=BG)

root.update_idletasks()
x = (root.winfo_screenwidth()  - 420) // 2
y = (root.winfo_screenheight() - 560) // 2
root.geometry(f"420x560+{x}+{y}")

# ── State ─────────────────────────────────────────────────────────────────────
mode       = tk.StringVar(value="M")
input_val  = tk.StringVar()
result_var = tk.StringVar(value="—")
status_var = tk.StringVar(value="")

# ── Convert logic (if/elif/else acts as switch) ───────────────────────────────
def convert():
    raw = input_val.get().strip()

    if not raw:
        flash("Enter a value first", ERROR)
        return
    try:
        value = float(raw)
    except ValueError:
        flash("Numbers only, please", ERROR)
        return
    if value < 0:
        flash("Negative time? Really?", ERROR)
        return

    chosen = mode.get()

    if chosen == "M":             # hours → minutes
        minutes    = value * 60
        hours_eq   = value
        result_var.set(f"{minutes:g} min")
    elif chosen == "H":           # minutes → hours
        hours_eq   = value / 60
        result_var.set(f"{hours_eq:.5g} hr")
    else:
        flash("Invalid Option", ERROR)
        result_var.set("—")
        return

    if hours_eq > 24:
        flash("Too long!", ACCENT2)
    else:
        flash("Less than a day processing.", SUCCESS)

    blink()

def blink():
    result_label.config(fg=ACCENT2)
    root.after(150, lambda: result_label.config(fg=TEXT_PRI))

def flash(msg, color):
    status_var.set(msg)
    status_label.config(fg=color)

# ── Mode toggle ───────────────────────────────────────────────────────────────
def set_mode(m):
    mode.set(m)
    input_val.set("")
    result_var.set("—")
    status_var.set("")
    refresh_toggle()
    input_label.config(text="Hours" if m == "M" else "Minutes")
    entry.focus_set()

def refresh_toggle():
    m = mode.get()
    for btn, key in ((btn_m, "M"), (btn_h, "H")):
        btn.config(
            bg=ACCENT   if m == key else GLASS_BD,
            fg=TEXT_PRI if m == key else TEXT_SEC,
        )

# ── Key validation: digits + single dot only ──────────────────────────────────
def only_numbers(P):
    if P == "":
        return True
    if P.count(".") <= 1 and P.replace(".", "").isdigit():
        return True
    return False

vcmd = root.register(only_numbers)
root.bind("<Return>", lambda e: convert())

# ══════════════════════════════════════════════════════════════════════════════
# UI
# ══════════════════════════════════════════════════════════════════════════════
outer = tk.Frame(root, bg=BG)
outer.place(relx=0.5, rely=0.5, anchor="center", width=370, height=510)

# Title
tk.Label(outer, text="Time Converter",
         font=("Helvetica Neue", 22, "bold"),
         bg=BG, fg=TEXT_PRI).pack(pady=(0, 4))
tk.Label(outer, text="Hours  ↔  Minutes",
         font=("Helvetica Neue", 9),
         bg=BG, fg=TEXT_SEC).pack(pady=(0, 22))

# ── Main card ─────────────────────────────────────────────────────────────────
card = tk.Frame(outer, bg=GLASS_BG,
                highlightbackground=GLASS_BD, highlightthickness=1)
card.pack(fill="x")
inn = tk.Frame(card, bg=GLASS_BG)
inn.pack(fill="x", padx=28, pady=28)

# Toggle row
tog = tk.Frame(inn, bg=GLASS_BD)
tog.pack(fill="x", pady=(0, 22))

btn_m = tk.Button(tog, text="Hour → Min",
                  font=("Helvetica Neue", 11, "bold"),
                  bd=0, cursor="hand2", pady=9,
                  activebackground=BTN_ACT, activeforeground=TEXT_PRI,
                  command=lambda: set_mode("M"))
btn_m.pack(side="left", fill="x", expand=True)

btn_h = tk.Button(tog, text="Min → Hour",
                  font=("Helvetica Neue", 11, "bold"),
                  bd=0, cursor="hand2", pady=9,
                  activebackground=BTN_ACT, activeforeground=TEXT_PRI,
                  command=lambda: set_mode("H"))
btn_h.pack(side="left", fill="x", expand=True)

# Input label
input_label = tk.Label(inn, text="Hours",
                       font=("Helvetica Neue", 11),
                       bg=GLASS_BG, fg=TEXT_SEC, anchor="w")
input_label.pack(fill="x", pady=(0, 6))

# Entry box
ef = tk.Frame(inn, bg=ENTRY_BG,
              highlightbackground=GLASS_BD, highlightthickness=1)
ef.pack(fill="x", pady=(0, 20))
entry = tk.Entry(ef, textvariable=input_val,
                 font=("Helvetica Neue", 15),
                 bg=ENTRY_BG, fg=TEXT_PRI,
                 insertbackground=ACCENT2, bd=0,
                 validate="key", validatecommand=(vcmd, "%P"))
entry.pack(fill="x", padx=14, pady=12)

# Convert button
tk.Button(inn, text="Convert",
          font=("Helvetica Neue", 11, "bold"),
          bd=0, cursor="hand2", pady=12,
          bg=ACCENT, fg=TEXT_PRI,
          activebackground=BTN_ACT, activeforeground=TEXT_PRI,
          command=convert).pack(fill="x")

# ── Result card ───────────────────────────────────────────────────────────────
rc = tk.Frame(outer, bg=GLASS_BG,
              highlightbackground=GLASS_BD, highlightthickness=1)
rc.pack(fill="x", pady=(12, 0))
ri = tk.Frame(rc, bg=GLASS_BG)
ri.pack(fill="x", padx=28, pady=20)

tk.Label(ri, text="Result",
         font=("Helvetica Neue", 11),
         bg=GLASS_BG, fg=TEXT_SEC, anchor="w").pack(fill="x")

result_label = tk.Label(ri, textvariable=result_var,
                        font=("Helvetica Neue", 28, "bold"),
                        bg=GLASS_BG, fg=TEXT_PRI, anchor="w")
result_label.pack(fill="x", pady=(4, 8))

status_label = tk.Label(ri, textvariable=status_var,
                        font=("Helvetica Neue", 9),
                        bg=GLASS_BG, fg=SUCCESS, anchor="w")
status_label.pack(fill="x")

# Init
refresh_toggle()
entry.focus_set()
root.mainloop()