#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>


int num_fills = 0;
int fill_dims[20][2];
char* fill_vars[20];

// choose_handler is a function that handles expressions in choose(...) and prints to the "file.c" file
void choose_handler();
// choose_handler is a function that handles expressions in for(...) and prints to the "file.c" file
void for_handler();
// delete_char is a helper function that deletes a char at given position
void delete_char();
// concat is a helper function that concatenates char to a char array
void concat();
// elements_in_fillvars is a function that returns the number of elements in fillvars
int elements_in_fillvars();
// sqrt() handler
void sqt();
// tr() handler
void tr();
// multiplication operation (*) handler
void mult();
// subtraction (-) operation handler
void sub();
// addition (+) operation handler
void add();

// helper global integer variables
int total_sum = 0;
int stack_usage = 0;

// print(..) for .mat file
void printer();
// helper function for tokenizing the lines
void myInsert(char* arr,int position,char value);
// tokenizing function that adding space char on necessary places
void tokenize();
// trims whitespaces for readability
void trim_space();
// parses the tokenized lines to 2D char* array
void token_placement();
// char* array that holds lines as tokenized
char *tokenizedLines[80];
// 2d char array that holds tokens seperately line by line
char *lineTokens[80][80];
void replace_n();
// helper append function
void array_append();
// return 1 if given name is valid char name , 0 if not
int is_valid_varname();
// return 1 if given name is valid fill name , 0 if not
int is_valid_fillname();
// return 1 if given name is valid scalar name , 0 if not
int is_scalar();
// return 1 if given name is valid vector name , 0 if not
int is_vector();
// return 1 if given name is valid matrix name , 0 if not
int is_matrix();
// returns index of given variable in all_variables array
int indexof();
// converts flot to int if the float is a whole number ex: 1.000000, 14.000000
int float_conversion(float f);

// checks whether given word or line has square bracket or not
int has_square_bracket();
// checks whether given word or line has comma or not
int has_comma();

// checks if global stack is full or not
int isfull();
// checks if global stack is empty or not
int isempty();
// push() function for global stack
void push();
// pup() function for global stack
char* pop();
// peek() function for global stack
char* peek();
// checks if postfix evaluation stack is full or not
int isfullEvaluation();
// checks if postfix evaluation stack is empty or not
int isemptyEvaluation();
// push() function for postfix evaluation stack
void pushEvaluation();
// pop() function for postfix evaluation stack
char* popEvaluation();
// peek() function for postfix evaluation stack
char* peekEvaluation();
// function that returns the precedence of the given operator ex: + , * , tr , sqrt
int precedence();

// postfix evaluation stack
char evaluationStack[20][256];
int eval_count = 0;
char evaluated[256];
void clear_evaluated();
void to_evaluated();

// global stack that converts given expression to postfix
char stack[50][20];
int count = 0;
char postfix[20][20];
// converts given expression to postfix
void to_postfix();
// clears global stack
void clear_stack();
// clears postfix 2d array
void clear_postfix();
// clears evaluation stack
void clear_evalstack();

char* all_vars[60];
char* scalar_vars[20];
char* vector_vars[20];
char* matrix_vars[20];
float scal_vals[20];
int vector_dims[20][2];
int matrix_dims[20][2];
int all_dims[60][2];



