%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

extern int yylex();
extern int yyparse();
extern FILE *yyin;

void yyerror(const char *str) {
	fprintf(stderr, "Syntax error: %s.\n", str);
	exit(0);
}

int yywrap() {
	return 1;
}

int yydebug = 1;

%}

%union {
	int		integer;
	float	real;
	char	*string;
}

%token <integer>	INT_CONSTANT
%token <real>		REAL_CONSTANT
%token <string>		STR_CONSTANT

%token	INT			"int"
%token	DOUBLE		"double"
%token	NEQ			"!="
%token	ASSIGN		"="
%token	ADD			"+"
%token	SUB			"-"
%token	MUL			"*"
%token	DIV			"/"
%token	MOD			"%"
%token	LT			"<"
%token	LE			"<="
%token	GT			">"
%token	GE			">="
%token	EQ			"=="
%token	SHL			"<<"
%token	SHR			">>"
%token	NOT			"!"
%token	AND			"&&"
%token	OR			"||"
%token	TYPEDEF		"typedef"
%token	IF			"if"
%token	ELSE		"else"
%token	WHILE		"while"
%token	CIN			"cin"
%token	COUT		"cout"
%token	RETURN		"return"
%token	SEMICOLON	";"
%token	COMMA		","
%token	OPEN_BRACE	"{"
%token	CLOSE_BRACE	"}"
%token	OPEN_PARA	"("
%token	CLOSE_PARA	")"
%token	MAIN		"main"
%token	ENDL		"endl"

%token	IDENTIFIER

%start	program

%%

program: INT MAIN OPEN_PARA CLOSE_PARA OPEN_BRACE statement_list CLOSE_BRACE
	   ;

statement_list: statement
			  | statement statement_list
			  ;

statement: declaration_statement
		 | typedefinition_statement
		 | assignment_statement
		 | if_statement
		 | while_statement
		 | input_statement
		 | output_statement
		 | return_statement
		 ;

declaration_statement: var_type var_list SEMICOLON
					 ;

var_type: INT | DOUBLE | user_defined_type
		;

var_list: IDENTIFIER
		| IDENTIFIER COMMA var_list
		;

user_defined_type: IDENTIFIER
				 ;

typedefinition_statement: TYPEDEF var_type user_defined_type SEMICOLON
						;

assignment_statement: IDENTIFIER ASSIGN expr SEMICOLON
					;

expr: IDENTIFIER
	| INT_CONSTANT
	| REAL_CONSTANT
	| expr operator expr
	;

operator: ADD | SUB | MUL | DIV | MOD | LT | LE | GT | GE | EQ | NEQ | NOT | AND | OR
		;

if_statement: IF OPEN_PARA expr CLOSE_PARA statement else_statement
			| IF OPEN_PARA expr CLOSE_PARA OPEN_BRACE statement_list CLOSE_BRACE else_statement
			;

else_statement:
			  | ELSE statement
			  | ELSE OPEN_BRACE statement_list CLOSE_BRACE
			  ;

while_statement: WHILE OPEN_PARA expr CLOSE_PARA statement
			   | WHILE OPEN_PARA expr CLOSE_PARA OPEN_BRACE statement_list CLOSE_BRACE
			   ;

input_statement: CIN SHR input_list SEMICOLON
			   ;

input_list: IDENTIFIER | IDENTIFIER SHR input_list
		  ;

output_statement: COUT SHL output_list SEMICOLON
				;

output_list: IDENTIFIER
		   | STR_CONSTANT
		   | ENDL
		   | IDENTIFIER SHL output_list
		   | STR_CONSTANT SHL output_list
		   | ENDL SHL output_list
		   ;

return_statement: RETURN return_code SEMICOLON
				;

return_code: IDENTIFIER | INT_CONSTANT
		   ;

%%

int main()
{
	yyparse();
	printf("The source code is syntactically correct. No syntax errors were found.\n");
	return 0;
}
