#include <QApplication>
#include <QWidget>
#include <QFrame>
#include <QLabel>
#include <QLineEdit>
#include <QPushButton>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QScreen>
#include <QString>
#include <QFont>
#include <QCursor>
#include <functional>

class SegmentedToggle : public QFrame {
public:
    explicit SegmentedToggle(std::function<void(const QString&)> callback,
                             QWidget *parent = nullptr)
        : QFrame(parent), onModeChanged(callback) {
        setObjectName("toggle");
        setFixedHeight(44);

        btnA = new QPushButton("°C → °F");
        btnB = new QPushButton("°F → °C");

        btnA->setCursor(Qt::PointingHandCursor);
        btnB->setCursor(Qt::PointingHandCursor);
        btnA->setFlat(true);
        btnB->setFlat(true);

        QFont f("Segoe UI", 10, QFont::Bold);
        btnA->setFont(f);
        btnB->setFont(f);

        auto *layout = new QHBoxLayout(this);
        layout->setContentsMargins(0, 0, 0, 0);
        layout->setSpacing(0);
        layout->addWidget(btnA);
        layout->addWidget(btnB);

        mode = "A";
        applyModeStyles();

        QObject::connect(btnA, &QPushButton::clicked, [this]() {
            setMode("A");
        });
        QObject::connect(btnB, &QPushButton::clicked, [this]() {
            setMode("B");
        });
    }

    QString currentMode() const {
        return mode;
    }

private:
    QPushButton *btnA;
    QPushButton *btnB;
    QString mode;
    std::function<void(const QString&)> onModeChanged;

    void applyModeStyles() {
        const QString accent = "#7c8cff";
        const QString panel = "#17181f";
        const QString textDim = "#9aa0ad";

        if (mode == "A") {
            btnA->setStyleSheet(QString(
                                    "QPushButton { background:%1; color:white; border:none; }"
                                    ).arg(accent));
            btnB->setStyleSheet(QString(
                                    "QPushButton { background:%1; color:%2; border:none; }"
                                    ).arg(panel, textDim));
        } else {
            btnA->setStyleSheet(QString(
                                    "QPushButton { background:%1; color:%2; border:none; }"
                                    ).arg(panel, textDim));
            btnB->setStyleSheet(QString(
                                    "QPushButton { background:%1; color:white; border:none; }"
                                    ).arg(accent));
        }
    }

    void setMode(const QString &newMode) {
        mode = newMode;
        applyModeStyles();
        if (onModeChanged) {
            onModeChanged(mode);
        }
    }
};

class HoverButton : public QPushButton {
public:
    HoverButton(const QString &text, bool primary, QWidget *parent = nullptr)
        : QPushButton(text, parent), isPrimary(primary) {
        setCursor(Qt::PointingHandCursor);
        setFixedHeight(44);
        setFont(QFont("Segoe UI", 10, QFont::Bold));
        setFlat(false);
        setMouseTracking(true);
        updateStyle(false);
    }

protected:
    void enterEvent(QEnterEvent *) override {
        updateStyle(true);
    }

    void leaveEvent(QEvent *) override {
        updateStyle(false);
    }

private:
    bool isPrimary;

    void updateStyle(bool hover) {
        QString bg;
        QString fg;
        QString border;

        if (isPrimary) {
            bg = hover ? "#93a0ff" : "#7c8cff";
            fg = "white";
            border = "#7c8cff";
        } else {
            bg = hover ? "#252833" : "#1d1f27";
            fg = "#f3f4f6";
            border = "#2a2d37";
        }

        setStyleSheet(QString(
                          "QPushButton {"
                          "background:%1;"
                          "color:%2;"
                          "border:1px solid %3;"
                          "padding:12px 16px;"
                          "text-align:center;"
                          "}"
                          ).arg(bg, fg, border));
    }
};

class TemperatureConverter : public QWidget {
public:
    TemperatureConverter() {
        setWindowTitle("Temperature Converter");
        setFixedWidth(420);

        buildUI();
        setFixedHeight(WIN_H_NORMAL);
        centerWindow();
    }

private:
    const QString BG       = "#101014";
    const QString PANEL    = "#17181f";
    const QString PANEL2   = "#1d1f27";
    const QString BORDER   = "#2a2d37";
    const QString ACCENT   = "#7c8cff";
    const QString TEXT     = "#f3f4f6";
    const QString TEXTDIM  = "#9aa0ad";
    const QString SUCCESS  = "#4ade80";
    const QString WARN     = "#facc15";
    const QString ERROR    = "#f87171";
    const QString ENTRYBG  = "#111319";

