#include "stdafx.h"
#if 0
#include <vector>
#include <iostream>
#include <mutex>
#include <future>

using namespace std;

#define T	40
#define N	4

int result;
mutex mtx;

void print_vec(vector<int> v) {
	for (int i = 0; i < v.size(); i++) {
		cout << v[i] << " ";
	}
	cout << endl;
}

bool pred(vector<int> const &v) {
	return true;
}

void swap(int *a, int *b) {
	*a = *a ^ *b;
	*b = *a ^ *b;
	*a = *a ^ *b;
}

void work(
	vector<int>	v,
	int			i,
	int			n,
	int			nrThreads
) {
	if (i == n) {
		print_vec(v);
		if (pred(v)) {
			mtx.lock();
			result += 1;
			mtx.unlock();
		}
		return;
	}

	cout << "nrThreads = " << nrThreads << endl;
	if (nrThreads > 1) {
		for (int j = i; j < n; j++) {
			swap(v[i], v[j]);
			
			future<void> other = async([v, i, n, nrThreads]() {
				work(v, i + 1, n, nrThreads / (n - i));
			});

			swap(v[i], v[j]);
		}
	}
	else {
		for (int j = i; j < n; j++) {
			swap(v[i], v[j]);
			work(v, i + 1, n, 1);
			swap(v[i], v[j]);
		}
	}
}

void solve(vector<int> &v) {
	work(v, 0, N, T);

	cout << result << endl;
}

int main() {
	vector<int> v;

	result = 0;

	// init v
	for (int i = 1; i <= N; i++) {
		v.push_back(i);
	}

	solve(v);
}

#endif