#include <QApplication>
#include <QWidget>
#include <QFrame>
#include <QLabel>
#include <QPushButton>
#include <QLineEdit>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QTimer>
#include <QScreen>
#include <QGuiApplication>
#include <QRegularExpressionValidator>
#include <QRegularExpression>
#include <QString>
#include <QFont>

class TimeConverter : public QWidget {
public:
    TimeConverter(QWidget *parent = nullptr) : QWidget(parent), mode("M") {
        setWindowTitle("Time Converter");
        setFixedSize(420, 560);

        buildUI();
        centerWindow();
        refreshToggle();
        entry->setFocus();
    }

private:
    // ── Colours ──────────────────────────────────────────────────────────────
    const QString BG       = "#0f0f13";
    const QString GLASS_BG = "#1c1c26";
    const QString GLASS_BD = "#2e2e42";
    const QString ACCENT   = "#7c6aff";
    const QString ACCENT2  = "#a78bfa";
    const QString TEXT_PRI = "#f0f0ff";
    const QString TEXT_SEC = "#8888aa";
    const QString ENTRY_BG = "#14141e";
    const QString BTN_ACT  = "#6c5ce7";
    const QString ERROR_C  = "#f87171";
    const QString SUCCESS  = "#4ade80";

    QString mode;

    QLabel *inputLabel = nullptr;
    QLabel *resultLabel = nullptr;
    QLabel *statusLabel = nullptr;

    QLineEdit *entry = nullptr;

    QPushButton *btnM = nullptr;
    QPushButton *btnH = nullptr;

    QString resultText = "—";
    QString statusText = "";

    void buildUI() {
        setStyleSheet(QString("background:%1;").arg(BG));

        auto *root = new QVBoxLayout(this);
        root->setContentsMargins(25, 25, 25, 25);
        root->setSpacing(0);

        auto *outer = new QFrame();
        outer->setStyleSheet(QString("background:%1; border:none;").arg(BG));
        root->addWidget(outer);

        auto *outerLayout = new QVBoxLayout(outer);
        outerLayout->setContentsMargins(0, 0, 0, 0);
        outerLayout->setSpacing(0);

        QLabel *title = new QLabel("Time Converter");
        title->setAlignment(Qt::AlignHCenter);
        title->setStyleSheet(QString("color:%1; background:%2;").arg(TEXT_PRI, BG));
        title->setFont(QFont("Segoe UI", 22, QFont::Bold));
        outerLayout->addWidget(title);

        outerLayout->addSpacing(4);

        QLabel *subtitle = new QLabel("Hours  ↔  Minutes");
        subtitle->setAlignment(Qt::AlignHCenter);
        subtitle->setStyleSheet(QString("color:%1; background:%2;").arg(TEXT_SEC, BG));
        subtitle->setFont(QFont("Segoe UI", 9));
        outerLayout->addWidget(subtitle);

        outerLayout->addSpacing(22);

        // Main card
        QFrame *card = new QFrame();
        card->setStyleSheet(QString(
                                "QFrame {"
                                " background:%1;"
                                " border:1px solid %2;"
                                "}"
                                ).arg(GLASS_BG, GLASS_BD));
        outerLayout->addWidget(card);

        auto *inn = new QVBoxLayout(card);
        inn->setContentsMargins(28, 28, 28, 28);
        inn->setSpacing(0);

        // Toggle row
        QFrame *tog = new QFrame();
        tog->setStyleSheet(QString(
                               "QFrame {"
                               " background:%1;"
                               " border:none;"
                               "}"
                               ).arg(GLASS_BD));
        inn->addWidget(tog);
        inn->addSpacing(22);

        auto *togLayout = new QHBoxLayout(tog);
        togLayout->setContentsMargins(0, 0, 0, 0);
        togLayout->setSpacing(0);

        btnM = new QPushButton("Hour → Min");
        btnH = new QPushButton("Min → Hour");

        btnM->setCursor(Qt::PointingHandCursor);
        btnH->setCursor(Qt::PointingHandCursor);

        connect(btnM, &QPushButton::clicked, this, [this]() { setMode("M"); });
        connect(btnH, &QPushButton::clicked, this, [this]() { setMode("H"); });

        togLayout->addWidget(btnM);
        togLayout->addWidget(btnH);

        // Input label
        inputLabel = new QLabel("Hours");
        inputLabel->setStyleSheet(QString("color:%1; background:%2;").arg(TEXT_SEC, GLASS_BG));
        inputLabel->setFont(QFont("Segoe UI", 11));
        inn->addWidget(inputLabel);
        inn->addSpacing(6);

        // Entry box frame
        QFrame *ef = new QFrame();
        ef->setStyleSheet(QString(
                              "QFrame {"
                              " background:%1;"
                              " border:1px solid %2;"
                              "}"
                              ).arg(ENTRY_BG, GLASS_BD));
        inn->addWidget(ef);
        inn->addSpacing(20);

        auto *efLayout = new QVBoxLayout(ef);
        efLayout->setContentsMargins(14, 12, 14, 12);

        entry = new QLineEdit();
        entry->setStyleSheet(QString(
                                 "QLineEdit {"
                                 " background:%1;"
                                 " color:%2;"
                                 " border:none;"
                                 " selection-background-color:%3;"
                                 "}"
                                 ).arg(ENTRY_BG, TEXT_PRI, ACCENT2));
        entry->setFont(QFont("Segoe UI", 15));

        QRegularExpression rx("^\\d*\\.?\\d*$");
        entry->setValidator(new QRegularExpressionValidator(rx, this));
        connect(entry, &QLineEdit::returnPressed, this, [this]() { convert(); });

        efLayout->addWidget(entry);

        // Convert button
        QPushButton *convertBtn = new QPushButton("Convert");
        convertBtn->setCursor(Qt::PointingHandCursor);
        convertBtn->setStyleSheet(QString(
                                      "QPushButton {"
                                      " background:%1;"
                                      " color:%2;"
                                      " border:none;"
                                      " padding:12px;"
                                      " font-size:11pt;"
                                      " font-weight:bold;"
                                      "}"
                                      "QPushButton:pressed {"
                                      " background:%3;"
                                      " color:%2;"
                                      "}"
                                      ).arg(ACCENT, TEXT_PRI, BTN_ACT));
        connect(convertBtn, &QPushButton::clicked, this, [this]() { convert(); });
        inn->addWidget(convertBtn);

        // Result card
        outerLayout->addSpacing(12);

        QFrame *rc = new QFrame();
        rc->setStyleSheet(QString(
                              "QFrame {"
                              " background:%1;"
                              " border:1px solid %2;"
                              "}"
                              ).arg(GLASS_BG, GLASS_BD));
        outerLayout->addWidget(rc);

        auto *ri = new QVBoxLayout(rc);
        ri->setContentsMargins(28, 20, 28, 20);
        ri->setSpacing(0);

        QLabel *resultTitle = new QLabel("Result");
        resultTitle->setStyleSheet(QString("color:%1; background:%2;").arg(TEXT_SEC, GLASS_BG));
        resultTitle->setFont(QFont("Segoe UI", 11));
        ri->addWidget(resultTitle);
        ri->addSpacing(4);

        resultLabel = new QLabel(resultText);
        resultLabel->setStyleSheet(QString("color:%1; background:%2;").arg(TEXT_PRI, GLASS_BG));
        resultLabel->setFont(QFont("Segoe UI", 28, QFont::Bold));
        ri->addWidget(resultLabel);
        ri->addSpacing(8);

        statusLabel = new QLabel(statusText);
        statusLabel->setStyleSheet(QString("color:%1; background:%2;").arg(SUCCESS, GLASS_BG));
        statusLabel->setFont(QFont("Segoe UI", 9));
        ri->addWidget(statusLabel);
    }

