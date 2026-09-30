package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.cards.domain.ab.CardDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cob.core.payload.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ApplyCreditCardRequestFields {

	@ApiModelProperty(required = true, example = "APZDEP")
	@JsonProperty("appId")
	private String appId;

	@ApiModelProperty(required = true, position = 2, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;

	@ApiModelProperty(required = true, position = 3, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
	
	@ApiModelProperty(required = true, position = 2, example = "Y")
	@JsonProperty("isExistingCustomer")
	private String isExistingCustomer;	
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicationMaster")
	private ApplicationMaster applicationMaster;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cardDetails")
	private CardDetails cardDetails;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerDetailsList")
	private List<CustomerDetails> customerDetailsList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("occupationDetailsWrapperList")
	private List<OccupationDetailsWrapper> occupationDetailsWrapperList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("addressDetailsWrapperList")
	private List<AddressDetailsWrapper> addressDetailsWrapperList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicationDocumentsWrapperList")
	private List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList ;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("workflow")
	private WorkFlowDetails workflow;
	
	@JsonProperty("applnWfDefinitionList")
	private List<WorkflowDefinition> applnWfDefinitionList;
	
	@JsonProperty("applicationTimelineDtl")
	private List<ApplicationTimelineDtl> applicationTimelineDtl;
	
}