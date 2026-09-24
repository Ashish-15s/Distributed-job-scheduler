package com.scheduler.distributed_job_scheduler.repository;

import com.scheduler.distributed_job_scheduler.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}