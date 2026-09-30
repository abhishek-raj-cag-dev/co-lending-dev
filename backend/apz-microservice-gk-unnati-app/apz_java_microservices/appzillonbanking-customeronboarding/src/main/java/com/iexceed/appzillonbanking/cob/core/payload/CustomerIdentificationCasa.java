package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.util.List;

import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class CustomerIdentificationCasa {

	private String applicationId;
	
	private String customerId;
	
	private String custDtlId;
	
	private List<String> addressList;
	
	private List<String> occupationList;
	
	private List<String> nomineeList;
	
	private List<String> documentList;
	
	private String accNumber;
	
	private String casaAccNumber;
	
	private String depAccNumber;
	
	private String loanAccNumber;
	
	private List<String> bankFacilityList;
	
	private List<String> fatcaDetailsList;
	
	private List<String> crsDetailsList;
	
	private int versionNum;
	
	private List<WorkflowDefinition> applnWfDefinitionList;
	
	private String fundAccRefNum;
	
	private BigDecimal appDocId;

	private String nextStage;
}
