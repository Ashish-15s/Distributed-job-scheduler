package com.scheduler.distributed_job_scheduler.repository;


import com.scheduler.distributed_job_scheduler.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {
}