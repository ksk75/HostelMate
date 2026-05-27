# HostelMate — Hostel Expense Management System

A complete, production-ready Java Web Application (Servlets & JSP) designed for college hostel residents to manage their shared expenses, track monthly rent, and settle balances easily.

## Features

- **Authentication:** Secure login and registration with BCrypt password hashing.
- **Role-Based Access Control (RBAC):** Distinct dashboards and capabilities for `STUDENT` and `ADMIN` roles.
- **Expense Management:** Add, edit, and delete personal or shared expenses. Support for equal and custom splitting.
- **Balance Settlement:** Automatic calculation of "who owes whom" and a simplified settlement workflow.
- **Dashboard & Analytics:** Interactive charts (Chart.js) for monthly trends and category breakdowns.
- **Notifications:** Real-time alert system for new expenses and pending settlements.
- **Admin Module:** Complete management of users, rooms, and system-wide reports.

## Technology Stack

- **Backend:** Java 11, Servlets, JSP, JDBC
- **Frontend:** HTML5, CSS3, JavaScript (Vanilla), Bootstrap 5, Chart.js, Bootstrap Icons
- **Database:** MySQL 8.x
- **Build Tool:** Maven

## Project Setup Instructions

### 1. Database Configuration
1. Install and start MySQL Server.
2. Open your MySQL client and execute the SQL scripts found in the `database` folder:
   - Run `database/schema.sql` to create the `hostelmate_db` database and all necessary tables.
   - Run `database/sample_data.sql` to populate the database with initial categories, rooms, an admin user, and some sample student data.
3. Update database credentials if necessary in `src/main/java/com/hostelmate/util/DBConnection.java`.

### 2. Build the Project
1. Ensure you have Maven installed.
2. Open a terminal in the project root directory (where `pom.xml` is located).
3. Run the following command to build the project and download dependencies:
   ```bash
   mvn clean install
   ```

### 3. Deploy to Tomcat
1. Ensure you have Apache Tomcat (version 9.0+) installed.
2. Copy the generated `target/hostelmate.war` file to the `webapps` directory of your Tomcat installation.
3. Start Tomcat.
4. Access the application in your browser at:
   ```
   http://localhost:8080/hostelmate
   ```

## Default Accounts (from sample data)
- **Admin:** admin@hostelmate.com / password123
- **Student 1:** rahul@hostelmate.com / password123
- **Student 2:** priya@hostelmate.com / password123

## Screenshots & UI
The application features a modern, premium design with Dark Mode support, glassmorphism elements, and fully responsive layouts optimized for both desktop and mobile devices.

---
*Developed for MCA Final-Year Mini Project.*
