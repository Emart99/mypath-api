package com.mypath.backend.auth;

import com.mypath.backend.user.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter @Setter
public class RegisterRequestDTO {
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

    public RegisterRequestDTO(String username, String password, String email, String firstName, String lastName, String phone, String bio, String imageUrl, Boolean visibility) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.visibility = visibility;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.role = Role.USER;

    }
}
