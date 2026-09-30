package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_WAGE_BRANCH_MASTER")
@Getter
@Setter
@NoArgsConstructor
public class BranchWageDetails {

	@Id
	@Column(name = "BRANCH_ID")
	private String branchId;

	@Column(name = "STATE")
	private String state;

	@Column(name = "BRANCH_NAME")
	private String branchName;

	@Column(name = "DISTRICT_NAME")
	private String districtName;

	@Column(name = "MALE_WAGE")
	private String maleWage;

	@Column(name = "FEMALE_WAGE")
	private String femaleWage;

}
