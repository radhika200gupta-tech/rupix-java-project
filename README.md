RUPIX — Freelancer Escrow & Digital Payment System

A modular, multithreaded Java console application that combines UPI-style wallet payments, merchant settlements, and freelancer milestone-based escrow into a unified digital payment system.

RUPIX demonstrates Core Java, Object-Oriented Programming, Java Collections Framework, Multithreading, Synchronization, Exception Handling, File I/O, and transaction management through a practical financial application.

1. Problem Statement

Digital payment systems involve multiple operations, including person-to-person transfers, merchant payments, settlement processing, and secure handling of project-based payments.

Freelancers often face payment uncertainty because clients and service providers need a reliable mechanism to manage project funds and release payments after completing agreed milestones.

Similarly, digital payment systems must handle concurrent transactions, maintain accurate wallet balances, calculate merchant deductions, and ensure that failed transactions do not result in incorrect balance updates.

RUPIX addresses these requirements by integrating three interconnected modules:

UPI and Wallet Management

Merchant Payment and Settlement

Freelancer Escrow and Milestone Management

The application provides a terminal-based environment to demonstrate how these operations can be implemented using Core Java without requiring an external database or third-party libraries.

2. Project Objectives

UPI Wallet Management: Register users, create wallets, add money, and perform UPI-style transfers.

Concurrent Transactions: Execute multiple payment operations using Java multithreading and synchronization.

Merchant Payments: Support customer-to-merchant payments and merchant registration.

Settlement Processing: Calculate Merchant Discount Rate (MDR) deductions and process merchant settlements.

Freelancer Escrow: Create projects, deposit funds, and manage milestone-based payment releases.

Transaction Integrity: Handle failed operations and maintain consistent wallet balances.

Data Persistence: Store application records in text files and restore them when the application restarts.

Academic Demonstration: Implement important Java programming concepts through an integrated financial application.

3. Scope and Limitations

Application Scope

Terminal-based digital payment simulation.

User and wallet registration.

UPI-style person-to-person transfers.

Merchant registration and payments.

MDR calculation and settlement processing.

Freelancer project funding and milestone releases.

Concurrent payment execution.

File-based data persistence.

Transaction and escrow reporting.

Limitations

Does not connect to actual UPI infrastructure or banking networks.

Does not process real financial transactions.

Does not integrate with external payment gateways.

Uses local text files instead of a relational database.

Does not provide the transaction guarantees of a production database.

Does not include a web-based interface.

Important: RUPIX is an academic payment-system simulation. It demonstrates financial transaction workflows and concurrency concepts without handling real money.

4. Technologies Used

Technology

Purpose

Java

Core application development

Java OOP

Encapsulation, inheritance, abstraction and modular design

Java Collections

ArrayList, HashMap, HashSet and Queue

Java Concurrency

Runnable, Thread and synchronization

Java Exception Handling

Custom exceptions and error management

Java File I/O

Reading and writing application records

Git & GitHub

Version control and team collaboration

External libraries: None required. The application uses standard Java libraries.

Database: No SQL/JDBC or external database is used.

5. Application Architecture

The application follows a modular package-based architecture.

src/
└── com/rupix/
│
├── Main.java
│
├── model/
│   ├── User.java
│   ├── Wallet.java
│   ├── Transaction.java
│   ├── Merchant.java
│   ├── MerchantPayment.java
│   ├── Settlement.java
│   ├── EscrowProject.java
│   └── Milestone.java
│
├── service/
│   ├── WalletService.java
│   ├── PaymentService.java
│   ├── MerchantService.java
│   ├── SettlementService.java
│   ├── EscrowService.java
│   └── MilestoneService.java
│
├── concurrency/
│   ├── PaymentTask.java
│   └── ConcurrencyDemo.java
│
├── settlement/
│   └── SettlementQueue.java
│
├── escrow/
│   └── EscrowStatus.java
│
├── exception/
│   ├── InsufficientBalanceException.java
│   ├── InvalidAmountException.java
│   ├── InvalidMerchantException.java
│   └── InvalidEscrowException.java
│
├── ui/
│   ├── PaymentMenu.java
│   ├── MerchantMenu.java
│   └── EscrowMenu.java
│
└── util/
├── FileStorage.java
├── MerchantReport.java
├── TransactionComparator.java
├── EscrowReport.java
└── InputValidator.java

Total: 31 Java source files.

Module Responsibilities

Module

Responsibility

model

User, wallet, transaction, merchant and escrow entities

service

Payment, wallet, merchant, settlement and escrow operations

concurrency

Concurrent payment execution

settlement

Pending merchant settlement management

escrow

Escrow status management

