#include <gmp.h>
#include <stdio.h>
#include <time.h>

void gcd_algo1(
	mpz_t	a,
	mpz_t 	b,
	mpz_t	*gcd
)
/*
 * The Euclidean algorithm.
 * https://en.wikipedia.org/wiki/Euclidean_algorithm
 */
{
	mpz_t r;

	if (NULL == gcd) return;

	mpz_init(r);
	mpz_set_ui(r, 0); // r = 10; // "ui" ~ unsigned int

	while (mpz_cmp_ui(b, 0) > 0)
	{
		mpz_mod(r, a, b); // r = a % b
		mpz_set(a, b); // a = b
		mpz_set(b, r); // b = r
	}

	mpz_set(*gcd, a); // *gcd = a

	mpz_clear(r);
}

int main(int argc, char **argv)
{
	mpz_t a;
	mpz_t b;
	mpz_t gcd;
	int status_flag = 0;
	clock_t start;
	clock_t end;

	if (argc != 3)
	{
		fprintf(stderr, "Usage: %s <a> <b>\n", argv[0]);
		return 1;
	}

	mpz_init(a);
	mpz_set_ui(a, 0);
	mpz_init(b);
	mpz_set_ui(b, 0);
	mpz_init(gcd);
	mpz_set_ui(gcd, 0);

	status_flag = mpz_set_str(a, argv[1], 10);
	if (status_flag != 0)
	{
		fprintf(stderr, "Error: invalid argument 'a'\n");
		return 1;
	}

	status_flag = mpz_set_str(b, argv[2], 10);
	if (status_flag != 0)
	{
		fprintf(stderr, "Error: invalid argument 'b'\n");
		return 1;
	}

	start = clock();
	gcd_algo1(a, b, &gcd);
	end = clock();

	printf("gcd = ");
	mpz_out_str(stdout, 10, gcd);
	printf("\n");

	printf("time = %2.9f\n", (double)(end - start) / CLOCKS_PER_SEC * 1000.0);

	mpz_clears(a, b, gcd, NULL);

	return 0;
}
