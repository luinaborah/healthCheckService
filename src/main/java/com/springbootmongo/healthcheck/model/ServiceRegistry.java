package com.springbootmongo.healthcheck.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(description="All details about the services Registered. ")
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceRegistry {
	@Id
	@Indexed(unique = true)
	@JsonProperty("name")
	@ApiModelProperty(notes="Name should be the service name")
	private String name;
	
	@JsonProperty("enabled")
	private boolean enabled;
	
	@JsonProperty("instance")
	private List<Instance> instance = null;
	
	//Getters Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<Instance> getInstance() {
		return instance;
	}

	public void setInstance(List<Instance> instance) {
		this.instance = instance;
	}
}