int main(int argc,char *argv[]){
    char* codeC = "#include <stdio.h>\n"
                  "#include <stdlib.h>\n"
                  "#include <string.h>\n"
                  "#include <math.h>\n\n"
                  "int indexof(char* varname,char* arr[]){\n"
                  "int ind = 0;\n"
                  "while(arr[ind] != NULL){\n"
                  "if(strcmp(varname,arr[ind])== 0){\n"
                  "return ind;\n"
                  "}\n"
                  "ind++;\n"
                   "}\n"
                   "return -1;\n"
                   "}\n\n"
                  "int get_scal_val(float f){\n"
                  "return (int) f;\n"
                  "}\n\n"
                  "float choose(float exp1,float exp2,float exp3,float exp4){\n"
                  "if(exp1 == 0.000000){\n"
                  "return exp2;\n"
                  "}\n"
                  "else if(exp1 > 0){\n"
                  "return exp3;\n"
                  "}\n"
                  "else{\n"
                  "return exp4;\n"
                  "}\n"
                  "}\n\n"
                  "int main(){\n"
                "char* all_vars[60];\n"
                "char* scalar_vars[20];\n"
                "char* vector_vars[20];\n"
                "char* matrix_vars[20];\n"
                "float scal_vals[20];\n"
                "int vector_dims[20][2];\n"
                "int matrix_dims[20][2];\n";

    FILE *fp;
    char lines[256][80];
    /* Open file for reading Filename is given on the command line */
    if(argc != 2){
        printf("Give filename as command line argument");
        return(1);
    }

    fp = fopen(argv[1], "r");
    if(fp == NULL) {
        printf("Cannot open %s\n",argv[1]);
        return(1);
    }

    FILE *outptr;
    outptr = fopen("file.c","w");
    if (outptr == NULL) {
        printf("Error!");
        return(1);
    }
    fprintf(outptr, "%s", codeC);


    int i = 0;
    while( fgets(lines[i],80,fp) != NULL ) {
        i++;
    }
    int tot = i;
    for(i = 0; i < tot; i++){
        replace_n(lines[i]);
        tokenize(lines[i]);
        token_placement(lines[i],i);
    }

    int line_num = 0;
    char* varname;
    int dim1,dim2;

    int num_of_scalars = 0;
    int num_of_matrices = 0;
    int num_of_vectors = 0;
    int num_of_vars = 0;

    float scal_val = 0;


    int nested = 0;
// **************************************************************************************************************************************
// **************************************************************************************************************************************
// **************************************************************************************************************************************
    while(line_num < tot){
        if(lineTokens[line_num][0] != NULL){
            if(strcmp(lineTokens[line_num][0],"scalar") == 0){
                varname = lineTokens[line_num][1];
                all_vars[num_of_vars] = varname;
                all_dims[num_of_vars][0] = 1;
                all_dims[num_of_vars][1] = 1;
                fprintf(outptr,"all_vars[%d] = \"%s\";\n",num_of_vars,varname);
                scalar_vars[num_of_scalars] = varname;
                fprintf(outptr,"scalar_vars[%d] = \"%s\";\n",num_of_scalars,varname);
                scal_vals[num_of_scalars] = 0;
                fprintf(outptr,"scal_vals[%d] = 0;\n",num_of_scalars);
                fprintf(outptr,"float %s[1][1];\n",varname);
                num_of_scalars++;
                num_of_vars++;
            }
            else if(strcmp(lineTokens[line_num][0],"vector") == 0){
                varname = lineTokens[line_num][1];
                all_vars[num_of_vars] = varname;
                fprintf(outptr,"all_vars[%d] = \"%s\";\n",num_of_vars,varname);
                vector_vars[num_of_vectors] = varname;
                fprintf(outptr,"vector_vars[%d] = \"%s\";\n",num_of_vectors,varname);
                dim1 = strtol(lineTokens[line_num][3],NULL,10);
                dim2 = 1;
                all_dims[num_of_vars][0] = dim1;
                all_dims[num_of_vars][1] = dim2;
                vector_dims[num_of_vectors][0] = dim1;
                vector_dims[num_of_vectors][1] = dim2;
                fprintf(outptr,"vector_dims[%d][0] = %d;\n",num_of_vectors,dim1);
                fprintf(outptr,"vector_dims[%d][1] = %d;\n",num_of_vectors,dim1);
                fprintf(outptr,"float %s[%d][1];\n",varname,dim1);
                num_of_vectors++;
                num_of_vars++;
            }
            else if(strcmp(lineTokens[line_num][0],"matrix") == 0){
                varname = lineTokens[line_num][1];
                all_vars[num_of_vars] = varname;
                fprintf(outptr,"all_vars[%d] = \"%s\";\n",num_of_vars,varname);
                matrix_vars[num_of_matrices] = varname;
                fprintf(outptr,"matrix_vars[%d] = \"%s\";\n",num_of_matrices,varname);
                dim1 = strtol(lineTokens[line_num][3],NULL,10);
                dim2 = strtol(lineTokens[line_num][5],NULL,10);
                all_dims[num_of_vars][0] = dim1;
                all_dims[num_of_vars][1] = dim2;
                matrix_dims[num_of_matrices][0] = dim1;
                matrix_dims[num_of_matrices][1] = dim2;
                fprintf(outptr,"matrix_dims[%d][0] = %d;\n",num_of_matrices,dim1);
                fprintf(outptr,"matrix_dims[%d][1] = %d;\n",num_of_matrices,dim2);
                fprintf(outptr,"float %s[%d][%d];\n",varname,dim1,dim2);
                num_of_matrices++;
                num_of_vars++;
            }
            else if(strcmp(lineTokens[line_num][0],"print") == 0){
                char exp[10][50];
                int a = 2;
                int k = 0;
                while(strcmp(lineTokens[line_num][a],")") != 0){
                    strcpy(exp[k],lineTokens[line_num][a]);
                    a++;
                    k++;
                }
                printer(outptr,exp);
            }
            else if(lineTokens[line_num][1] != NULL && strcmp(lineTokens[line_num][1],"=") == 0){
                varname = lineTokens[line_num][0];
                int elem = 0;
                char rightexpr[100][100];
                char express[100] = "";
                int start = 2;
                while(lineTokens[line_num][start] != NULL){
                    strcpy(rightexpr[elem],lineTokens[line_num][start]);
                    start++;
                    elem++;
                }
                int o = 0;
                int indexing = 0;
                while(rightexpr[o][0] != '\0'){
                    if(strcmp(rightexpr[o],"[") == 0){
                        int space_ind = strlen(express)-1;
                        express[space_ind] = '\0';
                        indexing = 1;
                        strcat(express,rightexpr[o]);
                    }
                    else if(strcmp(rightexpr[o],"]") == 0){
                        indexing = 0;
                        strcat(express,rightexpr[o]);
                        strcat(express," ");
                    }
                    else{
                        if(indexing == 1){
                            strcat(express,rightexpr[o]);
                        }
                        else{
                            strcat(express,rightexpr[o]);
                            strcat(express," ");
                        }
                    }
                    o++;
                }
                o = 0;
                char copied_express[100];
                strcpy(copied_express,express);
                char* first_token = strtok(copied_express," ");
                if(is_scalar(varname,scalar_vars) == 1){
                    if(strtok(NULL," ") == NULL){
                        if(strtod(lineTokens[line_num][2],NULL) != 0.000000){
                            scal_val = strtod(lineTokens[line_num][2],NULL);
                            scal_vals[indexof(varname,scalar_vars)] = scal_val;
                            fprintf(outptr,"%s[0][0] = %f;\n",varname,scal_val);
                        }
                        else{
                            char* var2 = lineTokens[line_num][2];;
                            if(is_scalar(var2,scalar_vars) == 1){
                                fprintf(outptr,"%s[0][0] = %s[0][0];\n",varname,var2);
                            }
                            else if(is_vector(var2,vector_vars) == 1){
                                int index = strtod(lineTokens[line_num][4],NULL);
                                fprintf(outptr,"%s[0][0] = %s[%d][0];\n",varname,var2,index-1);
                            }
                            else if(is_matrix(var2,matrix_vars) == 1){
                                int index1 = strtod(lineTokens[line_num][4],NULL);
                                int index2 = strtod(lineTokens[line_num][6],NULL);
                                fprintf(outptr,"%s[0][0] = %s[%d][%d];\n",varname,var2,index1-1,index2-1);
                            }

                        }
                    }
                    else if(strcmp(lineTokens[line_num][2],"choose")==0){
                        int commas = 0;
                        int inside = 0;
                        char choosexp1[40] = "";
                        char choosexp2[40] = "";
                        char choosexp3[40] = "";
                        char choosexp4[40] = "";

                        int inx = 4;
                        while(strcmp(lineTokens[line_num][inx],")") != 0){
                            if(strcmp(lineTokens[line_num][inx],"[") == 0){
                                inside = 1;
                            }
                            if(strcmp(lineTokens[line_num][inx],"]") == 0){
                                inside = 0;
                            }
                            if(strcmp(lineTokens[line_num][inx],",") == 0 && inside == 0){
                                commas++;
                                inx++;
                            }
                            if(commas == 0){
                                strcat(choosexp1,lineTokens[line_num][inx]);
                                strcat(choosexp1," ");
                            }
                            else if(commas == 1){
                                strcat(choosexp2,lineTokens[line_num][inx]);
                                strcat(choosexp2," ");
                            }
                            else if(commas == 2){
                                strcat(choosexp3,lineTokens[line_num][inx]);
                                strcat(choosexp3," ");
                            }
                            else{
                                strcat(choosexp4,lineTokens[line_num][inx]);
                                strcat(choosexp4," ");
                            }
                            inx++;
                        }
                        fprintf(outptr,"%s[0][0] = ",varname);

                        fprintf(outptr,"choose(");
                        choose_handler(choosexp1,outptr);
                        fprintf(outptr,",");
                        choose_handler(choosexp2,outptr);
                        fprintf(outptr,",");
                        choose_handler(choosexp3,outptr);
                        fprintf(outptr,",");
                        choose_handler(choosexp4,outptr);
                        fprintf(outptr,") ; \n");
                    }
                    else{
                        to_postfix(express);
                        to_evaluated(varname,postfix,outptr,line_num);
                        clear_postfix();
                        clear_stack();
                        clear_evaluated();
                        clear_evalstack();
                    }
                }
                else if(is_vector(varname,vector_vars) == 1){
                    if(express[0] == '{'){
                        int z = 3;
                        int y = 0;
                        while(lineTokens[line_num][z] != NULL){
                            if(strcmp(lineTokens[line_num][z],"}")== 0){
                                break;
                            }
                            else{
                                fprintf(outptr,"%s[%d][0] = %f;\n",varname,y, strtod(lineTokens[line_num][z],NULL));
                            }
                            z++;
                            y++;
                        }
                    }
                    else{
                        to_postfix(express);
                        to_evaluated(varname,postfix,outptr,line_num);
                        clear_postfix();
                        clear_stack();
                        clear_evaluated();
                        clear_evalstack();
                    }
                }
                else if(is_matrix(varname,matrix_vars) == 1){
                    if(strcmp(lineTokens[line_num][2],"{") == 0){
                        int z = 3;
                        for(int x=0;x<matrix_dims[indexof(varname,matrix_vars)][0];x++){
                            for(int y=0;y<matrix_dims[indexof(varname,matrix_vars)][1];y++){
                                fprintf(outptr,"%s[%d][%d] = %f;\n",varname,x,y, strtod(lineTokens[line_num][z],NULL));
                                z++;
                            }
                        }
                    }
                    else{
                        to_postfix(express);
                        to_evaluated(varname,postfix,outptr,line_num);
                        clear_postfix();
                        clear_stack();
                        clear_evaluated();
                        clear_evalstack();
                    }
                }

                int p =0;
                while(rightexpr[p][0] != '\0'){
                    memset(rightexpr[p],0,sizeof(rightexpr[p]));
                    p++;
                }
                memset(express,0,sizeof(express));

                elem = 0;
            }
            else if(strcmp(lineTokens[line_num][0],"printsep") == 0){
                fprintf(outptr,"printf(\"------------\\n\");\n");
            }
            else if(strcmp(lineTokens[line_num][0],"for") == 0){
                if(strcmp(lineTokens[line_num][3],",") != 0){
                    int seen_dot = 0;
                    int a = 0;
                    char exp1[20] = "";
                    int e1 = 0;
                    char exp2[20] = "";
                    int e2 = 0;
                    char exp3[20] = "";
                    int e3 = 0;
                    char exp4[20] = "";
                    int e4 = 0;
                    while(lineTokens[line_num][a] != NULL){
                        if(strcmp(lineTokens[line_num][a],"(") == 0){
                            e1 = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],"in") == 0){
                            e1 = 0;
                            e2 = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],":") == 0 && seen_dot == 0){
                            e2 = 0;
                            e3 = 1;
                            seen_dot = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],":") == 0 && seen_dot == 1){
                            e3 = 0;
                            e4 = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],")") == 0 && seen_dot == 1){
                            e4 = 0;
                        }
                        if(e1 == 1 && e2 == 0 && e3 == 0 && e4 == 0){
                            strcat(exp1,lineTokens[line_num][a]);
                            strcat(exp1," ");
                        }
                        else if(e1 == 0 && e2 == 1 && e3 == 0 && e4 == 0){
                            strcat(exp2,lineTokens[line_num][a]);
                            strcat(exp2," ");
                        }
                        else if(e1 == 0 && e2 == 0 && e3 == 1 && e4 == 0){
                            strcat(exp3,lineTokens[line_num][a]);
                            strcat(exp3," ");
                        }
                        else if(e1 == 0 && e2 == 0 && e3 == 0 && e4 == 1) {
                            strcat(exp4, lineTokens[line_num][a]);
                            strcat(exp4," ");
                        }
                        a++;
                    }
                    if(exp4[0] == '{'){
                        delete_char(exp4,0);
                    }
                    fprintf(outptr,"for(");
                    for_handler(exp1,outptr);
                    fprintf(outptr,"=");
                    for_handler(exp2,outptr);
                    fprintf(outptr,";");
                    for_handler(exp1,outptr);
                    fprintf(outptr,"<=");
                    for_handler(exp3,outptr);
                    fprintf(outptr,";");
                    for_handler(exp1,outptr);
                    fprintf(outptr,"+=");
                    for_handler(exp4,outptr);
                    fprintf(outptr," ) {\n");
                }
                else{
                    nested = 1;
                    int seen_dot = 0;
                    int a = 0;
                    char id1[20] = "";
                    int firstid = 0;
                    char id2[20] = "";
                    int secondid = 0;
                    char exp1[20] = "";
                    int e1 = 0;
                    char exp2[20] = "";
                    int e2 = 0;
                    char exp3[20] = "";
                    int e3 = 0;
                    char exp4[20] = "";
                    int e4 = 0;
                    char exp5[20] = "";
                    int e5 = 0;
                    char exp6[20] = "";
                    int e6 = 0;
                    while(lineTokens[line_num][a] != NULL){
                        if(strcmp(lineTokens[line_num][a],"(") == 0){
                            firstid = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],",") == 0 && seen_dot == 0){
                            firstid = 0;
                            secondid = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],"in") == 0){
                            secondid = 0;
                            e1 = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],":") == 0 && seen_dot == 0){
                            e1 = 0;
                            e2 = 1;
                            a++;
                            seen_dot++;
                        }
                        else if(strcmp(lineTokens[line_num][a],":") == 0 && seen_dot == 1){
                            e2 = 0;
                            e3 = 1;
                            a++;
                            seen_dot++;
                        }
                        else if(strcmp(lineTokens[line_num][a],",") == 0 && seen_dot == 2){
                            e3 = 0;
                            e4 = 1;
                            a++;
                        }
                        else if(strcmp(lineTokens[line_num][a],":") == 0 && seen_dot == 2){
                            e4 = 0;
                            e5 = 1;
                            a++;
                            seen_dot++;
                        }
                        else if(strcmp(lineTokens[line_num][a],":") == 0 && seen_dot == 3){
                            e5 = 0;
                            e6 = 1;
                            a++;
                            seen_dot++;
                        }
                        else if(strcmp(lineTokens[line_num][a],")") == 0 && seen_dot == 4){
                            e6 = 0;
                        }
                        if(firstid == 1){
                            strcat(id1,lineTokens[line_num][a]);
                            strcat(id1," ");
                        }
                        else if(secondid == 1){
                            strcat(id2,lineTokens[line_num][a]);
                            strcat(id2," ");
                        }
                        else if(e1 == 1){
                            strcat(exp1,lineTokens[line_num][a]);
                            strcat(exp1," ");
                        }
                        else if(e2 == 1) {
                            strcat(exp2, lineTokens[line_num][a]);
                            strcat(exp2," ");
                        }
                        else if(e3 == 1) {
                            strcat(exp3, lineTokens[line_num][a]);
                            strcat(exp3," ");
                        }
                        else if(e4 == 1) {
                            strcat(exp4, lineTokens[line_num][a]);
                            strcat(exp4," ");
                        }
                        else if(e5 == 1) {
                            strcat(exp5, lineTokens[line_num][a]);
                            strcat(exp5," ");
                        }
                        else if(e6 == 1) {
                            strcat(exp6, lineTokens[line_num][a]);
                            strcat(exp6," ");
                        }
                        a++;
                    }
                    if(exp6[0] == '{'){
                        delete_char(exp4,0);
                    }
                    fprintf(outptr,"for(");
                    for_handler(id1,outptr);
                    fprintf(outptr,"=");
                    for_handler(exp1,outptr);
                    fprintf(outptr,";");
                    for_handler(id1,outptr);
                    fprintf(outptr,"<=");
                    for_handler(exp2,outptr);
                    fprintf(outptr,";");
                    for_handler(id1,outptr);
                    fprintf(outptr,"+=");
                    for_handler(exp3,outptr);
                    fprintf(outptr," ) {\n");
                    //***********************
                    //***********************
                    //***********************
                    fprintf(outptr,"for(");
                    for_handler(id2,outptr);
                    fprintf(outptr,"=");
                    for_handler(exp4,outptr);
                    fprintf(outptr,";");
                    for_handler(id2,outptr);
                    fprintf(outptr,"<=");
                    for_handler(exp5,outptr);
                    fprintf(outptr,";");
                    for_handler(id2,outptr);
                    fprintf(outptr,"+=");
                    for_handler(exp6,outptr);
                    fprintf(outptr," ) {\n");
                }
            }
            else if(strcmp(lineTokens[line_num][0],"}") == 0){
                if(nested == 0){
                    fprintf(outptr,"}\n");
                }
                else{
                    fprintf(outptr,"}\n}\n");
                    nested = 0;
                }
            }
        }
        line_num++;
        fprintf(outptr,"\n");
    }
    fprintf(outptr,"return 0;\n");
    fprintf(outptr,"}\n");
    fclose(outptr);
    fclose(fp);

    return 0;
}
int float_conversion(float f){
    int c = (int)f;
    return c;
}

