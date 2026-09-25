package com.scheduler.distributed_job_scheduler.controller;

import com.scheduler.distributed_job_scheduler.dto.CreateJobRequest;
import com.scheduler.distributed_job_scheduler.dto.UpdateJobRequest;
import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Job createJob(@Valid @RequestBody CreateJobRequest request) {

        return jobService.createJob(
                request.getName(),
                request.getCronExpression(),
                request.getTargetUrl()
        );
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public Job getJob(@PathVariable Long id) {
        return jobService.getJob(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
    }

    @PutMapping("/{id}")
    public Job updateJob(
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobRequest request) {

        return jobService.updateJob(
                id,
                request.getName(),
                request.getCronExpression(),
                request.getTargetUrl()
        );
    }


}