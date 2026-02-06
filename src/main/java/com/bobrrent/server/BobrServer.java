package com.bobrrent.server;

import java.sql.*;
import java.io.*;
import java.net.*;

public class BobrServer {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "system";
    private static final String DB_PASS = "Ahmed2016";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("SERVER STARTED...");
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
                    } else if (command.equals("LOGIN")) {
                        out.println(checkLoginSQL(parts[1], parts[2]) ? "SUCCESS" : "FAILURE");
                    } else if (command.equals("GET_CARS")) {
                        out.println(fetchCarsFromSQL());
                    } else if (command.equals("BOOK_CAR")) {
                        out.println(processBooking(parts));
                    } else if (command.equals("GET_MY_BOOKINGS")) {
                        out.println(fetchUserBookings(parts[1]));
                    } else if (command.equals("GET_PROFILE")) {
                        out.println(fetchUserProfile(parts[1]));
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static String processBooking(String[] p) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            try {
                String bookSql = "INSERT INTO RENTALS (CUSTOMER_PHONE, BRAND, MODEL, START_DATE, END_DATE, TOTAL_PRICE) " +
                        "VALUES (?, ?, ?, TO_DATE(?,'YYYY-MM-DD'), TO_DATE(?,'YYYY-MM-DD'), ?)";

                try (PreparedStatement ps1 = conn.prepareStatement(bookSql)) {
                    ps1.setString(1, p[1]); // phone
                    ps1.setString(2, p[2]); // brand
                    ps1.setString(3, p[3]); // model
                    ps1.setString(4, p[4]); // start date
                    ps1.setString(5, p[5]); // end date
                    ps1.setDouble(6, Double.parseDouble(p[6]));
                    ps1.executeUpdate();
                }

                // Update car status so it cannot be booked again
                String updateSql = "UPDATE CAR_INFO SET STATUS = 'rented' WHERE BRAND = ? AND MODEL = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                    ps2.setString(1, p[2]);
                    ps2.setString(2, p[3]);
                    ps2.executeUpdate();
                }

                conn.commit();
                return "SUCCESS";
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return "FAILURE";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private static String fetchUserBookings(String phone) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT BRAND, MODEL, START_DATE, END_DATE, TOTAL_PRICE FROM RENTALS WHERE CUSTOMER_PHONE = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(rs.getString(1)).append(" ").append(rs.getString(2)).append(",")
                        .append(rs.getDate(3)).append(",").append(rs.getDate(4)).append(",")
                        .append(rs.getDouble(5)).append(";");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sb.toString();
    }

    private static String fetchCarsFromSQL() {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT BRAND, MODEL, PRICE_PER_DAY, STATUS FROM CAR_INFO")) {
            while (rs.next()) {
                sb.append(rs.getString(1)).append(",").append(rs.getString(2)).append(",")
                        .append(rs.getString(3)).append(",").append(rs.getString(4)).append(";");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sb.toString();
    }

    private static String fetchUserProfile(String phone) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT FIRST_NAME, LAST_NAME, GENDER, BIRTHDAY FROM CUSTOMERS WHERE PHONE = ?")) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString(1) + "," + rs.getString(2) + "," + rs.getString(3) + "," + rs.getString(4);
        } catch (SQLException e) { e.printStackTrace(); }
        return "ERROR";
    }

    private static void saveUserToSQL(String[] p) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO CUSTOMERS (PHONE, PASSWORD, FIRST_NAME, LAST_NAME, GENDER, BIRTHDAY) VALUES (?, ?, ?, ?, ?, ?)")) {
            for (int i = 1; i <= 6; i++) pstmt.setString(i, p[i]);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static boolean checkLoginSQL(String phone, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM CUSTOMERS WHERE PHONE = ? AND PASSWORD = ?")) {
            pstmt.setString(1, phone); pstmt.setString(2, password);
            return pstmt.executeQuery().next();
        } catch (Exception e) { return false; }
    }
}