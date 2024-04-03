package com.noble.tardis.security.enums;

public enum UserRole {
    COMMON("COMMON"), MODERATOR("MODERATOR"), ADMIN("ADMIN"), ROOT("ROOT");

    private final String description;

    UserRole(String role) {
        description = role;
    }

    public String asString() {
        return description;
    }
}
