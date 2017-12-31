

all: classic pollard

classic:
	g++ -Wall -Wextra -Werror classic.cpp -lm -o classic

pollard:
	gcc -Wall -Wextra -Werror pollard.c -lgmp -o pollard

clean:
	rm -f classic pollard

fclean: clean

re: fclean all
