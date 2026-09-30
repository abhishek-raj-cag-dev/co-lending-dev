package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.LoanDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationTimelineDtl;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.ExistingLoanDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cob.domain.ab.BankingFacilities;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyLoanRequestFields {

	@ApiModelProperty(required = true, example = "APZDEP")
	@JsonProperty("appId")
	private String appId;

	@ApiModelProperty(required = true, position = 2, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;

	@ApiModelProperty(required = true, position = 3, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;

	@ApiModelProperty(required = true, position = 4, example = "Y")
	@JsonProperty("isExistingCustomer")
	private String isExistingCustomer;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicationMaster")
	private ApplicationMaster applicationMaster;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerDetailsList")
	private List<CustomerDetails> customerDetailsList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("addressDetailsWrapperList")
	private List<AddressDetailsWrapper> addressDetailsWrapperList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("existingLoanDetailsWrapperList")
	private List<ExistingLoanDetailsWrapper> existingLoanDetailsWrapperList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("occupationDetailsWrapperList")
	private List<OccupationDetailsWrapper> occupationDetailsWrapperList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("insuranceDetailsWrapperList")
	private List<InsuranceDetailsWrapper> insuranceDetailsWrapperList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("bankDetailsWrapperList")
	private List<BankDetailsWrapper> bankDetailsWrapperList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cibilDetailsWrapperList")
	private List<CibilDetailsWrapper> cibilDetailsWrapperList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("loanDetails")
	private LoanDetails loanDetails;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicationDocumentsWrapperList")
	private List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("workflow")
	private WorkFlowDetails workflow;

	@JsonProperty("applnWfDefinitionList")
	private List<WorkflowDefinition> applnWfDefinitionList;

	@JsonProperty("applicationTimelineDtl")
	private List<ApplicationTimelineDtl> applicationTimelineDtl;

	@JsonProperty("bankingFacilityList")
	private List<BankingFacilities> bankingFacilityList;

	@JsonProperty("queryResponse")
	private JsonNode queryResponse;

	@Override
	public String toString() {
		return "ApplyLoanRequestFields [appId=" + appId + ", applicationId=" + applicationId + ", versionNum="
				+ versionNum + ", isExistingCustomer=" + isExistingCustomer + ", applicationMaster=" + applicationMaster
				+ ", customerDetailsList=" + customerDetailsList + ", addressDetailsWrapperList="
				+ addressDetailsWrapperList + ", existingLoanDetailsWrapperList=" + existingLoanDetailsWrapperList
				+ ", occupationDetailsWrapperList=" + occupationDetailsWrapperList + ", insuranceDetailsWrapperList="
				+ insuranceDetailsWrapperList + ", bankDetailsWrapperList=" + bankDetailsWrapperList
				+ ", cibilDetailsWrapperList=" + cibilDetailsWrapperList + ", loanDetails=" + loanDetails
				+ ", applicationDocumentsWrapperList=" + applicationDocumentsWrapperList + ", workflow=" + workflow
				+ ", applnWfDefinitionList=" + applnWfDefinitionList + ", applicationTimelineDtl="
				+ applicationTimelineDtl + ", bankingFacilityList=" + bankingFacilityList + "]";
	}

}
