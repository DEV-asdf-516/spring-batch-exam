package com.example.batch.scheduler;

import com.example.batch.Job.BatchConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public abstract class BatchScheduler {

    protected ThreadPoolTaskScheduler scheduler;
    protected final JobLauncher jobLauncher;
    protected final JobRegistry jobRegistry;
    protected final BatchConfig batchConfig;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(){
        JobRegistryBeanPostProcessor jobProcessor = new JobRegistryBeanPostProcessor();
        jobProcessor.setJobRegistry(jobRegistry);
        return jobProcessor;
    }

    protected void startScheduler(){
        log.info("================================================");
        log.info(">>> [{}] START",batchConfig.getJobName());
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        log.info("[{}] cron: {}",batchConfig.getJobName(),batchConfig.getCronExpression());
        CronTrigger cronTrigger = new CronTrigger(batchConfig.getCronExpression());
        scheduler.schedule(runJob(),cronTrigger);
    }

    protected void stopScheduler(){
        log.info(">>> [{}] STOP",batchConfig.getJobName());
        if(scheduler == null){
            return ;
        }
        scheduler.shutdown();
    }

    public void updateCronExpression(String newCron) {
        stopScheduler();
        batchConfig.setCronExpression(newCron);
        startScheduler();
    }

    private Runnable runJob() {
        return () -> {
            launch(batchConfig.getJobName());
        };
    }

    private void launch(String jobName){
        String time = LocalDateTime.now().toString();
       try {
           Job job = jobRegistry.getJob(jobName);
           JobParametersBuilder jobParam = new JobParametersBuilder().addString("time", time);
           jobLauncher.run(job, jobParam.toJobParameters());
       } catch (
                NoSuchJobException|
                JobInstanceAlreadyCompleteException |
                JobExecutionAlreadyRunningException |
                JobParametersInvalidException |
                JobRestartException e
       ) {
           throw new RuntimeException(e);
        }
    }
}


