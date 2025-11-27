package com.example;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * UserController class that acts as the Controller in the MVVM pattern.
 * This class handles the interaction between the View (FXML) and the ViewModel.
 */
public class UserController implements Initializable {
    
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private Label fullNameLabel;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private ListView<Student> studentListView;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Label userCountLabel;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private DatePicker birthDatePicker;
    
    @FXML
    private ComboBox<String> sortCriteriaCombo;
    
    @FXML
    private CheckBox ascendingCheckBox;
    
    @FXML
    private TextField searchField;

    private UserViewModel viewModel;

    public UserController() {
        this.viewModel = new UserViewModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate sort criteria combobox
        sortCriteriaCombo.setItems(FXCollections.observableArrayList("First Name", "Last Name", "Email", "Birth Date"));
        sortCriteriaCombo.setValue("First Name"); // Set default selection
        
        setupDataBinding();
        setupEventHandlers();
        setupListView();
        setupSearchListener();
    }

    /**
     * Sets up the data binding between View controls and ViewModel properties.
     * This is the key aspect of the MVVM pattern in JavaFX.
     */
    private void setupDataBinding() {
        // Bidirectional binding for text fields
        firstNameField.textProperty().bindBidirectional(viewModel.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(viewModel.lastNameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        birthDatePicker.valueProperty().bindBidirectional(viewModel.birthDateProperty());
        
        // Bind full name label to computed property from ViewModel
        fullNameLabel.textProperty().bind(Bindings.createStringBinding(
            () -> {
                String fullName = viewModel.getFullName();
                return fullName.isEmpty() ? "Full Name: " : "Full Name: " + fullName;
            },
            viewModel.firstNameProperty(),
            viewModel.lastNameProperty()
        ));
        
        // Bind save button enabled state to form validation
        saveButton.disableProperty().bind(Bindings.createBooleanBinding(
            () -> !viewModel.isValidStudent(),
            viewModel.firstNameProperty(),
            viewModel.lastNameProperty()
        ));
        
        // Bind delete button enabled state to list selection
        deleteButton.disableProperty().bind(
            studentListView.getSelectionModel().selectedItemProperty().isNull()
        );
        
        // Bind user count label
        userCountLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "Total Users: " + viewModel.getStudentCount(),
            viewModel.getStudents()
        ));
    }

    /**
     * Sets up event handlers for buttons and other controls.
     */
    private void setupEventHandlers() {
        // Save button handler
        saveButton.setOnAction(event -> handleSaveUser()); // this is command binding "setOnAction"
        
        // Clear button handler
        clearButton.setOnAction(event -> handleClearForm());
        
        // Delete button handler
        deleteButton.setOnAction(event -> handleDeleteUser());
        
        // Sort criteria combobox handler
        sortCriteriaCombo.setOnAction(event -> handleSortChanged());
        
        // Ascending/Descending checkbox handler
        ascendingCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> handleSortChanged());
        
        // Double-click on list item to edit
        studentListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
                if (selectedStudent != null) {
                    handleEditUser(selectedStudent);
                }
            }
        });
    }

    /**
     * Sets up the ListView to display students from the ViewModel.
     */
    private void setupListView() {
        studentListView.setItems(viewModel.getStudents());
        
        // Set custom cell factory to display both name and email
        studentListView.setCellFactory(listView -> new javafx.scene.control.ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    // Display format: "Full Name (email@example.com)"
                    String displayText = student.getFullName();
                    if (student.getEmail() != null && !student.getEmail().isEmpty()) {
                        displayText += " (" + student.getEmail() + ")";
                    }
                    setText(displayText);
                }
            }
        });
        
        // Add listener to update UI when list changes
        viewModel.getStudents().addListener((ListChangeListener<Student>) change -> {
            // This will trigger the binding update for student count
        });
    }

    /**
     * Handles the Save User button action.
     * This method is referenced in the FXML file.
     */
    @FXML
    private void handleSaveUser() {
        if (viewModel.isValidStudent()) {
            viewModel.addStudent();
            System.out.println("Student Saved: " + viewModel.getFullName());
            
            // Optional: Show success message or feedback to user
            showSuccessMessage("Student saved successfully!");
        }
    }

    /**
     * Handles the Clear Form button action.
     */
    @FXML
    private void handleClearForm() {
        viewModel.clearForm();
        studentListView.getSelectionModel().clearSelection();
        System.out.println("Form cleared");
    }

    /**
     * Handles the Delete User button action.
     */
    @FXML
    private void handleDeleteUser() {
        Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            viewModel.removeStudent(selectedStudent);
            System.out.println("Student deleted: " + selectedStudent.getFullName());
            showSuccessMessage("Student deleted successfully!");
        }
    }

    /**
     * Handles editing a student by populating the form with their data.
     */
    private void handleEditUser(Student student) {
        if (student != null) {
            viewModel.setFirstName(student.getFirstName());
            viewModel.setLastName(student.getLastName());
            viewModel.setEmail(student.getEmail());
            viewModel.setBirthDate(student.getBirthDate());
            
            // Remove the student from the list so it can be re-added when saved
            viewModel.removeStudent(student);
            
            System.out.println("Editing student: " + student.getFullName());
        }
    }

    /**
     * Shows a success message (in a real application, this might show a toast or status bar message).
     */
    private void showSuccessMessage(String message) {
        // In a real application, you might show this in a status bar, toast, or dialog
        System.out.println("Success: " + message);
    }
    
    /**
     * Handles sorting when criteria or order changes.
     */
    private void handleSortChanged() {
        String sortCriteria = sortCriteriaCombo.getValue();
        boolean isAscending = ascendingCheckBox.isSelected();
        
        if (sortCriteria != null) {
            viewModel.sortStudents(sortCriteria, isAscending);
            System.out.println("Sorted by: " + sortCriteria + ", Ascending: " + isAscending);
        }
    }

    /**
     * Sets up the search field listener to filter students by search query.
     */
    private void setupSearchListener() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            handleSearchChanged(newVal);
        });
    }

    /**
     * Handles search input changes and applies filter to student list.
     */
    private void handleSearchChanged(String query) {
        viewModel.searchStudents(query);
        System.out.println("Search query: " + (query.isEmpty() ? "cleared" : query));
    }

    /**
     * Gets the ViewModel instance (useful for testing or external access).
     */
    public UserViewModel getViewModel() {
        return viewModel;
    }

    /**
     * Sets a custom ViewModel (useful for testing or dependency injection).
     */
    public void setViewModel(UserViewModel viewModel) {
        this.viewModel = viewModel;
    }
}