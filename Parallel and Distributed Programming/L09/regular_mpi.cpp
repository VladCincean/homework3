#include <mpi.h>
//#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <iostream>
#include <vector>

using namespace std;

#define TAG_N		1
#define TAG_BEGIN	2
#define TAG_END		3
#define TAG_P_DATA	4
#define TAG_Q_DATA	5
#define TAG_R_DATA	6

void print_polynomial(vector<int> p)
{
	int n = p.size();

	for (int i = 0; i < n; i++) {
		cout << p[i] << "X^" << n - i - 1 << " ";
	}
	cout << endl;
}

/**
 *	Generates a random polynomial of degree n
 *	#Input:
 *		n - degree of the polynomial
 *	#Output
 *		p - the polynomial
 */
void generate(vector<int> &p, unsigned int n)
{
	p.resize(n);

	for (int i = 0; i < n; i++) {
		p[i] = rand();
		p[i] = (p[i] < 0) ? -p[i] : p[i];
		p[i] = p[i] % 10;
	}
}

/**
 *	Performs r := p * q on nrProcs processes
 *	#Input:
 *		p - a polynomial
 *		q - another polynomial
 *		nrProcs - int
 *	#Output:
 *		r := p * q
 */
void master(vector<int> &p, vector<int> &q, vector<int> &r, int nrProcs)
{
	// I. split and send the work
	cout << "[master] split and send the work" << endl;
	int n = p.size();
	int size = p.size() + q.size() - 1;
	for (int i = 1; i < nrProcs; i++) {
		int begin = (i * size) / nrProcs;
		int end = min(size, ((i + 1) * size) / nrProcs);

		MPI_Bsend(&n, 1, MPI_INT, i, TAG_N, MPI_COMM_WORLD);
		MPI_Bsend(&begin, 1, MPI_INT, i, TAG_BEGIN, MPI_COMM_WORLD);
		MPI_Bsend(&end, 1, MPI_INT, i, TAG_END, MPI_COMM_WORLD);
		MPI_Bsend(p.data(), min((int)p.size(), end), MPI_INT, i, TAG_P_DATA, MPI_COMM_WORLD);
		MPI_Bsend(q.data(), min((int)q.size(), end), MPI_INT, i, TAG_Q_DATA, MPI_COMM_WORLD);
	}

	// II. perform master's part of work
	cout << "[master] perform master's part of work" << endl;
	for (int i = 0; i < (size / nrProcs); i++) {
		for (int x = 0; (x <= (int)p.size()) && (x <= i); x++) {
			int y = i - x;

			if (y >= (int)q.size()) {
				continue;
			}

			r[i] += p[x] * q[y];
		}
	}

	// III. collect final results
	cout << "[master] collect final results" << endl;
	for (int i = 1; i < nrProcs; i++) {
		MPI_Status status;
		int begin = (i * size) / nrProcs;
		int end = min(size, ((i + 1) * size) / nrProcs);
		MPI_Recv(r.data() + begin, end - begin, MPI_INT, i, TAG_R_DATA, MPI_COMM_WORLD, &status);
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

	p.resize(end);
	q.resize(end);
	r.resize(end - begin);

	for (int i = 0; i < end - begin; i++) {
		r[i] = 0;
	}

	MPI_Recv(p.data(), min(n, end), MPI_INT, 0, TAG_P_DATA, MPI_COMM_WORLD, &status);
	MPI_Recv(q.data(), min(n, end), MPI_INT, 0, TAG_Q_DATA, MPI_COMM_WORLD, &status);

	for (int i = begin; i < end; i++) {
		for (int x = 0; (x <= (int)p.size()) && (x <= i); x++) {
			int y = i - x;

			if (y >= (int)q.size()) {
				continue;
			}

			r[i - begin] += p[x] * q[y];
		}
	}

	MPI_Bsend(r.data(), end - begin, MPI_INT, 0, TAG_R_DATA, MPI_COMM_WORLD);

	cout << "[worker " << me << "] finished" << endl;
}

int main(int argc, char **argv)
{
	if ((argc != 2) || (atoi(argv[1]) < 0)) {
		cerr << "usage: regular_mpi <n>" << endl <<"\t<n> - degree of polynomial" << endl;
		return 1;
	}

	MPI_Init(0, 0);
    int me;
    int size;
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &me);
    cout << "Hello, I am " << me << " out of " << size << "." << endl;	
	
	vector<int> p;
	vector<int> q;
	vector<int> r;
	unsigned int n;

    if (me == 0) {
		n = atoi(argv[1]) + 1;
		p.resize(n);
		q.resize(n);
		r.resize(2 * n - 1);
		srand(time(NULL));
		generate(p, n);
		generate(q, n);
		for (int i = 0; i < r.size(); i++) {
			r[i] = 0;
		}
		
		master(p, q, r, size);
    } else {
		worker(me);
    }
    
    MPI_Finalize();

	if (me == 0) {
		cout << "p: ";
		print_polynomial(p);
		cout << "q: ";
		print_polynomial(q);
		cout << "r: ";
		print_polynomial(r);
	}

	return 0;
}