int indexof(char* varname,char* arr[]){
    int ind = 0;
    while(arr[ind] != NULL){
        if(strcmp(varname,arr[ind])== 0){
            return ind;
        }
        ind++;
    }
    return -1;
}

void replace_n(char* line){
    int len = strlen(line);
    for(int x = 0; x < len; x++){
        if(line[x] == '\n'){
            line[x] = '\0';
            break;
        }
    }
}
void myInsert(char* arr,int position,char value){
    int n = strlen(arr);
    for(int i=n-1;i>=position;i--){
        arr[i+1] = arr[i];
    }
    arr[position] = value;
}

void tokenize(char *line){
    int index = 0;
    while(line[index] != '\0'){
        if(line[index] == '(' || line[index] == ')' || line[index] == '{' || line[index] == '}' || line[index] == '[' || line[index] == ']' || line[index] == '+' || line[index] == '*' || line[index] == '-' || line[index] == ',' || line[index] == ':' || line[index] == '='){
            if(index > 0){
                if(strcmp(" ",&line[index-1]) == 0 && strcmp(" ",&line[index+1]) != 0){
                    myInsert(line,index+1,' ');
                    index++;
                }
                else if(strcmp(" ",&line[index-1]) != 0 && strcmp(" ",&line[index+1]) == 0){
                    myInsert(line,index,' ');
                    index++;
                }
                else if(strcmp(" ",&line[index-1]) == 0 && strcmp(" ",&line[index+1]) == 0){
                    continue;
                }
                else{
                    myInsert(line,index,' ');
                    myInsert(line,index+2,' ');
                    index += 2;
                }
            }
            else{
                myInsert(line,index+1,' ');
                index++;
            }
        }
        index++;
    }
}

