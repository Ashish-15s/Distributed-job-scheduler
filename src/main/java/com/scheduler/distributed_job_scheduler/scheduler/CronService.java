package com.scheduler.distributed_job_scheduler.scheduler;


import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CronService {

    private final CronParser parser;

    public CronService() {
        this.parser = new CronParser(
                CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX)
        );
    }

    public ZonedDateTime getNextExecution(String cronExpression) {

        Cron cron = parser.parse(cronExpression);
        cron.validate();

        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        return executionTime.nextExecution(ZonedDateTime.now())
                .orElseThrow(() ->
                        new IllegalArgumentException("Unable to calculate next execution"));
    }
}