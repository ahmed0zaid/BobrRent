package com.bobrrent.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDate;
import java.time.Period;

public class SignupController implements Initializable {

    @FXML private TextField first_name;
    @FXML private TextField last_name;
    @FXML private TextField phone;
    @FXML private PasswordField password;
    @FXML private ChoiceBox<String> genderChoiceBox;
    @FXML private DatePicker birthDatePicker;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        genderChoiceBox.setItems(FXCollections.observableArrayList("Male", "Female"));
    }
    @FXML
    public void handleSignup(ActionEvent event) {

        if (phone.getText().isEmpty() || password.getText().isEmpty() ||
                first_name.getText().isEmpty() || last_name.getText().isEmpty() ||
                genderChoiceBox.getValue() == null || birthDatePicker.getValue() == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("SORRY, YOU CAN'T LEAVE EMPTY FIELDS");
            alert.showAndWait();
            System.out.println("SORRY, YOU CAN'T LEAVE EMPTY FIELDS");
            return;
        }

        //String bday = birthDatePicker.getValue().toString();
        LocalDate birthday = birthDatePicker.getValue();
        LocalDate today = LocalDate.now();
        int age = Period.between(birthday, today).getYears();

        if (age < 18) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Registration Restricted");
            alert.setHeaderText("Age Requirement Not Met");
            alert.setContentText("You must be at least 18 years old to use BóbrRent. Your current age is: " + age);

            alert.showAndWait();
            System.out.println("Warning: Age Requirement Not Met at least 18 years old");
            return;
        }


        String message = "SIGNUP," + phone.getText() + "," + password.getText() + "," +
                first_name.getText() + "," + last_name.getText() + "," +
                genderChoiceBox.getValue() + "," + birthday;

        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);
            String response = in.readLine();

            if ("SUCCESS".equals(response)) {
                System.out.println("Account created!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Account Created");
                alert.setContentText("You have been successfully created!");
                alert.showAndWait();
                goToLoginPage(event);
            }
        } catch (IOException e) {
            System.out.println("Server Error: Registration failed.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Server Error");
            alert.setContentText("Registration failed");
            alert.showAndWait();
        }
    }
    @FXML
    public void gotosignup(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
    private void goToLoginPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("BóbrRent - Login");
        stage.show();
    }
}