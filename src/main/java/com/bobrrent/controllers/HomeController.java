package com.bobrrent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {
    @FXML private VBox mainContent; // Matches fx:id in home.fxml

    @FXML
    public void handleBrowseCars(ActionEvent event) {
        try {
            // Loads your new catalog design
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/browsecar.fxml"));
            Node node = loader.load();
            mainContent.getChildren().setAll(node); // Swaps center content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDashboard(ActionEvent event) {
        try {
            // Reloads the original marketing home screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/home.fxml"));
            mainContent.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleMyBookings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/bookings.fxml"));
            mainContent.getChildren().setAll((javafx.scene.Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/profile.fxml"));
            Node node = loader.load();
            mainContent.getChildren().setAll(node); // Injects profile into the center area
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }
}