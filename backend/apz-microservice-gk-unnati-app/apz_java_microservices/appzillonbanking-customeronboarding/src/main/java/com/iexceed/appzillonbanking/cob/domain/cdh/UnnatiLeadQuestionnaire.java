package com.iexceed.appzillonbanking.cob.domain.cdh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unnati_lead_questionnaire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnnatiLeadQuestionnaire {

	@Id
	@Column(name = "customer_id", nullable = false)
	private String customerId;

	@Column(name = "owns_business", length = 255)
	private String ownsBusiness;

	@Column(name = "nature_of_business", length = 255)
	private String natureOfBusiness;

	@Column(name = "business_age_years", length = 255)
	private String businessAgeYears;

	@Column(name = "monthly_business_net_income", length = 20)
	private String monthlyBusinessNetIncome;

}