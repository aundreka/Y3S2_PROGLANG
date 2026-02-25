#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_NAME 64
#define MAX_CHILDREN 50

// ---------- UI helpers ----------
void line(char ch, int n) {
    for (int i = 0; i < n; i++) putchar(ch);
    putchar('\n');
}

void header(const char *title) {
    line('=', 60);
    printf("  %s\n", title);
    line('=', 60);
}

void section(const char *title) {
    printf("\n");
    line('-', 60);
    printf("  %s\n", title);
    line('-', 60);
}

void trim_newline(char *s) {
    s[strcspn(s, "\n")] = '\0';
}

// ---------- Input helpers ----------
void getString(const char *prompt, char *out, int size) {
    while (1) {
        printf("%-32s: ", prompt);
        if (!fgets(out, size, stdin)) {
            printf("\nInput error. Exiting.\n");
            exit(1);
        }
        trim_newline(out);

        if (strlen(out) == 0) {
            printf("  [!] Please enter a value.\n");
            continue;
        }
        break;
    }
}

int getIntInRange(const char *prompt, int min, int max) {
    char buf[128];
    while (1) {
        printf("%-32s: ", prompt);
        if (!fgets(buf, sizeof(buf), stdin)) {
            printf("\nInput error. Exiting.\n");
            exit(1);
        }

        char *endptr;
        long val = strtol(buf, &endptr, 10);

        // Valid if: at least one digit parsed AND remaining chars are just newline/space
        if (endptr == buf) {
            printf("  [!] Invalid number. Try again.\n");
            continue;
        }
        while (*endptr == ' ' || *endptr == '\t') endptr++;
        if (*endptr != '\n' && *endptr != '\0') {
            printf("  [!] Invalid characters detected. Try again.\n");
            continue;
        }

        if (val < min || val > max) {
            printf("  [!] Enter a value between %d and %d.\n", min, max);
            continue;
        }
        return (int)val;
    }
}

int main(void) {
    char parentFirst[MAX_NAME], parentLast[MAX_NAME];

    // Dynamic children storage (up to MAX_CHILDREN)
    char childNames[MAX_CHILDREN][MAX_NAME];
    int childAges[MAX_CHILDREN];

    header("PERSONAL INFORMATION SYSTEM");

    section("Parent Information");
    getString("First Name", parentFirst, sizeof(parentFirst));
    getString("Last Name", parentLast, sizeof(parentLast));

    section("Children Information");
    int n = getIntInRange("How many children? (1-50)", 1, MAX_CHILDREN);

    int eldestAge = -1;
    int eldestIndex = -1;

    for (int i = 0; i < n; i++) {
        char labelName[80];
        char labelAge[80];

        snprintf(labelName, sizeof(labelName), "Child #%d First Name", i + 1);
        snprintf(labelAge, sizeof(labelAge), "Child #%d Age", i + 1);

        getString(labelName, childNames[i], sizeof(childNames[i]));
        childAges[i] = getIntInRange(labelAge, 1, 130);

        if (childAges[i] > eldestAge) {
            eldestAge = childAges[i];
            eldestIndex = i;
        }
    }

    // Display summary
    section("Summary");
    printf("  Parent\n");
    printf("  %-18s: %s\n", "First Name", parentFirst);
    printf("  %-18s: %s\n", "Last Name", parentLast);

    printf("\n  Children (%d)\n", n);
    for (int i = 0; i < n; i++) {
        printf("  • %-20s  Age: %d\n", childNames[i], childAges[i]);
    }

    line('=', 60);
    printf("  Hi %s!\n", parentFirst);
    printf("  The age of your eldest child is %d", eldestAge);
    if (eldestIndex != -1) {
        printf(" (%s).", childNames[eldestIndex]);
    }
    printf("\n");
    line('=', 60);

    return 0;
}