#include "stdafx.h"
#include <string>
#include <iostream>
#include <fstream>
#include <time.h>
#include <condition_variable>
#include <list>
#include <functional>
#include <vector>
#include <thread>
#include <atomic>
#include <algorithm>

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

#pragma region Pool
class ThreadPool {
private:
	std::mutex m_mutex;
	std::condition_variable m_cond;
	std::condition_variable m_condEnd;
	std::list<std::function<void()>> m_queue;
	bool m_end;
	size_t m_liveThreads;
	std::vector<std::thread> m_threads;

public:
	explicit ThreadPool(size_t nrThreads) : m_end(false), m_liveThreads(nrThreads) {
		m_threads.reserve(nrThreads);
		for (size_t i = 0; i < nrThreads; i++) {
			m_threads.emplace_back([this]() {this->run(); });
		}
	}

	~ThreadPool() {
		close();
		for (std::thread& t : m_threads) {
			t.join();
		}
	}

	void close() {
		std::unique_lock<std::mutex> lock(m_mutex);
		m_end = true;
		m_cond.notify_all();
		while (m_liveThreads > 0) {
			m_condEnd.wait(lock);
		}
	}

	void enqueue(std::function<void()> func) {
		std::unique_lock<std::mutex> lock(m_mutex);
		m_queue.push_back(std::move(func));
		m_cond.notify_one();
	}

	//template<typename Func, typename... Args>
	//void enqueue(Func func, Args&&... args) {
	//	std::function<void()> f = [=]() {func(args...); };
	//	enqueue(std::move(f));
	//}

private:
	void run() {
		while (true) {
			std::function<void()> toExec;

			{
				std::unique_lock<std::mutex> lock(m_mutex);
				while (m_queue.empty() && !m_end) {
					m_cond.wait(lock);
				}
				if (m_queue.empty()) {
					--m_liveThreads;
					if (0 == m_liveThreads) {
						m_condEnd.notify_all();
					}
					return;
				}
				toExec = std::move(m_queue.front());
				m_queue.pop_front();
			}

			toExec();
		}
	}
};

#pragma endregion Pool

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

#if defined DO_ADD
	//loadFromFile("Add.txt");
	loadFromFile("Random.txt");
#elif defined DO_MULTIPLY
	//loadFromFile("Mul.txt");
	loadFromFile("Random.txt");
#else
	return 0;
#endif

	printf("Thread pool, in C++.\n");

	ThreadPool pool(NT);

	start = clock();

	for (int tid = 0; tid < NT; tid++) {
#if defined DO_ADD
		pool.enqueue([tid]() {addElems(tid); });
#elif defined DO_MULTIPLY
		pool.enqueue([tid]() {mulElems(tid); });
#endif
	}

	pool.close();

	end = clock();

	//printResultMatrix();
	printf("Execution time: %1.8f\n", (double)(end - start) / CLOCKS_PER_SEC);

	return 0;
}
