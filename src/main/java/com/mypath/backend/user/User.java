package com.mypath.backend.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter@Setter@AllArgsConstructor
@Table(name="user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String bio;
    private String imageUrl;
    private Boolean visibility;
    private Date createdAt;
    private Date updatedAt;
    private Role role;

    public User(User _user) {
        this.username = _user.username;
        this.password = _user.password;
        this.email = _user.email;
        this.firstName = _user.firstName;
        this.lastName = _user.lastName;
        this.phone = _user.phone;
        this.bio = _user.bio;
        this.imageUrl = _user.imageUrl;
        this.visibility = _user.visibility;
        this.createdAt = _user.createdAt;
        this.updatedAt = _user.updatedAt;
        this.role = _user.role;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority((role.name())));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
