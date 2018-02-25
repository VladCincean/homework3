#include "stdafx.h"
#if 1
#include <vector>
#include <iostream>
#include <mutex>
#include <future>
#include <atomic>

using namespace std;

#define T	4
#define N	5
#define K	3

int result;
mutex mtx;
atomic<int> found;

void print_vec(vector<int> v) {
	for (int i = 0; i < v.size(); i++) {
		cout << v[i] << " ";
	}
	cout << endl;
}

bool given_function(vector<int> const &v) {
	//return false;
	if (v[0] == 2 && v[1] == 1) {
		return true;
	}
	return false;
}

void work(vector<int> objects, vector<int> colors, int k, int n, int i, int nrThreads) {
	if (found != 0) {
		// already found
		return;
	}

	if (i == n) {

		print_vec(objects);

		if (given_function(objects) == true) {
			int wasFound = found.fetch_or(1);
			if (wasFound == 0) {
				// we are the first to find
				cout << "solution: ";
				print_vec(objects);
			}
		}
		return;
	}

	if (nrThreads > 1) {
		for (int j = 0; j < k; j++) {
			objects[i] = colors[j];
			future<void> other = async([objects, colors, k, n, i, j, nrThreads]() {
				work(objects, colors, k, n, i + 1, nrThreads / k);
			});
			objects[i] = 0;
		}
	}
	else {
		for (int j = 0; j < k; j++) {
			objects[i] = colors[j];
			work(objects, colors, k, n, i + 1, 1);
			objects[i] = 0;
		}
	}
}

void solve(vector<int> &objects, vector<int> &colors) {
	work(objects, colors, colors.size(), objects.size(), 0, T);

	cout << found << endl;
}

int main() {
	vector<int> objects;
	vector<int> colors;

	found.fetch_add(0);

	// init objects
	for (int i = 1; i <= N; i++) {
		objects.push_back(0);
	}

	// init colors
	for (int i = 1; i <= K; i++) {
		colors.push_back(i);
	}

	solve(objects, colors);
}

#endif