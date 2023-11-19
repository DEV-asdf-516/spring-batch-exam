package com.example.batch.Job;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
@Data
@RequiredArgsConstructor
@Slf4j
public class DynamicBatchJob implements BatchConfig{

    private String cronExpression;
    @Value("${job-setting.dynamic.enabled}")
    private boolean isJobEnabled;
    @Value("${job-setting.dynamic.name}")
    private String jobName;

    private static final String[] cronExpressions = {
        // 동적 cron 변경을 위한 임시 cron 식 변수
        "0 * * * * ?",
        "10 * * * * ?",
        "20 * * * * ?",
        "30 * * * * ?",
        "40 * * * * ?",
        "50 * * * * ?",
    };

    @Bean("dynamic")
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
        Step step = new StepBuilder("dynamicStep",jobRepository)
                   .allowStartIfComplete(true)
                   .tasklet(dynamicTasklet(),transactionManager)
                   .build();
        return step;
    }

    private Tasklet dynamicTasklet(){
        return ((contribution, chunkContext) -> {
            log.info("***** dynamic scheduling batch *****");
            return RepeatStatus.FINISHED;
        });
    }

    public String getRandomCronExpression(){
        Random random = new Random();
        List<String> crons = Arrays.asList(cronExpressions);
        String cron = crons.get(random.nextInt(crons.size()));
        return cron;
    }
}
