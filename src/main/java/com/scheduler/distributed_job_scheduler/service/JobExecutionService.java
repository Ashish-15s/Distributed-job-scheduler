package com.scheduler.distributed_job_scheduler.service;

import com.scheduler.distributed_job_scheduler.entity.ExecutionStatus;
import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobExecution;
import com.scheduler.distributed_job_scheduler.repository.JobExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final JobExecutionRepository jobExecutionRepository;
    private final JobExecutor jobExecutor;

    public void execute(Job job) {

        JobExecution execution = JobExecution.builder()
                .job(job)
                .status(ExecutionStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .retryCount(0)
                .maxRetries(3)
                .build();

        jobExecutionRepository.save(execution);

        int maxRetries = 3;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {

            try {
                jobExecutor.execute(job.getTargetUrl());

                execution.setStatus(ExecutionStatus.SUCCESS);
                execution.setCompletedAt(LocalDateTime.now());
                execution.setRetryCount(attempt);

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