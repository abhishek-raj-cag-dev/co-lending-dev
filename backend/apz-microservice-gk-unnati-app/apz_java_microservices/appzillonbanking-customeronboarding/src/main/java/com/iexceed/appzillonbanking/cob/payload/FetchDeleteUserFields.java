package com.iexceed.appzillonbanking.cob.payload;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.LucUploadLoanRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FetchDeleteUserFields {
	
	@Override
	public String toString() {
		return "FetchDeleteUserFields [appId=" + appId + ", applicationId=" + applicationId + ", versionNum="
				+ versionNum + ", status=" + status + ", userId=" + userId + ", custDtlId=" + custDtlId + ", remarks="
				+ remarks + ", workFlow=" + workFlow + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "APZCBO")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 2, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 3, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
	
	@ApiModelProperty(required = true, position = 4, example = "APPROVED")
	@JsonProperty("status")
	private String status;
	
	@ApiModelProperty(required = true, position = 5, example = "john") // used for self assisted flow.
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = true, position = 6, example = "56892125") // used for self assisted flow.
	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;
	
	@ApiModelProperty(required = true, position = 7, example = "Invalid Details") // used for self assisted flow.
	@JsonProperty("remarks")
	private String remarks;
	
	@ApiModelProperty(required = true, position = 4, example = "")
	@JsonProperty("workflow")
	private WorkFlowDetails workFlow;

    @ApiModelProperty(required = false)
    @JsonProperty("LUCRequest")
    private LucUploadLoanRequest LUCRequest;

	@ApiModelProperty(required = false)
	@JsonProperty("isPreview")
	private String isPreview;


	/**
	 * @author Ankit.CAG Default "N" Field used to distinguish whether the
	 *         ApplicationMaster object data comes from CDH or PostgreSQL.
	 * @cdhFlag: Y or N
	 */
	@ApiModelProperty(required = true)
	@JsonProperty("cdhFlag")
	private String cdhFlag = "N";
    
    @ApiModelProperty(required = false)
    @JsonProperty("customerId")
    private String customerId;
    
    @ApiModelProperty(required = false)
    @JsonProperty("ProductId")
    private String ProductId;

	@ApiModelProperty(required = false)
	@JsonProperty("presactionLoanAmount")
	private String presactionLoanAmount;
}
