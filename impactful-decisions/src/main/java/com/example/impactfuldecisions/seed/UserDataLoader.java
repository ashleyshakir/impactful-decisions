package com.example.impactfuldecisions.seed;

import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.Option;
import com.example.impactfuldecisions.models.User;
import com.example.impactfuldecisions.repository.CriteriaRepository;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.repository.OptionRepository;
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
    private final CriteriaRepository criteriaRepository;
    private final OptionRepository optionRepository;

    @Autowired
    public UserDataLoader(UserRepository userRepository,
                          DecisionRepository decisionRepository,
                          CriteriaRepository criteriaRepository,
                          OptionRepository optionRepository,
                          @Lazy PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.decisionRepository = decisionRepository;
        this.criteriaRepository = criteriaRepository;
        this.optionRepository = optionRepository;
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

        Criteria criteria1 = new Criteria();
        criteria1.setName("Price");
        criteria1.setWeight(0.5);
        criteria1.setDecision(decision1);
        criteriaRepository.save(criteria1);

        Criteria criteria2 = new Criteria();
        criteria2.setName("Family Time");
        criteria2.setWeight(0.3);
        criteria2.setDecision(decision1);
        criteriaRepository.save(criteria2);

        Criteria criteria3 = new Criteria();
        criteria3.setName("Sleep");
        criteria3.setWeight(0.2);
        criteria3.setDecision(decision1);
        criteriaRepository.save(criteria3);

        Option option1 = new Option();
        option1.setName("Go to London");
        option1.setDecision(decision1);
        optionRepository.save(option1);

        Option option2 = new Option();
        option2.setName("Stay home");
        option2.setDecision(decision1);
        optionRepository.save(option2);
    }
}
