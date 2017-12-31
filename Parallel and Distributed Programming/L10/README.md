# Lab 10 - Distributed protocols

## Goal
The goal of this lab is to deal with the consistency issues of the distributed programming.

## Requirement
Implement a simple distributed shared memory (DSM) mechanism. The lab shall have a main program and a DSM library. There shall be a predefined number of communicating processes. The DSM mechanism shall provide a predefined number of integer variables residing on each of the processes. The DSM shall provide the following operations:

* write a value to a variable (local or residing in another process);
* a callback informing the main program that a variable managed by the DSM has changed. All processes shall receive the same sequence of data change callbacks; it is not allowed that process P sees first a change on a variable A and then a change on a variable B, while another process Q sees the change on B first and the change on A second.
* a "compare and exchange" operation, that compares a variable with a given value and, if equal, it sets the variable to another given value. Be careful at the interaction with the previous requirement.
