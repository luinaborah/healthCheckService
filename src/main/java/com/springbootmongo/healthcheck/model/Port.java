package com.springbootmongo.healthcheck.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Port {
	
	@JsonProperty("number")
	private Integer number;
	
	@JsonProperty("enabled")
	private boolean enabled;
	
	@JsonProperty("secured")
	private boolean secured;
	
	
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean getSecured() {
		return secured;
	}
	public void setSecured(boolean secured) {
		this.secured = secured;
	}

}
