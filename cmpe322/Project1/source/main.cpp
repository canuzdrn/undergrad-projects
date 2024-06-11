#include <iostream>
#include <string>
#include <cstring>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <vector>
#include <sstream>
#include <fstream>
#include <pwd.h>

using namespace std;

vector<string> parsedCommand;
vector<string> commandHistory;

void tokenize(string command){  /* this function basically tokenize the given command by using whitespace as its delimeter*/
    
    stringstream data(command);

    string line;
    while(getline(data,line,' ')){
        parsedCommand.push_back(line);
    }

}

int main(){
    string inp;
    pid_t pid;
    passwd* user = getpwuid(getuid());  /* gets the user*/
    char* username = user->pw_name;     /* gets the username from the passwd object (user)*/
    while(true){
        printf("%s >>> ",username);
        getline(cin,inp);
        tokenize(inp);

        if(commandHistory.size() > 15){
            commandHistory.erase(commandHistory.begin());
        }

        if(parsedCommand[0] != "dididothat"){   /* i do not add dididothat to command history*/
            commandHistory.push_back(inp);
        }

        if(parsedCommand[0] == "listdir"){  /* if the command is listdir we execute ls linux command that is located at /usr/bin*/
            pid = fork();
            if(pid == 0){
                execl("/bin/ls","/bin/ls",NULL); // ending the argument list with NULL since execl() is a sentinel function
            }
            else if(pid == -1){
                cout << "Fork failed..." << endl;
            }
            else{
                wait(NULL);
            }
        }
        else if(parsedCommand[0] == "mycomputername"){  /* if the command is mycomputername we execute hostname linux command that is located at /usr/bin*/
            pid = fork();
            if(pid == 0){
                execl("/bin/hostname","/bin/hostname",NULL);
            }
            else if(pid == -1){
                cout << "Fork failed..." << endl;
            }
            else{
                wait(NULL);
            }
        }
        else if(parsedCommand[0] == "whatsmyip"){       /* if the command is whatsmyip we execute hostname -i linux command that is located at /usr/bin */
            pid = fork();
            if(pid == 0){
                execl("/bin/hostname","/bin/hostname","-i",NULL);
            }
            else if(pid == -1){
                cout << "Fork failed..." << endl;
            }
            else{
                wait(NULL);
            }
        }
        else if(parsedCommand[0] == "printfile" && parsedCommand.size() == 2){  /* printfile (filename) command basically print the file line by line as the user presses Enter in order to process along the file*/
            string filename = parsedCommand[1], line;
            int lineNum = 0, currentLine = 0;
            char keyboardPress;
            ifstream myfile_for_len (filename);

            while(getline(myfile_for_len,line)){
                lineNum++;
            }
            myfile_for_len.close();

            ifstream myfile (filename);
            if(lineNum > 0 && lineNum != 1){
                getline(myfile,line);
                cout << line;
                currentLine++;
            }
            else if(lineNum == 1){
                getline(myfile,line);
                cout << line << endl;
            }
            else{
                parsedCommand.clear();  // due to continue we do not visit the end of while --> so cleaned our parsedCommand vector here
                continue;
            }

            while(getline(myfile,line)){
                currentLine++;
                while(true){
                scanf("%c",&keyboardPress);         // used scanf instead of cin, since cin did not work for detecting new line char
                    if(keyboardPress == '\n'){
                        if(currentLine == lineNum){
                            cout << line << endl;
                        }
                        else{
                            cout << line;
                        }
                        break;
                    }
                    else{
                        cout << keyboardPress;
                    }
                }
            }

            myfile.close();
        }
        else if(parsedCommand[0] == "printfile" && parsedCommand.size() == 4){  /* printfile filename1 > filename2 command basically executes cp filename1 filename2 linux command*/
            string copyFrom,copyTo;
            copyFrom = parsedCommand.at(1);
            copyTo = parsedCommand.at(3);
            ifstream fromFile;
            ofstream toFile;
            fromFile.open(copyFrom);
            toFile.open(copyTo);
            pid = fork();
            if(pid == 0){
                execlp("/bin/cp","/bin/cp",copyFrom.c_str(),copyTo.c_str(),NULL);
            }
            else if(pid == -1){
                cout << "Fork failed..." << endl;
            }
            else{
                wait(NULL);
            }
            fromFile.close();
            toFile.close();
        }
        else if(parsedCommand[0] == "dididothat"){  /* dididothat sample_command is basically searches for a match for sample_command in commandHistory vector*/
            string searchedCommand = "";
            int found = -1;

            for(int i=1; i<parsedCommand.size();i++){
                if(i > 1){
                    searchedCommand += " ";     // since the given command is parsed w.r.t. whitespace we need to add whitespace manually for correct string comparison result
                    searchedCommand += parsedCommand.at(i);
                }
                else{
                    searchedCommand += parsedCommand.at(i);
                }
            }

            for(int i=0;i<commandHistory.size();i++){
                if(commandHistory.at(i) == searchedCommand){
                    found = 1;
                    break;
                }
            }
            if(found == 1){
                cout << "Yes" << endl;
            }
            else{
                cout << "No" << endl;
            }
        }

        else if(parsedCommand[0] == "hellotext"){   /* after downloading the Gedit editor, it automatically adds the command gedit into /usr/bin*/
            pid = fork();                           /* Hence, we can directly execute the linux command "gedit" in order to open the gedit text editor*/
            if(pid == 0){
                execl("/bin/gedit","/bin/gedit",NULL);
            }
            else if(pid == -1){
                cout << "Fork failed..." << endl;
            }
            else{
                wait(NULL);
            }
        }
        else if(parsedCommand[0] == "exit"){
            exit(0);
        }
        else{
            cout << "Invalid command..!" << endl;
        }
        parsedCommand.clear();
    }
    
    return 0;
}
