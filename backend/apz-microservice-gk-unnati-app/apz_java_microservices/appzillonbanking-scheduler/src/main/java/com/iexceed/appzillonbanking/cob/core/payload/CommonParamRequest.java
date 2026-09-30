package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class CommonParamRequest {
	
	@ApiModelProperty(required = true, position = 1, example = "PreLoginAppParams")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 2, example = "APZRMB")
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("requestObj")
	private CommonParamRequestFields requestObj;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public CommonParamRequestFields getRequestObj() {
		return requestObj;
	}

	public void setRequestObj(CommonParamRequestFields requestObj) {
		this.requestObj = requestObj;
	}

	@Override
	public String toString() {
		return "CommonParamRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj=" + requestObj + "]";
	}
}
