
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>
#include <limits.h>

#define STUDENTS 10
#define MAX_NAME 64
#define USE_COLOR 1

#if USE_COLOR
#define C_RESET  "\x1b[0m"
#define C_TITLE  "\x1b[1;38;5;27m"
#define C_LABEL  "\x1b[38;5;33m"
#define C_GOOD   "\x1b[38;5;34m"
#define C_WARN   "\x1b[38;5;208m"
#define C_ERR    "\x1b[1;38;5;196m"
#define C_FAINT  "\x1b[38;5;245m"
#else
#define C_RESET  ""
#define C_TITLE  ""
#define C_LABEL  ""
#define C_GOOD   ""
#define C_WARN   ""
#define C_ERR    ""
#define C_FAINT  ""
#endif

static void trim_newline(char *s) {
    size_t n = strlen(s);
    while (n > 0 && (s[n - 1] == '\n' || s[n - 1] == '\r')) {
        s[n - 1] = '\0';
        n--;
    }
}

static void trim_spaces(char *s) {
    // left trim
    size_t start = 0;
    while (s[start] && isspace((unsigned char)s[start])) start++;

    if (start > 0) memmove(s, s + start, strlen(s + start) + 1);

    // right trim
    size_t n = strlen(s);
    while (n > 0 && isspace((unsigned char)s[n - 1])) {
        s[n - 1] = '\0';
        n--;
    }
}

static int is_valid_name_char(char c) {
    return (isalpha((unsigned char)c) || c == ' ' || c == '-' || c == '\'');
}

static int is_valid_name(const char *s) {
    if (!s || !*s) return 0;
    int has_letter = 0;
    for (size_t i = 0; s[i]; i++) {
        if (!is_valid_name_char(s[i])) return 0;
        if (isalpha((unsigned char)s[i])) has_letter = 1;
    }
    return has_letter;
}

static void print_hr(int width) {
    putchar('+');
    for (int i = 0; i < width - 2; i++) putchar('-');
    puts("+");
}

static void print_line(int width, const char *left) {
    int inner = width - 2;
    printf("|%-*.*s|\n", inner, inner, left ? left : "");
}

static void print_center(int width, const char *text) {
    int inner = width - 2;
    int len = (int)strlen(text);
    if (len > inner) len = inner;

    int pad_left = (inner - len) / 2;
    int pad_right = inner - len - pad_left;

    putchar('|');
    for (int i = 0; i < pad_left; i++) putchar(' ');
    fwrite(text, 1, len, stdout);
    for (int i = 0; i < pad_right; i++) putchar(' ');
    puts("|");
}

static void clear_screen(void) {
    // ANSI clear screen + home cursor (works in most terminals)
    printf("\x1b[2J\x1b[H");
}

static void read_line_prompt(const char *prompt, char *out, size_t out_sz) {
    while (1) {
        printf("%s%s%s", C_LABEL, prompt, C_RESET);
        if (!fgets(out, (int)out_sz, stdin)) {
            // EOF -> exit cleanly
            puts("\nInput ended.");
            exit(0);
        }
        trim_newline(out);
        trim_spaces(out);

        if (!is_valid_name(out)) {
            printf("%s✖ Please enter a valid name (letters, space, - , ').%s\n\n", C_ERR, C_RESET);
            continue;
        }
        return;
    }
}

static int read_int_in_range(const char *prompt, int minv, int maxv) {
    char buf[128];
    while (1) {
        printf("%s%s%s", C_LABEL, prompt, C_RESET);
        if (!fgets(buf, (int)sizeof(buf), stdin)) {
            puts("\nInput ended.");
            exit(0);
        }
        trim_newline(buf);
        trim_spaces(buf);

        if (buf[0] == '\0') {
            printf("%s✖ This field is required.%s\n\n", C_ERR, C_RESET);
            continue;
        }

        // validate integer strictly
        char *end = NULL;
        long v = strtol(buf, &end, 10);
        while (end && *end && isspace((unsigned char)*end)) end++;

        if (!end || *end != '\0') {
            printf("%s✖ Please enter a whole number.%s\n\n", C_ERR, C_RESET);
            continue;
        }
        if (v < minv || v > maxv) {
            printf("%s✖ Grade must be between %d and %d.%s\n\n", C_ERR, minv, maxv, C_RESET);
            continue;
        }
        return (int)v;
    }
}

int main(void) {
    const int W = 72;

    char instr_first[MAX_NAME];
    char instr_last[MAX_NAME];

    char student_last[STUDENTS][MAX_NAME];
    int  student_grade[STUDENTS];

    clear_screen();

    print_hr(W);
    print_center(W, "CSCN09C - Grade Entry Form");
    print_hr(W);
    print_line(W, "");
    print_line(W, "  Please fill out the following fields (10 students required).");
    print_line(W, "");
    print_hr(W);

    // Instructor details
    puts("");
    print_hr(W);
    print_center(W, "Instructor Information");
    print_hr(W);
    print_line(W, "");
    read_line_prompt("Instructor First Name: ", instr_first, sizeof(instr_first));
    read_line_prompt("Instructor Last  Name: ", instr_last,  sizeof(instr_last));

    // Student entries
    for (int i = 0; i < STUDENTS; i++) {
        puts("");
        print_hr(W);
        char header[128];
        snprintf(header, sizeof(header), "Student Entry %d of %d", i + 1, STUDENTS);
        print_center(W, header);
        print_hr(W);
        print_line(W, "");

        char prompt1[128];
        snprintf(prompt1, sizeof(prompt1), "%d%s Entry - Student Last Name: ",
                 i + 1, (i == 0) ? "st" : (i == 1) ? "nd" : (i == 2) ? "rd" : "th");
        read_line_prompt(prompt1, student_last[i], sizeof(student_last[i]));

        student_grade[i] = read_int_in_range("Student Grade (0-100): ", 0, 100);

        printf("%s✓ Saved.%s\n", C_GOOD, C_RESET);
    }

    // Compute lowest grade
    int lowest = INT_MAX;
    for (int i = 0; i < STUDENTS; i++) {
        if (student_grade[i] < lowest) lowest = student_grade[i];
    }

    // Summary “thank you” page
    clear_screen();
    print_hr(W);
    print_center(W, "Submission Summary");
    print_hr(W);
    print_line(W, "");
    {
        char line1[200];
        snprintf(line1, sizeof(line1), "Instructor Name: %s %s", instr_first, instr_last);
        print_line(W, line1);
    }
    {
        char line2[200];
        snprintf(line2, sizeof(line2), "Instructor Last Name: %s", instr_last);
        print_line(W, line2);
    }
    print_line(W, "");
    {
        char line3[200];
        snprintf(line3, sizeof(line3), "The lowest grade among the %d students is %d.", STUDENTS, lowest);
        print_line(W, line3);
    }
    print_line(W, "");
    print_line(W, "  Thank you! Your responses have been recorded.");
    print_line(W, "");
    print_hr(W);

    // Also match your sample’s final sentence closely:
    printf("\n%sInstructor Name:%s %s %s\n", C_LABEL, C_RESET, instr_first, instr_last);
    printf("%sThe lowest grade of your students is %d.%s\n\n", C_LABEL, lowest, C_RESET);

    return 0;
}