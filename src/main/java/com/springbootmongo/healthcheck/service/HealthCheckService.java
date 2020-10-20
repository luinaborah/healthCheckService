package com.springbootmongo.healthcheck.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.springbootmongo.healthcheck.model.Response;
import com.springbootmongo.healthcheck.model.ServiceRegistry;
import com.springbootmongo.healthcheck.repository.HealthCheckRepository;
import com.springbootmongo.healthcheck.utils.HealthCheckUtil;

@Component
public class HealthCheckService {
	@Autowired
	HealthCheckRepository healthCheckRepository;
	@Autowired
	ThreadPoolTaskExecutor threadPoolTaskExecutor;
	@Autowired
	RestTemplate rest;

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public Map<String, String> retrieveServiceStatus(String name) throws InterruptedException, ExecutionException, TimeoutException {
		Optional<ServiceRegistry> serviceData = healthCheckRepository.findById(name);
		Map<String, String> statusMap = new HashMap<>();
		if (serviceData.isPresent()) {
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<HttpHeaders> entity = new HttpEntity<>(header);
			ServiceRegistry reg = serviceData.get();
			List<String> urlList = HealthCheckUtil.getUrlList(reg.getInstance());

			long startTime = System.currentTimeMillis();
			statusMap = completableFutureForHealthCheck(urlList, entity, threadPoolTaskExecutor, rest);
			LOG.info(("Total time taken : " + (System.currentTimeMillis() - startTime)));
		}
		return statusMap;
	}

	private Map<String, String> completableFutureForHealthCheck(List<String> urlList, HttpEntity<HttpHeaders> entity,
			ThreadPoolTaskExecutor threadPoolTaskExecutor, RestTemplate rest)
			throws InterruptedException, ExecutionException, TimeoutException {

		Map<String, String> statusMap = new HashMap<>();
		List<CompletableFuture<Pair<String, Response>>> statusRespCompletableFutureList = new ArrayList<>();

		for (String url : urlList) {
			CompletableFuture<Pair<String, Response>> statusRespCompletableFuture = CompletableFuture.supplyAsync(() -> {
				Pair<String, Response> statusReponse = null;
				try {
					statusReponse = getServiceResponse(url, entity, rest);
				} catch (Exception e) {
					LOG.error("Exception occurred during statusRespRulesCallableHelper", e);
				}
				return statusReponse;
			}, threadPoolTaskExecutor);
			statusRespCompletableFutureList.add(statusRespCompletableFuture);
		}

		CompletableFuture<Void> allDoneFuture = CompletableFuture
				.allOf(statusRespCompletableFutureList.toArray(new CompletableFuture[statusRespCompletableFutureList.size()]));
		CompletableFuture<List<Pair<String, Response>>> comList = allDoneFuture.thenApply(future -> statusRespCompletableFutureList.stream()
				.map(completableFuture -> completableFuture.join()).collect(Collectors.toList()));

		if (allDoneFuture != null) {
			List<Pair<String, Response>> all = comList.get(1000, TimeUnit.SECONDS);
			for (Pair<String, Response> pair : all) {
				statusMap.put(pair.getFirst(), pair.getSecond().getStatus());
			}
		}
		return statusMap;
	}

	private Pair<String, Response> getServiceResponse(String urls, HttpEntity<HttpHeaders> entity, RestTemplate rest) {
		Response res;
		try {
			res = rest.getForObject(urls, Response.class);
		} catch (Exception ex) {
			res = new Response();
			res.setStatus("DOWN");
		}
		return Pair.of(urls, res);
	}
	
	public Map<String, String> retriveStatusOfAllServices() throws InterruptedException, ExecutionException, TimeoutException {
		Map<String, String> statusMap = new HashMap<>();
		List<String> allUrlList = new ArrayList<>();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<HttpHeaders> entity = new HttpEntity<>(header);
		
		List<ServiceRegistry> list = healthCheckRepository.findAll();
		if (!list.isEmpty()) {
			for (ServiceRegistry serviceRegistry : list) {
				List<String> urlList = HealthCheckUtil.getUrlList(serviceRegistry.getInstance());
				allUrlList.addAll(urlList);
			}

			long startTime = System.currentTimeMillis();
			statusMap = completableFutureForHealthCheck(allUrlList, entity,threadPoolTaskExecutor,rest);
			LOG.info(("Total time taken : " + (System.currentTimeMillis() - startTime)));
		}
		
		return statusMap;	
	}
}
