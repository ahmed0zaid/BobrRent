package com.bobrrent.models;
import java.io.Serializable;
import java.time.LocalDate;

public class User implements Serializable {
        private String firstName;
        private String lastName;
        private String phone;
        private String password;
        private String gender;
        private LocalDate dob;
}

