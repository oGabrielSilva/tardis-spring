package com.noble.tardis.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.noble.tardis.security.dto.UserDto;
import com.noble.tardis.security.entities.User;
import com.noble.tardis.security.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findDetailsByEmail(username);
    }

    public User findOne(String email) {
        return userRepository.findByEmail(email);
    }

    public User findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User create(String email, String password) {
        var mail = email.split("@")[0];
        var u = new User(email, mail, passwordEncoder.encode(password));
        return userRepository.save(u);
    }

    public String generateAuthToken(User u) {
        return tokenService.token(u);
    }

    public UserDto toDataTransferObject(User user) {
        return new UserDto(user.getEmail(), user.getUsername(), user.getName(), user.getAvatarURL(),
                user.isEmailChecked(), user.getAllUserRoles());
    }

    public boolean passwordMatches(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

}
