# Library Management System

## Author

**Derick Burjack Cardoso**
Student ID: **6300410**


## Overview

This project is a **Library Management System** developed in Java using Object-Oriented Programming principles. It allows users to borrow, return, and search for library items such as books, DVDs, and magazines, while enforcing borrowing rules and maintaining persistent storage using CSV files.

---

## Features

### User Management

* Three user types: **Student, Teacher, Admin**
* Each user has a unique ID and a list of borrowed items
* Borrowing limits:

  * Student: 5 books only
  * Teacher: 10 items
  * Admin: reporting and backup access

### Item Management

* Three item types:

  * Book (ISBN, author, genre)
  * DVD (director, duration)
  * Magazine (issue number, publisher)
* Each item has:

  * Unique ID
  * Title
  * Status (IN_STORE, BORROWED, LOST)

### Core Functionality

* Borrow items (with validation rules)
* Return items
* Search items by title or creator
* Admin reporting (group items by status)
* Data persistence using CSV files

---

## Project Structure

```
src/main/java/org/derick
 ┣ domain
 ┃ ┣ User.java
 ┃ ┣ Student.java
 ┃ ┣ Teacher.java
 ┃ ┣ Admin.java
 ┃ ┣ Item.java
 ┃ ┣ Book.java
 ┃ ┣ DVD.java
 ┃ ┣ Magazine.java
 ┃ ┣ Library.java
 ┣ interfaces
 ┃ ┣ Reportable.java
 ┣ util
 ┃ ┣ Constants.java
 ┣ resources
 ┃ ┣ ClassDiagram.png
 ┃ ┣ items.csv
 ┃ ┣ Report.pdf
 ┃ ┣ UserGuide.pdf
 ┃ ┣ users.csv
```

---

## How to Run

### 1. Clone the repository

```bash
git clone <https://github.com/slimetime3021/LibrarySystem.git>
```

### 2. Open the project

Open the project in IntelliJ IDEA or any Java IDE.

### 3. Build the project

If using Maven:

```bash
mvn clean install
```

### 4. Run the application

Run the `Main` class (if available in your project).

---

## Data Storage

The system uses CSV files for persistence:

* `items.csv` → stores all library items
* `users.csv` → stores all users and borrowed items

These files are loaded at startup and updated through backup functions.

---

## Key Classes

### User (Abstract)

Base class for all users. Handles:

* ID generation
* Borrowed items list
* Borrowing limits

### Item (Abstract)

Base class for all library items. Handles:

* ID system
* Title and status
* Global item registry

### Library

Core service class responsible for:

* Borrowing logic
* Returning items
* Searching items
* Loading and saving CSV data

### Admin

Special user that can:

* Generate reports
* Backup data

---

## Design Principles

* Inheritance (User → Student/Teacher/Admin, Item → Book/DVD/Magazine)
* Polymorphism (save() method overrides)
* Encapsulation (private/protected fields)
* Interfaces (Reportable for admin features)

---

## Limitations

* No database integration (CSV only)
* No authentication system