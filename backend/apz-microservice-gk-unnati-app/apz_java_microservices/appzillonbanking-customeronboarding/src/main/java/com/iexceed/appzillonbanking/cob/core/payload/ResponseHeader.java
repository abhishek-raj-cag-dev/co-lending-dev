package com.iexceed.appzillonbanking.cob.core.payload;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ResponseHeader {
	
	@ApiModelProperty(example = "0(SUCCESS)/1(FAILURE)", allowableValues = "0, 1")
	@JsonProperty("ResponseCode")
	private String responseCode;
	
	@ApiModelProperty(hidden=true)
	@JsonProperty("ErrorCode")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String errorCode;
	
	@ApiModelProperty(example = "This field contains the error message if the API execution fails")
	@JsonProperty("ResponseMessage")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String responseMessage;
	
	@JsonIgnore
	private HttpStatus httpStatus;
	
	@Override
	public String toString() {
		return "ResponseHeader [responseCode=" + responseCode + ", errorCode=" + errorCode + ", responseMessage="
				+ responseMessage + ", httpStatus=" + httpStatus + "]";
	}
}