void token_placement(char* tokenized, int line_num){
    char *token = strtok(tokenized," ");
    lineTokens[line_num][0] = token;
    int order = 1;
    while(token != NULL){
        token = strtok(NULL," ");
        lineTokens[line_num][order] = token;
        order++;
    }

}

int is_valid_varname(char* varname, char* variables[]){
    int ind = 0;
    while(variables[ind] != NULL){
        if(strcmp(varname,variables[ind])== 0){
            return 1;
        }
        ind++;
    }
    return 0;
}
int is_valid_fillname(char* fillname, char* fillvars[]){
    int ind = 0;
    while(fillvars[ind] != NULL){
        if(strcmp(fillname,fillvars[ind])== 0){
            return 1;
        }
        ind++;
    }
    return 0;
}
int is_scalar(char* varname, char* scalars[]){
    int ind = 0;
    while(scalars[ind] != NULL){
        if(strcmp(varname,scalars[ind])== 0){
            return 1;
        }
        ind++;
    }
    return 0;
}
int is_vector(char* varname, char* vectors[]){
    int ind = 0;
    while(vectors[ind] != NULL){
        if(strcmp(varname,vectors[ind])== 0){
            return 1;
        }
        ind++;
    }
    return 0;
}
int is_matrix(char* varname, char* matrices[]){
    int ind = 0;
    while(matrices[ind] != NULL){
        if(strcmp(varname,matrices[ind])== 0){
            return 1;
        }
        ind++;
    }
    return 0;
}
void push(char token[]){
    if(isfull() == 0){
        strcpy(stack[count],token);
        count++;
    }
}
char* pop(){
    if(isempty() == 0){
        char* top = stack[count-1];
        count--;
        return top;
    }
    return NULL;
}

