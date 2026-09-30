package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class CommonParamRequestFields {
	
	@ApiModelProperty(required = true, example = "syncPreLogin")
	@JsonProperty("accessType")
	public String accessType;
	
	@ApiModelProperty(required = true, example = "SETTINGS")
	@JsonProperty("code")
	public String code;

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "CommonParamRequestFields [accessType=" + accessType + ", code=" + code + "]";
	}
}
