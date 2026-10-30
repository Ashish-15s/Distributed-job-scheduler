package com.scheduler.distributed_job_scheduler.service;

import com.scheduler.distributed_job_scheduler.entity.ExecutionStatus;
import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobExecution;
import com.scheduler.distributed_job_scheduler.exception.ResourceNotFoundException;
import com.scheduler.distributed_job_scheduler.kafka.JobExecutionMessage;
import com.scheduler.distributed_job_scheduler.kafka.JobExecutionProducer;
import com.scheduler.distributed_job_scheduler.repository.JobExecutionRepository;
import com.scheduler.distributed_job_scheduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final JobExecutionRepository jobExecutionRepository;
    private final JobExecutor jobExecutor;
    private final JobRepository jobRepository;
    private final JobExecutionProducer jobExecutionProducer;

    public void execute(Long jobId, Long executionId) {

        // Atomically claim the execution
        int claimed = jobExecutionRepository.updateStatusIfCurrent(
                executionId,
                ExecutionStatus.RUNNING,
                ExecutionStatus.PROCESSING,
                LocalDateTime.now()
        );

        // Another consumer already claimed or completed it
        if (claimed == 0) {
            return;
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found: " + jobId));

        JobExecution execution = jobExecutionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Execution not found: " + executionId));

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

    public void recoverStuckExecutions() {

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);

        List<JobExecution> stuckExecutions =
                jobExecutionRepository.findByStatusAndProcessingStartedAtBefore(
                        ExecutionStatus.PROCESSING,
                        cutoff
                );

        for (JobExecution execution : stuckExecutions) {

            int recovered = jobExecutionRepository.recoverExecution(
                    execution.getId(),
                    ExecutionStatus.PROCESSING,
                    ExecutionStatus.RUNNING,
                    cutoff
            );

            if (recovered == 1) {
                jobExecutionProducer.send(
                        new JobExecutionMessage(
                                execution.getJob().getId(),
                                execution.getId()
                        )
                );

                System.out.println("Recovered execution: " + execution.getId());
            }
        }
    }

}