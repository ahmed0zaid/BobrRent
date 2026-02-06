package com.bobrrent.utils;

public class UserSession {
    private static String loggedInPhone;

    public static void setPhone(String phone) {
        loggedInPhone = phone;
    }

    public static String getPhone() {
        return loggedInPhone;
    }

    public static void cleanSession() {
        loggedInPhone = null;
    }
}