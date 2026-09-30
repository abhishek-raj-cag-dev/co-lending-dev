package com.iexceed.appzillonbanking.cob.admin.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
	
	
	private static final Logger logger = LogManager.getLogger(CommonUtils.class);

	public static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String DDMMYYFORMAT = "dd-MM-yyyy";
	
	public static final String DDMMYYSLASHFORMAT = "dd/MM/yyyy";
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String SUCCESS_UC = "SUCCESS";
	public static final String FAILURE = "failure";
	public static final String FAILURE_UC = "FAILURE";

	public static final String FAILURE_CODE = "1";
	public static final String SUCCESS_CODE = "0";
	public static final String EXCEPTION_OCCURED = "Exception Occured: ";
	
	
	public static final String TB_ABOB_COMMON_CODES = "tbAbmiCommonCodes";
	public static final String CODE = "code";
	public static final String CODE_TYPE = "codeType";
	public static final String CODE_DESC = "codeDesc";
	public static final String LANGUAGE = "language";
	public static final String ACCESS_TYPE = "accessType";
	

	/**
	 * Method to return timestamp based on the string date and date format.
	 * @param dateStr
	 * @param conversionFormat
	 * @return
	 */
	public Timestamp convertStringToDate(String dateStr, String conversionFormat) {
		Timestamp timestamp = null;
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(conversionFormat);
			date = sdf.parse(dateStr);
			timestamp = new Timestamp(date.getTime());
		} catch (ParseException e) {
			logger.error("Date Parse Exception:"+e.getStackTrace());
		}	
		return timestamp;
	}
}
