#include <iostream>
#include <queue>
#include <thread>
#include <vector>
#include <cstdlib>
#include <ctime>

using namespace std;

#define N			4
#define NR_DIGITS	10

vector<queue<int>> gQueues;
vector<bool> gIsDone;

void sum(int tid)
{
	int leftChild = 2 * tid;
	int rightChild = 2 * tid + 1;
	bool isDone = false;

	if (tid < N / 2) {	// first layer
		isDone = true;
	}

	while ((!isDone) || (!gQueues[leftChild].empty()) || (!gQueues[rightChild].empty())) {
		if (tid >= N / 2) { // maybe wait for children to finish or queue to fill
			while ((!isDone) || (gQueues[leftChild].empty()) || (gQueues[rightChild].empty())) {
				if (gIsDone[leftChild - N] && gIsDone[rightChild - N]) {
					isDone = true;
				}
			}
		}

		int elemFromLeftChild = gQueues[leftChild].front();
		int elemFromRightChild = gQueues[rightChild].front();
		gQueues[leftChild].pop();
		gQueues[rightChild].pop();
		gQueues[tid + N].push(elemFromLeftChild + elemFromRightChild);
	}

	gIsDone[tid] = true;
}

int main()
{
	int nrThreads = N - 1;
	vector<thread> threads;
	vector<int> result;
	clock_t start;
	clock_t end;

	srand(time(0));

	for (int i = 0; i < 2 * N; i++) {
		queue<int> q;

		if (i < N) {
			for (int j = 0; j < NR_DIGITS; j++) {
				int digit = rand() % 10;
				q.push(digit);
			}
		}
		gQueues.push_back(q);
	}

	threads.reserve(nrThreads);
	
	for (int tid = 0; tid < nrThreads; tid++) {
		gIsDone.push_back(false);
	}

	start = clock();

	for (int tid = 0; tid < nrThreads; tid++) {
		threads.emplace_back(sum, tid);
	}

	for (thread& t : threads) {
		t.join();
	}

	while (!gQueues[N * 2 - 2].empty()) {
		result.push_back(gQueues[N * 2 - 2].front());
		gQueues[N * 2 - 2].pop();
	}

	for (size_t i = 0; i < result.size(); i++) {
		int carry = result[i] / 10;
		if (carry > 0) {
			if (i == result.size() - 1) {
				result.push_back(0);
			}
			result[i] %= 10;
			result[i + 1] += carry;
		}
	}

	end = clock();
	cout << "time: " << (end - start) / (double)CLOCKS_PER_SEC << endl;

	for (size_t i = 0; i < result.size(); i++) {
		cout << result[i] << " ";
	}
	cout << endl;

    return 0;
}
