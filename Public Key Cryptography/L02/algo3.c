#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void gcd_algo3(
	unsigned long long	a,
	unsigned long long	b,
	unsigned long long	*gcd
)
/*
 * Divison-based Euclidean algorithm
 * https://en.wikipedia.org/wiki/Euclidean_algorithm
 */
{
	if (a == 0)
	{
		*gcd = b;
		return;
	}

	if (b == 0)
	{
		*gcd = a;
		return;
	}

	while (a != b)
	{
		if (a > b)
		{
			a = a - b;
		}
		else
		{
			b = b - a;
		}
	}

	*gcd = a;
}

int main(int argc, char **argv)
{
	unsigned long long a = 0;
	unsigned long long b = 0;
	unsigned long long gcd = 0;
	clock_t start;
	clock_t end;
	double time;

	if (argc != 3)
	{
		fprintf(stderr, "Usage: %s <a> <b>\n", argv[0]);
		return 1;
	}

	a = atol(argv[1]);
	b = atol(argv[2]);

	start = clock();
	gcd_algo3(a, b, &gcd);
	end = clock();

	printf("gcd = %llu\n", gcd);
	time = (double)(end - start) / CLOCKS_PER_SEC * 1000.0;
	printf("time = %.9f\n", time);
	return 0;
}
