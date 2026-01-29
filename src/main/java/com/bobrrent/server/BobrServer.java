package com.bobrrent.server;
import java.sql.*;
import java.io.*;
import java.net.*;

public class BobrServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("SERVER STARTED aAnd WAITING FOR THE CLIENT ....");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String request = in.readLine();
                    if (request == null) continue;

                    String[] parts = request.split(",");
                    String command = parts[0];

                    if (command.equals("SIGNUP")) {
                        saveUserToSQL(parts);
                        out.println("SUCCESS");
                    }
                    else if (command.equals("LOGIN")) {
                        boolean valid = checkLoginSQL(parts[1], parts[2]);
                        out.println(valid ? "SUCCESS" : "FAILURE");
                    }
                    //else if (command.equals("GET_CARS")) {
                        //String allCars = fetchCarsFromSQL();
                        //out.println(allCars);
                    //}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUserToSQL(String[] parts) {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String dbUser = "system";
        String dbPass = "Ahmed2016";

        String sql = "INSERT INTO CUSTOMERS (PHONE, PASSWORD, FIRST_NAME, LAST_NAME, GENDER, BIRTHDAY) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parts[1]);
            pstmt.setString(2, parts[2]);
            pstmt.setString(3, parts[3]);
            pstmt.setString(4, parts[4]);
            pstmt.setString(5, parts[5]);
            pstmt.setString(6, parts[6]);

            pstmt.executeUpdate();
            System.out.println("SUCCESS to add it to SQL");
        } catch (SQLException e) {
            System.out.println("FAILURE to add it to SQL" + e.getMessage());
        }
    }


    private static boolean checkLoginSQL(String phone, String password) {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String dbUser = "system";
        String dbPass = "Ahmed2016";

        System.out.println("trying to connect to Database...");

        String sql = "SELECT * FROM CUSTOMERS WHERE PHONE = ? AND PASSWORD = ?";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                System.out.println("Database Connected Successfully!");
                pstmt.setString(1, phone);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                boolean exists = rs.next();
                System.out.println("User found in SQL: " + exists);
                return exists;

            }
        } catch (ClassNotFoundException e) {
            System.out.println("DRIVER ERROR: Did you reload Maven?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL ERROR: Check your URL/User/Pass!");
            e.printStackTrace();
        }
        return false;
    }
}
