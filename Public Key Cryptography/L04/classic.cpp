#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <vector>

#define	DEBUG	1

using namespace std;

void classic(
		long long	n,
		long long	*d
)
/*
 *	The classic trial division algorithm for the factorization problem.
 *	#Input:
 *		n	- an odd composite number
 *	#Output:
 *		d	- a non-trivial factor of n
 * */
{
	for (long long i = 2; i <= (long long)sqrt(n); i++) {
		if (n % i == 0) {
			*d = i;
			if (DEBUG) { printf("d = %lld\n", i); }
			return;
		}
	}

	*d = n;
}

void factorize(
		long long	n,
		vector<long long> *factors
)
{
	long long d;

	while (n != 1) {
		classic(n, &d);

		factors->push_back(d);
		n /= d;
	}
}

int main(int argc, char **argv) {
	long long 			n;
	vector<long long>	factors;
	clock_t				start;
	clock_t				end;
	
	if (argc != 2) {
		fprintf(stderr, "Ussage: classic <n>\n");
		fprintf(stderr, "\tn - an odd composite number\n");
		return 1;
	}

	n = atol(argv[1]);
	if (n < 2) {
		printf("Wrong input :(\n");
		return 1;
	}

	start = clock();
	factorize(n, &factors);
	end = clock();

	printf("factors:");
	for (vector<long long>::iterator it = factors.begin(); it != factors.end(); it++) {
		printf(" %lld", *it);
	}
	printf("\n");

	printf("time = %f\n", (double)(end - start) / CLOCKS_PER_SEC * 1000.0);

	return 0;
}
