package com.example.impactfuldecisions.seed;

import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.User;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DecisionRepository decisionRepository;

    @Autowired
    public UserDataLoader(UserRepository userRepository,
                          DecisionRepository decisionRepository,
                          @Lazy PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.decisionRepository = decisionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
    private void loadUserData() {
        User user1 = new User();
        user1.setUsername("decisionmaker101");
        user1.setEmailAddress("impactfuldecisionsteam@gmail.com");
        user1.setPassword(passwordEncoder.encode("password12345"));
        userRepository.save(user1);

        Decision decision1 = new Decision();
        decision1.setTitle("Should I go on the London trip?");
        decision1.setDescription("Deciding whether or not it is a good idea to travel right now.");
        decision1.setCreationDate(LocalDate.now(Clock.systemDefaultZone()));
        decision1.setUser(user1);
        decisionRepository.save(decision1);
    }
}
