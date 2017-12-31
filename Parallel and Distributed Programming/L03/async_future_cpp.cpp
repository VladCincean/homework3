#include "stdafx.h"
#include <string>
#include <iostream>
#include <fstream>
#include <time.h>
#include <vector>
#include <future>

#pragma warning(disable:4996)

using namespace std;

//#define DO_ADD
#define DO_MULTIPLY

#define N	1000
#define NT	1		// NT: 1..(N * N)

#pragma region GlobalVariables

int n;			// matrix dimension
int A[N][N];	// first matrix
int B[N][N];	// second matrix
int C[N][N];	// result matrix

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

	f.close();
}

void printResultMatrix() {
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			cout << C[i][j] << " ";
		}
		cout << endl;
	}
}

void addElems(int tid) {
	int i = tid / n;
	int j = tid % n;

	while (i < n && j < n) {
		C[i][j] = A[i][j] + B[i][j];

		i = (n * i + j + NT) / n;
		j = (j + NT) % n;
	}
}

void mulElems(int tid) {
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
}

int main()
{
	vector<thread> threads = vector<thread>();
	time_t start, end;
	vector<future<void>> futures;

#if defined DO_ADD
	//loadFromFile("Add.txt");
	loadFromFile("Random.txt");
#elif defined DO_MULTIPLY
	//loadFromFile("Mul.txt");
	loadFromFile("Random.txt");
#else
	return 0;
#endif

	printf("3. std::async() and std::future(), in C++.\n");

	start = clock();

	for (int tid = 0; tid < NT; tid++) {
#if defined DO_ADD
		futures.push_back(async([tid]() {addElems(tid); }));
#elif defined DO_MULTIPLY
		futures.push_back(async([tid]() {mulElems(tid); }));
#endif
	}

	for (int tid = 0; tid < NT; tid++) {
		futures[tid].get();
	}

	end = clock();

	//printResultMatrix();
	printf("Execution time: %1.8f\n", (double)(end - start) / CLOCKS_PER_SEC);

	return 0;
}
