package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FetchRoleRequestWrapper {
	
	@JsonProperty("apiRequest")
	private FetchRoleRequest fetchRoleRequest;
}