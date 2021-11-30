package com.epam.finalproject.cinema.util;

public class RegisterValidationUtil {

    public static boolean isValidLogin(String login) {
        if (login == null)
            return false;
        String regex = "^[a-zA-Z0-9_-]{3,16}";
        return login.matches(regex);
    }

    public static boolean isValidPassword(String password) {
        if (password == null)
            return false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@\\$%^&(){}:;<>,.?\\/~_+-=|]).{8,32}$";
        return password.matches(regex);
    }

    public static boolean isValidEmail(String email) {
        if (email == null)
            return false;
        String regex = "^\\w+([\\.-]?\\w+)*@+(\\w[\\.-]?\\w+)*(\\.\\w{2,3})+$";
        return email.matches(regex);
    }
}
