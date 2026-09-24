package com.scheduler.distributed_job_scheduler.service;

import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobStatus;
import com.scheduler.distributed_job_scheduler.exception.ResourceNotFoundException;
import com.scheduler.distributed_job_scheduler.repository.JobRepository;
import com.scheduler.distributed_job_scheduler.scheduler.CronService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    private final CronService cronService;

    public Job createJob(String name, String cronExpression, String targetUrl) {
        ZonedDateTime nextExecution =
                cronService.getNextExecution(cronExpression);

        Job job = Job.builder()
                .name(name)
                .cronExpression(cronExpression)
                .targetUrl(targetUrl)
                .status(JobStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .nextRunAt(nextExecution.toLocalDateTime())
                .build();

        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public Job updateJob(Long id, String name, String cronExpression, String targetUrl) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found: " + id));

        job.setName(name);
        job.setCronExpression(cronExpression);
        job.setTargetUrl(targetUrl);
        job.setUpdatedAt(LocalDateTime.now());

        return jobRepository.save(job);
    }
}