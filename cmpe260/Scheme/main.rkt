; necdet can uzduran
; 2019400195
; compiling: yes
; complete: yes

#lang racket

(provide (all-defined-out))

; 10 points
;(define := (lambda (var value) 0))
; 10 points
;(define -- (lambda args 0))
; 10 points
;(define @ (lambda (bindings expr) 0))
; 20 points
;(define split_at_delim (lambda (delim args) 0))
; 30 points
;(define parse_expr (lambda (expr) 0))
; 20 points
;(define eval_expr (lambda (expr) 0))

; 1
(define :=
  (lambda (var value)
    (list var value)))
;

;2
(define --
  (lambda (firstAssignment . restAssignments) ;used rest argument "." , since the number of parameters is arbitrary
    (cons 'let (list (cons firstAssignment restAssignments)))))
;  

;3
(define @
  (lambda (bindings expr)
    (list 'let (cadr bindings) (car expr)) ))

;
;4

(define (splitter delim args listoflist)
  (cond
    [(null? args) listoflist]
    [(eqv? (car args) delim)(splitter delim (cdr args) (cons '() listoflist ))]
    [else (splitter delim (cdr args) (cons (append (car listoflist)  (cons (car args) '())) (cdr listoflist)) )] ))

(define split_at_delim (lambda (delim args)
  (reverse(splitter delim args '(()) ) ) ) )
;

;5
(define (endof lst)
  (car (reverse lst)))

; below functions are handler functions for different conditions described below
; used map to apply split_at_delim at each element of the list (just as if applying a function in a for loop)

(define (plus_handler splittedExp) ; handling expression that contains "+" 
  (append (list '+) (map (lambda (splittedExp) (parse_expr splittedExp)) splittedExp)))

(define (mult_handler splittedExp) ;handling expression that contains "*"
  (append (list '*) (map (lambda (splittedExp) (parse_expr splittedExp)) splittedExp)))

(define (at_handler splittedExp) ;handling expression that contains "@"
  (append (parse_expr  (car splittedExp)) (cons (parse_expr  (cdr splittedExp)) '())))

(define (empty_exception splittedList); this is for the exception when the last element of the result of the split_at_delim is empty set (ex : '('x := 5 --) -split_at_delim-> (('x := 5)()))
  (remove '() splittedList))

(define (dashed_handler splittedExp) ;organizing expression that contains "--" , eval needed to be used to handle quoted variables
  (eval (cons '-- (map (lambda (splittedExp) (cons ':=  (cons (car splittedExp)(cons (endof splittedExp) '())))) splittedExp)))) 

; parse_expr is basically parses given expression directly according to determined conditions (having + , * or @)
; most of the tricky part of the recursion hadnled with MAP function
; with also helper function dashed_handler, we can organize the given infix notated expression to prefix for "--" operation

(define parse_expr (lambda (expr) 
      (cond
            ((member '+ expr) ;  if there is "+" in expression-> split the expression -> to its own handler
             (let ([plussed (split_at_delim '+ expr )])  (plus_handler plussed)))
            ((member '* expr) ; if there is "*" in expression-> split the expression -> to its own handler
             (let ([multed (split_at_delim '* expr )])  (mult_handler multed)))
            ((member '@ expr) ;  if there is "@" in expression-> split the expression -> to its own handler
             (let ([atted (split_at_delim '@ expr )])  (at_handler atted)))
            ((member '-- expr)
             (let ([dashed (empty_exception (split_at_delim '-- expr ))])(dashed_handler dashed)))
            ((list? (car expr)) ; handling (expression) , ((((expression))))
             (parse_expr(car expr))) 
            (else (car expr)) ; encountered with the list of handled prefixes and/or numbers , variables (according to the CFG)
      )))
;

;6
(define eval_expr (lambda(expr)
  (eval (parse_expr expr))))
;








