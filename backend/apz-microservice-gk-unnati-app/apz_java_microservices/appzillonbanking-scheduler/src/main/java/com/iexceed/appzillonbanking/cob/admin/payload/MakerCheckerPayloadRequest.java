package com.iexceed.appzillonbanking.cob.admin.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/Swagger-MC-Properties/Admin-rest.properties")
public class MakerCheckerPayloadRequest {

	@JsonProperty("id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT1592769688943", position = 1, required = true, value = "${mcReq.id.value}")
	private String id;
	
	@JsonProperty("featureId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT", position = 2, required = true, value = "${mcReq.featureId.value}")
	private String featureId;

	@JsonProperty("userAction")
	@ApiModelProperty(example = "ADD", position = 3, required = true, value = "${mcReq.userAction.value}")
	private String userAction;
	
	@JsonProperty("payload")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "{\"tbAbmiCommonCodes\":{\"code\":\"ADDRESSDETAILS\",\"codeType\":\"COMM\",\"language\":\"EN\",\"accessType\":\"syncPostLogin\",\"codeDesc\":{\"ADDRESSDETAILS\":[\"AddressType~Y\",\"Address~Y\",\"State~Y\",\"City~Y\",\"Country~Y\",\"PinCode~Y\"]}}}", position = 4, required = true, value = "${mcReq.payload.value}")
	private String payload;

	@JsonProperty("makerId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "admin", position = 5, required = true, value = "${mcReq.makerId.value}")
	private String makerId;
	
	@JsonProperty("checkerId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "adminauth", position = 6, required = true, value = "${mcReq.checkerId.value}")
	private String checkerId;

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

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getMakerId() {
		return makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	@Override
	public String toString() {
		return "MakerCheckerPayloadRequest [id=" + id + ", featureId=" + featureId + ", userAction=" + userAction
				+ ", payload=" + payload + ", makerId=" + makerId + ", checkerId=" + checkerId + "]";
	}
}
