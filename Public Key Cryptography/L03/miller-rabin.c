#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <time.h>

#define DEBUG 1

long long repeated_squaring_modular_exponentiation(long long x, long long y, long long n)
{
	long long c = 0;
	long long r = 1;

	// parameter validation
	if ((x < 0) || (y < 0) || (n < 0)) {
		return -1;
	}

	c = x % n;
	while (y > 0) {
		if (y & 1) {
			r = (r * c) % n;
		}

		c = (c * c) % n;

		y = y >> 1;
	}

	return r;
}

double miller_rabin(long long n, long long k)
{
	long long s = 0;
	long long t = 0;
	long long a = 0;
	long long i = 0;
	long long e = 0;
	double p = 0.0;

	// parameter validation
	if ((n < 2) || (k < 1)) {
		return -1.0;
	}

	// 2 is 100% prime
	if (n == 2) {
		return 1.0;
	}

	p = 1.0 - (1.0 / pow(4.0, (double)k));

	// Step 0: Write n - 1 = 2^s * t, where t is odd
	for (s = 0; (t % 2) == 0; ) {
		s += 1;
		t = (n - 1) / pow(2, s);
		if (DEBUG) printf("s = %lld, t = %lld\n", s, t);
	}

	if (DEBUG) printf("s = %lld, t = %lld\n", s, t);

	srand(time(NULL));

	while (k > 0) {
		// Step 1: Choose (randomly) 1 < a < n 
		a = 2 + (rand() % (n - 2));
		if (DEBUG) printf("k = %lld, a = %lld\n", k, a);
		
		// Step 2: Compute a^(t), a^(2 * t), a^(2^(2) * t), ..., a^(2^(s) * t)
		for (i = 0; i <= s; i++) {
			e = repeated_squaring_modular_exponentiation(a, t * pow(2, i), n);
			if (DEBUG) printf("i = %lld, e = %lld\n", i, e);

			// Step 3: if the sequence gets to 1, then n is possible to be prime
			// so, we will repeat steps 1-3 with another base a, at most k times
			if (e == 1) {
				break;
			}
		}
		
		// Step 4: the algorithm stops and n is composite
		if (i == s + 1) {
			return 0.0;
		}

		k--;
	}

	// n is prime with probability 1 - (1 / 4^k)
	return p;
}

int main(int argc, char **argv)
{
	if (argc < 3) {
		fprintf(stderr, "Usage: miller-rabin <n> <k>\n");
		return 0;
	}

	long long n = atol(argv[1]);
	long long k = atol(argv[2]);
	double p = miller_rabin(n, k);

	if (p < 0.0) {
		printf("error: invalid parameters\n");
		return 0;
	}

	printf("P(%lld is prime) = %1.7lf\n", n, p);

	return 0;
}
