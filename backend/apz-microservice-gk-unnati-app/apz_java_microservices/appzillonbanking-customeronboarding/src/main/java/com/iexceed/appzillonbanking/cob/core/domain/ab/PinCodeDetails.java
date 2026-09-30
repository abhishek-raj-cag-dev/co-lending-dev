package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="TB_CGOB_PINCODE_MASTER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinCodeDetails {

	@Id
	@Column(name = "PIN_CODE")
	private int pinCode;
	
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "DISTRICT")
	private String district;
	
	@Column(name = "CITY")
	private String city;
	
	@Column(name = "AREA")
	private String area;
	
	@Column(name = "COUNTRY")
	private String country;
}
