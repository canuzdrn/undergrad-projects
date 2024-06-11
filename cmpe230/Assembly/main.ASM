code segment

main_reader:              ; the main driver function where we read the characters from the console stream
XOR DX, DX                ; Getting rid of any residuals from any former operation
XOR CX,CX                 ; Getting rid of any residuals from any former operation
MOV AH, 01H               ; The command to read a character from the console
INT 21h
CMP AL, "+"               ; checking for the addition operator
JE Add_Operation
CMP AL, "*"               ; checking for the multiplication operator
JE Multiply
CMP AL,"/"                ; checking for the division operator
JE Divide
CMP AL, "^"               ; checking for the XOR operator
JE XOR_Operation
CMP AL, "&"               ; checking for the AND operator
JE AND_jumper
CMP AL, "|"               ; checking for the OR operator
JE OR_jumper

operand_reader:      
CMP AL,":" 			; below is digit , greater is letter
JB process_digit_jumper
JG process_letter_jumper

Add_Operation:
XOR AX, AX                ; Cleaning the register from any trace of residual data by initializing it to Zero
XOR BX, BX
POP AX                    ; Popping from the stack the first operand
POP BX                    ; Popping from the stack the second operand
ADD AX, BX 
PUSH AX                   ; Pushing the result stored in AX to the memory's stack
MOV AH, 01H               ; read another character
INT 21h      
CMP AL," "                ; checking for spaces character placed after the operator
JE main_reader
CMP AL,0D
JE result_converter       ; checking for endline character placed after the operator

Multiply:
XOR AX, AX                ; Cleaning the register from any trace of residual data by initializing it to Zero
XOR BX, BX  
POP AX                    ; Popping from the stack the first operand
POP BX                    ; Popping from the stack the second operand
MUL BX                    ; Multiply the content of AX by the content of BX
PUSH AX                   ; Pushing the result stored in AX to the memory's stack
MOV AH, 01H               ; read another character
INT 21h
CMP AL," "                ; checking for spaces character placed after the operator
JE main_reader
CMP AL,0D                 ; checking for endline character placed after the operator
JE result_converter 

Divide:   
XOR AX, AX                ; Cleaning the registers from any trace of residual data by initializing it to Zero
XOR BX, BX
XOR DX, DX   
POP BX                    ; Popping from the stack the first operand
POP AX                    ; Popping from the stack the second operand
DIV BX
PUSH AX
MOV AH, 01H               ; read another character
INT 21h
CMP AL," "                ; checking for spaces character placed after the operator
JE main_reader
CMP AL,0D                 ; checking for endline character placed after the operator
JE result_converter

XOR_Operation:            ; XOR Operation's block
XOR AX, AX
XOR BX, BX
POP AX
POP BX
XOR AX, BX
PUSH AX
MOV AH, 01H
INT 21h
CMP AL," "			  ; checking for spaces character placed after the operator
JE main_reader
CMP AL,0D			  ; checking for endline character placed after the operator
JE result_converter

main_jumper: 
JMP main_reader            ; buffer jump call to the main_reader sub-routine

AND_jumper:                ; buffer jump call to the code block that executes the AND operation
JMP AND_Operation

OR_jumper:                 ; buffer jump call to the code block that executes the OR operation
JMP OR_Operation

process_digit_jumper:      ; buffer jump call to the process_digit sub-routine
JMP process_digit

process_letter_jumper:     ; buffer jump call to the process_letter sub-routine
JMP process_letter

result_converter:          ; Converting the final result to a Hexadecimal Number
MOV DL,13
MOV AH,02H
INT 21H
MOV DL,10
MOV AH,02H
INT 21H
POP AX                     
MOV DX,0                   ; Cleaning the registers from any trace of residual data by initializing it to Zero
MOV BX,0
MOV BX,16                  ; Moving 16 to BX in preparation for the division-based decimal-hexadecimal conversion
DIV BX                     ; dividing by 16 and storing the remainder in DX. This step will be repeated 3 times more to get 4-bit output
PUSH DX
MOV DX,0
DIV BX
PUSH DX
MOV DX,0
DIV BX
PUSH DX
PUSH AX
MOV AH, 02h                 
MOV BX,4                   ; the value stored in BX would serve as an iteration counter while printing
JMP global_printer

AND_Operation:             ; AND Operation's block
XOR AX, AX
XOR BX, BX
POP AX
POP BX
AND AX, BX
PUSH AX
MOV AH, 01H
INT 21h
CMP AL," "			  ; checking for spaces character placed after the operator
JE main_jumper
CMP AL,0D			  ; checking for endline character placed after the operator
JE result_converter

OR_Operation:              ; OR Operation's block
XOR AX, AX
XOR BX, BX
POP AX
POP BX
OR AX, BX
PUSH AX
MOV AH, 01H
INT 21h
CMP AL," "			  ; checking for spaces character placed after the operator
JE main_jumper
CMP AL,0D			  ; checking for endline character placed after the operator
JE result_converter

operand_reader_jumper:
JMP operand_reader

process_digit:           ;convert digit characters to their corresponding decimal values
MOV DL, AL
SUB DL, "0"
MOV temp,DX
MOV AX,CX
MOV BX,16
MUL BX
MOV CX, AX
ADD CX, temp
MOV AH, 01H
INT 21h
CMP AL,0D			  ; checking for endline character placed after the operand , if there is that means we are dealing with only one operand input
JE one_operand_input
CMP AL, " "
JE operand_pusher
JNE operand_reader_jumper

process_letter:         ;convert alphabetic characters to their corresponding decimal values
MOV DL, AL
SUB DL, "7"
MOV temp,DX
MOV AX,CX
MOV BX,16
MUL BX
MOV CX, AX
ADD CX, temp
MOV AH, 01H
INT 21h
CMP AL,0D			  ; checking for endline character placed after the operand
JE one_operand_input
CMP AL, " "
JE operand_pusher
JNE operand_reader_jumper

operand_pusher:       ; Push token to the stack
PUSH CX
JMP main_jumper

one_operand_input:       ; processes only one operand input
PUSH CX
JMP result_converter

global_printer:       ; Printing process management takes place here
CMP BX,0
JE endstmt
MOV DX,0
POP DX
CMP DX,9
JG letter_printer
JBE int_printer

letter_printer:       ; In case we encounter a number which is bigger than 9
ADD DX,55
MOV AH, 02h
INT 21H
DEC BX
JMP global_printer

int_printer:          ; In case we encounter a number which is less than 9
ADD DX,48
MOV AH, 02h
INT 21H
DEC BX
JMP global_printer

endstmt:
INT 20H


temp dw 0		    ; temp variable used in conversion from input char to it's corresponding decimal value
code ends
