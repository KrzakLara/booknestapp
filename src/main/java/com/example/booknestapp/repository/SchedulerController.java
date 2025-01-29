package com.example.booknestapp.repository;

import com.example.booknestapp.scheduler.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    private final SchedulerService schedulerService;

    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/add-job/{jobName}")
    public String addJob(@PathVariable String jobName) {
        schedulerService.addCustomJob(jobName, () -> {
            logger.info("Executing dynamic job: {}", jobName);
        });
        return "Job " + jobName + " added successfully.";
    }

    @DeleteMapping("/remove-job/{jobName}")
    public String removeJob(@PathVariable String jobName) {
        schedulerService.removeCustomJob(jobName);
        return "Job " + jobName + " removed successfully.";
    }

    @PostMapping("/execute-jobs")
    public String executeJobs() {
        schedulerService.executeJobs();
        return "All registered jobs executed.";
    }
}

