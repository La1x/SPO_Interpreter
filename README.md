# SPO_Interpreter

Support items:
- assign
- declare
- functions:
	- print
	- write
	- fact //factorial
	- pow
- structs
- while

Grammar will load from file "grammar" in main directory.

// print info:
Parser:
	- match()
	- say()
	- print()
PostfixMaker:
	- print()
VarTable:
	- print()

/*
    NON_TERMINALS
*/

lang : (expr)+
expr : declare | assign | struct_expr | while_expr | function_kw
declare : VAR_KW VAR SM
assign : VAR ASSIGN_OP stmt SM
stmt : operand (OP stmt)* SM
operand : (BRK_O operand) | (stmtUnit (BRK_C)*) | functionStmt
stmtUnit : DIGIT | struct_stmt | VAR
functionStmt : FUNCTION BRK_O stmtUnit (BRK_C | SEP stmtUnit BRK_C)
struct_stmt : VAR DOT_OP VAR
struct_expr : struct_decl CBRK_O struct_body CBRK_C
struct_decl : STRUCT_KW VAR
struct_body : (declare | assign)*
while_expr : while_loop
while_loop : while_decl CBRK_O while_body CBRK_C 
while_decl : WHILE_KW BRK_O while_limit BRK_C
while_body : (expr)*
while_limit : VAR OP stmtUnit
function_kw : FUNCTION BRK_O stmtUnit BRK_C SM

/*
    TERMINALS
*/

VAR_KW : var
FOR_KW : for
WHILE_KW : while
STRUCT_KW : struct
FUNCTION : pow|fact|print|write
DIGIT : 0|[1-9]{1}[0-9]*
VAR : [a-zA-Z_]+
WS : \\s*
SM : ';'
SEP : ','
ASSIGN_OP : '='
DOT_OP : '.'
PLUS_OP : '+'
MINUS_OP : '-'
DEL_OP : '/'
MULT_OP : '*'
GRT_OP : '>'
LST_OP : '<'
BRK_O : '('
BRK_C : ')'
CBRK_O : '{'
CBRK_C : '}'
