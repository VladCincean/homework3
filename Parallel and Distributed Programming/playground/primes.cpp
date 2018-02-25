#include "stdafx.h"
#if 0
#include <vector>
#include <iostream>
#include <mutex>
#include <future>
#include <math.h>
#include <stdio.h>

using namespace std;

#define T	4
#define N	1000

bool sieve[N + 1];

vector<int> primes;

void worker(int tid, int begin, int end) {
	for (int i = begin; i < end; i++) {
		int p = primes[i];

		if (sieve[p]) {
			//cout << "worker " << tid << " processing prime " << p << endl;
			printf("worker %d processing prime %d\n", tid, p);
			int i = 2;
			while (p * i <= N) {
				sieve[p * i] = false;
				i += 1;
			}
		}
	}
}

int main() {
	int sqrtN = (int) sqrt(N);

	sieve[0] = false;   // 0 is neither prime nor composite
	sieve[1] = false;   // 1 is neither prime nor composite
	sieve[2] = true;    // 2 is prime
	for (int p = 3; p <= N; p++) {
		sieve[p] = true; // assume all are primes
	}

	// serially generate all prime numbers until sqrt(N)
	for (int p = 2; p <= sqrtN; p++) {
		if (sieve[p] == true) {
			//cout << "main processing prime " << p << endl;
			printf("main processing prime %d\n", p);
			int i = 2;
			while (p * i <= sqrtN) {
				sieve[p * i] = false;
				i += 1;
			}
		}
	}

	// now, we have all primes up to sqrt(n)
	for (int p = 2; p <= sqrtN; p++) {
		if (sieve[p] == true) {
			primes.push_back(p);
		}
	}

	// give work to threads
	vector<thread> threads;
	for (int tid = 0; tid < T; tid++) {
		int begin = tid * primes.size() / T;
		int end = (tid + 1) * primes.size() / T;
		threads.emplace_back(worker, tid, begin, end);
	}

	// wait for threads to finish
	for (int tid = 0; tid < T; tid++) {
		threads[tid].join();
	}

	// now, we have all the primes up to N
	for (int p = sqrtN + 1; p <= N; p++) {
		if (sieve[p]) {
			primes.push_back(p);
		}
	}

	// print them
	for (int i = 0; i < primes.size(); i++) {
		cout << primes[i] << " ";
	}
	cout << endl;

	return 0;
}

#endif