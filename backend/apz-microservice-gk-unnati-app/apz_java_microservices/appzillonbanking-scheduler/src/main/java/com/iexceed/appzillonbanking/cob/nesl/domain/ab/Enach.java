package com.iexceed.appzillonbanking.cob.nesl.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;




@Entity
@Table(name = "TB_ABOB_ENACH")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Enach {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENACH_ID")
	private Long enachId; 

	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "RETRY_ATTEMPTS")
	private BigDecimal retryAttempts;

	@Column(name = "CUSTOMER_TYPE")
	private String customerType;

	@Column(name = "REQUEST_STRING")
	private String requestString;
	
	@Column(name = "ENACH_REQ_ID")
	private String enachReqId;

	@Column(name = "URL_STRING")
	private String urlString;

	@Column(name = "REQ_ERROR_CODE")
	private String reqErrorCode;
	
	@Column(name = "REQ_ERROR_DESC")
	private String reqErrorDesc;
	
	@CreationTimestamp
	@Column(name = "CREATE_TS")
	private LocalDateTime createTs;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "RES_CODE")
	private String resCode;
	
	@Column(name = "RES_DESC")
	private String resDesc;
	
	@Column(name = "PG_TRAN_ID")
	private String pgTranId;
	
	@UpdateTimestamp
	@Column(name = "UPDATED_TS")
	private LocalDateTime updatedTs;
	
	@Column(name = "ENACH_TYPE")
	private String enachType;

}
