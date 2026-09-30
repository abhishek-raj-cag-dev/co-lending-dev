package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExistingLoanDetailsId implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "LOAN_DTLS_ID",nullable = false)
	private BigDecimal loanDtlsId;
	
	@Id
	@Column(name = "EXISTINGLOANS_DTLS_ID",nullable = false)
	private BigDecimal existingLoanId;
}
