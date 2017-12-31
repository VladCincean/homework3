# Lab 7 - Parallelizing techniques (2)

## Goal
The goal of this lab is to implement a simple but non-trivial parallel algorithm.

## Requirement
1. Given a sequence of _n_ numbers, compute the sums of the first _k_ numbers, for each _k_ between 1 and _n_. Parallelize the computations, to optimize for low latency on a large number of processors. Use at most 2 * _n_ additions, but no more than 2*log(_n_) additions on each computation path from inputs to an output. Example: if the input sequence is 1 5 2 4, then the output should be 1 6 8 12.

2. Add _n_ big numbers. We want the result to be obtained digit by digit, starting with the least significant one, and as soon as possible. For this reason, you should use _n_-1 threads, each adding two numbers. Each thread should pass the result to the next thread. Arrange the threads in a binary tree. Each thread should pass the sum to the next thread through a queue, digit by digit.
