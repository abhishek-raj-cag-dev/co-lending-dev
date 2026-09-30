package com.iexceed.appzillonbanking.cob.payload;

import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationCasa;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CheckAppCreateAppElements {
	
	private CustomerIdentificationCasa createAppRes;
	
	private String checkAppRes;
}