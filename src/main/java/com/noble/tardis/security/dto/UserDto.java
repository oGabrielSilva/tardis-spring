package com.noble.tardis.security.dto;

import java.util.List;

public record UserDto(
                String email,
                String username,
                String name,
                String avatarURL,
                boolean emailChecked,
                List<String> authorities) {

}
