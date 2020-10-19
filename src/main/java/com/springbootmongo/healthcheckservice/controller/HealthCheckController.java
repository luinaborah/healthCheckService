package com.springbootmongo.healthcheckservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.springbootmongo.healthcheckservice.model.ServiceRegistry;
import com.springbootmongo.healthcheckservice.repository.HealthCheckRepository;
import com.springbootmongo.healthcheckservice.utils.HealthCheckUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/service/v1")
public class HealthCheckController {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	RestTemplate rest;
	@Autowired
	ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Autowired
	HealthCheckRepository healthCheckRepository;
	
	@ApiOperation(value = "View the list of services registered")
	@GetMapping("/registeredService/all")
	public ResponseEntity<List<ServiceRegistry>> getAllServices() {
		LOG.info("Getting all instances.");
		try {
			List<ServiceRegistry> list = healthCheckRepository.findAll();
			if (list.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "Get health details of specific service")
	@GetMapping("/healthcheck/{name}")
	public ResponseEntity<Map<String, String>> getHealthCheckForName(@PathVariable String name)
			throws InterruptedException, ExecutionException, TimeoutException {
		LOG.info("Getting service with name: {}.", name);
		
		Optional<ServiceRegistry> serviceData = healthCheckRepository.findById(name);
		if (serviceData.isPresent()) {
			Map<String, String> statusMap = new HashMap<>();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<HttpHeaders> entity = new HttpEntity<>(header);
			ServiceRegistry reg = serviceData.get();
			List<String> urlList = HealthCheckUtil.getUrlList(reg.getInstance());

			long startTime = System.currentTimeMillis();
			statusMap = HealthCheckUtil.completableFutureForHealthCheck(urlList, entity,threadPoolTaskExecutor, rest);
			LOG.info(("Total time taken : " + (System.currentTimeMillis() - startTime)));
			return new ResponseEntity<>(statusMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		/*
		ResponseEntity<ServiceRegistry> response = rest.exchange("http://localhost:1111/serviceregistry/v1/" + name, HttpMethod.GET, entity,
				new ParameterizedTypeReference<ServiceRegistry>() {
				});*/
	}
	
	@ApiOperation(value = "Get the health of all instances registered")
	@GetMapping("/healthcheck/all")
	public ResponseEntity<Map<String, String>> getHealthCheckForAllServices()
			throws InterruptedException, ExecutionException, TimeoutException {
		Map<String, String> statusMap = new HashMap<>();
		List<String> allUrlList = new ArrayList<>();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<HttpHeaders> entity = new HttpEntity<>(header);
		
		try {
			List<ServiceRegistry> list = healthCheckRepository.findAll();
			if (list.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			for (ServiceRegistry serviceRegistry : list) {
				List<String> urlList = HealthCheckUtil.getUrlList(serviceRegistry.getInstance());
				allUrlList.addAll(urlList);
			}

			long startTime = System.currentTimeMillis();
			statusMap = HealthCheckUtil.completableFutureForHealthCheck(allUrlList, entity,threadPoolTaskExecutor,rest);
			LOG.info(("Total time taken : " + (System.currentTimeMillis() - startTime)));
			return new ResponseEntity<>(statusMap, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		/*ResponseEntity<List<ServiceRegistry>> response = rest.exchange("http://localhost:1111/serviceregistry/v1/all", HttpMethod.GET,
				entity, new ParameterizedTypeReference<List<ServiceRegistry>>() {
				});
		List<ServiceRegistry> listOfServices = response.getBody();
		*/
	}

}
