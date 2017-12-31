#include <stdio.h>
#include <stdlib.h>
#include <gmp.h>
#include <time.h>

#define NO_OF_TRIALS	20

#define DEBUG			1

void pollard(
		mpz_t	n,
		mpz_t	B,
		mpz_t	*d
)
/*
 *	The p - 1 Pollard algorithm for the factorization problem.
 *	#Input:
 *		n	- an odd composite number
 *		B	- a bound
 *	#Output:
 *		d	- a non-trivial factor of n 
 * */
{
	mpz_t			k;
	mpz_t			a;
	mpz_t			n4;
	mpz_t			n2;
	gmp_randstate_t	state;

	if (NULL == d) return;

	// init variables
	mpz_init(k);
	mpz_set_ui(k, 1);		// k := 1
	mpz_init(a);
	mpz_init(n4);
	mpz_sub_ui(n4, n, 4);	// n4 := n - 4
	mpz_init(n2);
	mpz_mod_ui(n2, n, 2);	// n2 := n % 2
	gmp_randinit_mt(state);
	gmp_randseed_ui(state, time(NULL));

	if (mpz_cmp_ui(n2, 0) == 0) {
		mpz_set_ui(*d, 2);
		goto CleanUp;
	}

	for (int i = 0; i < NO_OF_TRIALS; i++) {
		// k := lcm(1, ..., B)
		for (unsigned long int j = 1; mpz_cmp_ui(B, j) >= 0; j++) {
			mpz_lcm_ui(k, k, j);
		}

		if (DEBUG) { printf("k = "); mpz_out_str(stdout, 10, k); printf("\n"); }

		// a := a random number, 1 < a < n - 1
		mpz_urandomm(a, state, n4); // a random number 0 <= a <= n - 4
		mpz_add_ui(a, a, 2);		// a random number 2 <= a <= n - 2
//		mpz_set_ui(a, 2);		
		if (DEBUG) { printf("a = "); mpz_out_str(stdout, 10, a); printf("\n"); }

		// a := a^k mod n
		mpz_powm(a, a, k, n);

		if (DEBUG) { printf("a^k mod n = "); mpz_out_str(stdout, 10, a); printf("\n"); }

		// d := gcd(a - 1, n)
		mpz_sub_ui(a, a, 1);
		mpz_gcd(*d, a, n);

		if (DEBUG) { printf("d = "); mpz_out_str(stdout, 10, *d); printf("\n"); }

		// if d == 1 or d == n, then we failed => another try
		if ((mpz_cmp_ui(*d, 1) != 0) && (mpz_cmp(*d, n) != 0)) {
			// d != 1 and d != n, then success
			goto CleanUp;
		}
	}

	// if d == 1 or d == n, then we failed and we do not have any trials left
	mpz_set_ui(*d, 0);

CleanUp:
	mpz_clear(k);
	mpz_clear(a);
	mpz_clear(n4);
	gmp_randclear(state);
}

int main(int argc, char **argv) {
	mpz_t	n;
	mpz_t	B;
	mpz_t	d;
	clock_t	start;
	clock_t	end;

	if (argc != 3) {
		fprintf(stderr, "Usage: pollard <n> <B>\n");
		fprintf(stderr, "\tn - an odd composite number\n");
		fprintf(stderr, "\tB - a bound\n");
		return 1;
	}

	mpz_init(n);
	mpz_init(B);
	mpz_init(d);

	mpz_set_str(n, argv[1], 10);
	mpz_set_str(B, argv[2], 10);

	start = clock();
	pollard(n, B, &d);
	end = clock();

	if (mpz_cmp_ui(d, 0) == 0) {
		printf("FAILURE!\n");
	}
	else {
		printf("A factor of ");
		mpz_out_str(stdout, 10, n);
		printf(" is ");
		mpz_out_str(stdout, 10, d);
		printf("\n");
	}

	printf("time = %f\n", (double)(end - start) / CLOCKS_PER_SEC * 1000.0);

	mpz_clear(n);
	mpz_clear(B);
	mpz_clear(d);

	return 0;
}
