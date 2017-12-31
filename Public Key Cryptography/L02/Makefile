ALGO1 = algo1
ALGO2 = algo2
ALGO3 = algo3

algo1:
	gcc -Wall -Wextra -Werror algo1.c -o algo1 -lgmp

algo2:
	gcc -Wall -Wextra -Werror algo2.c -o algo2

algo3:
	gcc -Wall -Wextra -Werror algo3.c -o algo3


all: algo1 algo2 algo3

clean:
	rm -f algo1 algo2 algo3

fclean: clean

re: fclean all
