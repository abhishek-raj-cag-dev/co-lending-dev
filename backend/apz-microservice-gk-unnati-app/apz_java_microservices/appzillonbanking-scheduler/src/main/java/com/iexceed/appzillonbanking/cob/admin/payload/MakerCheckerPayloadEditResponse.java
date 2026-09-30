package com.iexceed.appzillonbanking.cob.admin.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class MakerCheckerPayloadEditResponse {
	
	@JsonProperty("id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT1592769688943", position = 1, required = false, value = "${mcReq.id.value}")
	private String id;
	
	@JsonProperty("status")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "FAILURE", position = 2, required = false, value = "${mcReq.status.value}")
	private String status;
	
	@JsonProperty("featureId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT", position = 3, required = false, value = "${mcReq.featureId.value}")
	private String featureId;

	@JsonProperty("errorCode")
	@ApiModelProperty(example = "1", position = 4, required = false, value = "${mcReq.errorCode.value}")
	private String errorCode;
	
	@JsonProperty("errorMessage")
	@ApiModelProperty(example = "Same Maker Checker not allowed to Authorize", position = 5, required = false, value = "${mcReq.errorMessage.value}")
	private String errorMessage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