char* peek(){
    if(isempty() == 0){
        char* top = stack[count-1];
        return top;
    }
    return NULL;
}
int isfull(){
    if(count == 50){
        return 1;
    }
    return 0;
}
int isempty(){
    if(count == 0){
        return 1;
    }
    return 0;
}
int precedence(char* operator){
    if(strcmp(operator,"+") == 0 || strcmp(operator,"-") == 0){
        return 1;
    }
    else if(strcmp(operator,"*") == 0 || strcmp(operator,"/") == 0){
        return 2;
    }
    else if(strcmp(operator,"(") == 0 || strcmp(operator,")") == 0){
        return 3;
    }
    else{
        return 4;
    }
}
void to_postfix(char expression[]){
    int elem = 0;
    char* word = strtok(expression," ");
    int w = 0;
    while(word != NULL){
        if(strcmp(word,"+")==0 || strcmp(word,"-")==0 || strcmp(word,"*")==0 || strcmp(word,"/")==0 || strcmp(word,"tr")==0 || strcmp(word,"sqrt")==0 || strcmp(word,"(")==0 || strcmp(word,")")==0){
            if(strcmp(word,"(") == 0){
                push(word);
            }
            else if(strcmp(word,")") == 0){
                while(isempty() != 1){
                    if(strcmp(peek(),"(") == 0){

                        pop();
                        break;
                    }
                    else{
                        char* operat = pop();
                        strcpy(postfix[elem],operat);
                        elem++;
                    }
                }
            }
            else if(isempty() != 1){
                if(precedence(word) <= precedence(peek()) && strcmp(peek(),"(") != 0){
                    while(isempty() != 1){
                        if(strcmp(peek(),"(")!=0 && precedence(word) <= precedence(peek())){
                            char* op = pop();
                            strcpy(postfix[elem],op);
                            elem++;
                        }
                        else{
                            break;
                        }
                    }
                    push(word);
                }
                else{
                    push(word);
                }
            }
            else if(isempty() == 1){
                push(word);
            }
            else{
                push(word);
            }
        }
        else{
            strcpy(postfix[elem],word);
            elem++;
        }

        word = strtok(NULL," ");

    }
    if(isempty() != 1){
        while(isempty() != 1){
            strcpy(postfix[elem],pop());
            elem++;
        }
    }
}
void to_evaluated(char* varname,char postfixexpr[20][20],FILE* outptr,int linenumber){
    stack_usage++;

    //----------------
    int a = 0;
    int elem = 0;
    char* op;
    char operand1[100];
    char operand2[100];
    char pusher[256];
    int num_of_operat = 0;
    char new_var1[10];
    char new_var2[10];
    while(postfixexpr[a][0] != '\0') {
        op = postfixexpr[a];
        if (strcmp(op, "+") != 0 && strcmp(op, "*") != 0 && strcmp(op, "-") != 0 && strcmp(op, "tr") != 0 && strcmp(op, "sqrt") != 0) {
            pushEvaluation(op);
        }
        else if (strcmp(op, "tr") == 0) {
            strcpy(operand1, popEvaluation());
            sprintf(new_var1, "fill%d%d", stack_usage, num_of_operat);
            char* q = strdup(new_var1);
            fill_vars[num_fills] = q;

            tr(new_var1, operand1,outptr, linenumber);
            pushEvaluation(new_var1);
            num_of_operat++;
        }
        else if (strcmp(op, "sqrt") == 0) {
            strcpy(operand1, popEvaluation());
            sprintf(new_var1, "fill%d%d", stack_usage, num_of_operat);
            char* q = strdup(new_var1);
            fill_vars[num_fills] = q;
            sqt(new_var1, operand1,outptr,linenumber);
            pushEvaluation(new_var1);
            num_of_operat++;
        }
        else if (strcmp(op, "+") == 0) {
            strcpy(operand2, popEvaluation());
            strcpy(operand1, popEvaluation());
            sprintf(new_var1, "fill%d%d", stack_usage, num_of_operat);
            char* q = strdup(new_var1);
            fill_vars[num_fills] = q;

            add(new_var1, operand1, operand2, outptr,linenumber);
            pushEvaluation(new_var1);
            num_of_operat++;
        }
        else if (strcmp(op, "-") == 0) {
            strcpy(operand2, popEvaluation());
            strcpy(operand1, popEvaluation());
            sprintf(new_var1, "fill%d%d", stack_usage, num_of_operat);
            char* q = strdup(new_var1);
            fill_vars[num_fills] = q;

            sub(new_var1, operand1, operand2, outptr,linenumber);
            pushEvaluation(new_var1);
            num_of_operat++;
        }
        else {
            strcpy(operand2, popEvaluation());
            strcpy(operand1, popEvaluation());
            sprintf(new_var1, "fill%d%d", stack_usage, num_of_operat);

            char* q = strdup(new_var1);
            fill_vars[num_fills] = q;

            mult(new_var1, operand1, operand2, outptr,linenumber);
            pushEvaluation(new_var1);
            num_of_operat++;
        }
        a++;
    }
    int indx_var = indexof(varname,all_vars);
    int eval_dim1 = 0;
    int eval_dim2 = 0;
    int var_dim1 = all_dims[indx_var][0];
    int var_dim2 = all_dims[indx_var][1];

    char* eval =popEvaluation();
    if(is_valid_fillname(eval,fill_vars) == 1){
        int indx_fill = indexof(eval,fill_vars);
        eval_dim1 = fill_dims[indx_fill][0];
        eval_dim2 = fill_dims[indx_fill][1];
    }
    else if(is_valid_varname(eval,all_vars) == 1){
        int indx_fill = indexof(eval,all_vars);
        eval_dim1 = all_dims[indx_fill][0];
        eval_dim2 = all_dims[indx_fill][1];
    }
    if((var_dim1 == eval_dim1) && (var_dim2 == eval_dim2)){
        fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",eval_dim1);
        fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",eval_dim2);
        fprintf(outptr,"\t\t%s[myi][myj] = %s[myi][myj];\n",varname,eval);
        fprintf(outptr,"\t}\n");
        fprintf(outptr,"}\n");
    }
    else{
        printf("Error (Line %d)",linenumber+1);
        fclose(outptr);
        remove("file.c");
        exit(0);
    }

}
void mult(char * new_var1,char* operand1,char *operand2,FILE* outptr,int linenumber){
    int dim1_op1 = -1;
    int dim2_op1 = -1;
    int dim1_op2 = -1;
    int dim2_op2 = -1;
    if(is_valid_varname(operand1,all_vars) == 1){
        int index = indexof(operand1,all_vars);
        dim1_op1 = all_dims[index][0];
        dim2_op1  = all_dims[index][1];
    }

    else if(is_valid_fillname(operand1,fill_vars) == 1){
        int index = indexof(operand1,fill_vars);
        dim1_op1 = fill_dims[index][0];
        dim2_op1  = fill_dims[index][1];
    }

    if(is_valid_varname(operand2,all_vars) == 1){
        int index = indexof(operand2,all_vars);
        dim1_op2 = all_dims[index][0];
        dim2_op2  = all_dims[index][1];
    }
    else if(is_valid_fillname(operand2,fill_vars) == 1){
        int index = indexof(operand2,fill_vars);
        dim1_op2 = fill_dims[index][0];
        dim2_op2  = fill_dims[index][1];
    }

    if(dim1_op1 != -1 && dim2_op1 != -1 && dim1_op2 != -1 && dim2_op2 != -1){
        if(dim2_op1 == dim1_op2){
            fill_dims[num_fills][0] = dim1_op1;
            fill_dims[num_fills][1] = dim2_op2;
            num_fills++;

            fprintf(outptr,"float sum%d = 0;\n",total_sum);
            fprintf(outptr,"float %s[%d][%d];\n",new_var1,dim1_op1,dim2_op2);
            fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",dim1_op1);
            fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",dim2_op2);
            fprintf(outptr,"\t\tfor(int myk=0;myk<%d;myk++){\n",dim1_op2);
            fprintf(outptr,"\t\tsum%d += %s[myi][myk] * %s[myk][myj];\n",total_sum,operand1,operand2);
            fprintf(outptr,"\t\t}\n");
            fprintf(outptr,"\t%s[myi][myj] = sum%d;\n",new_var1,total_sum);
            fprintf(outptr,"\tsum%d = 0;\n",total_sum);
            fprintf(outptr,"\t}\n");
            fprintf(outptr,"}\n");
            total_sum++;
        }
        else{
            printf("Error (Line %d)",linenumber+1);
            fclose(outptr);
            remove("file.c");
            exit(0);
        }
    }
    else if(dim1_op1 == -1 && dim2_op1 == -1 && dim1_op2 != -1 && dim2_op2 != -1){
        fill_dims[num_fills][0] = dim1_op2;
        fill_dims[num_fills][1] = dim2_op2;
        num_fills++;
        fprintf(outptr,"float %s[%d][%d];\n",new_var1,dim1_op2,dim2_op2);
        fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",dim1_op2);
        fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",dim2_op2);
        fprintf(outptr,"\t\t%s[myi][myj] = %s * %s[myi][myj];\n",new_var1,operand1,operand2);
        fprintf(outptr,"\t}\n");
        fprintf(outptr,"}\n");
        total_sum++;
    }
    else if(dim1_op1 != -1 && dim2_op1 != -1 && dim1_op2 == -1 && dim2_op2 == -1){
        fill_dims[num_fills][0] = dim1_op1;
        fill_dims[num_fills][1] = dim2_op1;
        num_fills++;
        fprintf(outptr,"float %s[%d][%d];\n",new_var1,dim1_op1,dim2_op1);
        fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",dim1_op1);
        fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",dim2_op1);
        fprintf(outptr,"\t\t%s[myi][myj] = %s * %s[myi][myj];\n",new_var1,operand2,operand1);
        fprintf(outptr,"\t}\n");
        fprintf(outptr,"}\n");
        total_sum++;
    }
    else{
        fill_dims[num_fills][0] = 1;
        fill_dims[num_fills][1] = 1;
        num_fills++;
        fprintf(outptr,"float %s[1][1];\n",new_var1);
        fprintf(outptr,"%s[0][0] = %s * %s;\n",new_var1,operand1,operand2);
    }

}
void sub(char * new_var1,char* operand1,char *operand2,FILE* outptr,int linenumber){

    int dim1_op1 = -1;
    int dim2_op1 = -1;
    int dim1_op2 = -1;
    int dim2_op2 = -1;
    if(is_valid_varname(operand1,all_vars) == 1){
        int index = indexof(operand1,all_vars);
        dim1_op1 = all_dims[index][0];
        dim2_op1  = all_dims[index][1];
    }

    else if(is_valid_fillname(operand1,fill_vars) == 1){
        int index = indexof(operand1,fill_vars);
        dim1_op1 = fill_dims[index][0];
        dim2_op1  = fill_dims[index][1];
    }

    if(is_valid_varname(operand2,all_vars) == 1){
        int index = indexof(operand2,all_vars);
        dim1_op2 = all_dims[index][0];
        dim2_op2  = all_dims[index][1];
    }
    else if(is_valid_fillname(operand2,fill_vars) == 1){
        int index = indexof(operand2,fill_vars);
        dim1_op2 = fill_dims[index][0];
        dim2_op2  = fill_dims[index][1];
    }

    if(dim1_op1 != -1 && dim2_op1 != -1 && dim1_op2 != -1 && dim2_op2 != -1){
        if((dim1_op1 == dim1_op2) && (dim2_op1 == dim2_op2)){

            fill_dims[num_fills][0] = dim1_op1;
            fill_dims[num_fills][1] = dim2_op1;
            num_fills++;

            fprintf(outptr,"float %s[%d][%d];\n",new_var1,dim1_op1,dim2_op1);
            fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",dim1_op1);
            fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",dim1_op2);
            fprintf(outptr,"\t\t%s[myi][myj] = %s[myi][myj] - %s[myi][myj];\n",new_var1,operand1,operand2);
            fprintf(outptr,"\t}\n");
            fprintf(outptr,"}\n");
            total_sum++;
        }
        else{
            printf("Error (Line %d)",linenumber+1);
            fclose(outptr);
            remove("file.c");
            exit(0);
        }
    }
    else if(dim1_op1 == -1 && dim2_op1 == -1 && dim1_op2 == 1 && dim2_op2 == 1){
        fill_dims[num_fills][0] = dim1_op2;
        fill_dims[num_fills][1] = dim2_op2;
        num_fills++;

        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = %s - %s[0][0];\n",new_var1,operand1,operand2);
        total_sum++;
    }
    else if(dim1_op1 == 1 && dim2_op1 == 1 && dim1_op2 == -1 && dim2_op2 == -1){
        fill_dims[num_fills][0] = dim1_op1;
        fill_dims[num_fills][1] = dim2_op1;
        num_fills++;

        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = %s - %s[0][0];\n",new_var1,operand2,operand1);
        total_sum++;
    }
    else{
        fill_dims[num_fills][0] = 1;
        fill_dims[num_fills][1] = 1;
        num_fills++;
        fprintf(outptr,"float %s[1][1];\n",new_var1);
        fprintf(outptr,"%s[0][0] = %s - %s;\n",new_var1,operand1,operand2);
    }
}

