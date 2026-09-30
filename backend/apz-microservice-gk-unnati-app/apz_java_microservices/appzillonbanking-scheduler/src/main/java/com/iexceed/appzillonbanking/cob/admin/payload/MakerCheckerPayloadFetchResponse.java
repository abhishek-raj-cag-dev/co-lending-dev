package com.iexceed.appzillonbanking.cob.admin.payload;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;


public class MakerCheckerPayloadFetchResponse {

	@JsonProperty("id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT1592769688943", position = 1, required = true, value = "${mcReq.id.value}")
	private String id;
	
	@JsonProperty("featureId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "APZSFMNT", position = 3, required = true, value = "${mcReq.featureId.value}")
	private String featureId;

	@JsonProperty("userAction")
	@ApiModelProperty(example = "ADD", position = 4, required = true, value = "${mcReq.userAction.value}")
	private String userAction;

	@JsonProperty("payload")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "{\"tbAbmiCommonCodes\":{\"code\":\"ADDRESSDETAILS\",\"codeType\":\"COMM\",\"language\":\"EN\",\"accessType\":\"syncPostLogin\",\"codeDesc\":{\"ADDRESSDETAILS\":[\"AddressType~Y\",\"Address~Y\",\"State~Y\",\"City~Y\",\"Country~Y\",\"PinCode~Y\"]}}}", position = 5, required = true, value = "${mcReq.payload.value}")
	private String payload;

	@JsonProperty("authStatus")
	@ApiModelProperty(example = "U", position = 6, required = false, value = "${mcReq.authStatus.value}")
	private String authStatus;

	@JsonProperty("makerId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "admin", position = 7, required = true, value = "${mcReq.makerId.value}")
	private String makerId;

	@JsonProperty("makerTs")
	@ApiModelProperty(example = "21-06-20 04:06:04.151000000 PM", position = 8, required = false, value = "${mcReq.makerTs.value}")
	private String makerTs;
	
	@JsonProperty("checkerId")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(example = "adminauth", position = 9, required = true, value = "${mcReq.checkerId.value}")
	private String checkerId;
	
	@JsonProperty("checkerTs")
	@ApiModelProperty(example = "21-06-20 04:07:07.335000000 PM", position = 10, required = false, value = "${mcReq.checkerTs.value}")
	private Timestamp checkerTs;
	
	@JsonProperty("versionNo")
	@ApiModelProperty(example = "1", position = 11, required = true, value = "${mcReq.versionNo.value}")
	private int versionNo;
	
	
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

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public String getMakerId() {
		return makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	public String getMakerTs() {
		return makerTs;
	}

	public void setMakerTs(String makerTs) {
		this.makerTs = makerTs;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	public Timestamp getCheckerTs() {
		return checkerTs;
	}

	public void setCheckerTs(Timestamp checkerTs) {
		this.checkerTs = checkerTs;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

}
