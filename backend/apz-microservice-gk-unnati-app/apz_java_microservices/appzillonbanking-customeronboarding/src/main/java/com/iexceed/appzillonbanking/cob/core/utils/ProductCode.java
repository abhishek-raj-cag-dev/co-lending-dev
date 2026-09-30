package com.iexceed.appzillonbanking.cob.core.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ankit.CAG
 */
@Getter
@RequiredArgsConstructor
public enum ProductCode {


	VISHESH("1003", "Vishesh", "Vishesh", Constants.PRIMARY),
	UNNATI("1009", "GL.GRM.UNNATI.LN", "Unnati", Constants.PRIMARY),
	FAMILY_WELFARE("1007", "UNN.FAMILY.WELFARE.LN","UnnatiFamily", Constants.ADDITIONAL),
	UNNATI_SUPPLEMENTARY("1005", "GL.GRM.UNNATI.SUPP.LN","UnnatiSuppli", Constants.ADDITIONAL),
	UNNATI_RESTART("1004", "GL.GRM.UNNATI.RESTR","UnnatiRestart", Constants.ADDITIONAL),
	UNNATI_EMERGENCY("1006", "UNNATHI.EMERGENCY.LN", "UnnatiEmergency", Constants.ADDITIONAL),
	UNNATI_RENEW("1002", "UNN_RWL","Renewal", Constants.PRIMARY),
	OPEN_MARKET("1001","GL.GRM.UNN.LN.OM","OpenMarket", Constants.PRIMARY);

	private final String unnatiCode;
	private final String cdhCode;
	private final String fetchType;
	private final String productCategory;

	public static String getUnnatiCodeByFetchType(String fetchType) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getFetchType().equalsIgnoreCase(fetchType.trim())) {
				return productCode.getUnnatiCode();
			}
		}
		return getCodeByProductCode(ProductCode.UNNATI, ProductType.UNNATI);
	}

	public static boolean isValidUnnatiCode(String unnatiCode) {
		if(null == unnatiCode || unnatiCode.trim().isEmpty()) {
			return false;
		}
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getUnnatiCode().equals(unnatiCode)) {
				return true;
			}
		}
		return false;
	}

	public static String getCdhCodeByFetchType(String fetchType) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getFetchType().equalsIgnoreCase(fetchType.trim())) {
				return productCode.getCdhCode();
			}
		}
		return getCodeByProductCode(ProductCode.UNNATI, ProductType.CDH);
	}

	public static Set<String> getAllUnnatiCodes() {
		Set<String> unnatiCodes = new HashSet<>();
		for (ProductCode productCode : ProductCode.values()) {
			unnatiCodes.add(productCode.getUnnatiCode());
		}
		return unnatiCodes;
	}

	public static Set<String> getAllCdhCodes() {
		Set<String> maitrCodes = new HashSet<>();
		for (ProductCode productCode : ProductCode.values()) {
			maitrCodes.add(productCode.getCdhCode());
		}
		return maitrCodes;
	}

	public static String getUnnatiCodeByCdhCode(String code) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getCdhCode().equals(code)) {
				return productCode.getUnnatiCode();
			}
		}
		return null;
	}

	public static boolean isAdditionalFetchType(String fetchType) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getFetchType().equalsIgnoreCase(fetchType)
					&& productCode.getProductCategory().equalsIgnoreCase(Constants.ADDITIONAL)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAdditionalProduct(String unnatiCode) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getUnnatiCode().equalsIgnoreCase(unnatiCode)
					&& productCode.getProductCategory().equalsIgnoreCase(Constants.ADDITIONAL)) {
				return true;
			}
		}
		return false;
	}

	public static ProductCode getProductCodeByCdhCode(String code) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getCdhCode().equals(code)) {
				return productCode;
			}
		}
		return null;
	}

	public static ProductCode getProductCodeByUnnatiCode(String code) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getUnnatiCode().equals(code)) {
				return productCode;
			}
		}
		return null;
	}

	public static String getCdhCodeByUnnatiCode(String code) {
		for (ProductCode productCode : ProductCode.values()) {
			if (productCode.getUnnatiCode().equals(code)) {
				return productCode.getCdhCode();
			}
		}
		return null;
	}

	public String getCode(ProductType type) {
		switch (type) {
		case UNNATI:
			return unnatiCode;
		case CDH:
			return cdhCode;
		default:
			return "";
		}
	}

	public static String getCodeByProductCode(ProductCode productCode, ProductType type) {
		switch (type) {
		case UNNATI:
			return productCode.getUnnatiCode();
		case CDH:
			return productCode.getCdhCode();
		default:
			return "";
		}
	}

	public enum ProductType {
		UNNATI, CDH
	}

}