void add(char * new_var1,char* operand1,char *operand2,FILE* outptr,int linenumber){

    int dim1_op1 = -1;
    int dim2_op1 = -1;
    int dim1_op2 = -1;
    int dim2_op2 = -1;
    if(is_valid_varname(operand1,all_vars) == 1){
        int index = indexof(operand1,all_vars);
        dim1_op1 = all_dims[index][0];
        dim2_op1  = all_dims[index][1];
    }

    else if(is_valid_fillname(operand1,fill_vars) == 1){
        int index = indexof(operand1,fill_vars);
        dim1_op1 = fill_dims[index][0];
        dim2_op1  = fill_dims[index][1];
    }

    if(is_valid_varname(operand2,all_vars) == 1){
        int index = indexof(operand2,all_vars);
        dim1_op2 = all_dims[index][0];
        dim2_op2  = all_dims[index][1];
    }
    else if(is_valid_fillname(operand2,fill_vars) == 1){
        int index = indexof(operand2,fill_vars);
        dim1_op2 = fill_dims[index][0];
        dim2_op2  = fill_dims[index][1];
    }

    if(dim1_op1 != -1 && dim2_op1 != -1 && dim1_op2 != -1 && dim2_op2 != -1){
        if((dim1_op1 == dim1_op2) && (dim2_op1 == dim2_op2)){

            fill_dims[num_fills][0] = dim1_op1;
            fill_dims[num_fills][1] = dim2_op1;
            num_fills++;

            fprintf(outptr,"float %s[%d][%d];\n",new_var1,dim1_op1,dim2_op1);
            fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",dim1_op1);
            fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",dim1_op2);
            fprintf(outptr,"\t\t%s[myi][myj] = %s[myi][myj] + %s[myi][myj];\n",new_var1,operand1,operand2);
            fprintf(outptr,"\t}\n");
            fprintf(outptr,"}\n");
            total_sum++;
        }
        else{
            printf("Error (Line %d)",linenumber+1);
            fclose(outptr);
            remove("file.c");
            exit(0);
        }
    }
    else if(dim1_op1 == -1 && dim2_op1 == -1 && dim1_op2 == 1 && dim2_op2 == 1){
        fill_dims[num_fills][0] = dim1_op2;
        fill_dims[num_fills][1] = dim2_op2;
        num_fills++;

        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = %s + %s[0][0];\n",new_var1,operand1,operand2);
        total_sum++;
    }
    else if(dim1_op1 == 1 && dim2_op1 == 1 && dim1_op2 == -1 && dim2_op2 == -1){
        fill_dims[num_fills][0] = dim1_op1;
        fill_dims[num_fills][1] = dim2_op1;
        num_fills++;

        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = %s + %s[0][0];\n",new_var1,operand2,operand1);
        total_sum++;
    }
    else{
        fill_dims[num_fills][0] = 1;
        fill_dims[num_fills][1] = 1;
        num_fills++;
        fprintf(outptr,"float %s[1][1];\n",new_var1);
        fprintf(outptr,"%s[0][0] = %s + %s;\n",new_var1,operand1,operand2);
    }
}
void tr(char * new_var1,char* operand1,FILE* outptr,int linenumber){
    int dim1 = -1;
    int dim2 = -1;
    if(is_valid_varname(operand1,all_vars) == 1){
        int index = indexof(operand1,all_vars);
        dim1 = all_dims[index][0];
        dim2  = all_dims[index][1];
    }
    else if(is_valid_fillname(operand1,fill_vars) == 1){
        int index = indexof(operand1,fill_vars);
        dim1 = fill_dims[index][0];
        dim2  = fill_dims[index][1];

    }
    if(dim1 == 1 && dim2 == 1){
        fill_dims[num_fills][0] = 1;
        fill_dims[num_fills][1] = 1;
        num_fills++;
        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = %s[0][0];\n",new_var1,operand1);
    }
    else{
        fill_dims[num_fills][0] = dim2;
        fill_dims[num_fills][1] = dim1;
        num_fills++;

        fprintf(outptr,"float %s[%d][%d];\n",new_var1,dim2,dim1);
        fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",dim2);
        fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",dim1);
        fprintf(outptr,"\t\t%s[myi][myj] = %s[myj][myi];\n",new_var1,operand1);
        fprintf(outptr,"\t}\n");
        fprintf(outptr,"}\n");
        total_sum++;
    }
}

