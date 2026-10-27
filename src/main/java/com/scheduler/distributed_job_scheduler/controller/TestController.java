package com.scheduler.distributed_job_scheduler.controller;

import com.scheduler.distributed_job_scheduler.kafka.JobExecutionMessage;
import com.scheduler.distributed_job_scheduler.kafka.JobExecutionProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final JobExecutionProducer jobExecutionProducer;

    @GetMapping
    public String test() {
        System.out.println("TARGET JOB EXECUTED");
        return "OK";
    }

    @PostMapping("/kafka-test")
    public String kafkaTest() {
        JobExecutionMessage message =
                new JobExecutionMessage(1L, 100L);

        jobExecutionProducer.send(message);
        return "Message sent";
    }
}