package com.noble.tardis.modules.authentication.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noble.tardis.exception.BadRequest;
import com.noble.tardis.exception.Conflict;
import com.noble.tardis.exception.Unauthorized;
import com.noble.tardis.modules.authentication.dto.SessionDto;
import com.noble.tardis.modules.authentication.dto.SessionResponseDto;
import com.noble.tardis.modules.authentication.services.AuthService;
import com.noble.tardis.modules.authentication.validation.AuthValidation;
import com.noble.tardis.security.dto.UserDto;
import com.noble.tardis.security.entities.User;
import com.noble.tardis.security.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/user")
    public UserDto user(@AuthenticationPrincipal User user) {
        return userService.toDataTransferObject(user);
    }

    @PostMapping("/session")
    public ResponseEntity<SessionResponseDto> session(@RequestBody SessionDto data, HttpServletResponse response) {
        if (!validation.emailIsValid(data.email()))
            throw new BadRequest("Não foi fornecido um email válido");
        if (!validation.passwordIsValid(data.password()))
            throw new BadRequest("Não foi fornecida uma senha válida");
        var userByEmail = userService.findOne(data.email());
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
        if (!validation.emailIsValid(data.email()))
            throw new BadRequest("Não foi fornecido um email válido");
        if (!validation.passwordIsValid(data.password()))
            throw new BadRequest("Não foi fornecida uma senha válida");
        var userByEmail = userService.findOne(data.email());
        if (userByEmail != null)
            throw new Conflict("Email fornecido já foi cadastrado anteriormente");
        var user = userService.create(data.email(), data.password());
        var token = userService.generateAuthToken(user);
        var cookie = authService.generateAuthCookie(token);
        response.addCookie(cookie);
        var session = new SessionResponseDto(token, userService.toDataTransferObject(user));
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

}
