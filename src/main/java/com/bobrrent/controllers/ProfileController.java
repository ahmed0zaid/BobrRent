package com.bobrrent.controllers;

import com.bobrrent.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.*;
import java.net.Socket;

public class ProfileController {

    @FXML private Label fullNameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label genderLabel;
    @FXML private Label birthdayLabel;

    @FXML
    public void initialize() {
        loadUserProfile();
    }

    private void loadUserProfile() {
        String phone = UserSession.getPhone();
        phoneLabel.setText(phone);

        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {


            out.println("GET_PROFILE," + phone);
            String response = in.readLine();

            if (response != null && !response.equals("ERROR")) {

                String[] data = response.split(",");
                if (data.length >= 4) {
                    fullNameLabel.setText(data[0] + " " + data[1]);
                    genderLabel.setText(data[2]);
                    birthdayLabel.setText(data[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditProfile() {

        System.out.println("Edit profile clicked");
    }
}