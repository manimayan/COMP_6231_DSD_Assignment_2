## About The Project

### Distributed Class Management System (DCMS) using Java IDL - Assignment 2

### Outline
In this assignment, you are going to implement the distributed Class Management system (DCMS) from Assignment #1 in CORBA using Java IDL. In addition to the 4 operations introduced in Assignment #1 (namely, createTRecord, createSRecord, getRecordCounts, editRecord) the following operations also need to be implemented.

In this assignment you are going to develop this application in CORBA using Java IDL. 
Specifically, do the following: 

➲ Write the Java IDL interface definition for the modified DCMS with all the 5 specified operations. 

➲ Implement the modified DCMS. You should design a server that maximizes concurrency. In other words, use proper synchronization that allows multiple managers to correctly perform operations on the same or different records at the same time. 

➲ Test your application by running multiple managers with the 3 servers. Your test cases should check correct concurrent access of shared data, and the atomicity of transferRecord operation (e.g. what if a record being edited needs to be transferred and both operations were initiated at the same time.