#include <mpi.h>
#include <stdlib.h>
#include <iostream>
#include <vector>
#include <time.h>

using namespace std;

#define TAG_N		1
#define TAG_BEGIN	2
#define TAG_END		3
#define TAG_P_DATA	4
#define TAG_Q_DATA	5
#define TAG_R_DATA	6

void normalize_polynomial(vector<int> &p)
{
	while (p[p.size() - 1] == 0) {
		p.pop_back();
	}
}

void print_polynomial(vector<int> p)
{
	int n = p.size();

	for (int i = 0; i < n; i++) {
		cout << p[i] << "X^" << i << " ";
	}
	cout << endl;
}

/**
 *	Generates a random polynomial of degree n
 *	#Input:
 *		n - degree of the polynomial
 *	#Output:
 *		p - the polynomial
 */
void generate(vector <int> &p, unsigned n) {
	p.resize(n);

	for (int i = 0; i < n; i++) {
		p[i] = rand();
		p[i] = (p[i] < 0) ? -p[i] : p[i];
		p[i] = p[i] % 10;
	}
}

void sequential_regular(int *p, int *q, int *r, int n) {
	for (int i = 0; i < 2 * n; i++) {
		r[i] = 0;
	}

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			r[i + j] += p[i] * q[j];
		}
	}
}

void karatsuba(int *p, int *q, int *r, int n) {
	if (n <= 4) {
		sequential_regular(p, q, r, n);
		return;
	}

	vector<int> psv, qsv;
	psv.resize(n);
	qsv.resize(n);

	int *p1 = &p[0];
	int *p2 = &p[n / 2];
	int *q1 = &q[0];
	int *q2 = &q[n / 2];
	int *ps = psv.data();
	int *qs = qsv.data();
	int *p1q1 = &r[n * 0];
	int *p2q2 = &r[n * 1];
	int *p1p2q1q2 = &r[n * 2];

	for (int i = 0; i < n / 2; i++) {
		ps[i] = p1[i] + p2[i];
		qs[i] = q1[i] + q2[i];
	}

	karatsuba(p1, q1, p1q1, n / 2);
	karatsuba(p2, q2, p2q2, n / 2);
	karatsuba(ps, qs, p1p2q1q2, n / 2);

	for (int i = 0; i < n; i++) {
		p1p2q1q2[i] = p1p2q1q2[i] - p1q1[i] - p2q2[i];
	}

	for (int i = 0; i < n; i++) {
		r[i + n / 2] += p1p2q1q2[i];
	}
}

void master(vector<int> &p, vector<int> &q, vector<int> &r, int nrProcs)
{
	// I. split and send the work
	cout << "[master] split and send the work" << endl;
	int n = p.size();
	for (int i = 1; i < nrProcs; i++) {
		int begin = (i * n) / nrProcs;
		int end = min(n, ((i + 1) * n) / nrProcs);

		MPI_Bsend(&n, 1, MPI_INT, i, TAG_N, MPI_COMM_WORLD);
		MPI_Bsend(&begin, 1, MPI_INT, i, TAG_BEGIN, MPI_COMM_WORLD);
		MPI_Bsend(&end, 1, MPI_INT, i, TAG_END, MPI_COMM_WORLD);
		MPI_Bsend(p.data() + begin, end - begin, MPI_INT, i, TAG_P_DATA, MPI_COMM_WORLD);
		MPI_Bsend(q.data(), n, MPI_INT, i, TAG_Q_DATA, MPI_COMM_WORLD);
	}

	// II. perform master's part of work
	cout << "[master] perform master's part of work" << endl;
	{
		int begin = 0;
		int end = n / nrProcs;
		vector<int> p_partial;
		p_partial.resize(n);
		for (int i = 0; i < n; i++) {
			if (i < n / nrProcs) {
				p_partial[i] = p[i];
			}
			else {
				p_partial[i] = 0;
			}
		}

		karatsuba(p_partial.data(), q.data(), r.data(), p_partial.size());
	}

	// III. collect final results
	cout << "[master] collect final results" << endl;
	{
		vector<int> r_partial;
		r_partial.resize(2 * n - 1);
		for (int i = 1; i < nrProcs; i++) {
			MPI_Status status;
			int begin = (i * n) / nrProcs;
			int end = min(n, ((i + 1) * n) / nrProcs);

			MPI_Recv(r_partial.data(), 2 * n - 1, MPI_INT, i, TAG_R_DATA, MPI_COMM_WORLD, &status);

			for (int i = 0; i < 2 * n - 1; i++) {
				r[i] += r_partial[i];
			}
		}
	}
}

void worker(int me)
{
	int n;
	int begin;
	int end;
	vector<int> p;
	vector<int> q;
	vector<int> r;
	MPI_Status status;

	cout << "[worker " << me << "] started" << endl;

	MPI_Recv(&n, 1, MPI_INT, 0, TAG_N, MPI_COMM_WORLD, &status);
	MPI_Recv(&begin, 1, MPI_INT, 0, TAG_BEGIN, MPI_COMM_WORLD, &status);
	MPI_Recv(&end, 1, MPI_INT, 0, TAG_END, MPI_COMM_WORLD, &status);

	p.resize(n);
	q.resize(n);
	r.resize(6 * n);

	MPI_Recv(p.data() + begin, end - begin, MPI_INT, 0, TAG_P_DATA, MPI_COMM_WORLD, &status);
	MPI_Recv(q.data(), n, MPI_INT, 0, TAG_Q_DATA, MPI_COMM_WORLD, &status);

	karatsuba(p.data(), q.data(), r.data(), p.size());

	MPI_Bsend(r.data(), 2 * n - 1, MPI_INT, 0, TAG_R_DATA, MPI_COMM_WORLD);

	cout << "[worker " << me << "] finished" << endl;
}

int main(int argc, char **argv)
{
	if ((argc != 2) || (atoi(argv[1]) < 0)) {
		cerr << "usage: karatsuba_mpi <n>" << endl;
		cerr << "\t<n> - degree of polynomial" << endl;
		return 1;
	}

	MPI_Init(0, 0);
	int me;
	int size;
	MPI_Comm_size(MPI_COMM_WORLD, &size);
	MPI_Comm_rank(MPI_COMM_WORLD, &me);

	vector<int> p;
	vector<int> q;
	vector<int> r;
	unsigned int n;

	if (me == 0) {
		n = atoi(argv[1]) + 1;
		srand(time(NULL));
		generate(p, n);
		generate(q, n);
		while (n & (n - 1)) {
			++n;
			p.push_back(0);
			q.push_back(0);
		}
		r.resize(6 * n);
		master(p, q, r, size);
		r.resize(2 * n - 1);
	}
	else {
		worker(me);
	}

	MPI_Finalize();

	if (me == 0) {
		cout << "p: ";
		normalize_polynomial(p);
		print_polynomial(p);
		cout << "q: ";
		normalize_polynomial(q);
		print_polynomial(q);
		cout << "r: ";
		normalize_polynomial(r);
		print_polynomial(r);
	}

	return 0;
}
