package com.noble.tardis.modules.authentication.dto;

public record UpdateUserDto(
        String email,
        String username,
        String name,
        String password) {

}
