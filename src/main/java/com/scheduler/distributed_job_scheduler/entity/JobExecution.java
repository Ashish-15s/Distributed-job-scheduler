package com.scheduler.distributed_job_scheduler.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "job_executions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_execution_scheduled_for",
                        columnNames = {"job_id", "scheduled_for"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime processingStartedAt;

    private LocalDateTime completedAt;

    private String errorMessage;

    private Integer retryCount;

    private Integer maxRetries;

    @Column(nullable = false)
    private LocalDateTime scheduledFor;

}