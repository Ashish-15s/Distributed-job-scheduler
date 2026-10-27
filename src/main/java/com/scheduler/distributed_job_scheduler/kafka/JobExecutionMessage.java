package com.scheduler.distributed_job_scheduler.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionMessage {

    private Long jobId;
    private Long executionId;
}