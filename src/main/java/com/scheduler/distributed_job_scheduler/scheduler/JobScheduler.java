package com.scheduler.distributed_job_scheduler.scheduler;

import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobStatus;
import com.scheduler.distributed_job_scheduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class JobScheduler {

    private final JobRepository jobRepository;
    private final CronService cronService;

    @Scheduled(fixedRate = 1000)
    public void checkForDueJobs() {

        LocalDateTime now = LocalDateTime.now();

        List<Job> dueJobs =
                jobRepository.findByStatusAndNextRunAtLessThanEqual(
                        JobStatus.ACTIVE,
                        now
                );

        for (Job job : dueJobs) {
            System.out.println("Job is due: " + job.getName());

            ZonedDateTime nextExecution =
                    cronService.getNextExecution(job.getCronExpression());

            job.setNextRunAt(nextExecution.toLocalDateTime());

           // jobRepository.save(job);
        }
    }
}