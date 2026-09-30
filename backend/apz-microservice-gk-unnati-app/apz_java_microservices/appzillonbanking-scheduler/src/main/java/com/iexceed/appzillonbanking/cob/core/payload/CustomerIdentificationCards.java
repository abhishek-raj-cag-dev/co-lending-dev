package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.util.List;

import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerIdentificationCards {

	private String applicationId;
	
	private List<String> documentList;
	
	private List<String> addressList;
	
	private String custDtlId;
	
	private String customerId;
	
	private List<String> occupationList;
	
	private List<String> bankFacilityList;
	
	private BigDecimal cardDtlId;
	
	private String accNumber;
	
	private int versionNum;	
	
	private List<String> eligibleCardsList;
	
	private String creditLimit;
	
	private String withdrawalLimit;
	
	private String currency;
	
	private List<WorkflowDefinition> applnWfDefinitionList;
}