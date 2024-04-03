package com.noble.tardis.modules.authentication.validation;

import org.springframework.stereotype.Component;

@Component
public class AuthValidation {
    private static final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public boolean emailIsValid(String email) {
        return email != null && email.matches(emailRegex);
    }

    public boolean passwordIsValid(String password) {
        return password != null && password.length() >= 8;
    }
}
