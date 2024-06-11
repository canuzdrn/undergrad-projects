#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindow; }
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private:
    Ui::MainWindow *ui;
private slots:
    void plusClick();          ///sets isPlus to true for the subsequent evaluation of an addition operation
    void minusClick();         ///sets isMinus to true for the subsequent evaluation of a subtraction operation
    void equalClick();         /// Calls operation manager for expression evaluation
    void clearClick();         /// Sets the operator being edited currently to Zero
    void numberclick();        //// Multiplies the operator being handled by 16 and add to it the value resulting from the subtraction of the text on the button from 48
    void letterclick();        //// Multiplies the operator being handled by 16 and add to it the value resulting from the subtraction of the text on the button from 65

public:
    void operationManager();       //// The main driver function which evaluates infix expressions and outputs prints the result on the LCD
    int currentOperator = 1;       ///// Determines whether we are handling the first operator or the second one
    bool isPlus = false;           //// As we use infix notation, we have to store the type of the operation before evaluation
    bool isMinus = false;
    bool isEqual = false;          /////////////Checks whether the user pressed on the equal button or not. Handy in Handling Extreme Cases
    ////
    bool operatorPressed = false; //// true if the last button pressed is operator , false otherwise
    ////
    bool nothingSoFar = true;   //// true if there is nothing pressed so far (used for starting with "-" input    Example : -12 + 3)
    bool goingToInvert = false; //// if there is nothing so far goingToInvert evaluates to trur which indicates that next operand is going to be inverted (*-1)
    ////
    int firstOperator = 0;         ////Stores the cumulative integer equivalent of first operator's hexadecimal value.
    int secondOperator = 0;        ////Stores the cumulative integer equivalent of second operator's hexadecimal value.
};
#endif // MAINWINDOW_H