    void centerWindow() {
        QScreen *screen = QGuiApplication::primaryScreen();
        if (!screen) return;

        QRect screenGeometry = screen->availableGeometry();
        int x = screenGeometry.x() + (screenGeometry.width() - width()) / 2;
        int y = screenGeometry.y() + (screenGeometry.height() - height()) / 2;
        move(x, y);
    }

    void setMode(const QString &m) {
        mode = m;
        entry->clear();
        resultText = "—";
        resultLabel->setText(resultText);
        statusText.clear();
        statusLabel->setText(statusText);
        refreshToggle();
        inputLabel->setText(m == "M" ? "Hours" : "Minutes");
        entry->setFocus();
    }

    void refreshToggle() {
        auto setBtnStyle = [this](QPushButton *btn, const QString &bg, const QString &fg) {
            btn->setStyleSheet(QString(
                                   "QPushButton {"
                                   " background:%1;"
                                   " color:%2;"
                                   " border:none;"
                                   " padding:9px;"
                                   " font-size:11pt;"
                                   " font-weight:bold;"
                                   "}"
                                   "QPushButton:pressed {"
                                   " background:%3;"
                                   " color:%4;"
                                   "}"
                                   ).arg(bg, fg, BTN_ACT, TEXT_PRI));
        };

        if (mode == "M") {
            setBtnStyle(btnM, ACCENT, TEXT_PRI);
            setBtnStyle(btnH, GLASS_BD, TEXT_SEC);
        } else {
            setBtnStyle(btnM, GLASS_BD, TEXT_SEC);
            setBtnStyle(btnH, ACCENT, TEXT_PRI);
        }
    }

    void flash(const QString &msg, const QString &color) {
        statusText = msg;
        statusLabel->setText(statusText);
        statusLabel->setStyleSheet(QString("color:%1; background:%2;").arg(color, GLASS_BG));
    }

    void blink() {
        resultLabel->setStyleSheet(QString("color:%1; background:%2;").arg(ACCENT2, GLASS_BG));
        QTimer::singleShot(150, this, [this]() {
            resultLabel->setStyleSheet(QString("color:%1; background:%2;").arg(TEXT_PRI, GLASS_BG));
        });
    }

    void convert() {
        QString raw = entry->text().trimmed();

        if (raw.isEmpty()) {
            flash("Enter a value first", ERROR_C);
            return;
        }

        bool ok = false;
        double value = raw.toDouble(&ok);

        if (!ok) {
            flash("Numbers only, please", ERROR_C);
            return;
        }

        if (value < 0) {
            flash("Negative time? Really?", ERROR_C);
            return;
        }

        double hoursEq = 0.0;

        if (mode == "M") {
            double minutes = value * 60.0;
            hoursEq = value;
            resultText = QString("%1 min").arg(QString::number(minutes, 'g', 15));
            resultLabel->setText(resultText);
        } else if (mode == "H") {
            hoursEq = value / 60.0;
            resultText = QString("%1 hr").arg(QString::number(hoursEq, 'g', 5));
            resultLabel->setText(resultText);
        } else {
            flash("Invalid Option", ERROR_C);
            resultText = "—";
            resultLabel->setText(resultText);
            return;
        }

        if (hoursEq > 24) {
            flash("Too long!", ACCENT2);
        } else {
            flash("Less than a day processing.", SUCCESS);
        }

        blink();
    }
};

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);

    TimeConverter window;
    window.show();

    return app.exec();
}