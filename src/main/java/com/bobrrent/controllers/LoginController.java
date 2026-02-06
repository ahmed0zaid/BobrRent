package com.bobrrent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import com.bobrrent.utils.UserSession;

public class LoginController {

    @FXML private TextField phone;
    @FXML private PasswordField password;

    @FXML
    public void handlelogin(ActionEvent event) {
        String message = "LOGIN," + phone.getText() + "," + password.getText();

        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);
            String response = in.readLine();

            if ("SUCCESS".equals(response)) {
                UserSession.setPhone(phone.getText());
                gotohome(event);
            } else {
                System.out.println("Login Failed.");
            }
        } catch (IOException e) {
            System.out.println("Server Error");
        }
    }

    @FXML
    public void gotohome(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/home.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    @FXML
    public void gotosignup(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/signup.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}