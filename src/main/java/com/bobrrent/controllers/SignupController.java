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

public class SignupController implements Initializable {

    @FXML private TextField first_name;
    @FXML private TextField last_name;
    @FXML private TextField phone;
    @FXML private PasswordField password;
    @FXML private ChoiceBox<String> genderChoiceBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        genderChoiceBox.setItems(FXCollections.observableArrayList("Male", "Female"));
    }
    @FXML
    public void handleSignup(ActionEvent event) {

        if (phone.getText().isEmpty() || password.getText().isEmpty() ||
                first_name.getText().isEmpty() || last_name.getText().isEmpty() ||
                genderChoiceBox.getValue() == null) {
            System.out.println("Error: SORRY YOU CAN'T LEAVE EMPTY FIELDS");
                return;
        }
        String message = "SIGNUP," + phone.getText() + "," + password.getText() + "," +
                first_name.getText() + "," + last_name.getText() + "," +
                genderChoiceBox.getValue();

        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);
            String response = in.readLine();

            if ("SUCCESS".equals(response)) {
                System.out.println("Account created!");
                goToLoginPage(event);
            }
        } catch (IOException e) {
            System.out.println("Server Error: Registration failed.");
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