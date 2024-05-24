package com.jaywant.rentify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.jaywant.rentify.config.JwtProvider;
import com.jaywant.rentify.exception.UserException;
import com.jaywant.rentify.models.User;
import com.jaywant.rentify.repository.UserRepository;
import com.jaywant.rentify.request.LoginRequest;
import com.jaywant.rentify.response.AuthResponse;
import com.jaywant.rentify.service.CustomUserServiceImplementation;

import jakarta.validation.Valid;

@CrossOrigin("https://rentify-frontend-delta.vercel.app")
@RestController
@RequestMapping("/auth")
public class AppController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserServiceImplementation customUserServiceImplementation;


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody User user) throws UserException {
        String email = user.getEmail();
        String password = user.getPassword();

        // Check if email already exists
        if (userRepository.findByEmail(email) != null) {
            throw new UserException("Email is already used with another account");
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(password));
        // Save the new user
        User savedUser = userRepository.save(user);

        // Authenticate the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate JWT token
        String token = jwtProvider.generateToken(authentication);

        // Return the auth response with user details
        AuthResponse authResponse = new AuthResponse(token, "Signup Success", savedUser.getEmail(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getPhoneNumber(), savedUser.getId());
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }


    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@Valid @RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // Authenticate the user
        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate JWT token
        String token = jwtProvider.generateToken(authentication);

        // Fetch user details
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Return the auth response with user details
        AuthResponse authResponse = new AuthResponse(token, "Signin Success", user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getId());
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserServiceImplementation.loadUserByUsername(email);
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
