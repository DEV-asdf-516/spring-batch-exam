package com.example.batch.Job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

public interface BatchConfig {
    String getJobName();
    boolean isJobEnabled();
    String getCronExpression();
    void setCronExpression(String cronExpression);
    Job createJob(JobRepository jobRepository, PlatformTransactionManager transactionManager);
}
