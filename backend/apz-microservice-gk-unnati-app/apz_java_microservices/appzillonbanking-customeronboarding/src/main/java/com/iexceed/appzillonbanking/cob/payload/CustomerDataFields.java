package com.iexceed.appzillonbanking.cob.payload;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.domain.ab.BankingFacilities;
import com.iexceed.appzillonbanking.cob.domain.ab.CRSDetails;
import com.iexceed.appzillonbanking.cob.domain.ab.FatcaDetails;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiCoApplicantDetails;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequestFields.DBKITResponse;
import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CustomerDataFields {
	
	@ApiModelProperty(required = true, position = 1, example = "APZRMB")
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
	@JsonProperty("existingLoanDetailsWrapperList")
	private  List<ExistingLoanDetailsWrapper> existingLoanDetailsWrapperList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("nomineeDetailsWrapperList")
	private List<NomineeDetailsWrapper> nomineeDetailsWrapperList; 
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicationDocumentsWrapperList")
	private List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList ;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("bankingFacilityList")
	private List<BankingFacilities> bankingFacilityList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("fatcaDetailsList")
	private List<FatcaDetails> fatcaDetailsList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("crsDetailsList")
	private List<CRSDetails> crsDetailsList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("workflow")
	private WorkFlowDetails workflow;
	
	@JsonProperty("applnWfDefinitionList")
	private List<WorkflowDefinition> applnWfDefinitionList;
	
	@JsonProperty("depositDetails")
	private DepositDtls depositDetails;

	@JsonProperty("loanDetails")
	private LoanDetails loanDetails;
	
	@JsonProperty("applicationTimelineDtl")
	private List<ApplicationTimelineDtl> applicationTimelineDtl;
	
	@JsonProperty("fundAccount")
	private FundAccountRequestFields fundAccount;

	@JsonProperty("rpcStats")
	private List<RPCStatDetails> rpcStatDetails;

	@JsonProperty("verifiedStage")
	private List<String> verifiedStage;

    @JsonProperty("cdhLeadDetails")
    private UnnatiIexceedCDHLead cdhLeadDetails;

    /*
     * @Added for Additional product
     */
    @JsonProperty("cdhCoAppLeadDetails")
    private UnnatiCoApplicantDetails cdhCoAppLeadDetails;

	@JsonProperty("bcmpiStats")
	private List<BCMPIStatDetails> bcmpiStatDetails;

	@JsonProperty("bcmpiVerifiedStage")
	private List<String> bcmpiVerifiedStage;

    @JsonProperty("lucDetails")
    private LUCEntity lucDetails;
    
	@JsonProperty("bcmpiIncomeDetails")
	private BCMPIIncomeDetails bcmpiIncomeDetails;

	@JsonProperty("bcmpiLoanObligations")
	private BCMPILoanObligations bcmpiLoanObligations;

	@JsonProperty("bcmpiOtherDetails")
	private BCMPIOtherDetails bcmpiOtherDetails;

	@JsonProperty("deviationRATracker")
	private List<DeviationRATracker> deviationRATrackerList;

	@JsonProperty("dbKitStatDetails")
	private List<BCMPIStatDetails> dbKitStatDetails;

	@JsonProperty("dbKitResponse")
	private List<DBKITResponse> dbKitResponse;
	
	@JsonProperty("approvedDocs")
	private List<String> approvedDocs;

	@JsonProperty("dbVerificationQueries")
	private List<String> dbVerificationQueries;

	@JsonProperty("reuploadedDocs")
	private List<String> reuploadedDocs;

	@JsonProperty("dbKitVerifiedStage")
	private List<String> dbKitVerifiedStage;
	
	@JsonProperty("enachDetails")
	List<Enach> enachDetails;

	@JsonProperty("udhyamDetails")
	List<Udhyam> udhyamDetails;

	@JsonProperty("documentRecordDetails")
	List<Documents> documentRecordDetails;

	@JsonProperty("documentList")
	List<JsonObject> documentList;

	@JsonProperty("sourcingQueryResponse")
	private SourcingResponseTracker sourcingQueryResponse;
	
	@JsonProperty("applicationWorkflow")
	private List<ApplicationWorkflow> ApplicationWorkflowList;

    @JsonProperty("cibilDetailsHistory")
    private CibilDetailsHistory cibilDetailsHistory;

    @JsonProperty("srcVerifiedStages")
    private List<String> srcVerifiedStages;

    @JsonProperty("amlQuestion")
    private AMLQuestionnaireDetails amlQuestion;

    @JsonProperty("srcStats")
    private List<SrcStatDetails> srcStatDetails;

@JsonProperty("existingGLLoanDetails")
    private List<ExistingGLLoanDetails> existingGLLoanDetails;

    @JsonProperty("bipData")
    private List<BotEnabled> bipData;


    @Data
    public static class BotEnabled {
        @JsonProperty("BotEnabled")
        private String BotEnabled;
     }

}
