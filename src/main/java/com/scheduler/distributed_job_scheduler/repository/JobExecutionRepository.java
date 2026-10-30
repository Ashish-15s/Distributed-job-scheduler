package com.scheduler.distributed_job_scheduler.repository;


import com.scheduler.distributed_job_scheduler.entity.ExecutionStatus;
import com.scheduler.distributed_job_scheduler.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    @Modifying
    @Transactional
    @Query("""
    UPDATE JobExecution e
    SET e.status = :newStatus,
        e.processingStartedAt = :startedAt
    WHERE e.id = :executionId
    AND e.status = :currentStatus
""")
    int updateStatusIfCurrent(
            @Param("executionId") Long executionId,
            @Param("currentStatus") ExecutionStatus currentStatus,
            @Param("newStatus") ExecutionStatus newStatus,
            @Param("startedAt") LocalDateTime startedAt
    );

    List<JobExecution> findByStatusAndProcessingStartedAtBefore(
            ExecutionStatus status,
            LocalDateTime cutoff
    );

    @Modifying
    @Transactional
    @Query("""
    UPDATE JobExecution e
    SET e.status = :newStatus,
        e.processingStartedAt = null
    WHERE e.id = :executionId
    AND e.status = :currentStatus
    AND e.processingStartedAt < :cutoff
""")
    int recoverExecution(
            @Param("executionId") Long executionId,
            @Param("currentStatus") ExecutionStatus currentStatus,
            @Param("newStatus") ExecutionStatus newStatus,
            @Param("cutoff") LocalDateTime cutoff
    );
}