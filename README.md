# 📊 Enterprise Contract Compliance and Risk Analytics System using PostgreSQL and JDBC  
### *(SQL Use Case with PostgreSQL and JDBC Integration)*

> **Database design, implementation, and testing were carried out using `pgAdmin4`, ensuring a structured, optimized, and scalable SQL workflow.**

This project is a **SQL-driven enterprise analytics system** integrated with **Java (JDBC)**, developed as part of the **Virtusa Pre-Onboarding Training Assignment**.

The system focuses on **contract compliance, vendor risk evaluation, SLA monitoring, and financial risk analytics**, using advanced PostgreSQL features such as **Triggers, Stored Procedures, Views, and Indexing**.

---

## 📌 Project Overview

The application simulates a real-world **enterprise contract governance system**, enabling organizations to:

- Monitor contract lifecycle and compliance  
- Evaluate vendor performance and associated risks  
- Detect SLA violations and delays automatically  
- Analyze financial exposure and payment risks  
- Maintain audit logs for contract updates  

It demonstrates how **SQL can be used as a powerful backend engine for analytics and business logic**, with **JDBC enabling real-time interaction through Java**.

---

## ✨ Key Features

### 📑 Contract Management

- Maintain structured contract records with constraints  
- Track contract lifecycle (ACTIVE, SUSPENDED, EXPIRED, TERMINATED)  
- Department-wise contract allocation and ownership  
- Enforced data integrity using **foreign keys and constraints**  

---

### 🏢 Vendor Risk Analysis

- Compute **average risk score per vendor**
- Identify **top high-risk vendors** using ranking queries  
- Evaluate vendor reliability based on contract performance  
- Enable data-driven vendor decision-making  

---

### ⏳ Contract Compliance Monitoring

- Detect **delayed obligations (SLA violations)**  
- Track contracts nearing expiry (within 30 days)  
- Monitor obligation completion status  
- Ensure proactive compliance management  

---

### 💰 Financial Risk Insights

- Analyze **total contract value by status**  
- Track **pending and late payments**  
- Identify contracts with high financial exposure  
- Support financial planning and risk mitigation  

---

### 🚨 Automated Risk Detection (Triggers)

Implemented using **PostgreSQL Triggers (PL/pgSQL)**:

- Overdue obligations → **HIGH Risk Alerts**  
- Late payments → **MEDIUM Risk Alerts**  
- Automatic insertion into **risk_alerts table**  
- Real-time monitoring without manual intervention  

---

### 🧾 Audit & Logging System

- Tracks contract status changes automatically  
- Maintains **audit trail using triggers**  
- Ensures transparency and accountability  
- Useful for compliance and debugging  

---

### ⚙️ Risk Scoring Engine (Stored Procedure)

- Centralized risk calculation using **PL/pgSQL procedure**  
- Factors considered:
  - Delayed obligations  
  - Late payments  
  - Contract value  

- Risk categorization:
  - **HIGH (> 60)**  
  - **MEDIUM (> 25)**  
  - **LOW**  

- Uses **UPSERT (ON CONFLICT)** for efficient updates  

---

### 📊 Advanced SQL Analytics

- Complex queries using:
  - `JOIN`, `GROUP BY`, `ORDER BY`  
  - Aggregations (`SUM`, `COUNT`, `AVG`)  
- View (`high_risk_contracts`) for simplified reporting  
- Optimized query performance using **indexes**  

---

## 🛠️ Technology Stack

- **Database:** PostgreSQL  
- **Backend:** Java (JDBC)  
- **Tools:** pgAdmin4, IntelliJ IDEA  

### Concepts Applied:
- Advanced SQL Queries  
- Indexing & Optimization  
- Triggers (PL/pgSQL)  
- Stored Procedures  
- Views  
- JDBC Connectivity  
- Exception Handling  

---

## ⚙️ Database Setup

👉 All database operations were performed using `pgAdmin4`.


## 1. Create Database
```sql
CREATE DATABASE Contract_system;
````

## 2. Execute SQL Script

The script performs:

* Table creation with relationships
* Constraint enforcement
* Index creation for optimization
* Trigger-based automation
* Stored procedure for risk scoring
* Analytical view creation
* Sample data insertion

---

# 🔗 Database Configuration

Update credentials in `DBConnection.java`:

```java
private static final String USER_NAME = "postgres_username";
private static final String PASSWORD = "postgres_password";
```

---

# 🚀 Execution Steps

1. Open project in **IntelliJ IDEA**
2. Add PostgreSQL JDBC Driver (`postgresql-42.x.x.jar`)
3. Configure database credentials
4. Run `Main.java`

---

# 📖 Application Workflow

1. Launch system
2. Choose operation from menu:

   * High-Risk Contract Analysis
   * Vendor Performance Analysis
   * Contract Expiry Monitoring
   * SLA Violation Analysis
   * Payment Risk Assessment
   * Risk Distribution Analytics
   * High-Risk Vendor Identification
   * Delay Analysis
   * Financial Insights
   * Risk Score Calculation
3. System executes SQL queries dynamically
4. Results displayed in console

---

# 🌟 Highlights of the Project

* Strong use of SQL as business logic layer
* Real-time analytics using Java + JDBC
* Automated risk detection using Triggers
* Efficient schema design with normalization
* Optimized queries using indexes
* Enterprise-level database architecture

---

# 🏆 Learning Outcomes

* Advanced PostgreSQL development skills
* Understanding enterprise risk systems
* Writing triggers & stored procedures
* JDBC integration with Java
* Building scalable SQL-based applications

---

# 📑 Submission

* **Prepared by:** Kamani Shivani
* **Project Title:** Enterprise Contract Compliance and Risk Analytics System using PostgreSQL and JDBC
* **Type:** SQL Use Case with PostgreSQL and JDBC Integration
* **Submitted for:** Virtusa Pre-Onboarding Training Assignment

```

