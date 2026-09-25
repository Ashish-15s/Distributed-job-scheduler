package com.scheduler.distributed_job_scheduler.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class JobExecutor {

    private final RestClient restClient;

    public JobExecutor() {
        this.restClient = RestClient.create();
    }

    public void execute(String targetUrl) {
        restClient.get()
                .uri(targetUrl)
                .retrieve()
                .toBodilessEntity();
    }
}