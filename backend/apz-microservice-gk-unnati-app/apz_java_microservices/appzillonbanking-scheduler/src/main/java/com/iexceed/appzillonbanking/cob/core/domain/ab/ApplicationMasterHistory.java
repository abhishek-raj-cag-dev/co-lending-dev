package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "TB_ABOB_APPLICATION_MASTER_HIS")
@IdClass(ApplicationMasterId.class)
@Getter @Setter
public class ApplicationMasterHistory {

	@Id
	private String appId;
	
	@Id
	private String applicationId;
	
	@Id
	private int versionNum;
	
	@Column(name = "APPLICATION_DATE")
	private LocalDate applicationDate;	
	
	@Column(name = "CREATE_TS")
	private LocalDateTime createTs;
	
	@Column(name = "CREATED_BY")
	private  String createdBy;
	
	@Column(name = "APPLICATION_TYPE")
	private String applicationType;	
	
	@Column(name = "KYC_TYPE")
	private String kycType;	
	
	@Column(name = "APPLICATION_STATUS")
	private String applicationStatus;
	
	@Column(name = "CUSTOMER_ID")
	private BigDecimal customerId;
	
	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;
	
	@Column(name = "NATIONAL_ID")
	private String nationalId;	
	
	@Column(name = "PAN")
	private String pan;
	
	@Column(name = "PRODUCT_GROUP_CODE")
	private String productGroupCode;	
	
	@Column(name = "PRODUCT_CODE")
	private String productCode;
	
	@Column(name = "SEARCH_CODE1")
	private String searchCode1;               
	
	@Column(name = "SEARCH_CODE2")
	private String searchCode2;               
	
	@Column(name = "ASSIGNED_TO")
	private String assignedTo;
	
	@Column(name = "EMAILID")
	private String emailId;
	
	@Column(name = "CURRENT_STAGE")
	private String  currentStage;

	@Column(name = "DECLARATION_FLAG")
	private String declarationFlag;
	
	@Column(name = "ACCOUNT_NUMBER")
	private String accNumber;
	
	@Column(name = "MOBILE_VER_STATUS")
	private String mobileVerStatus;
	
	@Column(name = "EMAIL_VER_STATUS")
	private String emailVerStatus;
	
	@Column(name = "CURRENT_SCREEN_ID")
	private String  currentScreenId;

	@Column(name = "RELATED_APPLICATION_ID")
	private String relatedApplicationId;
	
	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}