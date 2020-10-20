package com.springbootmongo.healthcheck.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.springbootmongo.healthcheck.model.Instance;
import com.springbootmongo.healthcheck.model.Port;
import com.springbootmongo.healthcheck.model.Response;

public final class HealthCheckUtil {
	private HealthCheckUtil() {
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
