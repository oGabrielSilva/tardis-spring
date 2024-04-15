package com.noble.tardis.modules.authentication.validation;

import org.springframework.stereotype.Component;

@Component
public class AuthValidation {
    private static final String usernameRegex = "[^a-zA-Z0-9-_.]";
    private static final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public boolean isEmailValid(String email) {
        return email != null && email.matches(emailRegex);
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean isNameValid(String name) {
        return name != null && name.trim().length() >= 2;
    }

    public boolean isUsernameValid(String username) {
        return username != null && normalizeUsername(username.trim()).length() >= 1;
    }

    public String normalizeUsername(String username) {
        return username.replaceAll(usernameRegex, "");
    }
}
