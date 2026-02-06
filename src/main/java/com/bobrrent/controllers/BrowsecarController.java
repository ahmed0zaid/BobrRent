package com.bobrrent.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class BrowsecarController {

    @FXML private FlowPane carContainer;
    @FXML private TextField searchField;

    private List<String[]> allCarsData = new ArrayList<>();

    @FXML
    public void initialize() {
        loadCarsFromDatabase();
    }

    private void loadCarsFromDatabase() {
        carContainer.getChildren().clear();
        allCarsData.clear();
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_CARS");
            String response = in.readLine();

            if (response != null && !response.isEmpty()) {
                String[] cars = response.split(";");
                for (String c : cars) {
                    String[] p = c.split(",");
                    if (p.length >= 4) {
                        allCarsData.add(p);
                        createCarCard(p[0], p[1], p[2], p[3]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase();
        carContainer.getChildren().clear();

        for (String[] car : allCarsData) {
            String brand = car[0].toLowerCase();
            String model = car[1].toLowerCase();

            if (brand.contains(query) || model.contains(query)) {
                createCarCard(car[0], car[1], car[2], car[3]);
            }
        }
    }

    private void createCarCard(String brand, String model, String price, String status) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(230, 300);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        ImageView carImage = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/com/bobrrent/images/Car-Download-PNG.png"));
            carImage.setImage(img);
        } catch (Exception e) {
            System.out.println("Image path error");
        }
        carImage.setFitHeight(110);
        carImage.setPreserveRatio(true);

        Label lblTitle = new Label(brand + " " + model);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        Label lblPrice = new Label(price + " € / Day");
        lblPrice.setStyle("-fx-text-fill: #2E5525; -fx-font-weight: bold;");

        Button btnBook = new Button("Book Now");
        btnBook.setStyle("-fx-background-color: #2E5525; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 5 20;");
        btnBook.setDisable(status.equalsIgnoreCase("rented"));
        btnBook.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/booking_popup.fxml"));
                Stage popup = new Stage();
                popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                popup.setScene(new javafx.scene.Scene(loader.load()));

                BookingController controller = loader.getController();
                controller.initData(brand, model, price);

                popup.showAndWait();
                loadCarsFromDatabase();
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #E8F0E6; -fx-background-radius: 20; -fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 8);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-scale-x: 1.0; -fx-scale-y: 1.0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);"));

        card.getChildren().addAll(carImage, lblTitle, lblPrice, btnBook);
        carContainer.getChildren().add(card);
    }
}