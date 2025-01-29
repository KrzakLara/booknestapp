package com.example.booknestapp.repolayertests;

import com.example.booknestapp.repository.SchedulerController;
import com.example.booknestapp.scheduler.SchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SchedulerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SchedulerService schedulerService;

    @InjectMocks
    private SchedulerController schedulerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(schedulerController).build();

        // Mocking service methods
        doNothing().when(schedulerService).addCustomJob(any(), any());
        doNothing().when(schedulerService).removeCustomJob(any());
        doNothing().when(schedulerService).executeJobs();
    }

    @Test
    void shouldAddJobSuccessfully() throws Exception {
        String jobName = "testJob";

        mockMvc.perform(post("/api/scheduler/add-job/{jobName}", jobName))
                .andExpect(status().isOk())
                .andExpect(content().string("Job " + jobName + " added successfully."));

        // Fix: Wrap raw value with eq() to make it a matcher
        verify(schedulerService).addCustomJob(eq(jobName), any());
    }


    @Test
    void shouldRemoveJobSuccessfully() throws Exception {
        String jobName = "testJob";

        mockMvc.perform(delete("/api/scheduler/remove-job/{jobName}", jobName))
                .andExpect(status().isOk())
                .andExpect(content().string("Job " + jobName + " removed successfully."));

        verify(schedulerService).removeCustomJob(jobName);
    }

    @Test
    void shouldExecuteJobsSuccessfully() throws Exception {
        mockMvc.perform(post("/api/scheduler/execute-jobs"))
                .andExpect(status().isOk())
                .andExpect(content().string("All registered jobs executed."));

        verify(schedulerService).executeJobs();
    }
}
