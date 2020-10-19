package com.springbootmongo.healthcheckservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HealthCheckConfiguration {
	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(16);
		threadPoolTaskExecutor.setMaxPoolSize(16);
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
