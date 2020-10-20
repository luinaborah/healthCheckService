package com.springbootmongo.healthcheck.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springbootmongo.healthcheck.model.ServiceRegistry;
import com.springbootmongo.healthcheck.repository.HealthCheckRepository;
import com.springbootmongo.healthcheck.service.HealthCheckService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/service/v1")
public class HealthCheckController {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	HealthCheckService healthCheckService;
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
		Map<String, String> map = healthCheckService.retrieveServiceStatus(name);

		if(map.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(map, HttpStatus.OK);
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
		statusMap = healthCheckService.retriveStatusOfAllServices();
		if(statusMap.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(statusMap, HttpStatus.OK);
		}
		/*ResponseEntity<List<ServiceRegistry>> response = rest.exchange("http://localhost:1111/serviceregistry/v1/all", HttpMethod.GET,
				entity, new ParameterizedTypeReference<List<ServiceRegistry>>() {
				});
		List<ServiceRegistry> listOfServices = response.getBody();
		*/
	}
}
