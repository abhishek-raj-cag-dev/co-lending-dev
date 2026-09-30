package com.iexceed.appzillonbanking.cob.core.utils;

public enum CodeTypes {

	CASA("CASA", "CASA SELFONBOARDING"),   // NTB, ETB
	CASA_BO("CASA_BO", "CASA BACKOFFICE"),  //NTB, ETB
	
	DEPOSIT_NTB("DEPOSIT_NTB", "DEPOSIT SELFONBOARDING for NTB"),
	DEPOSIT_BO_NTB("DEPOSIT_BO_NTB", "DEPOSIT BACKOFFICE for NTB"),
	DEPOSIT_ETB("DEPOSIT_ETB", "DEPOSIT SELFONBOARDING for ETB"),
	DEPOSIT_BO_ETB("DEPOSIT_BO_ETB", "DEPOSIT BACKOFFICE for ETB"),
	
	CARD_NTB("CARD_NTB", "CARDS SELFONBOARDING for NTB"),
	CARD_BO_NTB("CARD_BO_NTB", "CARDS BACKOFFICE for NTB"),
	CARD_ETB("CARD_ETB", "CARDS SELFONBOARDING for ETB"),
	CARD_BO_ETB("CARD_BO_ETB", "CARDS BACKOFFICE for ETB"),
	
	LOAN_NTB("LOAN_NTB", "LOANS SELFONBOARDING for NTB"),
	LOAN_BO_NTB("LOAN_BO_NTB", "LOANS BACKOFFICE for NTB"),
	LOAN_ETB("LOAN_ETB", "LOANS SELFONBOARDING for ETB"),
	LOAN_BO_ETB("LOAN_BO_ETB", "LOANS BACKOFFICE for ETB"),
	
	CARD("CARD", "CARDS");
	
	
	
	private final String key;
	private final String value;
	
	CodeTypes(String key, String value) {
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