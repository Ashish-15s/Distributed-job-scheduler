package com.scheduler.distributed_job_scheduler.scheduler;

import com.scheduler.distributed_job_scheduler.entity.ExecutionStatus;
import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobExecution;
import com.scheduler.distributed_job_scheduler.entity.JobStatus;
import com.scheduler.distributed_job_scheduler.kafka.JobExecutionMessage;
import com.scheduler.distributed_job_scheduler.kafka.JobExecutionProducer;
import com.scheduler.distributed_job_scheduler.repository.JobExecutionRepository;
import com.scheduler.distributed_job_scheduler.repository.JobRepository;
import com.scheduler.distributed_job_scheduler.service.JobExecutionService;
import com.scheduler.distributed_job_scheduler.service.JobExecutor;
import com.scheduler.distributed_job_scheduler.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class JobScheduler {


    private final JobRepository jobRepository;
    private final CronService cronService;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobExecutor jobExecutor;
    private final JobExecutionService jobExecutionService;
    private final JobExecutionProducer jobExecutionProducer;
    private final RedisLockService redisLockService;

    @Scheduled(fixedRate = 1000)
    public void checkForDueJobs() {

        LocalDateTime now = LocalDateTime.now();


        List<Job> dueJobs =
                jobRepository.findByStatusAndNextRunAtLessThanEqual(
                        JobStatus.ACTIVE,
                        now
                );

        for (Job job : dueJobs) {
            String lockKey = "scheduler:job:" + job.getId();
            String token = redisLockService.acquireLock(
                    lockKey,
                    Duration.ofSeconds(30)
            );

            if (token == null) {
                continue;
            }

            try {
                LocalDateTime scheduledFor = job.getNextRunAt();

                JobExecution execution = JobExecution.builder()
                        .job(job)
                        .scheduledFor(scheduledFor)
                        .status(ExecutionStatus.RUNNING)
                        .startedAt(LocalDateTime.now())
                        .retryCount(0)
                        .maxRetries(3)
                        .build();

                execution = jobExecutionRepository.save(execution);

                jobExecutionProducer.send(
                        new JobExecutionMessage(job.getId(), execution.getId())
                );

                System.out.println("Job dispatched: " + job.getName());

                ZonedDateTime nextExecution =
                        cronService.getNextExecution(job.getCronExpression());

                job.setNextRunAt(nextExecution.toLocalDateTime());
                jobRepository.save(job);
            }
            finally {
                redisLockService.releaseLock(lockKey, token);
            }

        }
    }

    @Scheduled(fixedRate = 60000)
    public void recoverStuckJobs() {
        System.out.println("Recovered execution is running" );
        jobExecutionService.recoverStuckExecutions();
    }
}