package com.bobrrent.controllers;

import com.bobrrent.utils.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.*;
import java.net.Socket;

public class MyBookingsController {
    @FXML private VBox bookingsContainer;

    @FXML
    public void initialize() {
        loadUserBookings();
    }

    private void loadUserBookings() {
        bookingsContainer.getChildren().clear();
        String userPhone = UserSession.getPhone();
        if (userPhone == null) return;

        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_MY_BOOKINGS," + userPhone);
            String response = in.readLine();

            if (response != null && !response.isEmpty()) {
                String[] bookings = response.split(";");
                for (String b : bookings) {
                    String[] data = b.split(",");
                    if (data.length >= 4) {
                        addBookingCard(data[0], data[1], data[2], data[3]);
                    }
                }
            } else {
                Label noBookings = new Label("No rental records found in the system.");
                noBookings.setStyle("-fx-font-size: 16; -fx-text-fill: gray;");
                bookingsContainer.getChildren().add(noBookings);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void addBookingCard(String carName, String startDate, String endDate, String totalPrice) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new javafx.geometry.Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);");

        VBox details = new VBox(5);
        Label nameLbl = new Label("🚗 " + carName);
        nameLbl.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label dateLbl = new Label("Rental Period: " + startDate + " to " + endDate);
        details.getChildren().addAll(nameLbl, dateLbl);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label priceValue = new Label(totalPrice + " €");
        priceValue.setFont(Font.font("System", FontWeight.BOLD, 20));
        priceValue.setStyle("-fx-text-fill: #2E5525;");

        card.getChildren().addAll(details, priceValue);
        bookingsContainer.getChildren().add(card);
    }
}