void sqt(char * new_var1,char* operand1,FILE* outptr,int linenumber){
    fill_dims[num_fills][0] = 1;
    fill_dims[num_fills][1] = 1;
    num_fills++;
    if(has_square_bracket(operand1) == 1 && has_comma(operand1) == 1){
        char realvar[20] = "";
        int len = 0;
        char ind1[4] = "";
        char ind2[4] = "";
        while(operand1[len] != '['){
            realvar[len] = operand1[len];
            len++;
        }
        len++;
        while(operand1[len] != ','){
            if(isdigit(operand1[len]) != 0){
                ind1[strlen(ind1)] = operand1[len];
            }
            len++;
        }
        len++;
        while(operand1[len] != ']'){
            if(isdigit(operand1[len]) != 0){
                ind2[strlen(ind2)] = operand1[len];
            }
            len++;
        }
        int realind1 = strtol(ind1,NULL,10) - 1;
        int realind2 = strtol(ind2,NULL,10) - 1;
        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = sqrt(%s[%d][%d]);\n",new_var1,realvar,realind1,realind2);

    }
    else if(has_square_bracket(operand1) == 1 && has_comma(operand1) == 0){
        char realvar[20] = "";
        int len = 0;
        char ind1[4] = "";
        while(operand1[len] != '['){
            realvar[len] = operand1[len];
            len++;
        }
        len++;
        while(operand1[len] != ']'){
            ind1[len] = operand1[len];
            len++;
        }
        int realind1 = strtol(ind1,NULL,10) - 1;
        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = sqrt(%s[%d][0]);\n",new_var1,realvar,realind1);
    }
    else if(has_square_bracket(operand1) == 0 && has_comma(operand1) == 0){
        fprintf(outptr,"float %s[%d][%d];\n",new_var1,1,1);
        fprintf(outptr,"%s[0][0] = sqrt(%s);\n",new_var1,operand1);
    }
//    else{
//
//    }
}

void clear_stack(){
    int a = 0;
    while(stack[a][0] != '\0'){
        memset(stack[a],0,sizeof(stack[a]));
        a++;
    }
    count = 0;
}

void clear_postfix(){
    int a = 0;
    while(postfix[a][0] != '\0'){
        memset(postfix[a],0,sizeof(postfix[a]));
        a++;
    }
}
void clear_evaluated(){
    memset(evaluated,0,sizeof(evaluated));
}
void clear_evalstack(){
    int a = 0;
    while(evaluationStack[a][0] != '\0'){
        memset(evaluationStack[a],0,sizeof(evaluationStack[a]));
        a++;
    }
    eval_count = 0;
}
void pushEvaluation(char token[]){
    if(isfullEvaluation() == 0){
        strcpy(evaluationStack[eval_count],token);
        eval_count++;
    }
}
char* popEvaluation(){
    if(isemptyEvaluation() == 0){
        char* top = evaluationStack[eval_count-1];
        eval_count--;
        return top;
    }
    return NULL;
}