exception

Custom exception handling

ui

Terminal-based module menus

util

File persistence, validation and reports

Main.java

Application entry point and menu integration

6. Core Application Modules

Module 1 — UPI and Wallet Management

Handles user registration, wallet creation, balance management, and UPI-style money transfers.

Key classes:

User.java

Wallet.java

Transaction.java

WalletService.java

PaymentService.java

PaymentTask.java

ConcurrencyDemo.java

PaymentMenu.java

Features:

Register users with unique UPI IDs.

Create wallets for registered users.

Add money to wallets.

Check wallet balances.

Transfer money between users.

Record successful transactions.

Handle insufficient balance and invalid amounts.

Execute concurrent payment operations.

Shared responsibility: Member 1 maintains FileStorage.java and the common wallet/payment services used by the other modules.

Module 2 — Merchant Payment and Settlement

Handles merchant registration, customer merchant payments, MDR deductions, and settlement processing.

Key classes:

Merchant.java

MerchantPayment.java

Settlement.java

MerchantService.java

SettlementService.java

SettlementQueue.java

MerchantMenu.java

MerchantReport.java

TransactionComparator.java

Features:

Register merchants with unique UPI IDs.

Process customer-to-merchant payments.

Calculate MDR deductions.

Calculate merchant settlement amounts.

Maintain pending settlement records.

Process settlements.

Generate merchant transaction reports.

Sort transaction records.

Settlement calculation:

[
\text{MDR} = \text{Payment Amount} \times \frac{\text{MDR Rate}}{100}
]

[
\text{Settlement Amount} = \text{Payment Amount} - \text{MDR}
]

Module 3 — Freelancer Escrow and Milestone Management

Handles project-based payments through an escrow mechanism.

Key classes:

EscrowProject.java

Milestone.java

EscrowService.java

MilestoneService.java

EscrowMenu.java

EscrowStatus.java

EscrowReport.java

InvalidEscrowException.java

InputValidator.java

Main.java

Features:

Create freelancer projects.

Associate clients and freelancers with projects.

Fund projects through escrow.

Divide projects into milestones.

Track milestone completion and approval.

Release milestone payments.

Maintain escrow status.

Generate escrow reports.

Basic workflow:

Client creates a freelancer project.

Client deposits the required project amount.

Project funds are held in escrow.

Freelancer completes a milestone.

Client approves the milestone.

The corresponding payment is released.

The escrow status is updated.

7. Multithreading and Concurrency

RUPIX demonstrates concurrent payment execution using Java threads.

Key Components

PaymentTask

Implements the Runnable interface.

Represents an individual payment operation.

Executes payment logic through the shared payment service.

ConcurrencyDemo

Creates multiple payment threads.

Starts concurrent payment operations.

Demonstrates synchronization during wallet balance updates.

Uses join() to wait for the payment threads to complete.

Synchronization

The shared payment service coordinates wallet balance updates to prevent concurrent transactions from incorrectly modifying the same wallet balance.

For example, if two payment threads attempt to debit a wallet containing ₹1,000:

Thread 1 attempts to transfer ₹700.

Thread 2 attempts to transfer ₹500.

Only one transaction can successfully debit the wallet.

The other transaction fails due to insufficient balance.

This demonstrates the importance of synchronization and thread safety in financial applications.

8. File Persistence and Data Management

RUPIX uses Java File I/O to maintain application records between executions.

The shared FileStorage.java class provides common file-handling operations.

FileStorage Responsibilities

Read existing records.

Write updated records.

Append new records.

Support persistence across application restarts.

Core Methods

public List<String> readRecords(String fileName);

public void writeRecords(
String fileName,
List<String> records
);

public void appendRecord(
String fileName,
String record
);

Data Files

The application maintains eight text data files.

Data File

Information Stored

users.txt

User IDs, names and UPI IDs

wallets.txt

Wallet IDs and balances

transactions.txt

Sender, receiver, amount and status

merchants.txt

Merchant information

merchant_payments.txt

Customer merchant payments

settlements.txt

Settlement amounts and statuses

escrow_projects.txt

Project and funding information

milestones.txt

Milestone amounts and statuses

Application Persistence Workflow

When RUPIX starts:

Main.java starts the application.

FileStorage.java reads saved records.

Services load records into Java collections.

The application becomes ready for user operations.

When a transaction succeeds:

The service updates the in-memory objects.

Updated records are saved using FileStorage.

The data remains available after application restart.

Concurrency consideration: Wallet balance updates and their corresponding persistence operations must be coordinated under the same service-level synchronization. Snapshot saving should use a temporary file followed by replacement to reduce the risk of partially written files.

