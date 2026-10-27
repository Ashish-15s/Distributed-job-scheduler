package com.scheduler.distributed_job_scheduler.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class JobExecutionProducer {

    private static final String TOPIC = "job-execution";

    private final KafkaTemplate<String, JobExecutionMessage> kafkaTemplate;

    public JobExecutionProducer(
            KafkaTemplate<String, JobExecutionMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(JobExecutionMessage message) {
        kafkaTemplate.send(TOPIC, message);
    }
}