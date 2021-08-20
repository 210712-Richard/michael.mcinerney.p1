# Tuition Reimbursement System (TRMS)
## Project Description
A system that allows employees of a company to request reimbursements. The reimbursements are approved or denied by supervisors, department heads, and benefit coordinators.

## Technologies Used
* Java
* Datastax
* Javalin
* JUnit
* Log4J
* Maven
* Cassandra

## Features
* Submit reimbursement requests
* Approve reimbursements
* Auto=escalate approvals based on next approvee
* Add files to the request
* Auto-approve requests after a time limit
* Receive notifications on the state of the request

## Getting Started
To run the project locally, ensure that the following have been setup:
* Java 8 runtime environment
* An AWS Keyspace
* An S3 bucket
* An AWS IAM user with programatic access to the Keyspace
* An AWS IAM user with programatic access to the S3 bucket configued with the AWS CLI

After ensuring the environment is correct, follow these steps to run the project:
1. Clone the project using the link:
```
git clone https://github.com/210712-Richard/michael.mcinerney.p1
```
2. Set two environmental varaibles, AWS_USER and AWS_PASS, to a AWS IAM user with programmtic access to the Keyspace being used.
3. Go into the directory and build the application using:
```
mvn package
```
4. Run the following command to run the project:
```
java -cp target/Project1-0.0.1-SNAPSHOT.jar com.revature.Driver
```
After completing these steps, the application should be running on http://localhost:8080.
