package com.scheduler.distributed_job_scheduler.scheduler;

import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobStatus;
import com.scheduler.distributed_job_scheduler.repository.JobExecutionRepository;
import com.scheduler.distributed_job_scheduler.repository.JobRepository;
import com.scheduler.distributed_job_scheduler.service.JobExecutionService;
import com.scheduler.distributed_job_scheduler.service.JobExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Scheduled(fixedRate = 1000)
    public void checkForDueJobs() {

        LocalDateTime now = LocalDateTime.now();


        List<Job> dueJobs =
                jobRepository.findByStatusAndNextRunAtLessThanEqual(
                        JobStatus.ACTIVE,
                        now
                );

        for (Job job : dueJobs) {
            jobExecutionService.execute(job);
            System.out.println("Job is due: " + job.getName());

        }
    }
}