File-based persistence does not provide the same atomicity and recovery guarantees as a database transaction.

9. Java Collections Framework

RUPIX demonstrates multiple Java collection types.

Collection

Application Usage

ArrayList

Maintaining transaction and project records

HashMap

Mapping UPI IDs and wallet IDs to objects

HashSet

Tracking unique identifiers

Queue

Managing pending merchant settlements

These collections support efficient data organisation and retrieval across the three modules.

10. Exception Handling and Validation

The application uses custom exceptions to handle invalid operations.

Exception

Responsibility

InsufficientBalanceException

Handles insufficient wallet funds

InvalidAmountException

Handles invalid payment amounts

InvalidMerchantException

Handles invalid merchant operations

InvalidEscrowException

Handles invalid escrow operations

InputValidator.java provides common input validation functionality.

Exception handling helps prevent invalid operations from silently corrupting application state.

11. Application Workflow

RUPIX integrates three functional modules through a single terminal application.

                 RUPIX
                   |
              Main.java
                   |
        ┌──────────┼──────────┐
        |          |          |
     UPI &      Merchant    Freelancer
     Wallet    Settlement    Escrow
        |          |          |
Payment     Merchant     Milestone
Service     Service       Service
|          |          |
└──────────┼──────────┘
|
FileStorage
|
Text Data Files

All modules share the same persistence layer.

The wallet and payment services remain centrally managed by Member 1 to prevent duplicate balance-management implementations.

12. Team Distribution

RUPIX is developed by a three-member team.

Team Member

Functional Area

Assigned Modules

Member 1

UPI and Wallet

User, Wallet, Transaction, WalletService, PaymentService, Concurrency, PaymentMenu and exceptions

Member 2

Merchant Settlement

Merchant models, services, settlement queue, reports and merchant menu

Member 3

Freelancer Escrow

Escrow models, services, milestone management, validation, reports and Main.java

Shared File Ownership

File

Owner

Responsibility

FileStorage.java

Member 1

Shared file persistence

WalletService.java

Member 1

Central wallet management

PaymentService.java

Member 1

Central money transfer operations

Main.java

Member 3

Application integration

README.md

Member 1 coordinates

Project documentation

.gitignore

Member 1

Excludes generated and IDE files

Only Member 1 should modify the shared wallet and payment services. Only Member 3 should modify Main.java.

13. GitHub Collaboration Workflow

The project follows a three-branch development strategy.

Branch

Owner

Module

main

Shared

Integrated application

feature/payments

Member 1

UPI and Wallet

feature/settlements

Member 2

Merchant Settlement

feature/escrow

Member 3

Freelancer Escrow

Integration Order

Member 1 completes and merges the payment module.

Member 2 updates their branch and merges the settlement module.

Member 3 updates their branch and merges the escrow module.

Member 3 integrates all three menus through Main.java.

All members test the integrated application.

Every member should work only on their assigned files to reduce merge conflicts.

14. Testing and Verification

The application should be tested across all three modules.

UPI and Wallet Testing

User registration.

Unique UPI ID validation.

Wallet creation.

Adding money.

Successful transfers.

Insufficient balance.

Invalid payment amounts.

Concurrent payment execution.

Merchant Settlement Testing

Merchant registration.

Customer merchant payments.

MDR calculation.

Settlement amount calculation.

Pending settlement queue.

Settlement processing.

Freelancer Escrow Testing

Project creation.

Escrow funding.

Milestone creation.

Milestone approval.

Payment release.

Invalid project and milestone operations.

Persistence Testing

Save records successfully.

Restart the application.

Restore previously saved records.

Verify wallet balances and transaction histories.

Check consistency between related data files.

Integration Testing

All three menus accessible through Main.java.

Shared wallet and payment services used across modules.

No duplicate wallet services.

Correct transaction and settlement records.

15. Future Enhancements

Database integration using JDBC and MySQL.

Graphical user interface.

Enhanced transaction history and filtering.

Improved settlement reconciliation.

More advanced transaction recovery.

Authentication and role-based access.

Integration with actual payment gateway APIs in a future production-oriented version.

These enhancements are outside the current project scope.

16. Conclusion

RUPIX demonstrates how multiple financial workflows can be integrated into a single Java application using Object-Oriented Programming and software engineering principles.

By combining UPI-style wallet transfers, merchant settlement processing, freelancer escrow management, multithreading, synchronization, Java Collections, custom exception handling, and file persistence, the project provides a practical demonstration of Core Java concepts.

The modular three-member architecture promotes clear responsibility, shared service management, and collaborative development.

RUPIX — One system. Three payment workflows. Built with Core Java.