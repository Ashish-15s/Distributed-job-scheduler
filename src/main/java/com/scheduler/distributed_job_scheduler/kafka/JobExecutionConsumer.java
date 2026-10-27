package com.scheduler.distributed_job_scheduler.kafka;

import com.scheduler.distributed_job_scheduler.service.JobExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobExecutionConsumer {

    private final JobExecutionService jobExecutionService;

    @KafkaListener(
            topics = "job-execution",
            groupId = "job-worker"
    )
    public void consume(JobExecutionMessage message) {

        System.out.println(
                "Received jobId: " + message.getJobId()
                        + ", executionId: " + message.getExecutionId()
        );

        jobExecutionService.execute(
                message.getJobId(),
                message.getExecutionId()
        );
    }
}