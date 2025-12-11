package com.mypath.backend.auth;

import com.mypath.backend.jwt.JwtService;
import com.mypath.backend.user.Role;
import com.mypath.backend.user.User;
import com.mypath.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequestDTO registerRequest) {
        User _user = new User(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getPhone(),
                registerRequest.getPhone(),
                registerRequest.getBio(),
                registerRequest.getImageUrl(),
                registerRequest.getVisibility(),
                registerRequest.getCreatedAt(),
                registerRequest.getUpdatedAt(),
                Role.USER
        );
        userRepository.save(
                _user
        );
        return new AuthResponse(jwtService.getToken(_user));
    }

    public AuthResponse login(LoginRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user=userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return new AuthResponse(jwtService.getToken(user));
    }

    public List<User> getAllUsers(){
        return (List<User>) userRepository.findAll();
    }
}
