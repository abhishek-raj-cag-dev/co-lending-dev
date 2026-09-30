package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchIFSCRequest {

	@ApiModelProperty(required = true, position = 1, example = "fetchIFSC")
	@JsonProperty("interfaceName")
	private String interfaceName;

	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;

	@JsonProperty("requestObj")
	private FetchIFSCRequestFields requestObj;

	@Override
	public String toString() {
		return "FetchIFSCRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj=" + requestObj
				+ "]";
	}
}