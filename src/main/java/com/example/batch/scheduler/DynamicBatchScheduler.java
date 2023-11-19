package com.example.batch.scheduler;


import com.example.batch.Job.DynamicBatchJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DynamicBatchScheduler extends BatchScheduler {

    public DynamicBatchScheduler(JobLauncher jobLauncher,
                                 JobRegistry jobRegistry,
                                 DynamicBatchJob dynamicBatchJob) {
        super(jobLauncher, jobRegistry, dynamicBatchJob);
        if(dynamicBatchJob.isJobEnabled()){
            startScheduler();
        }
    }
}
