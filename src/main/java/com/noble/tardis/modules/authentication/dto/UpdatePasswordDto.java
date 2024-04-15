package com.noble.tardis.modules.authentication.dto;

public record UpdatePasswordDto(
        String password,
        String newPassword) {

}
