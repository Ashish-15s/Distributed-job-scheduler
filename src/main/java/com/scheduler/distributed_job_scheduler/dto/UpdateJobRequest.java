package com.scheduler.distributed_job_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateJobRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String cronExpression;

    @NotBlank
    private String targetUrl;
}