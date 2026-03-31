# 📊 Ledger System

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html)
[![Spring Boot 3.5.11](https://img.shields.io/badge/Spring_Boot-3.5.11-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A robust, enterprise-grade Ledger System built with **Java 25** and **Spring Boot 3.5.11**. This application manages accounts, tracks financial transactions with strong consistency, and ensures data integrity through advanced concurrency control and idempotency mechanisms.

---

## 🚀 Key Features

- **Double-Entry Bookkeeping**: Core support for tracking funds across multiple accounts.
- **Transaction Idempotency**: Uses a unique `request_key` for every transaction to prevent duplicate processing.
- **Optimistic Locking**: Implements JPA `@Version` to handle concurrent account updates safely.
- **DTO-Entity Separation**: Clear boundary between external API models and internal domain database models.
- **Dual Database Support**: PostgreSQL for persistent storage and H2 for fast, isolated integration testing.

---

## 🏗️ High-Level Design (HLD)

The system follows a standard N-tier architecture, ensuring scalability and maintainability.

```mermaid
graph TD
    User((User/Client)) -->|REST API| Controller[Web Controllers]
    Controller -->|DTOs| Service[Service Layer]
    Service -->|Entities| Repository[Repository Layer]
    Repository -->|JPA/Hibernate| DB[(PostgreSQL)]
    
    subgraph "Core Business Logic"
    Service
    end
    
    subgraph "Persistence"
    Repository
    DB
    end

    style Service fill:#f9f,stroke:#333,stroke-width:2px
    style DB fill:#00f5,stroke:#000,stroke-width:2px
```

### Component Flow
1. **API Layer**: Handles HTTP requests, validates input, and maps requests to DTOs.
2. **Service Layer**: Contains business rules, manages transaction boundaries, and coordinates repository calls.
3. **Repository Layer**: Abstracts database interactions using Spring Data JPA.
4. **Database**: Persistent storage of financial records with transactional ACID guarantees.

---

## 📐 Low-Level Design (LLD)

### Database Schema (ERD)

```mermaid
erDiagram
    ACCOUNT ||--o{ TRANSACTION : "from_account"
    ACCOUNT ||--o{ TRANSACTION : "to_account"

    ACCOUNT {
        long id PK
        string name
        string account_number UK
        decimal balance
        long version
    }

    TRANSACTION {
        long id PK
        long from_account_id FK
        long to_account_id FK
        decimal amount
        datetime timestamp
        string request_key UK
    }
```

### Core Class Diagram

```mermaid
classDiagram
    class Account {
        +Long id
        +String name
        +String accountNumber
        +BigDecimal balance
        +Long version
    }

    class Transaction {
        +Long id
        +Long fromAccountId
        +Long toAccountId
        +BigDecimal amount
        +LocalDateTime timestamp
        +String requestKey
    }

    class AccountService {
        <<interface>>
        +createAccount(AccountDTO)
        +getAccountById(Long)
        +getAllAccounts()
    }

    class TransactionService {
        <<interface>>
        +processTransaction(TransactionDTO)
        +getTransactionHistory(Long)
    }

    AccountService ..> AccountRepository
    TransactionService ..> TransactionRepository
    TransactionService ..> AccountService : "Update balances"
```

---

## 🛠️ Tech Stack

- **Runtime**: Java 25 (Latest LTS ready)
- **Framework**: Spring Boot 3.5.11
- **Data Access**: Spring Data JPA / Hibernate
- **Database**: 
  - **Dev/Prod**: PostgreSQL
  - **Test**: H2 (In-memory)
- **Tooling**:
  - **Build**: Maven
  - **Boilerplate**: Lombok
  - **Validation**: Jakarta Validation API

---

## 🚦 Getting Started

### Prerequisites
- JDK 25+
- Maven 3.9+
- PostgreSQL (Local or Docker)

### Installation
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/niranjantanpure/ledger-system.git
    cd ledger-system
    ```

2.  **Configure Database**:
    Set the following environment variables or update `src/main/resources/application.yml`:
    - `DB_URL`: `jdbc:postgresql://localhost:5432/ledger`
    - `DB_USERNAME`: `your_user`
    - `DB_PASSWORD`: `your_password`

3.  **Run Application**:
    ```bash
    mvn spring-boot:run
    ```

---

## 🧪 Testing

The project uses a dedicated test profile with an H2 in-memory database to ensure isolated and repeatable tests.

```bash
mvn test
```

---

## 📜 License

This project is licensed under the MIT License - see the `LICENSE` file for details.
