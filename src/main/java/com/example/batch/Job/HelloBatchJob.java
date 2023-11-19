package com.example.batch.Job;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Data
@RequiredArgsConstructor
@Slf4j
public class HelloBatchJob extends DefaultBatchConfiguration implements BatchConfig {

    @Value("${job-setting.hello.cron}")
    private String cronExpression;

    @Value("${job-setting.hello.enabled}")
    private boolean isJobEnabled;

    @Value("${job-setting.hello.name}")
    private String jobName;

    @Bean("hello")
    @Override
    public Job createJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Job job = new JobBuilder(getJobName(),jobRepository)
                       .incrementer(new RunIdIncrementer())
                       .start(executeStep(jobRepository,transactionManager))
                       .build();
        return job;
    }

    @Override
    public Step executeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Step step = new StepBuilder("helloStep",jobRepository)
                   .allowStartIfComplete(true)
                   .tasklet(helloTasklet(),transactionManager)
                   .build();
        return step;
    }
    public Tasklet helloTasklet(){
        return ((contribution, chunkContext) -> {
            log.info("***** hello batch! *****");
            return RepeatStatus.FINISHED;
        });
    }
}
