#include "stdafx.h"
#include <string>
#include <iostream>
#include <fstream>
#include <thread>
#include <vector>
#include <time.h>
#include <condition_variable>

using namespace std;

#define N	1000
#define NT	4		// NT: 1..(N * N)

#pragma region GlobalVariables

int n;			// matrix dimension
int A[N][N];	// first matrix
int B[N][N];	// second matrix
int C[N][N];	// third matrix
int D[N][N];	// result matrix

mutex				gMutexes[NT];
condition_variable	gConditionVariables[NT];
bool				gThreadDone[NT];
					// if all the elements that a thread are processing have a result in the first multiplication -> true

#pragma endregion GlobalVariables

void loadFromFile(string in) {
	ifstream f(in);

	f >> n;

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			f >> A[i][j];
		}
	}

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			f >> B[i][j];
		}
	}

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			f >> C[i][j];
		}
	}

	f.close();
}

void printResultMatrix() {
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			cout << D[i][j] << " ";
		}
		cout << endl;
	}
}

void mulElems1(int tid) {
	unique_lock<mutex> lock(gMutexes[tid]);

	int i = tid / n;
	int j = tid % n;

	while (i < n && j < n) {
		C[i][j] = 0;
		for (int k = 0; k < n; k++) {
			C[i][j] += A[i][k] * B[k][j];
		}

		i = (n * i + j + NT) / n;
		j = (j + NT) % n;
	}

	gThreadDone[tid] = true;
	gConditionVariables[tid].notify_all();
}

void mulElems2(int tid) {
	unique_lock<mutex> lock(gMutexes[tid]);

	gConditionVariables[tid].wait(lock, [tid]() {return gThreadDone[tid]; });

	int i = tid / n;
	int j = tid % n;

	while (i < n && j < n) {
		D[i][j] = 0;
		for (int k = 0; k < n; k++) {
			D[i][j] += B[i][k] * C[k][j];
		}

		i = (n * i + j + NT) / n;
		j = (j + NT) % n;
	}
}

int main()
{
	vector<thread> threads1 = vector<thread>();	// threads for the first multiplication
	vector<thread> threads2 = vector<thread>();	// threads for the second multiplication

	loadFromFile("Random.txt");

	time_t start, end;
	start = clock();

	// preinit global variables
	memset(gThreadDone, 0, sizeof(gThreadDone));

	// start the second set of threads; these will wait until data written by the first set of threads becomes available
	for (int tid = 0; tid < NT; tid++) {
		threads2.push_back(thread(&mulElems2, tid));
	}

	// start the first set of threads; these will start immediately and will notify when they are done
	for (int tid = 0; tid < NT; tid++) {
		threads1.push_back(thread(&mulElems1, tid));
	}

	for (int tid = 0; tid < NT; tid++) {
		threads1[tid].join();
	}

	for (int tid = 0; tid < NT; tid++) {
		threads2[tid].join();
	}

	end = clock();

	//printResultMatrix();
	printf("Execution time: %1.8f\n", (double)(end - start) / CLOCKS_PER_SEC);

	return 0;
}
