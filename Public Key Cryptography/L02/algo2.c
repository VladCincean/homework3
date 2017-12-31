#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void swap(
	unsigned long long	*a,
	unsigned long long	*b
)
{
	*a = *a ^ *b;
	*b = *a ^ *b;
	*a = *a ^ *b;
}

void gcd_algo2(
	unsigned long long	a,
	unsigned long long	b,
	unsigned long long	*gcd
)
/*
 * Stein's algorithm
 * https://en.wikipedia.org/wiki/Binary_GCD_algorithm
 */
{
	int shift = 0;
	
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

	for (shift = 0; ((a | b) & 1) == 0; shift++)
	{
		a >>= 1;
		b >>= 1;
	}

	while ((a & 1) == 0)
	{
		a >>= 1;
	}

	do
	{
		while ((b & 1) == 0)
		{
			b >>= 1;
		}

		if (a > b)
		{
			swap(&a, &b);
		}

		b = b - a;
	} while (b != 0);

	a <<= shift;
	
	*gcd = a;
}

int main(int argc, char **argv)
{
	unsigned long long a = 0;
	unsigned long long b = 0;
	unsigned long long gcd = 0;
	clock_t start;
	clock_t end;

	if (argc != 3)
	{
		fprintf(stderr, "Usage: %s <a> <b>\n", argv[0]);
		return 1;
	}

	a = atol(argv[1]);
	b = atol(argv[2]);

	start = clock();
	gcd_algo2(a, b, &gcd);
	end = clock();

	printf("gcd = %llu\n", gcd);
	printf("time = %2.9f\n", (double)(end - start) / CLOCKS_PER_SEC * 1000.0);

	return 0;
}
