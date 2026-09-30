package com.iexceed.appzillonbanking.cob.admin.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/Swagger-MC-Properties/Admin-rest.properties")
public class MakerCheckerFetchPayloadRequest {

	@JsonProperty("id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT1592769688943", position = 1, required = true, value = "${mcReq.id.value}")
	private String id;
	
	@JsonProperty("featureId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT", position = 2, required = true, value = "${mcReq.featureId.value}")
	private String featureId;

	@JsonProperty("userId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "admin", position = 3, required = true, value = "${mcReq.makerId.value}")
	private String userId;
	
	@JsonProperty("checkerId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "adminauth", position = 4, required = true, value = "${mcReq.checkerId.value}")
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	@Override
	public String toString() {
		return "MakerCheckerFetchPayloadRequest [id=" + id + ", featureId=" + featureId + ", userId=" + userId
				+ ", checkerId=" + checkerId + "]";
	}
}