char* peekEvaluation(){
    if(isemptyEvaluation() == 0){
        char* top = evaluationStack[eval_count-1];
        return top;
    }
    return NULL;
}
int isfullEvaluation(){
    if(eval_count == 20){
        return 1;
    }
    return 0;
}
int isemptyEvaluation(){
    if(eval_count == 0){
        return 1;
    }
    return 0;
}
void printer(FILE* outptr,char exp[10][50]){
    if(is_scalar(exp[0],scalar_vars) == 1){
        fprintf(outptr,"if(%s[0][0] == (int) %s[0][0]){\n",exp[0],exp[0]);
        fprintf(outptr,"\tprintf(\"%%d\\n\",(int) %s[0][0]);\n",exp[0]);
        fprintf(outptr,"}\n");
        fprintf(outptr,"else{\n");
        fprintf(outptr,"\tprintf(\"%%f\\n\",%s[0][0]);\n",exp[0]);
        fprintf(outptr,"}\n");

    }
    else if(is_vector(exp[0],vector_vars) == 1){
        if(exp[1] != NULL && strcmp(exp[1],"[") == 0){
            if(exp[3] != NULL && strcmp(exp[3],"]") == 0){
                fprintf(outptr,"if((%s[%ld][0] - (int) %s[%ld][0]) > 0){\n",exp[0],strtol(exp[2],NULL,10)-1,exp[0],strtol(exp[2],NULL,10)-1);
                fprintf(outptr,"\tprintf(\"%%f\\n\",%s[%ld][0]);\n",exp[0],strtol(exp[2],NULL,10)-1);
                fprintf(outptr,"}\n");
                fprintf(outptr,"else{\n");
                fprintf(outptr,"\tprintf(\"%%d\\n\",(int) %s[%ld][0]);\n",exp[0],strtol(exp[2],NULL,10)-1);
                fprintf(outptr,"}\n");
            }
        }
        else{
            fprintf(outptr,"for(int myindex=0;myindex < %d;myindex++){\n",vector_dims[indexof(exp[0],vector_vars)][0]);
            fprintf(outptr,"if((%s[myindex][0] - (int) %s[myindex][0]) > 0){\n",exp[0],exp[0]);
            fprintf(outptr,"\tprintf(\"%%f\\n\",%s[myindex][0]);\n",exp[0]);
            fprintf(outptr,"}\n");
            fprintf(outptr,"else{\n");
            fprintf(outptr,"\tprintf(\"%%d\\n\",(int) %s[myindex][0]);\n",exp[0]);
            fprintf(outptr,"}\n");
            fprintf(outptr,"}\n");
        }
    }
    else if(is_matrix(exp[0],matrix_vars) == 1){
        if(exp[1] != NULL && strcmp(exp[1],"[") == 0){
            if(exp[3] != NULL && strcmp(exp[3],"]") == 0){
                int index = strtol(exp[2],NULL,10) - 1;
                fprintf(outptr,"for(int myindex=0;myindex < %d;myindex++){\n",matrix_dims[indexof(exp[0],matrix_vars)][1]);
                fprintf(outptr,"if((%s[%d][myindex] - (int) %s[%d][myindex]) > 0){\n",exp[0],index,exp[0],index);
                fprintf(outptr,"\tprintf(\"%%f\\n\",%s[%d][myindex]);\n",exp[0],index);
                fprintf(outptr,"}\n");
                fprintf(outptr,"else{\n");
                fprintf(outptr,"\tprintf(\"%%d\\n\",(int) %s[%d][myindex]);\n",exp[0],index);
                fprintf(outptr,"}\n");
                fprintf(outptr,"}\n");
            }
            else{
                int index1 = strtol(exp[2],NULL,10)-1;
                int index2 = strtol(exp[4],NULL,10)-1;
                fprintf(outptr,"if((%s[%ld][%ld] - (int) %s[%ld][%ld]) > 0){\n",exp[0],index1,index2,exp[0],index1,index2);
                fprintf(outptr,"\tprintf(\"%%f\\n\",%s[%ld][%ld]);\n",exp[0],strtol(exp[2],NULL,10)-1);
                fprintf(outptr,"}\n");
                fprintf(outptr,"else{\n");
                fprintf(outptr,"\tprintf(\"%%d\\n\",(int) %s[%ld][%ld]);\n",exp[0],index1,index2,exp[0],index1,index2);
                fprintf(outptr,"}\n");
            }
        }
        else{
            fprintf(outptr,"for(int myi=0;myi<%d;myi++){\n",matrix_dims[indexof(exp[0],matrix_vars)][0]);
            fprintf(outptr,"\tfor(int myj=0;myj<%d;myj++){\n",matrix_dims[indexof(exp[0],matrix_vars)][1]);
            fprintf(outptr,"\t\tif((%s[myi][myj] - (int) %s[myi][myj]) > 0){\n",exp[0],exp[0]);
            fprintf(outptr,"\t\t\tprintf(\"%%f\\n\",%s[myi][myj]);\n",exp[0]);
            fprintf(outptr,"\t\t}\n");
            fprintf(outptr,"\t\telse{\n");
            fprintf(outptr,"\t\t\tprintf(\"%%d\\n\",(int) %s[myi][myj]);\n",exp[0]);
            fprintf(outptr,"\t\t}\n");
            fprintf(outptr,"\t}\n");
            fprintf(outptr,"}\n");
        }
    }
}
int has_square_bracket(char* operand){
    int a = 0;
    int found = 0;
    while(operand[a] != '\0'){
        if(operand[a] == '['){
            found = 1;
            break;
        }
        a++;
    }
    return found;
}
int has_comma(char* operand){
    int a = 0;
    int found = 0;
    while(operand[a] != '\0'){
        if(operand[a] == ','){
            found = 1;
            break;
        }
        a++;
    }
    return found;
}
int elements_in_fillvars(char* fillvars[]){
    int res = 0;
    while(fillvars[res] != NULL){
        res++;
    }
    return res;
}
void concat(char* dest,char* source){
    int len1 = strlen(dest);
    int len2 = strlen(source);
    int a = 0;
    while(source[a] != '\0'){
        dest[len1] = source[a];
        len1++;
        a++;
    }
}
void for_handler(char exp[],FILE* outptr){
    char* token = strtok(exp," ");
    while(token != NULL){
        if(is_scalar(token,scalar_vars) == 1){
            fprintf(outptr," %s[0][0] ",token);
        }
        else{
            fprintf(outptr," %s ",token);
        }
        token = strtok(NULL, " ");
    }
}

void delete_char(char *str, int i) {
    int len = strlen(str);

    for (; i < len - 1 ; i++)
    {
        str[i] = str[i+1];
    }

    str[i] = '\0';
}

void choose_handler(char exp[],FILE* outptr){
    char tokens[20][20] = {""};
    char print[200] = "";
    int inx = 0;
    // matrix = 2 , vector = 1
    int index_mode = 0;
    char* token = strtok(exp," ");
    while(token != NULL){
        strcpy(tokens[inx],token);
        token = strtok(NULL," ");
        inx++;
    }
    inx = 0;
    while(tokens[inx][0] != '\0'){

        if(strcmp(tokens[inx],"[") == 0){
            strcat(print,tokens[inx]);
            if(is_matrix(tokens[inx-1],matrix_vars) == 1){
                index_mode = 2;
            }
            else if(is_vector(tokens[inx-1],vector_vars) == 1){
                index_mode = 1;
            }
        }
        else if(strcmp(tokens[inx],"]") == 0){
            strcat(print,tokens[inx]);
            if(index_mode == 1){
                strcat(print,"[0]");
            }
            index_mode = 0;
        }
        else if(is_scalar(tokens[inx],scalar_vars) == 1){
            if(index_mode == 0){
                strcat(print,"get_scal_val(");
                strcat(print,tokens[inx]);
                strcat(print,")");
            }
            else{
                strcat(print,"get_scal_val(");
                strcat(print,tokens[inx]);
                strcat(print,"[0][0])-1");
            }
        }
        else if(strcmp(tokens[inx],",") == 0 && index_mode != 0){
            strcat(print,"][");
        }
        else{
            strcat(print,tokens[inx]);
        }
        inx++;
    }
    fprintf(outptr,"%s",print);
}