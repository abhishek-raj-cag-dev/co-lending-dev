package com.iexceed.appzillonbanking.cob.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PinCodeDetailsResponse {

	private String pincode;
	
	private String state;
	
	private String city;
	
	private String area;
	
	private String district;
	
	private String country;
}