    const int WIN_W = 420;
    const int WIN_H_NORMAL = 520;
    const int WIN_H_RESULT = 680;
    QString mode = "A";

    QLabel *inputLabel;
    QLineEdit *entryField;
    QLabel *errLabel;

    QFrame *resultFrame;
    QLabel *statusLabel;
    QLabel *resultValueLabel;
    QLabel *resultUnitLabel;
    QLabel *detailLabel;

    HoverButton *resetBtn;
    SegmentedToggle *toggle;

    void buildUI() {
        setStyleSheet(QString("background:%1;").arg(BG));

        auto *outerLayout = new QVBoxLayout(this);
        outerLayout->setContentsMargins(24, 24, 24, 24);

        QFrame *card = new QFrame;
        card->setStyleSheet(QString(
                                "QFrame { background:%1; border:1px solid %2; }"
                                ).arg(PANEL, BORDER));

        outerLayout->addWidget(card);

        auto *cardLayout = new QVBoxLayout(card);
        cardLayout->setContentsMargins(24, 22, 24, 22);
        cardLayout->setSpacing(0);

        QLabel *title = new QLabel("Temperature Converter");
        title->setStyleSheet(QString("color:%1;").arg(TEXT));
        title->setFont(QFont("Segoe UI", 20, QFont::Bold));
        cardLayout->addWidget(title);

        cardLayout->addSpacing(4);

        QLabel *subtitle = new QLabel("Convert values instantly with a clean minimal interface.");
        subtitle->setStyleSheet(QString("color:%1;").arg(TEXTDIM));
        subtitle->setFont(QFont("Segoe UI", 9));
        cardLayout->addWidget(subtitle);

        cardLayout->addSpacing(18);

        QFrame *divider = new QFrame;
        divider->setFixedHeight(1);
        divider->setStyleSheet(QString("background:%1; border:none;").arg(BORDER));
        cardLayout->addWidget(divider);

        cardLayout->addSpacing(18);

        QLabel *conversionLabel = new QLabel("Conversion");
        conversionLabel->setStyleSheet(QString("color:%1;").arg(TEXTDIM));
        conversionLabel->setFont(QFont("Segoe UI", 9));
        cardLayout->addWidget(conversionLabel);

        cardLayout->addSpacing(8);

        toggle = new SegmentedToggle([this](const QString &m) {
            onModeChanged(m);
        });
        toggle->setStyleSheet(QString(
                                  "QFrame#toggle { background:%1; border:1px solid %2; }"
                                  ).arg(PANEL, BORDER));
        cardLayout->addWidget(toggle);

        cardLayout->addSpacing(18);

        inputLabel = new QLabel("Temperature (°C)");
        inputLabel->setStyleSheet(QString("color:%1;").arg(TEXTDIM));
        inputLabel->setFont(QFont("Segoe UI", 9));
        cardLayout->addWidget(inputLabel);

        cardLayout->addSpacing(8);

        entryField = new QLineEdit;
        entryField->setFixedHeight(50);
        entryField->setFont(QFont("Segoe UI", 16));
        entryField->setStyleSheet(QString(
                                      "QLineEdit {"
                                      "background:%1;"
                                      "color:%2;"
                                      "border:1px solid %3;"
                                      "padding:0 14px;"
                                      "}"
                                      "QLineEdit:focus { border:1px solid %4; }"
                                      ).arg(ENTRYBG, TEXT, BORDER, ACCENT));
        cardLayout->addWidget(entryField);

        QObject::connect(entryField, &QLineEdit::returnPressed, [this]() {
            convert();
        });

        cardLayout->addSpacing(8);

        errLabel = new QLabel(" ");
        errLabel->setStyleSheet(QString("color:%1;").arg(ERROR));
        errLabel->setFont(QFont("Segoe UI", 9));
        cardLayout->addWidget(errLabel);

        cardLayout->addSpacing(12);

        auto *convertBtn = new HoverButton("Convert", true);
        cardLayout->addWidget(convertBtn);

        QObject::connect(convertBtn, &QPushButton::clicked, [this]() {
            convert();
        });

        cardLayout->addSpacing(18);

        resultFrame = new QFrame;
        resultFrame->setStyleSheet(QString(
                                       "QFrame { background:%1; border:1px solid %2; }"
                                       ).arg(PANEL2, BORDER));

        auto *resultLayout = new QVBoxLayout(resultFrame);
        resultLayout->setContentsMargins(18, 16, 18, 16);
        resultLayout->setSpacing(0);

        auto *topRow = new QHBoxLayout;
        QLabel *resultText = new QLabel("Result");
        resultText->setStyleSheet(QString("color:%1;").arg(TEXTDIM));
        resultText->setFont(QFont("Segoe UI", 9));

        statusLabel = new QLabel("");
        statusLabel->setStyleSheet(QString("color:%1;").arg(WARN));
        statusLabel->setFont(QFont("Segoe UI", 9, QFont::Bold));

        topRow->addWidget(resultText);
        topRow->addStretch();
        topRow->addWidget(statusLabel);
        resultLayout->addLayout(topRow);

        resultLayout->addSpacing(6);

        auto *valueRow = new QHBoxLayout;
        valueRow->setSpacing(8);

        resultValueLabel = new QLabel("");
        resultValueLabel->setStyleSheet(QString("color:%1;").arg(TEXT));
        resultValueLabel->setFont(QFont("Segoe UI", 28, QFont::Bold));

        resultUnitLabel = new QLabel("");
        resultUnitLabel->setStyleSheet(QString("color:%1;").arg(ACCENT));
        resultUnitLabel->setFont(QFont("Segoe UI", 13, QFont::Bold));

        valueRow->addWidget(resultValueLabel);
        valueRow->addWidget(resultUnitLabel);
        valueRow->addStretch();
        resultLayout->addLayout(valueRow);

        resultLayout->addSpacing(6);

        detailLabel = new QLabel("");
        detailLabel->setStyleSheet(QString("color:%1;").arg(TEXTDIM));
        detailLabel->setFont(QFont("Segoe UI", 9));
        resultLayout->addWidget(detailLabel);

        resultFrame->hide();
        cardLayout->addWidget(resultFrame);

        cardLayout->addSpacing(12);

        resetBtn = new HoverButton("Reset", false);
        resetBtn->hide();
        cardLayout->addWidget(resetBtn);

        QObject::connect(resetBtn, &QPushButton::clicked, [this]() {
            reset();
        });
    }

