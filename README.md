# Student Management System (MVVM) — Lab4MVVM

This repository contains a JavaFX-based Student Management System implemented using the MVVM pattern. The application has been extended with search and enhanced sorting and list display.

**Project location:** `D:/MvvmJavaFXExample` (branch: `Lab4MVVM`)

**Key changes / features**
- Search: live filtering by first name, last name, or email (case-insensitive).
- Sorting: new sort criteria `First Name`, `Last Name`, `Email`, and `Birth Date` with ascending/descending toggle.
- Users list view now displays the student's email alongside their full name: `Full Name (email@example.com)`.

**Files changed**
- `src/main/java/com/example/UserViewModel.java` — added `FilteredList` and `searchStudents()`.
- `src/main/java/com/example/UserController.java` — wired search field, added search listener, and custom ListView cell factory to show email.
- `src/main/java/com/example/Student.java` — model includes `email` and `birthDate`.
- `src/main/resources/user_view.fxml` — contains the UI (search field added earlier).

Prerequisites
- Java 11 (or compatible JDK used for compilation)
- Maven 3.6+ (3.9.x recommended)
- JavaFX libraries (the project uses OpenJFX 17 in the pom; Maven will resolve them)

Quick start (Windows PowerShell)

Open PowerShell and run:

```powershell
# Change to project directory
cd D:\MvvmJavaFXExample\src

# Compile
mvn clean compile

# Run the JavaFX application
mvn javafx:run
```

Notes and tips
- The UI includes a search field in the "Users List" header — type a query to filter names/emails immediately.
- Use the "Sort by" ComboBox to choose `First Name`, `Last Name`, `Email`, or `Birth Date`. Toggle the "Ascending" checkbox to reverse order.
- To add students, use the form on the left and click `Save User`.
- The ListView shows entries like: `Alice Smith (alice@example.com)`.
- If you want to build a distributable JAR, additional packaging steps are needed for JavaFX runtime modules (see OpenJFX packaging guides). Running with `mvn javafx:run` is the simplest approach during development.

Troubleshooting
- If JavaFX native libraries aren't found at runtime, ensure the JDK and JavaFX versions are compatible and that Maven downloaded the correct classifier for your OS. Running with `mvn javafx:run` typically supplies necessary runtime options configured in the `pom.xml`.
- If compilation issues occur, run Maven with `-X` for verbose output: `mvn -X clean compile`.

Contact / Next steps
- If you'd like, I can also pre-populate the app with your group members' names (and emails/birthdates) at startup, or create a script to import a CSV of students — tell me which you prefer and provide the data.

---
Generated: 2025-11-27
