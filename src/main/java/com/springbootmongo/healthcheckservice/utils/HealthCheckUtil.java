package com.springbootmongo.healthcheckservice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.springbootmongo.healthcheckservice.model.Instance;
import com.springbootmongo.healthcheckservice.model.Port;
import com.springbootmongo.healthcheckservice.model.Response;

public final class HealthCheckUtil {
	private HealthCheckUtil() {
	}

	private final static Logger LOG = LoggerFactory.getLogger(HealthCheckUtil.class);

	public static Pair<String, Response> getServiceResponse(String urls, HttpEntity<HttpHeaders> entity, RestTemplate rest) {
		Response res;
		try {
			res = rest.getForObject(urls, Response.class);
		} catch (Exception ex) {
			res = new Response();
			res.setStatus("DOWN");
		}
		return Pair.of(urls, res);
	}

	public static Map<String, String> completableFutureForHealthCheck(List<String> urlList, HttpEntity<HttpHeaders> entity, ThreadPoolTaskExecutor threadPoolTaskExecutor, RestTemplate rest)
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

	
	public static List<String> getUrlList(List<Instance> instance) {
		List<String> urlList = new ArrayList<>();
		for (Instance inst : instance) {
			StringBuilder url = new StringBuilder();
			List<Port> port = inst.getPort();
			for (Port p : port) {
				if (p.getEnabled()) {
					url.append(p.getSecured() ? "https://" : "http://");
					url.append(inst.getHostName());
					url.append(":");
					url.append(p.getNumber());
					url.append(inst.getHealthCheckService());
					urlList.add(url.toString());
				}
			}
		}
		return urlList;
	}
	
	public static String checkRegisterService(RestTemplate rest) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<HttpHeaders> entity = new HttpEntity<>(header);
		ResponseEntity<Response> response = rest.exchange("http://localhost:1111/actuator/health", HttpMethod.GET, entity,
				new ParameterizedTypeReference<Response>() {
				});
		String status = response.getBody().getStatus();
		return status;
	}

}
