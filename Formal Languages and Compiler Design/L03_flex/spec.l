%{

#include <iostream>
#include <vector>
#include <map>
#include <string>
#include <iostream>
#include <iomanip>

typedef struct	s_pif_entry
{
	int	token_code;
	int	st_index;
}				t_pif_entry;

std::vector<t_pif_entry>	g_pif;
std::map<std::string, int>	g_st;
int							g_current_line = 1;
int							g_last_index_st = 0;

int 	pif_add(int token_code, int st_index) {
	t_pif_entry entry;

	entry.token_code = token_code;
	entry.st_index = st_index;

	g_pif.push_back(entry);

	return 1;
}

int		st_pos(std::string symbol) {
	if (g_st.count(symbol) == 0) {
		++g_last_index_st;
		g_st.insert(std::pair<std::string, int>(symbol, g_last_index_st));
	}

	return g_st.at(symbol);
}

void	pif_print() {
	std::cout << "Program Internal Form (PIF):" << std::endl;
	std::cout << "| code | ST index |" << std::endl;
	for (std::vector<t_pif_entry>::iterator it = g_pif.begin();
			it != g_pif.end();
			++it) {
		std::cout << "| " << std::setw(4) << std::setfill(' ') << it->token_code;
		std::cout << " | " << std::setw(8) << std::setfill(' ') << it->st_index;
		std::cout << " |" << std::endl;
	}
}

void	st_print() {
	std::cout << "Symbol Table (ST):" << std::endl;
	std::cout << "|                     Symbol | ST index |" << std::endl;
	for (std::map<std::string, int>::iterator it = g_st.begin();
			it != g_st.end();
			++it) {
		std::cout << "| " << std::setw(26) << std::setfill(' ') << it->first;
		std::cout << " | " << std::setw(8) << std::setfill(' ') << it->second;
		std::cout << " |" << std::endl;
	}
}

%}

%option noyywrap

digit		[0-9]
letter		[a-zA-Z_]

%%

\n			{ g_current_line++; }
[ \t]+		/* pass whitespaces */ ;
int			{ pif_add(3, 0); }
double		{ pif_add(4, 0); }
!=			{ pif_add(5, 0); }
=			{ pif_add(6, 0); }
\+			{ pif_add(7, 0); }
-			{ pif_add(8, 0); }
\*			{ pif_add(9, 0); }
\/			{ pif_add(10, 0); }
%			{ pif_add(11, 0); }
\<			{ pif_add(12, 0); }
\<=			{ pif_add(13, 0); }
>			{ pif_add(14, 0); }
>=			{ pif_add(15, 0); }
==			{ pif_add(16, 0); }
\<\<		{ pif_add(17, 0); }
>>			{ pif_add(18, 0); }
!			{ pif_add(19, 0); }
&&			{ pif_add(20, 0); }
\|\|		{ pif_add(21, 0); }
typedef		{ pif_add(22, 0); }
if			{ pif_add(23, 0); }
else		{ pif_add(24, 0); }
while		{ pif_add(25, 0); }
cin			{ pif_add(26, 0); }
cout		{ pif_add(27, 0); }
return		{ pif_add(28, 0); }
;			{ pif_add(29, 0); }
,			{ pif_add(30, 0); }
\{			{ pif_add(31, 0); }
\}			{ pif_add(32, 0); }
\(			{ pif_add(33, 0); }
\)			{ pif_add(34, 0); }
main		{ pif_add(35, 0); }
endl		{ pif_add(36, 0); }

{letter}({letter}|{digit}){0,249}	{
	int i = st_pos(std::string(yytext));
	pif_add(0, i);	/* identifier */
}

[\+\-]?{digit}*\.{digit}+([eE][\+\-]?{digit}+)? {
	int i = st_pos(std::string(yytext));
	pif_add(1, i);	/* floating-point constant */
}

0|[\+\-]?[1-9]{digit}*	{
	int i = st_pos(std::string(yytext));
	pif_add(1, i);	/* int constant */
}

\"[^\"]*\"	{
	int i = st_pos(std::string(yytext));
	pif_add(2, i);	/* string constant */
}

. {	/* something else */
	if (yytext[0] != '\r') {
		std::cout << "Unexpected token: " << yytext << std::endl;
	}
}

%%

int main(int argc, char **argv)
{
	yylex();
	pif_print();
	st_print();
}
