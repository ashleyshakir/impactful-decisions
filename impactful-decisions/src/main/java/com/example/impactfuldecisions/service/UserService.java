package com.example.impactfuldecisions.service;


import com.example.impactfuldecisions.exceptions.UserExistsException;
import com.example.impactfuldecisions.models.User;
import com.example.impactfuldecisions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserService {
    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

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
    public User registerUser(User userObject) {
        if (!userRepository.existsByEmailAddress(userObject.getEmailAddress())) {
            userObject.setPassword((userObject.getPassword()));
            userRepository.save(userObject);
            return userObject;
        } else {
            throw new UserExistsException("user email address " + userObject.getEmailAddress() + " already exists");
        }
    }
}