package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "existing_GL_loan_details")
@Getter
@Setter
public class ExistingGLLoanDetails {

	@Override
	public String toString() {
		return "ExistingGLLoanDetails [customerId=" + customerId + ", existingLoanId=" + existingLoanId
				+ ", existingLoanName=" + existingLoanName + ", outstandingAmount=" + outstandingAmount + "]";
	}

	
	@JsonProperty("customerId")
	@Column(name = "Customer_id")
	private String customerId;

	@Id
	@JsonProperty("existingLoanId")
	@Column(name = "Existing_loan_Id")
	private String existingLoanId;

	@JsonProperty("existingLoanName")
	@Column(name = "Existing_Loan_name")
	private String existingLoanName;

	@JsonProperty("outstandingAmount")
	@Column(name = "Outstanding_amount")
	private BigDecimal outstandingAmount;
}