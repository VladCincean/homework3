#include <iostream>
#include <mpi.h>

int main()
{
    MPI_Init(0, 0);
    int me;
    int size;
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &me);
    std::cout << "Hello, I am " << me << " out of " << size << "\n";
    
    int message;
    if(me == 0) {
        message = 42;
    } else {
        MPI_Status status;
        int nr;
        MPI_Recv(&message, 1, MPI_INT, MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
        MPI_Get_count(&status, MPI_INT, &nr);
        if(nr != 1){
            std::cerr << "Wrong size\n";
        }
        std::cout << "Me="<<me << "; source=" << status.MPI_SOURCE << "; tag="<<status.MPI_TAG<<
            "; message="<<message<<"\n";
    }
    
    if(me == size-1) {
        std::cout << "Message = " << message << "\n";
    } else {
        ++message;
        MPI_Ssend(&message, 1, MPI_INT, me+1, 123, MPI_COMM_WORLD);
    }
    
    MPI_Finalize();
}