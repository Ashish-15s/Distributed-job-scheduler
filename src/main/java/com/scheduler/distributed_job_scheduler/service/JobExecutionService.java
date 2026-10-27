package com.scheduler.distributed_job_scheduler.service;

import com.scheduler.distributed_job_scheduler.entity.ExecutionStatus;
import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobExecution;
import com.scheduler.distributed_job_scheduler.exception.ResourceNotFoundException;
import com.scheduler.distributed_job_scheduler.repository.JobExecutionRepository;
import com.scheduler.distributed_job_scheduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final JobExecutionRepository jobExecutionRepository;
    private final JobExecutor jobExecutor;
    private final JobRepository jobRepository;

    public void execute(Long jobId, Long executionId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found: " + jobId));

        JobExecution execution = jobExecutionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Execution not found: " + executionId));

        int maxRetries = execution.getMaxRetries();

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                jobExecutor.execute(job.getTargetUrl());

                execution.setStatus(ExecutionStatus.SUCCESS);
                execution.setCompletedAt(LocalDateTime.now());
                execution.setRetryCount(attempt);
                execution.setErrorMessage(null);
                break;

            } catch (Exception e) {
                execution.setRetryCount(attempt);
                execution.setErrorMessage(e.getMessage());

                if (attempt == maxRetries) {
                    execution.setStatus(ExecutionStatus.FAILED);
                    execution.setCompletedAt(LocalDateTime.now());
                }
            }
        }

        jobExecutionRepository.save(execution);
    }
}