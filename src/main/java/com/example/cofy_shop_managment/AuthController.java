package com.example.cofy_shop_managment;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class AuthController {

    @FXML private VBox loginPane, registerPane, forgotPasswordPane;
    @FXML private TextField loginUsername, regUsername, regAnswer, forgotUsername, forgotAnswer;
    @FXML private PasswordField loginPassword, regPassword, newPassword, confirmPassword;
    @FXML private ComboBox<String> regQuestion, forgotQuestion;
    @FXML private StackPane formPane;
    @FXML private Button toggleBtn, changePasswordBtn;

    // Remove the duplicate @FXML annotation and fix the button reference
    @FXML private Button btnLogin; // Added missing button reference

    private final File userFile = new File("src/main/data/users.txt");

    @FXML
    public void initialize() {
        // --- Initialize Security Questions ---
        List<String> securityQuestions = Arrays.asList(
                "What is your pet's name?",
                "Your favorite color?",
                "Your birthplace?"
        );
        regQuestion.getItems().addAll(securityQuestions);
        forgotQuestion.getItems().addAll(securityQuestions);

        // --- Ensure User Data File Exists ---
        try {
            if (!userFile.exists()) {
                if (userFile.getParentFile() != null) {
                    userFile.getParentFile().mkdirs();
                }
                userFile.createNewFile();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Cannot create user data file: " + e.getMessage());
        }

        // --- Set Initial View State ---
        loginPane.setVisible(true);
        registerPane.setVisible(false);
        forgotPasswordPane.setVisible(false);
        toggleBtn.setText("Create Account");

        // --- Hide Password Change Fields Initially ---
        newPassword.setVisible(false);
        confirmPassword.setVisible(false);
        changePasswordBtn.setVisible(false);

        // --- Set up Login Button Action ---
        if (btnLogin != null) {
            btnLogin.setOnAction(e -> handleLogin());
        }
    }

    @FXML
    private void switchForm() {
        if (loginPane.isVisible()) {
            loginPane.setVisible(false);
            registerPane.setVisible(true);
            forgotPasswordPane.setVisible(false);
            toggleBtn.setText("Already have an Account");
        } else {
            loginPane.setVisible(true);
            registerPane.setVisible(false);
            forgotPasswordPane.setVisible(false);
            toggleBtn.setText("Create Account");
        }

        // Reset password change fields
        newPassword.setVisible(false);
        confirmPassword.setVisible(false);
        changePasswordBtn.setVisible(false);
    }

    @FXML
    private void showForgotPassword() {
        loginPane.setVisible(false);
        registerPane.setVisible(false);
        forgotPasswordPane.setVisible(true);
        toggleBtn.setText("Back to Login");

        // Clear fields
        forgotUsername.clear();
        forgotQuestion.setValue(null);
        forgotAnswer.clear();
        newPassword.clear();
        confirmPassword.clear();

        // Hide password change fields initially
        newPassword.setVisible(false);
        confirmPassword.setVisible(false);
        changePasswordBtn.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please enter both username and password.");
            return;
        }

        try (Scanner scanner = new Scanner(userFile)) {
            boolean found = false;
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    loadDashboard();
                    found = true;
                    break;
                }
            }

            if (!found) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot read user data: " + e.getMessage());
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginPane.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("Cafe Shop Management System - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        String username = regUsername.getText().trim();
        String password = regPassword.getText().trim();
        String question = regQuestion.getValue();
        String answer = regAnswer.getText().trim();

        if (username.isEmpty() || password.isEmpty() || question == null || answer.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill all fields.");
            return;
        }

        // Check if username already exists
        try (Scanner scanner = new Scanner(userFile)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists!");
                    return;
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot read user data: " + e.getMessage());
            return;
        }

        // Save user data
        try (FileWriter writer = new FileWriter(userFile, true)) {
            writer.write(username + "," + password + "," + question + "," + answer + "\n");
            showAlert(Alert.AlertType.INFORMATION, "Account Created", "You can now login.");

            // Clear registration fields
            regUsername.clear();
            regPassword.clear();
            regQuestion.setValue(null);
            regAnswer.clear();

            switchForm();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot save user data: " + e.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword() {
        String username = forgotUsername.getText().trim();
        String question = forgotQuestion.getValue();
        String answer = forgotAnswer.getText().trim();

        if (username.isEmpty() || question == null || answer.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill all fields.");
            return;
        }

        // Verify security question and answer
        try (Scanner scanner = new Scanner(userFile)) {
            boolean found = false;
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 4 && parts[0].equals(username) &&
                        parts[2].equals(question) && parts[3].equals(answer)) {
                    // Security question answered correctly - show password change fields
                    newPassword.setVisible(true);
                    confirmPassword.setVisible(true);
                    changePasswordBtn.setVisible(true);
                    found = true;
                    break;
                }
            }

            if (!found) {
                showAlert(Alert.AlertType.ERROR, "Verification Failed", "Invalid username, question, or answer!");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot read user data: " + e.getMessage());
        }
    }

    @FXML
    private void handlePasswordChange() {
        String username = forgotUsername.getText().trim();
        String newPass = newPassword.getText().trim();
        String confirmPass = confirmPassword.getText().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please enter and confirm your new password.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "New password and confirmation do not match!");
            return;
        }

        // Update password in the file
        try {
            List<String> userLines = new ArrayList<>();
            boolean userFound = false;

            // Read all users
            try (Scanner scanner = new Scanner(userFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[0].equals(username)) {
                        // Update password for this user
                        line = username + "," + newPass + "," + parts[2] + "," + parts[3];
                        userFound = true;
                    }
                    userLines.add(line);
                }
            }

            if (!userFound) {
                showAlert(Alert.AlertType.ERROR, "Error", "User not found!");
                return;
            }

            // Write all users back to file
            try (FileWriter writer = new FileWriter(userFile)) {
                for (String line : userLines) {
                    writer.write(line + "\n");
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");

            // Clear all fields and go back to login
            forgotUsername.clear();
            forgotQuestion.setValue(null);
            forgotAnswer.clear();
            newPassword.clear();
            confirmPassword.clear();
            newPassword.setVisible(false);
            confirmPassword.setVisible(false);
            changePasswordBtn.setVisible(false);

            switchForm(); // Go back to login

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot update password: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}