package com.iexceed.appzillonbanking.cob.loans.payload;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import com.iexceed.appzillonbanking.cob.payload.UploadDocumentRequestFields;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UploadLoanRequestFields {

	@ApiModelProperty(required = false, example = "APZDEP")
	@JsonProperty("appId")
	private String appId;

	@ApiModelProperty(required = true, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;

	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("applicationMaster")
	private ApplicationMaster applicationMaster;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("customerDetailsList")
	private List<CustomerDetails> customerDetailsList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("addressDetailsWrapperList")
	private List<AddressDetailsWrapper> addressDetailsWrapperList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("existingLoanDetailsWrapperList")
	private List<ExistingLoanDetailsWrapper> existingLoanDetailsWrapperList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("occupationDetailsWrapperList")
	private List<OccupationDetailsWrapper> occupationDetailsWrapperList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("insuranceDetailsWrapperList")
	private List<InsuranceDetailsWrapper> insuranceDetailsWrapperList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("bankDetailsWrapperList")
	private List<BankDetailsWrapper> bankDetailsWrapperList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("cibilDetailsWrapperList")
	private List<CibilDetailsWrapper> cibilDetailsWrapperList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("loanDetails")
	private LoanDetails loanDetails;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("applicationDocumentsWrapperList")
	private List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("workflow")
	private WorkFlowDetails workflow;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("applnWfDefinitionList")
	private List<WorkflowDefinition> applnWfDefinitionList;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("applicationTimelineDtl")
	private List<ApplicationTimelineDtl> applicationTimelineDtl;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("bankingFacilityList")
	private List<BankingFacilities> bankingFacilityList;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("stageId")
	private String stageId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerType")
	private String customerType;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("editedFields")
	private List<String> editedFields;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("approvedDocs")
	private List<String> approvedDocs;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("queries")
	private List<String> queries;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("requestType")
	private String requestType;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("uploadDocumentRequestFields")
	private UploadDocumentRequestFields uploadDocumentRequestFields;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("payload")
	private Map<String, Object> payload;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("loanObligationsWrapper")
	private LoanObligationsWrapper loanObligationsWrapper;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("bcmpiOtherDetailsWrapper")
	private BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("bcmpiIncomeDetailsWrapper")
	private BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("updatedLoanAmount")
	private BigDecimal updatedLoanAmount;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("customerDtlsId")
	private BigDecimal customerDtlsId;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("udhyamRegId")
	private String udhyamRegId;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("udhyamStatus")
    private String udhyamStatus;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("remarks")
    private String remarks;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("dbKitResponse")
	private List<DBKITResponse> dbKitResponse;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("reuploadedDocs")
	private List<String> reuploadedDocs;

	@JsonProperty("caglOs")
	private String caglOs;

	@JsonProperty("currentStage")
	private String currentStage;

	@Getter
	@Setter
	public static class DBKITResponse{
		@JsonProperty("docType")
		private String docType;

		@JsonProperty("response")
		private String response;
	}

	
}
