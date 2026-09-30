package com.iexceed.appzillonbanking.cob.core.utils;

public enum Products {
	
	CASA("CASA", "CASA Product"),
	DEPOSIT("DEPOSIT", "DEPOSIT Product"),
	CARDS("CARDS", "CARDS Product"),
	LOAN("LOAN", "LOAN Product");
	
	private final String key;
	private final String value;
	
	Products(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}