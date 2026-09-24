package com.scheduler.distributed_job_scheduler.repository;

import com.scheduler.distributed_job_scheduler.entity.Job;
import com.scheduler.distributed_job_scheduler.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatusAndNextRunAtLessThanEqual(
            JobStatus status,
            LocalDateTime time
    );
}