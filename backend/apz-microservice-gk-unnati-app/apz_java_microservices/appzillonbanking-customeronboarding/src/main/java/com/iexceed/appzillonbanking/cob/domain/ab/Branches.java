package com.iexceed.appzillonbanking.cob.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_BRANCH_MASTER")
@Getter @Setter
public class Branches {

	@Id
	@Column(name = "BRANCH_CODE")
	private String branchCode;
	
	@Column(name = "BRANCH_NAME")
	private String branchName;
	
	@Column(name = "BRANCH_TYPE")
	private String branchType;
	
	@Column(name = "BRANCH_LOCATION")
	private String branchLocation;
	
	@Column(name = "BRANCH_DIVISION")
	private String branchDivision;
	
	@Column(name = "BRANCH_RO")
	private String branchRo;
	
	@Column(name = "BRANCH_HO")
	private String branchHo;
	
	@Column(name = "BRANCH_ADDRESS")
	private String branchAddress;
	              
	@Column(name = "STATE_CODE")
	private String stateCode;
	
	@Column(name = "DISTRICT_CODE")
	private String districtCode;
	
	@Column(name = "COUNTRY_CODE")
	private String countrycode;
	
	@Column(name = "PINCODE")
	private String pinCode;
	
	@Column(name = "CITY_CODE")
	private String cityCode;               
}