package com.noble.tardis.modules.authentication.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.noble.tardis.exception.BadRequest;
import com.noble.tardis.exception.Conflict;
import com.noble.tardis.exception.NotFound;
import com.noble.tardis.exception.Unauthorized;
import com.noble.tardis.modules.authentication.dto.SessionDto;
import com.noble.tardis.modules.authentication.dto.SessionResponseDto;
import com.noble.tardis.modules.authentication.dto.UpdatePasswordDto;
import com.noble.tardis.modules.authentication.dto.UpdateUserDto;
import com.noble.tardis.modules.authentication.services.AuthService;
import com.noble.tardis.modules.authentication.validation.AuthValidation;
import com.noble.tardis.security.dto.UserDto;
import com.noble.tardis.security.entities.User;
import com.noble.tardis.security.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/authentication")
public class AuthRestController {

    @Autowired
    UserService userService;
    @Autowired
    AuthValidation validation;
    @Autowired
    AuthService authService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/username/{username}")
    public UserDto username(@PathVariable String username) {
        var user = userService.findOneByUsername(username);
        if (user == null)
            throw new NotFound("Username não foi encontrado");
        return userService.toDataTransferObject(user);
    }

    @GetMapping("/user")
    public UserDto user(@AuthenticationPrincipal User user) {
        return userService.toDataTransferObject(user);
    }

    @PostMapping("/session")
    public ResponseEntity<SessionResponseDto> session(@RequestBody SessionDto data, HttpServletResponse response) {
        if (!validation.isEmailValid(data.email()))
            throw new BadRequest("Não foi fornecido um email válido");
        if (!validation.isPasswordValid(data.password()))
            throw new BadRequest("Não foi fornecida uma senha válida");
        var userByEmail = userService.findOne(data.email().trim());
        if (userByEmail == null)
            throw new Conflict("Email fornecido não está cadastrado");
        if (!userService.passwordMatches(data.password(), userByEmail.getPassword()))
            throw new Unauthorized("Credenciais incorretas");
        var token = userService.generateAuthToken(userByEmail);
        var session = new SessionResponseDto(token, userService.toDataTransferObject(userByEmail));
        var cookie = authService.generateAuthCookie(token);
        response.addCookie(cookie);
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<SessionResponseDto> signUp(@RequestBody SessionDto data, HttpServletRequest request,
            HttpServletResponse response) {
        if (!validation.isEmailValid(data.email()))
            throw new BadRequest("Não foi fornecido um email válido");
        if (!validation.isPasswordValid(data.password()))
            throw new BadRequest("Não foi fornecida uma senha válida");
        var userByEmail = userService.findOne(data.email());
        if (userByEmail != null)
            throw new Conflict("Email fornecido já foi cadastrado anteriormente");
        var user = userService.create(data.email().trim(), data.password().trim());
        var token = userService.generateAuthToken(user);
        var cookie = authService.generateAuthCookie(token);
        response.addCookie(cookie);
        var session = new SessionResponseDto(token, userService.toDataTransferObject(user));
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

    @PutMapping("/user")
    public UserDto update(@AuthenticationPrincipal User user, @RequestBody UpdateUserDto data) {
        if (!validation.isNameValid(data.name())) {
            throw new BadRequest("Nome fornecido precisa ser válido");
        }
        if (!validation.isUsernameValid(data.username())) {
            throw new BadRequest("Username fornecido precisa ser válido");
        }
        if (data.email() != null && validation.isEmailValid(data.email()) && !data.email().equals(user.getEmail())) {
            var isPasswordCorrect = passwordEncoder.matches(data.password(), user.getPassword());
            if (!isPasswordCorrect)
                throw new Unauthorized("Credenciais estão incorretas");
            user.setEmailChecked(false);
            user.setEmail(data.email().trim());
        }
        var userByUsername = userService.findOneByUsername(data.username());
        if (userByUsername != null && !userByUsername.getId().equals(user.getId()))
            throw new Conflict("Usuário com username # já existe".replace("#", data.username()));
        user.setName(data.name().trim());
        user.setUsername(validation.normalizeUsername(data.username()));
        return userService.update(user);
    }

    @PatchMapping("/user/avatar")
    public Map<String, String> updateProfile(@RequestPart("avatar") MultipartFile avatar,
            @AuthenticationPrincipal User user) {
        if (avatar == null || avatar.isEmpty())
            throw new BadRequest("Avatar não recebido ou não válido");
        if (!avatar.getContentType().contains("image"))
            throw new BadRequest("Avatar precisa ser um arquivo de imagem");
        if (avatar.getSize() > 2097152)
            throw new BadRequest("Imagem fornecida é muito pesada");
        var url = userService.uploadAvatar(avatar, user);
        return Map.of("avatar", url, "message", "Sucesso! Sua foto de perfil foi atualizada");
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PatchMapping("/user/password")
    public void updatePassword(@AuthenticationPrincipal User user, @RequestBody UpdatePasswordDto data) {
        if (!validation.isPasswordValid(data.password()))
            throw new BadRequest("Credenciais inválidas");
        if (!validation.isPasswordValid(data.newPassword()))
            throw new BadRequest("A nova senha é inválida");
        if (data.password().equals(data.newPassword()))
            throw new BadRequest("A nova senha não pode ser igual a anterior");
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new Unauthorized("Credenciais inválidas");
        }
        user.setPassword(passwordEncoder.encode(data.newPassword()));
        userService.update(user);
    }

}
