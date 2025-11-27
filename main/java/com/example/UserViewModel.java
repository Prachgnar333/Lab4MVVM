package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.time.LocalDate;
import java.util.Comparator;

/**
 * UserViewModel class that acts as the ViewModel in the MVVM pattern.
 * This class exposes properties that the View can bind to and handles
 * the presentation logic.
 */
public class UserViewModel {
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> birthDate = new SimpleObjectProperty<>(null);
    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredStudents;

    public UserViewModel() {
        // Initialize with empty values
        this.filteredStudents = new FilteredList<>(students, p -> true); // Initially show all
    }

    // Property accessors for data binding
    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public ObjectProperty<LocalDate> birthDateProperty() {
        return birthDate;
    }

    public ObservableList<Student> getStudents() {
        return filteredStudents;
    }

    public ObservableList<Student> getAllStudents() {
        return students;
    }

    // Getter and setter methods for convenience
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public LocalDate getBirthDate() {
        return birthDate.get();
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate.set(birthDate);
    }

    /**
     * Computed property that returns the full name by combining first and last name.
     * This is called whenever the first or last name changes.
     */
    public String getFullName() {
        String first = firstName.get();
        String last = lastName.get();
        
        if (first == null) first = "";
        if (last == null) last = "";
        
        return (first + " " + last).trim();
    }

    /**
     * Adds a new student to the collection based on current first and last name values.
     */
    public void addStudent() {
        if (isValidStudent()) {
            Student newStudent = new Student(getFirstName(), getLastName(), getEmail(), getBirthDate());
            students.add(newStudent);
            clearForm();
        }
    }

    /**
     * Removes a student from the collection.
     */
    public void removeStudent(Student student) {
        if (student != null) {
            students.remove(student);
        }
    }

    /**
     * Clears the form by resetting first and last name properties.
     */
    public void clearForm() {
        setFirstName("");
        setLastName("");
        setEmail("");
        setBirthDate(null);
    }

    /**
     * Validates if the current first and last name form a valid student.
     */
    public boolean isValidStudent() {
        String first = getFirstName();
        String last = getLastName();
        return first != null && !first.trim().isEmpty() && 
               last != null && !last.trim().isEmpty();
    }

    /**
     * Gets the count of students in the collection.
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Sorts the students list based on the selected criteria and order.
     * 
     * @param criteria The sorting criteria: "First Name", "Last Name", "Email", or "Birth Date"
     * @param isAscending True for ascending order, false for descending
     */
    public void sortStudents(String criteria, boolean isAscending) {
        Comparator<Student> comparator = null;
        
        switch (criteria) {
            case "First Name":
                comparator = Comparator.comparing(s -> s.getFirstName() != null ? s.getFirstName() : "");
                break;
            case "Last Name":
                comparator = Comparator.comparing(s -> s.getLastName() != null ? s.getLastName() : "");
                break;
            case "Email":
                comparator = Comparator.comparing(s -> s.getEmail() != null ? s.getEmail() : "");
                break;
            case "Birth Date":
                comparator = Comparator.comparing(s -> s.getBirthDate() != null ? s.getBirthDate() : LocalDate.MIN);
                break;
            default:
                return; // No sorting if criteria is invalid
        }
        
        // Apply descending order if needed
        if (!isAscending) {
            comparator = comparator.reversed();
        }
        
        // Sort the observable list
        FXCollections.sort(students, comparator);
    }

    /**
     * Filters the students list based on a search query.
     * Performs case-insensitive matching on firstName, lastName, and email.
     * 
     * @param query The search query string (empty string shows all students)
     */
    public void searchStudents(String query) {
        if (query == null || query.isEmpty()) {
            // Show all students
            filteredStudents.setPredicate(p -> true);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredStudents.setPredicate(student -> 
                student.getFirstName().toLowerCase().contains(lowerCaseQuery) ||
                student.getLastName().toLowerCase().contains(lowerCaseQuery) ||
                (student.getEmail() != null && student.getEmail().toLowerCase().contains(lowerCaseQuery))
            );
        }
    }
}