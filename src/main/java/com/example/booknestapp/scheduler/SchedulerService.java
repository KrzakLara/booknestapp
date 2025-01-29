package com.example.booknestapp.scheduler;

import com.example.booknestapp.entity.User;
import com.example.booknestapp.repository.UserRepository;
import com.example.booknestapp.security.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Registry to hold custom jobs
    private final Map<String, Runnable> jobRegistry = new HashMap<>();

    public SchedulerService(UserRepository userRepository, JwtService jwtService, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    @Scheduled(cron = "${book.nest.app.schedule.config.polling-time}")
    public void runScheduledTask() {
        logger.info("Running scheduled task...");
        List<User> users = userRepository.findAll();
        users.forEach(user -> logger.info("Processing user: {}", user.getEmail()));
    }


    public void cleanExpiredTokens() {
        logger.info("Starting token cleanup...");
        userRepository.findAll().forEach(user -> {
            String email = user.getEmail();
            try {
                boolean tokenValid = jwtService.isTokenValid("dummyToken", userDetailsService.loadUserByUsername(email));
                if (!tokenValid) {
                    logger.info("Expired token detected for user: {}", email);
                }
            } catch (Exception e) {
                logger.error("Error during token validation for user: {}", email, e);
            }
        });
        logger.info("Token cleanup completed.");
    }


    public void addCustomJob(String jobName, Runnable job) {
        jobRegistry.put(jobName, job);
        logger.info("Custom job '{}' added to the registry.", jobName);
    }


    public void removeCustomJob(String jobName) {
        if (jobRegistry.containsKey(jobName)) {
            jobRegistry.remove(jobName);
            logger.info("Custom job '{}' removed from the registry.", jobName);
        } else {
            logger.warn("Custom job '{}' not found in the registry.", jobName);
        }
    }


    public void executeJobs() {
        logger.info("Executing all registered jobs...");
        jobRegistry.forEach((jobName, job) -> {
            logger.info("Executing job '{}'", jobName);
            job.run();
        });
        logger.info("All registered jobs executed.");
    }
}
