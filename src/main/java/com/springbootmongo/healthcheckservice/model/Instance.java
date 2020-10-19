package com.springbootmongo.healthcheckservice.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@Document(collection = "instance")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {
	@Id
	@JsonProperty("instanceId")
	private String instanceId;
	
	@JsonProperty("hostName")
	private String hostName;
	
	@JsonProperty("app")
	private String app;
	
	@JsonProperty("port")
	private List<Port> port = null;
	
	@JsonProperty("healthCheckService")
	private String healthCheckService;
	
	@JsonProperty("createdTimestamp")
	private String createdTimestamp;
	
	@JsonProperty("updatedTimestamp")
	private String updatedTimestamp;
	
	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("updatedBy")
	private String updatedBy;
	
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public List<Port> getPort() {
		return port;
	}
	public void setPort(List<Port> port) {
		this.port = port;
	}
	public String getHealthCheckService() {
		return healthCheckService;
	}
	public void setHealthCheckService(String healthCheckService) {
		this.healthCheckService = healthCheckService;
	}
	public String getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public String getUpdatedTimestamp() {
		return updatedTimestamp;
	}
	public void setUpdatedTimestamp(String updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}
