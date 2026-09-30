package com.iexceed.appzillonbanking.cob.core.payload;

import java.util.List;

import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerIdentificationLoan {

	private String applicationId;
	
	private String relatedApplicationId;
	
	private int versionNum;
	
	private String loanDtlId;
	
	private List<String> loanDtlIds;

	private List<String> existisingLoanDtlId;
	
	private List<String> documentList;
	
	private List<String> cbtlList;
	
	private List<String> bankList;
	
	private List<String> insuranceList;
	
	private String accNumber;
	
	private String customerId;
	
	private String custDtlId;
	
	private List<String> addressList;
	
	private List<String> occupationList;
	
	private List<WorkflowDefinition> applnWfDefinitionList;
	
	private CustomerIdentificationCasa casaCustomerIdentification;
}
