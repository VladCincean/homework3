all: lab4

lab4: lex.yy.c grammar.tab.c
	gcc lex.yy.c grammar.tab.c -o lab4

lex.yy.c:
	flex spec.l

grammar.tab.c:
	bison -d grammar.y

clean:
	rm -f lex.yy.c grammar.tab.c grammar.tab.h

fclean: clean
	rm -f lab4

re: fclean all


