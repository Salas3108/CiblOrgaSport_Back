package com.ciblorgasport.analyticsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    /**
     * Pool de threads dédié aux insertions analytics asynchrones.
     * Dimensionné pour ne pas saturer la base tout en absorbant les pics de trafic.
     */
    @Bean(name = "analyticsExecutor")
    public Executor analyticsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("analytics-async-");
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }
}
