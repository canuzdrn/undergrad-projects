#include "mainwindow.h"
#include "./ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    ui->LCD->setDigitCount(10);
    setWindowTitle(tr("Hexadecimal Calculator"));
    QPushButton *digitButton[10];
    for (int i = 0 ;i < 10; i++ ){
        QString butName = "bt" + QString::number(i);
        digitButton[i] = MainWindow::findChild<QPushButton *>(butName);
        connect(digitButton[i], SIGNAL(clicked()), this,SLOT(numberclick()));
    }
    QPushButton *characterButton[6];
    int asciiA = 65;
    for (int j = 0 ;j < 6; j++ ){
        char ch = asciiA + j;
        QString s = "";
        s += ch;
        QString butonName = "bt" + s;
        characterButton[j] = MainWindow::findChild<QPushButton *>(butonName);
        connect(characterButton[j], SIGNAL(clicked()), this,SLOT(letterclick()));

    }
    connect(ui->plus, SIGNAL(clicked()), this, SLOT(plusClick()));
    connect(ui->minus, SIGNAL(clicked()), this, SLOT(minusClick()));
    connect(ui->equal, SIGNAL(clicked()), this, SLOT(equalClick()));
    connect(ui->clear, SIGNAL(clicked()), this, SLOT(clearClick()));
}

MainWindow::~MainWindow()
{
    delete ui;
}
void MainWindow::operationManager(){

    if (isPlus == true && isEqual == true){
        if (operatorPressed){
            if(goingToInvert == true){
                firstOperator *= -1;
                goingToInvert = false;
            }
            int finalRes = firstOperator * 2;
            ui->LCD->display(finalRes);
            firstOperator = firstOperator + firstOperator;
            secondOperator = 0;
        }
        else{
            int finalRes = firstOperator + secondOperator;
            ui->LCD->display(finalRes);
            currentOperator --;
            firstOperator = firstOperator + secondOperator;
            secondOperator = 0;
            currentOperator = 1;
        }
    }
    else if (isMinus == true && isEqual == true){
        if (operatorPressed){
            int finalRes = firstOperator * 0;
            ui->LCD->display(finalRes);
            firstOperator = firstOperator - firstOperator;
            secondOperator = 0;
        }
        else{
            int finalRes = firstOperator - secondOperator;
            ui->LCD->display(finalRes);
            currentOperator --;
            firstOperator = firstOperator - secondOperator;
            secondOperator = 0;
            currentOperator = 1;
        }
    }
    else if (isPlus == true && isEqual == false){
        if(currentOperator == 1){
            currentOperator++;
            if(goingToInvert == true){
                firstOperator *= -1;
                goingToInvert = false;
            }
        }
        else{
            int finalRes = firstOperator + secondOperator;
            ui->LCD->display(finalRes);
            firstOperator = firstOperator + secondOperator;
            secondOperator = 0;
        }
    }
    else if (isMinus == true && isEqual == false){
        if(nothingSoFar == true){
            goingToInvert = true;
        }
        else{
            if (currentOperator == 1){
                currentOperator++;
                if(goingToInvert == true){
                    firstOperator *= -1;
                    goingToInvert = false;
                }
            }
            else{
                int finalRes = firstOperator - secondOperator;
                ui->LCD->display(finalRes);
                firstOperator = firstOperator - secondOperator;
                secondOperator = 0;
            }
        }
    }
    else{
        return;
    }

}
void MainWindow::plusClick(){
    operatorPressed = true;
    isPlus = true;
    isMinus = false;
    isEqual = false;
    operationManager();
}
void MainWindow::minusClick(){
    operatorPressed = true;
    isMinus = true;
    isPlus = false;
    isEqual = false;
    operationManager();
}
void MainWindow::equalClick(){
    isEqual = true;
    operationManager();
}
void MainWindow::clearClick(){
    operatorPressed = false;
    nothingSoFar = true;
    ui->LCD->display(0);
    if (currentOperator == 1){
        firstOperator = 0;
    }
    else{
        secondOperator = 0;
    }
}
void MainWindow::numberclick(){
    nothingSoFar = false;
    if (currentOperator == 1){
        QPushButton *button = (QPushButton*)sender();
        QString buttonText = button->text();
        int displayedVal = ui->LCD->intValue();
        int newVal = displayedVal*16 + buttonText.toInt();
        ui->LCD->display(newVal);

        firstOperator = firstOperator * 16;
        firstOperator = firstOperator + static_cast<int>(buttonText.toStdString().c_str()[0] - '0');
    }
    else{
        if(operatorPressed == true){
            QPushButton *button = (QPushButton*)sender();
            QString buttonText = button->text();
            int newVal = buttonText.toInt();
            ui->LCD->display(newVal);

            secondOperator = secondOperator * 16;
            secondOperator = secondOperator + static_cast<int>(buttonText.toStdString().c_str()[0] - '0');
            operatorPressed = false;
        }
        else{
            QPushButton *button = (QPushButton*)sender();
            QString buttonText = button->text();
            int displayedVal = ui->LCD->intValue();
            int newVal = displayedVal*16 + buttonText.toInt();
            ui->LCD->display(newVal);

            secondOperator = secondOperator * 16;
            secondOperator = secondOperator + static_cast<int>(buttonText.toStdString().c_str()[0] - '0');
        }
    }

}
void MainWindow::letterclick(){
    nothingSoFar = false;
    if (currentOperator == 1){
        QPushButton *button = (QPushButton*)sender();
        QString buttonText = button->text();
        int buttonVal = int(buttonText.toStdString().c_str()[0]) - 55;
        int displayedVal = ui->LCD->intValue();
        int newVal = displayedVal*16 + buttonVal;
        ui->LCD->display(newVal);

        firstOperator = firstOperator * 16;
        firstOperator = firstOperator + buttonVal;
    }
    else{
        if(operatorPressed == true){
            QPushButton *button = (QPushButton*)sender();
            QString buttonText = button->text();
            int buttonVal = int(buttonText.toStdString().c_str()[0]) - 55;
            ui->LCD->display(buttonVal);

            secondOperator = secondOperator * 16;
            secondOperator = secondOperator + buttonVal;
            operatorPressed = false;
        }
        else{
            QPushButton *button = (QPushButton*)sender();
            QString buttonText = button->text();
            int buttonVal = int(buttonText.toStdString().c_str()[0]) - 55;
            int displayedVal = ui->LCD->intValue();
            int newVal = displayedVal*16 + buttonVal;
            ui->LCD->display(newVal);

            secondOperator = secondOperator * 16;
            secondOperator = secondOperator + buttonVal;
        }
    }

}
