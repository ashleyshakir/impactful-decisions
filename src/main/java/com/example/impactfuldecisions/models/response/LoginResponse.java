package com.example.impactfuldecisions.models.response;


import com.example.impactfuldecisions.models.User;

/**
 * A class representing a login response, containing a JWT token.
 */
public class LoginResponse {

    // Include user object in response for front end purposes??
    private User user;
    private String jwt;

    public LoginResponse(String jwt, User user) {
        this.jwt = jwt;
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}