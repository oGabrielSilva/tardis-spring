package com.noble.tardis.security.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.noble.tardis.security.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_entity")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username = NanoIdUtils.randomNanoId();

    private String name;

    @Column(name = "avatar_url")
    private String avatarURL;

    private UserRole role = UserRole.COMMON;

    private String password;

    private boolean activated = true;
    private boolean locked = false;

    private boolean emailChecked = false;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public User() {
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public User(String email, String username, String name, String password) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public User(String email, String username, String name, UserRole role, String password) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public User(String email, String username, String name, String avatarURL, UserRole role, String password) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.avatarURL = avatarURL;
        this.role = role;
        this.password = password;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var roles = new ArrayList<GrantedAuthority>();
        switch (role) {
            case ROOT:
                roles.addAll(List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.ROOT.asString()),
                        new SimpleGrantedAuthority("ROLE_" + UserRole.ADMIN.asString()),
                        new SimpleGrantedAuthority("ROLE_" + UserRole.MODERATOR.asString())));
                break;
            case ADMIN:
                roles.addAll(List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.ADMIN.asString()),
                        new SimpleGrantedAuthority("ROLE_" + UserRole.MODERATOR.asString())));
                break;
            case MODERATOR:
                roles.add(new SimpleGrantedAuthority("ROLE_" + role.asString()));
                break;
        }
        roles.add(new SimpleGrantedAuthority("ROLE_" + UserRole.COMMON.asString()));
        return roles;
    }

    @SuppressWarnings("incomplete-switch")
    public List<String> getAllUserRoles() {
        var roles = new ArrayList<String>();
        switch (role) {
            case ROOT:
                roles.addAll(
                        List.of(UserRole.ROOT.asString(), UserRole.ADMIN.asString(), UserRole.MODERATOR.asString()));
                break;
            case ADMIN:
                roles.addAll(List.of(UserRole.ADMIN.asString(), UserRole.MODERATOR.asString()));
                break;
            case MODERATOR:
                roles.addAll(List.of(UserRole.MODERATOR.asString()));
                break;
        }
        roles.add((UserRole.COMMON.asString()));
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return activated;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isEmailChecked() {
        return emailChecked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setEmailChecked(boolean emailChecked) {
        this.emailChecked = emailChecked;
    }

}