    void onModeChanged(const QString &newMode) {
        mode = newMode;
        errLabel->setText(" ");
        hideResult();

        if (mode == "A") {
            inputLabel->setText("Temperature (°C)");
        } else {
            inputLabel->setText("Temperature (°F)");
        }
    }

    void convert() {
        QString raw = entryField->text().trimmed();

        if (raw.isEmpty()) {
            errLabel->setText("Enter a temperature value.");
            hideResult();
            return;
        }

        bool ok = false;
        double value = raw.toDouble(&ok);

        if (!ok) {
            errLabel->setText("Please enter a valid number.");
            hideResult();
            return;
        }

        errLabel->setText(" ");

        double converted;
        QString outUnit;
        QString detail;

        if (mode == "A") {
            converted = (value * 9.0 / 5.0) + 32.0;
            outUnit = "°F";
            detail = QString("%1 °C converted to Fahrenheit").arg(QString::number(value, 'f', 2));
        } else {
            converted = (value - 32.0) * 5.0 / 9.0;
            outUnit = "°C";
            detail = QString("%1 °F converted to Celsius").arg(QString::number(value, 'f', 2));
        }

        if (converted > 100.0) {
            statusLabel->setText("Too High");
            statusLabel->setStyleSheet(QString("color:%1;").arg(WARN));
        } else {
            statusLabel->setText("Normal");
            statusLabel->setStyleSheet(QString("color:%1;").arg(SUCCESS));
        }

        resultValueLabel->setText(QString::number(converted, 'f', 2));
        resultUnitLabel->setText(outUnit);
        detailLabel->setText(detail);

        showResult();
    }

    void showResult() {
        resultFrame->show();
        resetBtn->show();
        setFixedHeight(WIN_H_RESULT);
        centerWindow();
    }

    void hideResult() {
        resultFrame->hide();
        resetBtn->hide();
        setFixedHeight(WIN_H_NORMAL);
        centerWindow();
    }

    void reset() {
        entryField->clear();
        errLabel->setText(" ");
        resultValueLabel->setText("");
        resultUnitLabel->setText("");
        detailLabel->setText("");
        statusLabel->setText("");
        entryField->setFocus();
        hideResult();
    }

    void centerWindow() {
        QRect screenGeometry = QGuiApplication::primaryScreen()->availableGeometry();
        int x = screenGeometry.x() + (screenGeometry.width() - WIN_W) / 2;
        int y = screenGeometry.y() + (screenGeometry.height() - height()) / 1.5;
        move(x, y);
    }
};

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);

    TemperatureConverter window;
    window.show();

    return app.exec();
}