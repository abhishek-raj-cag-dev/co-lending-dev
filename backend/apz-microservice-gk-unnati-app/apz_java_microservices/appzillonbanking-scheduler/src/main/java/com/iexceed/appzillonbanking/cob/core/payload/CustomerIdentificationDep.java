package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.util.List;

import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerIdentificationDep {

	private String applicationId;
	
	private List<String> addressList;
	
	private List<String> nomineeList;
	
	private int versionNum;
	
	private BigDecimal depositDtlId;
	
	private String accNumber;
	
	private String customerId;
	
	private List<WorkflowDefinition> applnWfDefinitionList;
	
	private String txnRefNo;
	
	private CustomerIdentificationCasa casaCustomerIdentification;
	
	private String fundAccRefNum;
}
