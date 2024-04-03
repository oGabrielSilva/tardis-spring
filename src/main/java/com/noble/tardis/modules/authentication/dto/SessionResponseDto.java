package com.noble.tardis.modules.authentication.dto;

import com.noble.tardis.security.dto.UserDto;

public record SessionResponseDto(String token, UserDto user) {

}
