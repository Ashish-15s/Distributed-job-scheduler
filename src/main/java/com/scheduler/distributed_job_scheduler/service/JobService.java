package com.scheduler.distributed_job_scheduler.service;

import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobStatus;
import com.scheduler.distributed_job_scheduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job createJob(String name, String cronExpression, String targetUrl) {

        Job job = Job.builder()
                .name(name)
                .cronExpression(cronExpression)
                .targetUrl(targetUrl)
                .status(JobStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}