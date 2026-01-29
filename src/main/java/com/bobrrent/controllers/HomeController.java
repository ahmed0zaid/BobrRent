package com.bobrrent.controllers;

import com.bobrrent.models.Car;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class HomeController {

    @FXML private TableView<Car> carTable;
    @FXML private TableColumn<Car, String> brandCol;
    @FXML private TableColumn<Car, String> modelCol;
    @FXML private TableColumn<Car, Double> priceCol;
    @FXML private TableColumn<Car, String> statusCol;

    @FXML
    public void initialize() {
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadCarsFromServer();
    }

    private void loadCarsFromServer() {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_CARS");
            String response = in.readLine();

            if (response != null && !response.isEmpty()) {
                ObservableList<Car> carList = FXCollections.observableArrayList();
                String[] cars = response.split(";");
                for (String c : cars) {
                    String[] p = c.split(",");
                    carList.add(new Car(p[0], p[1], p[2], Double.parseDouble(p[3]), p[4]));
                }
                carTable.setItems(carList);
            }
        } catch (IOException e) {
            System.out.println("Error in dashboard to connect to server.");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bobrrent/fxml/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}