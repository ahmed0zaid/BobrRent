package com.bobrrent.controllers;

import com.bobrrent.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingController {

    @FXML private Label carNameLabel;
    @FXML private Label totalPriceLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private String brand;
    private String model;
    private double dailyPrice;

    public void initData(String brand, String model, String price) {
        this.brand = brand;
        this.model = model;
        this.dailyPrice = Double.parseDouble(price);

        carNameLabel.setText(brand + " " + model);

        startDatePicker.valueProperty().addListener((ov, oldVal, newVal) -> calculateTotal());
        endDatePicker.valueProperty().addListener((ov, oldVal, newVal) -> calculateTotal());
    }

    private void calculateTotal() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start != null && end != null && !end.isBefore(start)) {
            long days = ChronoUnit.DAYS.between(start, end);
            if (days <= 0) days = 1;

            double total = days * dailyPrice;
            totalPriceLabel.setText(String.format("%.2f €", total));
        } else {
            totalPriceLabel.setText("0.00 €");
        }
    }

    @FXML
    private void handleConfirm() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            System.out.println("Error: Dates not selected");
            return;
        }

        String phone = com.bobrrent.utils.UserSession.getPhone();
        if (phone == null) {
            System.out.println("Error: No user logged in (Session is null)");
            return;
        }

        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String totalStr = totalPriceLabel.getText().replace(" €", "").replace(",", ".");

            String bookingData = "BOOK_CAR," + phone + "," + brand + "," + model + "," +
                    startDatePicker.getValue() + "," + endDatePicker.getValue() + "," + totalStr;

            out.println(bookingData);

            String response = in.readLine();
            if ("SUCCESS".equals(response)) {
                closeWindow();
            } else {
                System.out.println("Server rejected booking: " + response);
            }
        } catch (IOException e) {
            System.out.println("Socket Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) carNameLabel.getScene().getWindow();
        stage.close();
    }
}