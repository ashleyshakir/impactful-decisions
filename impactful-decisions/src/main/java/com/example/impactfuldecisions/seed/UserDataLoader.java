package com.example.impactfuldecisions.seed;

import com.example.impactfuldecisions.models.*;
import com.example.impactfuldecisions.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DecisionRepository decisionRepository;
    private final CriteriaRepository criteriaRepository;
    private final OptionRepository optionRepository;
    private final ProConRepository proConRepository;

    @Autowired
    public UserDataLoader(UserRepository userRepository,
                          DecisionRepository decisionRepository,
                          CriteriaRepository criteriaRepository,
                          OptionRepository optionRepository,
                          ProConRepository proConRepository,
                          @Lazy PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.decisionRepository = decisionRepository;
        this.criteriaRepository = criteriaRepository;
        this.optionRepository = optionRepository;
        this.proConRepository = proConRepository;
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
        decision1.setCreationDate(LocalDateTime.now(Clock.systemDefaultZone()));
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

        ProCon proCon1 = new ProCon();
        proCon1.setType("pro");
        proCon1.setOption(option1);
        proCon1.setCriteria(criteria2);
        proCon1.setDescription("Getting to spend more time with family I don't see a lot");
        proCon1.setRating(6.0);
        proConRepository.save(proCon1);

        ProCon proCon2 = new ProCon();
        proCon2.setType("con");
        proCon2.setOption(option1);
        proCon2.setCriteria(criteria1);
        proCon2.setDescription("It is so expensive to travel over seas");
        proCon2.setRating(8.0);
        proConRepository.save(proCon2);

        ProCon proCon3 = new ProCon();
        proCon3.setType("con");
        proCon3.setOption(option1);
        proCon3.setCriteria(criteria3);
        proCon3.setDescription("I will be exhausted because the 5 hour time difference");
        proCon3.setRating(9.0);
        proConRepository.save(proCon3);

        ProCon proCon4 = new ProCon();
        proCon4.setType("con");
        proCon4.setOption(option2);
        proCon4.setCriteria(criteria2);
        proCon4.setDescription("I won't get to make new memories with the family");
        proCon4.setRating(5.0);
        proConRepository.save(proCon4);

        ProCon proCon5 = new ProCon();
        proCon5.setType("pro");
        proCon5.setOption(option2);
        proCon5.setCriteria(criteria1);
        proCon5.setDescription("I will save so much money if I don't go");
        proCon5.setRating(9.0);
        proConRepository.save(proCon5);

        ProCon proCon6 = new ProCon();
        proCon6.setType("pro");
        proCon6.setOption(option2);
        proCon6.setCriteria(criteria3);
        proCon6.setDescription("I will get normal sleep and be able to function at 100 percent!");
        proCon6.setRating(7.0);
        proConRepository.save(proCon6);

        Decision decision2 = new Decision();
        decision2.setTitle("Which car should I get?");
        decision2.setDescription("Deciding which car to get!");
        decision2.setCreationDate(LocalDateTime.now(Clock.systemDefaultZone()));
        decision2.setUser(user1);
        decisionRepository.save(decision2);

        Criteria criteria4 = new Criteria();
        criteria4.setName("Price");
        criteria4.setWeight(5.0);
        criteria4.setDecision(decision2);
        criteriaRepository.save(criteria4);

        Criteria criteria5 = new Criteria();
        criteria5.setName("Mileage");
        criteria5.setWeight(4.5);
        criteria5.setDecision(decision2);
        criteriaRepository.save(criteria5);

        Criteria criteria6 = new Criteria();
        criteria6.setName("Miles per Gallon");
        criteria6.setWeight(4.5);
        criteria6.setDecision(decision2);
        criteriaRepository.save(criteria6);

        Option option3 = new Option();
        option3.setName("Bronco");
        option3.setDecision(decision2);
        optionRepository.save(option3);

        Option option4 = new Option();
        option4.setName("4Runner");
        option4.setDecision(decision2);
        optionRepository.save(option4);
    }
}
