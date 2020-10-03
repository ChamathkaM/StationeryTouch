package com.example.stationerystore.Model;

public class User {
        private String firstName;
        private String lastName;
        private String userName;
        private String Email;
        private String password;
        private String type;
        private String phone;
        private String image;

        public User() {
        }

        public User(String firstName, String lastName, String userName, String email, String password, String type, String phone, String image) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.userName = userName;
            this.Email = email;
            this.password = password;
            this.type = type;
            this.phone = phone;
            this.image = image;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

