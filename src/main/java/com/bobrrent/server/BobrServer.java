package com.bobrrent.server;

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
                        saveUserToFile(request.substring(7));
                        out.println("SUCCESS");
                    }
                    else if (command.equals("LOGIN")) {
                        boolean valid = checkLogin(parts[1], parts[2]);
                        out.println(valid ? "SUCCESS" : "FAILURE");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUserToFile(String data) throws IOException {
        try (FileWriter fw = new FileWriter("users.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(data);
            bw.newLine();
        }
    }

    private static boolean checkLogin(String phone, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(phone) && details[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) { return false; }
        return false;
    }
}
