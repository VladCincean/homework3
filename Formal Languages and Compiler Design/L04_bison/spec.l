%{

#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "grammar.tab.h"

int g_current_line = 1;

%}

%option noyywrap

digit		[0-9]
letter		[a-zA-Z_]

%%

\n			{ g_current_line++; }
[ \t]+		/* pass whitespaces */ ;
int			{ return INT; }
double		{ return DOUBLE; }
!=			{ return NEQ; }
=			{ return ASSIGN; }
\+			{ return ADD; }
-			{ return SUB; }
\*			{ return MUL; }
\/			{ return DIV; }
%			{ return MOD; }
\<			{ return LT; }
\<=			{ return LE; }
>			{ return GT; }
>=			{ return GE; }
==			{ return EQ; }
\<\<		{ return SHL; }
>>			{ return SHR; }
!			{ return NOT; }
&&			{ return AND; }
\|\|		{ return OR; }
typedef		{ return TYPEDEF; }
if			{ return IF; }
else		{ return ELSE; }
while		{ return WHILE; }
cin			{ return CIN; }
cout		{ return COUT; }
return		{ return RETURN; }
;			{ return SEMICOLON; }
,			{ return COMMA; }
\{			{ return OPEN_BRACE; }
\}			{ return CLOSE_BRACE; }
\(			{ return OPEN_PARA; }
\)			{ return CLOSE_PARA; }
main		{ return MAIN; }
endl		{ return ENDL; }

{letter}({letter}|{digit}){0,249}	{
	yylval.string = strdup(yytext);
	return IDENTIFIER;
}

[\+\-]?{digit}*\.{digit}+([eE][\+\-]?{digit}+)? {
	yylval.real = atof(yytext);
	return REAL_CONSTANT;
}

0|[\+\-]?[1-9]{digit}*	{
	yylval.integer = atoi(yytext);
	return INT_CONSTANT;
}

\"[^\"]*\"	{
	yylval.string = strdup(yytext);
	return STR_CONSTANT;
}

. {	/* something else */
	if (yytext[0] != '\r') {
		printf("Unexpected token: %s in line %d\n", yytext, g_current_line);
	}
}

%%

// no main
