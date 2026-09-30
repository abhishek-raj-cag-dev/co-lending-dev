package com.iexceed.appzillonbanking.cob.payload;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter

public class LITDomain {

	private String litCode;
	
	private String litDesc;

	@Override
	public String toString() {
		return litCode+"~"+litDesc+System.getProperty("line.separator");
	}
}