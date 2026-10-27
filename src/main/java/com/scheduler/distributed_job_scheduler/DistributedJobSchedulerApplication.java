package com.scheduler.distributed_job_scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableKafka
public class DistributedJobSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedJobSchedulerApplication.class, args);
    }

}
