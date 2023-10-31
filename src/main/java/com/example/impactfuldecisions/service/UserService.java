package com.example.impactfuldecisions.service;


import com.example.impactfuldecisions.exceptions.UserExistsException;
import com.example.impactfuldecisions.models.User;
import com.example.impactfuldecisions.models.request.LoginRequest;
import com.example.impactfuldecisions.repository.UserRepository;
import com.example.impactfuldecisions.security.JWTUtils;
import com.example.impactfuldecisions.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, JWTUtils jwtUtils, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public User findByUserEmailAddress(String emailAddress) {
        return userRepository.findUserByEmailAddress(emailAddress);
    }
    /**
     * Register a new user in the system.
     *
     * @param userObject The User object to be created.
     * @return The created User object.
     * @throws UserExistsException if the user's email address already exists in the system.
     */
    public User registerUser(User userObject){
        if(!userRepository.existsByEmailAddress(userObject.getEmailAddress())){
            userObject.setPassword(passwordEncoder.encode(userObject.getPassword()));
            userRepository.save(userObject);
            return userObject;
        } else {
            throw new UserExistsException("user email address " + userObject.getEmailAddress() + " already exists");
        }
    }
    /**
     * Login a user and generate a JWT token upon successful authentication.
     *
     * @param loginRequest The login request containing user credentials.
     * @return An Optional containing the JWT token if authentication is successful, otherwise empty.
     */
    public Optional<String> loginUser(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmailAddress(), loginRequest.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            return Optional.of(jwtUtils.generateJwtToken(myUserDetails));
        } catch (Exception e) {
            return Optional.empty();
        }
    }



}
