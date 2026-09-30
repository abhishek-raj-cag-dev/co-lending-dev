package com.iexceed.appzillonbanking.cob.core.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.JsonMappingException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BCMPIStatDetails;
import com.iexceed.appzillonbanking.cob.core.payload.ErrorParameterValues;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.RPCStatDetails;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.constant.Markup;
import reactor.core.publisher.Mono;

public class CommonUtils {
	private static final Logger logger = LogManager.getLogger(CommonUtils.class);
	protected static Map<String, String> urlMap = null;
	protected static Map<String, String> commonProperties = null;
	private static Map<String, String> externalProperties = null;
	protected static Map<String, List<String>> productTypeMap = null;
	private static Map<String, Map<String, ErrorParameterValues>> hostToApzErrorMap = null;
	protected static Map<String, List<String>> statusCodeMap = null;

	private static HashMap<String, String> authToken = new HashMap<>();
	private static SecureRandom random = new SecureRandom();

	private CommonUtils() {
	}

	public static void generateHeaderForNoResult(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.NORECORD.getErrorMessage());
		responseHeader.setResponseCode(Errors.NORECORD.getErrorCode());
	}

	public static void generateHeaderForGenericError(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		responseHeader.setResponseMessage(Errors.PROCESSINGREQUESTERROR.getErrorMessage());
	}

	public static void generateHeaderForSuccess(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
		responseHeader.setResponseMessage("");
	}

	public static String getUrlFromCommonUrlMap(String urlKey) {
		return urlMap.get(urlKey);
	}

	public static String getCommonProperties(String commonKey) {
		return commonProperties.get(commonKey);
	}

	public static String getExternalProperties(String commonKey) {
		return externalProperties.get(commonKey);
	}

	public static void initializeHostToApzErrorMap(Map<String, Map<String, ErrorParameterValues>> map) {
		hostToApzErrorMap = map;
	}

	// POC sample method for GENERIC non-hystrix implementation
	public static String restTemplateHelper(String externalServiceRequest, String url) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestBodyAndHeader = new HttpEntity<>(externalServiceRequest, headers);
		// externalServiceResponse validations not added as of now.
		return restTemplate.postForObject(url, requestBodyAndHeader, String.class);
	}

	// POC sample method for GENERIC non-hystrix implementation
	public static void initializeServiceUrls(Map<String, String> serviceUrlMap) {
		urlMap = serviceUrlMap;
	}

	public static void initializeProductTypeMap(Map<String, List<String>> productTypeMaps) {
		productTypeMap = productTypeMaps;
	}

	public static void initializeStatusCodeMap(Map<String, List<String>> statusCodeMaps) {
		statusCodeMap = statusCodeMaps;
	}

	public static void initializeCommonProperties(Map<String, String> commonValues) {
		commonProperties = commonValues;
	}

	public static void initializeExternalProperties(Map<String, String> commonValues) {
		externalProperties = commonValues;
	}

	public static Response setError(String errorMsg, String errorCode) {
		logger.debug("Start : setError");
		logger.debug("errorMsg = " + errorMsg);
		logger.debug("errorCode = " + errorCode);

		Response response = new Response();
		ResponseHeader externalServiceRespHeader = new ResponseHeader();
		ResponseBody externalServiceRespBody = new ResponseBody();

		externalServiceRespHeader.setResponseCode(errorCode);
		externalServiceRespHeader.setResponseMessage(errorMsg);
		externalServiceRespBody.setResponseObj("");

		response.setResponseHeader(externalServiceRespHeader);
		response.setResponseBody(externalServiceRespBody);

		logger.debug("End : setError");
		return response;
	}

	public static Response setSuccessResp(String apiResponse) {
		logger.debug("Start : setSuccessResp");
		logger.debug("apiResponse = " + apiResponse);

		Response response = new Response();
		ResponseHeader externalServiceRespHeader = new ResponseHeader();
		ResponseBody externalServiceRespBody = new ResponseBody();

		externalServiceRespHeader.setResponseCode("0");
		externalServiceRespHeader.setResponseMessage("");
		externalServiceRespBody.setResponseObj(apiResponse);

		response.setResponseHeader(externalServiceRespHeader);
		response.setResponseBody(externalServiceRespBody);

		logger.debug("End : setSuccessResp");
		return response;
	}

	public static Header obtainHeader(String appId, String interfaceId, String userId, String masterTxnRefNo,
			String deviceId) {

		Header header = new Header();
		header.setAppId(appId);
		header.setInterfaceId(interfaceId);

		header.setUserId(userId);
		header.setMasterTxnRefNo(masterTxnRefNo);
		header.setDeviceId(deviceId);
		return header;
	}

	public static String convertPojoToString(Object demoServiceRequest) throws JSONException, JsonProcessingException {
		ObjectMapper mapperObj = new ObjectMapper();
		mapperObj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JSONObject restApiRequest = new JSONObject(mapperObj.writeValueAsString(demoServiceRequest));
		return restApiRequest.toString();
	}

	public static Integer generateOneTimePassword(int min, int max) {
		// same as generate random number above.
		return random.nextInt((max - min) + 1) + min;
	}

	public static String generateRandomNumStr() {
		return System.currentTimeMillis() + "" + random.nextInt(9999);
	}

	public static BigDecimal generateRandomNum() {
		return new BigDecimal(
				(System.currentTimeMillis() + "" + (new SecureRandom().nextInt(9999) + 1000))
						.substring(5, 17));
		// taking substring to avoid rounding off issue at UI while parsing.
	}

	public static Response formFailResponse(String responseObj, String resCode) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.PROCESSINGREQUESTERROR.getErrorMessage());
		responseHeader.setResponseCode(resCode);
		responseBody.setResponseObj(responseObj);
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return response;
	}

	public static Mono<Response> formFailResponseMono(String responseObj, String resCode) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.PROCESSINGREQUESTERROR.getErrorMessage());
		responseHeader.setResponseCode(resCode);
		responseBody.setResponseObj(responseObj);
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "readPropertyFileFallback")
	public static Properties readPropertyFile() throws IOException {
		Properties prop = new Properties();
		try (FileReader fileReader = new FileReader(
				getCommonProperties(SpringCloudProperties.PROP_FILE_PATH.getKey()))) {
			prop.load(fileReader);
		} catch (IOException e) {
			logger.error("Exception in readPropertyFile ", e);
			throw e;
		}
		return prop;
	}
    public static String stripTrailingDots(String s) {
        int end = s.length() - 1;
        while (end >= 0 && s.charAt(end) == '.') {
            end--;
        }
        return s.substring(0, end + 1);
    }

    public static String normalizeInput(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        int length = input.length();
        boolean skipSpace = false;

        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            if (c == ':' || c == ',') {
                // Remove spaces before the symbol
                while (result.length() > 0 && result.charAt(result.length() - 1) == ' ') {
                    result.deleteCharAt(result.length() - 1);
                }

                result.append(c);
                skipSpace = true;  // Avoid adding spaces immediately after the symbol
            } else if (Character.isWhitespace(c)) {
                if (!skipSpace) {
                    result.append(' ');
                }
            } else {
                result.append(c);
                skipSpace = false;
            }
        }

        return result.toString().trim();
    }



	public static boolean isNullOrEmpty(String str) {
		return (str == null || "".equalsIgnoreCase(str));
	}

	public static boolean validateEmailId(String emailId) {
		if (CommonUtils.isNullOrEmpty(emailId)) {
			return true;
		} else {
			Pattern pattern = Pattern.compile(Constants.REGEX_EMAIL);
			Matcher matcher = pattern.matcher(emailId);
			return matcher.matches();
		}
	}

	public static boolean validateMobile(String mobileNum) {
		if (CommonUtils.isNullOrEmpty(mobileNum)) {
			return true;
		} else {
			String regex;
			String[] splitMobileNum = mobileNum.split(" ");
			if (splitMobileNum.length > 1 && !CommonUtils.isNullOrEmpty(splitMobileNum[1])) { // with country code
				regex = Constants.REGEX_PH_WITH_COUNTRY;
			} else {
				regex = Constants.REGEX_PH_WITHOUT_COUNTRY;
			}
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(mobileNum);
			return matcher.matches();
		}
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "verifyNationalIdFallback")
	public static Response verifyNationalId(String nationalIdName, String nationalIdValue) {
		Response response = new Response();
		String responseObj = "";
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		if (Constants.NATIONALIDPAN.equalsIgnoreCase(nationalIdName)) {
			if (verifyPan(nationalIdValue.toUpperCase())) {
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseObj = "Valid PAN";
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_PAN.getKey());
				responseObj = ResponseCodes.INVALID_PAN.getValue();
			}
		}
		if (Constants.NATIONALIDUPI.equalsIgnoreCase(nationalIdName)) {
			if (verifyUpi(nationalIdValue)) {
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseObj = "Valid UPI ID";
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALIDUPI.getKey());
				responseObj = ResponseCodes.INVALIDUPI.getValue();
			}
		}
		response.setResponseHeader(responseHeader);
		responseBody.setResponseObj(responseObj);
		response.setResponseBody(responseBody);
		return response;
	}

	private static boolean verifyPan(String pan) {
		Pattern p = Pattern.compile(Constants.REGEX_PAN);
		if (CommonUtils.isNullOrEmpty(pan)) {
			return false;
		}
		Matcher m = p.matcher(pan);
		return m.matches();
	}

	private static boolean verifyUpi(String upi) {
		Pattern p = Pattern.compile(Constants.REGEX_UPI);
		if (CommonUtils.isNullOrEmpty(upi)) {
			return false;
		}
		Matcher m = p.matcher(upi);
		return m.matches();
	}

	public static String formatAmount(double amount) {
		logger.debug("Start : formatAmount with request = " + amount);
		String forattedAmount;

		try {
			logger.debug("formatting the decimals to 2 digits");
			DecimalFormat df = new DecimalFormat("0.00");
			forattedAmount = df.format(amount);

			String decimalAmt = forattedAmount.substring(forattedAmount.length() - 3);
			logger.debug("decimalAmt = " + decimalAmt);

			logger.debug("formatting the entire amount");
			forattedAmount = addCommasToNumericString(forattedAmount.substring(0, forattedAmount.length() - 3))
					+ decimalAmt;
		}

		catch (Exception e) {
			logger.debug("Error while formatting the amount, error msg = " + e);
			forattedAmount = String.valueOf(amount);
		}

		logger.debug("End : formatAmount with response = " + forattedAmount);
		return forattedAmount;
	}

	public static String unformatAmount(String amount) {
		logger.debug("Start : formatAmount with request = " + amount);
		String unformattedAmount = "";

		try {
			unformattedAmount = amount.replaceAll(",", "").split("\\.")[0];
		} catch (Exception e) {
			logger.debug("Error while unformatting the amount, error msg = " + e);
		}
		logger.debug("End : formatAmount with response = " + unformattedAmount);
		return unformattedAmount;
	}

	public static String getHighmarkValue(String val) {
		logger.debug("Start : getHighmarkValue with request = " + val);
		String highmarkValue = "";

		try {
			highmarkValue = (((val).split("\\|")[0]).split(":")[1]).trim();
		} catch (Exception e) {
			logger.debug("Error while getHighmarkValue, error msg = " + e);
		}
		logger.debug("End : getHighmarkValue with response = " + highmarkValue);
		return highmarkValue;
	}

	private static String addCommasToNumericString(String digits) {
		logger.debug("Start : AddCommasToNumericString with request = " + digits);
		StringBuilder result = new StringBuilder();

		for (int i = 1; i <= digits.length(); ++i) {
			char ch = digits.charAt(digits.length() - i);
			if (i % 3 == 1 && i > 1) {
				result.insert(0, ",");
			}
			result.insert(0, ch);
		}

		logger.debug("End : AddCommasToNumericString with response = " + result);
		return result.toString();
	}

	public static HashMap<String, String> getAuthToken() {
		return authToken;
	}

	public static void appendAuthToken(String key, String value) {
		authToken.put(key, value);
	}

	public static void generateHeaderForTokenError(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode("1");
		responseHeader.setErrorCode(Errors.AUTHTOKENFAILURE.getErrorCode());
		responseHeader.setResponseMessage(Errors.AUTHTOKENFAILURE.getErrorMessage());
	}

	// ALL FALLBACK METHODS

	private boolean verifyNationalIdFallback(String nationalIdName, String nationalIdValue, Exception e) {
		logger.error("verifyNationalIdFallback error : ", e);
		return false;
	}

	public static String URLtoBase64Str(String fileURL) {
		logger.debug("fileURL : " + fileURL);
		String finalURL = StringEscapeUtils.unescapeJava(fileURL.replaceAll("~", "&"));
		logger.debug("finalURL : " + finalURL);
		String base64 = "";
		try {
			Properties prop = CommonUtils.readPropertyFile();
			URL url = new URL(finalURL);
			// InputStream is = url.openStream();
			URLConnection con = url.openConnection();
			con.setConnectTimeout(
					Integer.parseInt(prop.getProperty(CobFlagsProperties.CB_REPORT_CONNECT_TIMEOUT.getKey())) * 1000);
			con.setReadTimeout(
					Integer.parseInt(prop.getProperty(CobFlagsProperties.CB_REPORT_READ_TIMEOUT.getKey())) * 1000);
			InputStream is = con.getInputStream();
			byte[] bytes = IOUtils.toByteArray(is);
			base64 = Base64.getEncoder().encodeToString(bytes);
			logger.debug(base64);
		} catch (MalformedURLException e) {
			
			logger.debug("finalURL MalformedURLException : " + e.getMessage());
			
		} catch (IOException e) {
			
			logger.debug("finalURL IOException : " + e.getMessage());
			
		}
		return base64;
	}

	public static String mergePDFFiles(String docName, List<ApplicationDocumentsPayload> payloads, String filePath) {
		String PDFBase64Str = "";
		PDDocument doc = new PDDocument();
		List<ApplicationDocumentsPayload> sortedList = new ArrayList<>();
		String pdfFilePath = filePath + "" + docName + ".pdf";
		logger.debug("pdfFilePath : " + pdfFilePath);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			logger.debug("payloads.size() : " + payloads.size());
			payloads.stream().forEach(System.out::println);
			sortedList = payloads;
			if (payloads.size() >= 2) {
				List<ApplicationDocumentsPayload> filteredList = payloads.stream()
						.filter(p -> (p.getDocLevel() != null)).collect(Collectors.toList());
				logger.debug("filteredList.size() : " + filteredList.size());
				if (filteredList.size() == payloads.size()) {
					sortedList = payloads.stream()
							.sorted(Comparator.comparing(ApplicationDocumentsPayload::getDocLevel))
							.collect(Collectors.toList());
				}
			}
			sortedList.stream().forEach(System.out::println);
			for (ApplicationDocumentsPayload payload : sortedList) {
				logger.debug("payload.getDocumentFormat() : " + payload.getDocumentFileName() + " : "
						+ payload.getDocLevel());
				addImageAsNewPage(doc, filePath + "" + payload.getDocumentFileName());
			}
			if (sortedList.size() < 2) {
				PDPage blankPage = new PDPage();
				doc.addPage(blankPage);
			}
			
			 // no need to name intermediate resources if you don't want to
			doc.save(baos);
			PDFBase64Str = Base64.getEncoder().encodeToString(baos.toByteArray());
		} catch (IOException e) {
				logger.error("Error to merge files. Error: " + e.getMessage(), e);
				throw new RuntimeException("Error to merge files. Error: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Error to merge files. Error: " + e.getMessage(), e);
			throw new RuntimeException("Error to merge files. Error: " + e.getMessage(), e);
		} finally {
			try {
				doc.close();
			} catch (IOException e) {
				logger.error("Error to close PDF document. Error: " + e.getMessage(), e);
			}
		}
		return PDFBase64Str;
	}

	public static void deleteFile(String lfilename) {
		try {
			Files.deleteIfExists(Paths.get(lfilename));
		} catch (Exception e) {
		}
	}

	public static void deleteFilesInFolder(String folderPath) {
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
					if (file.isFile() && (!file.delete())) {
                            // Log this instead of swallowing it silently
						logger.error("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        } else {
        	logger.error("Invalid folder path: " + folderPath);
        }
    }

	private static void addImageAsNewPage(PDDocument doc, String imagePath) {
		try {
			logger.info("imagePath : " + imagePath);

			BufferedImage awtImage = ImageIO.read(new File(imagePath));
			if (awtImage == null) {
				logger.error("Could not read image: " + imagePath);
				throw new RuntimeException("Could not read image: " + imagePath);
			}

            // Ensure consistent color model
			BufferedImage rgbImage = new BufferedImage(
					awtImage.getWidth(),
					awtImage.getHeight(),
					BufferedImage.TYPE_INT_RGB
			);
			rgbImage.createGraphics().drawImage(awtImage, 0, 0, null);

			PDImageXObject image = PDImageXObject.createFromByteArray(
					doc,
					bufferedImageToByteArray(rgbImage, "jpg"),
					imagePath
			);

			int originalWidth = image.getWidth();
			int originalHeight = image.getHeight();

// Choose A4 portrait or landscape
            PDRectangle pageSize = (originalWidth > originalHeight)
                    ? new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth())
                    : PDRectangle.A4;

			float pageWidth = pageSize.getWidth();
			float pageHeight = pageSize.getHeight();

// Scale to fit page, preserving aspect ratio
			float ratio = Math.min(pageWidth / originalWidth, pageHeight / originalHeight);

			float scaledWidth = originalWidth * ratio;
			float scaledHeight = originalHeight * ratio;

// Center the image
			float x = (pageWidth - scaledWidth) / 2;
			float y = (pageHeight - scaledHeight) / 2;

// Create page and draw image
			PDPage page = new PDPage(pageSize);
			doc.addPage(page);

			try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
				contents.drawImage(image, x, y, scaledWidth, scaledHeight);
			}


		} catch (IOException e) {
			logger.error("Exception while adding image :: imagePath " + imagePath + " -- " + e.getMessage(), e);
			throw new RuntimeException("Exception while adding image :: imagePath " + imagePath + " -- " + e.getMessage(), e);
		}
	}

	private static byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, format, baos);
		return baos.toByteArray();
	}

	public static int getDateDiff(LocalDate fromDateLD, LocalDate toDateLD) {
		ZoneId zoneId = ZoneId.systemDefault();
		Date toDate = Date.from(toDateLD.atStartOfDay(zoneId).toInstant());
		Date fromDate = Date.from(fromDateLD.atStartOfDay(zoneId).toInstant());
		long difference_In_Time = toDate.getTime() - fromDate.getTime();
		logger.debug("difference_In_Time : " + difference_In_Time);
		// LOG.debug("Difference " + "between two dates is: ");
		int difference_In_Days = (int) TimeUnit.MILLISECONDS.toDays(difference_In_Time);
		logger.debug("difference_In_Days : " + difference_In_Days);
		return difference_In_Days;
	}

	public static String formatCustomDate(String date, String toDate) {
		String fromFormat = "";
		if (null != date && !"".equals(date)) {
			if (date.matches("([0-9]{1}([0-9]{1})?)-([0-9]{1}([0-9]{1})?)-([0-9]{4})")) {
				fromFormat = "dd-MM-yyyy";
			} else if (date.matches("([0-9]{1}([0-9]{1})?)/([0-9]{1}([0-9]{1})?)/([0-9]{4})")) {
				fromFormat = Constants.DATEFORMAT2;
			} else if (date.matches("([0-9]{4})-([0-9]{1}([0-9]{1})?)-([0-9]{1}([0-9]{1})?)")) {
				fromFormat = Constants.DATEFORMAT1;
			} else if (date.matches("([0-9]{4})/([0-9]{1}([0-9]{1})?)/([0-9]{1}([0-9]{1})?)")) {
				fromFormat = "yyyy/MM/dd";
			}
			SimpleDateFormat from = new SimpleDateFormat(fromFormat);
			SimpleDateFormat toForm = new SimpleDateFormat(toDate);
			logger.debug("DOB ----- FROM " + date);
			try {
				date = toForm.format(from.parse(date));
				logger.debug(" ########## DOB ----- TO " + date);
			} catch (ParseException e) {

			}
		} else {
			date = null;
		}
		return date;
	}
	
	public static String getDefaultValueIfObjNull(Object obj) {
		String value = "";
		if (null != obj) {
			value = String.valueOf(obj).trim();
		}
		return value;
	}

	public static String formattime(LocalDateTime dt) {
		
		if (null != dt) {
			String dtStr = dt.toString().replaceAll("\\..*", "").replaceAll("T", " ");
			return LocalDateTime.parse(dtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
					.format(Constants.FORMATTER);
		} else {
			return null;
		}
	}

	public static String formatdate(LocalDate dt) {
		
		if (null != dt) {
			return LocalDate.parse(dt.toString(), DateTimeFormatter.ofPattern(Constants.DATEFORMAT1))
					.format(Constants.DATEFORMATTER);
		} else {
			return null;
		}
	}

		/**
	 * Method to return timestamp based on the string date and date format.
	 * 
	 * @param dateStr
	 * @param conversionFormat
	 * @return
	 */
	public static Timestamp convertStringToDateTime(String dateStr, String conversionFormat) {
		Timestamp timestamp = null;
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(conversionFormat);
			date = sdf.parse(dateStr);
			timestamp = new Timestamp(date.getTime());
		} catch (ParseException e) {
			logger.error("Date Parse Exception:" + e.getStackTrace());
		}
		return timestamp;
	}

	public static Date convertStringToDate(String dateStr, String conversionFormat) {

		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(conversionFormat);
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.error("Date Parse Exception:" + e.getStackTrace());
		}
		return date;

	}

	public static LocalDate convertStringToLocalDate(String dateStr, String conversionFormat) {
		LocalDate localDate = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(conversionFormat);
			localDate = LocalDate.parse(dateStr, formatter);
		} catch (DateTimeParseException e) {
			logger.error("Date Parse Exception: " + e.getMessage(), e);
		}
		return localDate;
	}
	
	public static String newgenDocUpload(String fileContent, String type, String applicationId, String docName) {
		Properties prop;
		try {
			prop = CommonUtils.readPropertyFile();

			if (null == type)
				type = "pdf";
			logger.debug("applicationId : " + applicationId);
			String folderPath =  prop.getProperty(CobFlagsProperties.NEWGEN_FILE_UPLOAD.getKey()) + "/LOAN/"
					+ applicationId + "/";
			// Ensure directory exists
		    File directory = new File(folderPath);
		    if (!directory.exists()) {
		        boolean isCreated = directory.mkdirs();
		        if (!isCreated) {
		            throw new IOException("Failed to create directory: " + folderPath);
		        }
		    }
			// Path to the folder where you want to save the PDF file
			String filePath = folderPath + docName + "." + type;
			logger.debug("filePath : " + filePath);
			// Decode the Base64 string
			byte[] decodedBytes = Base64.getDecoder().decode(fileContent);

			// Write the decoded bytes to a PDF file
			try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
				fileOutputStream.write(decodedBytes);				
				logger.debug("PDF saved successfully to " + filePath);
				return fileContent;
			} catch (IOException e) {
				logger.debug("Error writing PDF file: " + e.getMessage());
			}
		} catch (IOException e1) {
			
			
		}
		return StringUtils.EMPTY;
	}
	
	public static boolean isPdf(byte[] byteStream) {
        // PDF files start with "%PDF-"
		return byteStream.length >= 5 && byteStream[0] == 0x25 && byteStream[1] == 0x50 && byteStream[2] == 0x44
				&& byteStream[3] == 0x46;
    }

    // Method to check if the byte stream is JSON (starts with '{' or '[')
    public static boolean isJson(byte[] byteStream) {
        // JSON files often start with '{' or '['
        return byteStream.length > 0 && (byteStream[0] == 0x7B || byteStream[0] == 0x5B); // 0x7B = '{', 0x5B = '['
    }

 // Method to convert byte stream to POJO (Message)
    public static JSONObject convertByteStreamToJson(byte[] byteStream) throws IOException {
    	ObjectMapper objectMapper = new ObjectMapper();
        
        // Parse byte stream into a JsonNode (tree model of the JSON)
        return new JSONObject(objectMapper.readTree(byteStream).toString());
    }
    
	public static String getContentFromFile(String path) throws IOException {

		String jsonStr = "";
		File file = new File(path);
		try (BufferedReader b = new BufferedReader(new FileReader(file));) {
		String st;
		StringBuilder bld = new StringBuilder();
		while ((st = b.readLine()) != null) {
			bld.append(st);
		}
		jsonStr = bld.toString();
		} catch (Exception e) {
			
			logger.error("Exception in getContentFromFile " + path + " - ", e.getMessage());
		}
		return jsonStr;

	}

	public static String fetchTAT(String timestampStr) {
		// Parse the timestamp string into LocalDateTime
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		LocalDateTime givenTimestamp = LocalDateTime.parse(timestampStr, formatter);

		// Get the current timestamp
		LocalDateTime currentTimestamp = LocalDateTime.now();

		// Calculate the duration between the two timestamps
		Duration duration = Duration.between(givenTimestamp, currentTimestamp);

		// Output the difference in terms of days, hours, minutes, and seconds
		long days = duration.toDays();
		long hours = duration.toHours() % 24;
		long minutes = duration.toMinutes() % 60;
		long seconds = duration.getSeconds() % 60;
		logger.debug("Days: " + days);
		logger.debug("Hours: " + hours);
		logger.debug("Minutes: " + minutes);
		logger.debug("Seconds: " + seconds);
		return seconds + "S|" + minutes + "M|" + hours + "H|" + days + "D";

	}

	public static List<String> buildStatusList(AppStatus... statuses) {
		List<String> statusList = new ArrayList<>();
		for (AppStatus status : statuses) {
			if (status != null && status.getValue() != null) {
				statusList.add(status.getValue());
			}
		}
		return statusList;
	}

	public static boolean isStatusIn(String status, List<String> Appstatuses) {
		if (CommonUtils.isNullOrEmpty(status)) {
			return false;
		}
		for (String s : Appstatuses) {
			if (s.equalsIgnoreCase(status)) {
				return true;
			}
		}
		return false;
	}


	public static Response buildErrorResponse(String message) {
		Response response = new Response();
		ResponseHeader header = new ResponseHeader();
		ResponseBody body = new ResponseBody();
		header.setResponseCode(ResponseCodes.FAILURE.getKey());
		body.setResponseObj(message);
		response.setResponseHeader(header);
		response.setResponseBody(body);
		return response;
	}

	public static void buildErrorResponse(String message, Response response) {
		ResponseHeader header = new ResponseHeader();
		ResponseBody body = new ResponseBody();
		header.setResponseCode(ResponseCodes.FAILURE.getKey());
		body.setResponseObj(message);
		response.setResponseHeader(header);
		response.setResponseBody(body);
	}

	public static boolean isApplicationLocked(FetchDeleteUserRequest fetchUserDetailsRequest,
			ApplicationMaster applicationMasterData) {
		boolean locked = false;
		logger.debug(
				"isApplicationLocked method fetchUserDetailsRequest.toString(): " + fetchUserDetailsRequest.toString());
		logger.debug(
				"isApplicationLocked method applicationMasterData.toString(): " + applicationMasterData.toString());
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		
		if (!fetchUserDetailsRequest.getRequestObj().getUserId().equalsIgnoreCase(applicationMasterData.getUpdatedBy())
				&& null != applicationMasterData.getLockTs()) {
			LocalDateTime currentTimestamp = LocalDateTime.now();
			Duration duration = Duration.between(applicationMasterData.getLockTs(), currentTimestamp);
			logger.debug("isApplicationLocked method duration.toHours(): " + duration.toHours());
			if (duration.toHours() < Integer
					.parseInt(prop.getProperty(CobFlagsProperties.FETCH_APPLICATION_EXPIRY_DURATION.getKey()))) {
				locked = true;
			}
		}
		} catch (IOException e) {
			logger.error("Error while reading property file in deleteNominee ", e);
		}
		return locked;
	}

	public static List<RPCStatDetails> parseRPCStageVerificationData(String editedFieldsInput, String queryInput) {
	    logger.debug("Received editedFieldsInput: " + editedFieldsInput);
	    logger.debug("Received queryInput: " + queryInput);

	    // Initialize maps to group data by stageID and custType
	    Map<String, Map<String, List<String>>> stageData = new HashMap<>();
	    List<RPCStatDetails> rpcStats = new ArrayList<>();

	    // Split both inputs by pipe to get individual field entries
	    String[] editedFields = StringUtils.isEmpty(editedFieldsInput) ? new String[0] : editedFieldsInput.split("\\|");
	    String[] queries = StringUtils.isEmpty(queryInput) ? new String[0] : queryInput.split("\\|");

	    logger.debug("Parsed editedFields: " + editedFields);
	    logger.debug("Parsed queries: " + queries);
	    
	    // Process editedFields and group them
	    for (String field : editedFields) {
	        String[] parts = field.split("_");
	        if (parts.length != 3) {
	            logger.warn("Skipping invalid edited field: " + field);
	            continue; // Invalid format, skip
	        }

	        int stageID = Integer.parseInt(parts[0]);
	        String custType = parts[1];
	        String fieldName = parts[2];

	        String stageKey = stageID + "_" + custType;
	        stageData.putIfAbsent(stageKey, new HashMap<>());
	        stageData.get(stageKey).computeIfAbsent(Constants.EDITEDFIELDS, k -> new ArrayList<>()).add(fieldName);
	        
	        logger.debug("Processed edited field - stageID: " + stageID + ", custType: " + custType + ", fieldName: " + fieldName);
	    }

	    // Process queries and group them by stageID and custType
	    for (String query : queries) {
	        String[] parts = query.split("_");
	        if (parts.length != 3) {
	            logger.warn("Skipping invalid query: " + query);
	            continue; // Invalid format, skip
	        }

	        int stageID = Integer.parseInt(parts[0]);
	        String custType = parts[1];
	        String queryName = parts[2];

	        String stageKey = stageID + "_" + custType;
	        stageData.putIfAbsent(stageKey, new HashMap<>());
	        stageData.get(stageKey).computeIfAbsent(Constants.QUERY, k -> new ArrayList<>()).add(queryName);

	        logger.debug("Processed query - stageID: " + stageID + ", custType: " + custType + ", queryName: " + queryName);
	    }

	    // Create the final JSON structure
	    for (String key : stageData.keySet()) {
	        RPCStatDetails rpcStat = new RPCStatDetails();
	        String[] keys = key.split("_");
	        int stageID = Integer.parseInt(keys[0]);
	        String custType = keys[1];

	        Map<String, List<String>> data = stageData.get(key);

	        rpcStat.setStageID(stageID);
	        rpcStat.setCustType(custType);
	        rpcStat.setEditedFields(data.get(Constants.EDITEDFIELDS) != null ? data.get(Constants.EDITEDFIELDS) : new ArrayList<>());
	        rpcStat.setQuery(data.get(Constants.QUERY) != null ? data.get(Constants.QUERY) : new ArrayList<>());

	        rpcStats.add(rpcStat);

	        logger.debug("Created RPCStatDetails - stageID: " + stageID + ", custType: " + custType 
	                     + ", editedFields: " + rpcStat.getEditedFields() 
	                     + ", query: " + rpcStat.getQuery());
	    }

	    return rpcStats;
	}

	public static boolean verifyQuery(String query) {
		Pattern p = Pattern.compile(Constants.REGEX_QUERIES);
		if (CommonUtils.isNullOrEmpty(query)) {
			return false;
		}
		Matcher m = p.matcher(query);
		return m.find();
	}

	 public static List<BCMPIStatDetails> parseBCMPIStageVerificationData(String editedFieldsInput, String queryInput){
        logger.debug("Entry parseBCMPIStageVerificationData method");
        logger.debug("editedFields: {}", editedFieldsInput);
        logger.debug("queries: {}", queryInput);
        Map<String, Map<String,List<String>>> stageData = new HashMap<>();
        List<BCMPIStatDetails> bcmpiStatDetails = new ArrayList<>();

       String[] editedFields = StringUtils.isEmpty(editedFieldsInput) ? new String[0] : editedFieldsInput.split("\\|");
	    String[] queries = StringUtils.isEmpty(queryInput) ? new String[0] : queryInput.split("\\|");

	    logger.debug("Parsed editedFields: " + editedFields);
	    logger.debug("Parsed queries: " + queries);

         for (String field : editedFields) {
	        String[] parts = field.split("_");
	        if (parts.length != 3) {
	            logger.warn("Skipping invalid edited field: " + field);
	            continue; // Invalid format, skip
	        }

	        int stageID = Integer.parseInt(parts[0]);
	        String custType = parts[1];
	        String fieldName = parts[2];

	        String stageKey = stageID + "_" + custType;
	        stageData.putIfAbsent(stageKey, new HashMap<>());
	        stageData.get(stageKey).computeIfAbsent(Constants.EDITEDFIELDS, k -> new ArrayList<>()).add(fieldName);
	        
	        logger.debug("Processed edited field - stageID: " + stageID + ", custType: " + custType + ", fieldName: " + fieldName);
	    }

        for (String query : queries) {
			String[] parts = query.split("_");
		
			if (parts.length < 3 || parts.length > 4) {
				logger.warn("Skipping invalid query: " + query);
				continue; // Invalid format, skip
			}
		
			int stageID;
			try {
				stageID = Integer.parseInt(parts[0]);
			} catch (NumberFormatException e) {
				logger.warn("Skipping query with invalid stage ID: " + query);
				continue;
			}
		
			String queryCode;
			String timeStamp;
			if (parts.length == 3) {
				queryCode = parts[1];
				timeStamp = parts[2];
			} else {
				queryCode = parts[1] + "_" + parts[2];
				timeStamp = parts[3];
			}
		
			String stageKey = String.valueOf(stageID);
			stageData.putIfAbsent(stageKey, new HashMap<>());
			stageData.get(stageKey).computeIfAbsent(Constants.QUERY, k -> new ArrayList<>()).add(queryCode);
			stageData.get(stageKey).computeIfAbsent(Constants.TIMESTAMP, t -> new ArrayList<>()).add(timeStamp);
		
			logger.debug("Processed query - stageID: " + stageID + ", queryCode: " + queryCode +" , timeStamp: "+timeStamp);
		}
		

        for (String key : stageData.keySet()){
            BCMPIStatDetails bcmpiStat = new BCMPIStatDetails();
            String[] keys = key.split("_");
            int stageId = Integer.parseInt(keys[0]);

            Map<String, List<String>> data = stageData.get(key);

            bcmpiStat.setStageID(stageId);
            bcmpiStat.setEditedFields(data.get(Constants.EDITEDFIELDS)!= null ? data.get(Constants.EDITEDFIELDS) : new ArrayList<>());
            bcmpiStat.setQuery(data.get(Constants.QUERY) != null ? data.get(Constants.QUERY) : new ArrayList<>());
			bcmpiStat.setTimeStamp(data.get(Constants.TIMESTAMP) != null 
				? data.get(Constants.TIMESTAMP).stream()
					.map(timestamp -> LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
					.collect(Collectors.toList()) 
				: new ArrayList<>());
            bcmpiStatDetails.add(bcmpiStat);

            logger.debug("Created bcmpiStatDetails - stageID:" +stageId+", editedFields: " + bcmpiStat.getEditedFields() + ", query: " + bcmpiStat.getQuery()+" , timeStamp: "+bcmpiStat.getTimeStamp());
        }
        return bcmpiStatDetails;
    }
	 
	public static String getCurDateMinusOne(LocalDate cbDateDb) {
		LocalDate cbDate = cbDateDb.minusDays(1);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
		String formattedDate = cbDate.format(formatter);
		return formattedDate;

    }

	public static String getCurDate() {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
		String formattedDate = currentDate.format(formatter);
		return formattedDate;
	}

	public static String getCurDatePlusone() {
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
		String formattedDate = tomorrow.format(formatter);
		return formattedDate;

	}
	
	public static String getDatePlus30Years() {
	    LocalDate futureDate = LocalDate.now().plusYears(30);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
	    return futureDate.format(formatter);
	}
    
    public static String formatIndianCurrency1(String amountStr) {
        try {
        	if(StringUtils.isNotEmpty(amountStr)){
        			//amountStr.isEmpty()) {
        		double amount = Double.parseDouble(amountStr);
                String pattern = "##,##,###.##";

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');

                DecimalFormat formatter = new DecimalFormat(pattern, symbols);
                formatter.setGroupingUsed(true);

                String formatted = formatter.format(amount);
                return "Rs." + formatted + "/-";
        	}else {
        		  return "";
        	}   

        } catch (NumberFormatException e) {
            return "";  // or throw exception or handle as needed
        }
    }
    
    public static String formatIndianCurrency(String amountStr) {
   	 if (amountStr == null || amountStr.trim().isEmpty()) {
   	        return "0.00/-";
   	    }

   	    try {
   	        double amount = Double.parseDouble(amountStr.trim());
   	        boolean isNegative = amount < 0;
   	        amount = Math.abs(amount); // Work with the positive value

   	        String[] parts = String.format(Locale.ENGLISH, "%.2f", amount).split("\\.");
   	        String intPart = parts[0];
   	        String decPart = parts[1];

   	        StringBuilder result = new StringBuilder();
   	        int len = intPart.length();

   	        if (len > 3) {
   	            result.insert(0, "," + intPart.substring(len - 3));
   	            intPart = intPart.substring(0, len - 3);

   	            while (intPart.length() > 2) {
   	                result.insert(0, "," + intPart.substring(intPart.length() - 2));
   	                intPart = intPart.substring(0, intPart.length() - 2);
   	            }
   	        }
   	        result.insert(0, intPart);

   	        String formatted = result + "." + decPart;
   	        return (isNegative ? "- Rs. " : "Rs. ") + formatted + "/-";
//   	        return (isNegative ? "-" : "") + formatted + "/-";
   	    } catch (NumberFormatException e) {
   	        return "";
   	    }
   }

    private static String formatIndianCurrency(double amount) {
        String[] parts = String.format(Locale.ENGLISH, "%.2f", amount).split("\\.");
        String intPart = parts[0];
        String decPart = parts[1];

        StringBuilder result = new StringBuilder();
        int len = intPart.length();

        if (len > 3) {
            result.insert(0, "," + intPart.substring(len - 3));
            intPart = intPart.substring(0, len - 3);

            while (intPart.length() > 2) {
                result.insert(0, "," + intPart.substring(intPart.length() - 2));
                intPart = intPart.substring(0, intPart.length() - 2);
            }
        }
        result.insert(0, intPart);
//        return "₹ " + result + "." + decPart;
        return "Rs." + result + "." + decPart +" /-";
    }
    
	public static ComponentBuilder<?, ?> buildStyledParagraph(String template, Map<String, String> replacements) {
		Pattern pattern = Pattern.compile("<(\\w+)>");
		Matcher matcher = pattern.matcher(template);
		StringBuilder html = new StringBuilder();

		int lastEnd = 0;
		while (matcher.find()) {
			html.append(template, lastEnd, matcher.start());

			String key = matcher.group(1);
			String value = replacements.getOrDefault(key, "[" + key + "]");
			html.append("<u><b>").append(value).append("</b></u>");

			lastEnd = matcher.end();
		}

		if (lastEnd < template.length()) {
			html.append(template.substring(lastEnd));
		}

		return DynamicReports.cmp.text(html.toString()).setMarkup(Markup.HTML);
	}

	private static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };

	private static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };

	public static String convert(long number) {
		if (number == 0)
			return "Zero";
		return convertNumber(number).trim();
	}

	public static String convertNumber(long number) {
		if (number < 20)
			return units[(int) number];
		if (number < 100)
			return tens[(int) number / 10] + " " + units[(int) number % 10];
		if (number < 1000)
			return units[(int) number / 100] + " Hundred " + convertNumber(number % 100);
		if (number < 100000)
			return convertNumber(number / 1000) + " Thousand " + convertNumber(number % 1000);
		if (number < 10000000)
			return convertNumber(number / 100000) + " Lakh " + convertNumber(number % 100000);
		return convertNumber(number / 10000000) + " Crore " + convertNumber(number % 10000000);
	}
	public static String amountFormat(String amount) {
		if (amount == null || amount.trim().isEmpty()) {
   	        return "0.00";
   	    }
		String[] parts = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(amount)).split("\\.");
        String intPart = parts[0];
        String decPart = parts[1];

        StringBuilder result = new StringBuilder();
        int len = intPart.length();

        if (len > 3) {
            result.insert(0, "," + intPart.substring(len - 3));
            intPart = intPart.substring(0, len - 3);

            while (intPart.length() > 2) {
                result.insert(0, "," + intPart.substring(intPart.length() - 2));
                intPart = intPart.substring(0, intPart.length() - 2);
            }
        }
        result.insert(0, intPart);
//        return "₹ " + result + "." + decPart;
        return result + "." + decPart;
    }
	public static void main(String[] args) {
	        System.out.println(amountFormat("0"));
//	        System.out.println(dateFormat1("20250330"));
	        System.out.println(dateFormat4("10 May 2025")); //'MMM' expects "May", not "MAY".
//	        System.out.println(dateFormat3("2025-10-20"));
	        System.out.println(formatDateWithInputOutput("2025-12-10", Constants.DATEFORMAT1, Constants.DATEFORMAT2));
	        System.out.println(dateFormat2("30 MAY 2025"));
	}
	
	public static String dateFormat1(String inputDateStr) {	
		  if (inputDateStr == null || inputDateStr.isEmpty()) {
		        return "";
		    }
		 DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	     LocalDate date = LocalDate.parse(inputDateStr, inputFormatter);
	     DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
	     String formattedDate = date.format(outputFormatter);
	     System.out.println(formattedDate);  // Output: 24/04/2025
		return formattedDate;
	}
	

	public static String dateFormat2(String inputDateStr) {
		  if (inputDateStr == null || inputDateStr.isEmpty()) {
		        return "";
		    }
	    DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
	            .parseCaseInsensitive() // <-- this is the key
	            .appendPattern("dd MMM yyyy")
	            .toFormatter(Locale.ENGLISH);

	    LocalDate date = LocalDate.parse(inputDateStr.trim(), inputFormatter);

	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);

	    String formattedDate = date.format(outputFormatter);
	    return formattedDate;
	} 
	
	public static String dateFormat4(String inputDateStr) {	
		  if (inputDateStr == null || inputDateStr.isEmpty()) {
		        return "";
		    }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH); //'MMM' expects "May", not "MAY".
	     LocalDate date = LocalDate.parse(inputDateStr, inputFormatter);
	     DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
	     String formattedDate = date.format(outputFormatter);
		return formattedDate;
	}  
	
	public static String dateFormat3(String inputDateStr) {	
		  if (inputDateStr == null || inputDateStr.isEmpty()) {
		        return "";
		    }
		 DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT1);
	     LocalDate date = LocalDate.parse(inputDateStr, inputFormatter);
	     DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
	     String formattedDate = date.format(outputFormatter);
		return formattedDate;
	}
	
	public static String formatDateWithInputOutput(String inputDateStr, String inputPattern, String outputPattern) {
	    if (inputDateStr == null || inputDateStr.isEmpty()) {
	        return "";
	    }
	    try {
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputPattern);
	        LocalDate date = LocalDate.parse(inputDateStr, inputFormatter);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputPattern);
	        return date.format(outputFormatter);
	    } catch (Exception e) {
	        // Log error if needed
	        return inputDateStr; // fallback — return as is if parse fails
	    }
	}
	
	public static String formatDateTimeToDateStr(LocalDateTime dateTime) {
		 if (dateTime == null) {
	            return "";
	        }
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT2);
         String formattedDate = dateTime.format(formatter);
//         System.out.println("Formatted date: " + formattedDate);    
//         String formattedDate1 = dateTime.toLocalDate().format(formatter);
//         System.out.println("Formatted formattedDate1: " + formattedDate1);
		return formattedDate;
	}
	
	public static String getDefaultValue(Object obj) {
	    String value = "NA";
	
	    if (obj != null) {
	        String str = String.valueOf(obj).trim();
	        if (!str.isEmpty() && !Constants.PLEASE_SELECT.equalsIgnoreCase(str)) {
	            value = str;
	        }
	    }
	    return value;
	}
	public static List<String> getStringWithLimit(String value, int limit) {
	    List<String> result = new ArrayList<>();
	    if (StringUtils.isBlank(value) || limit <= 0) {
	        return result;
	    }
    
	    logger.debug("Splitting value: '{}' with limit: {}", value, limit);

	    String[] words = value.trim().split("\\s+");
	    logger.debug("Words: {}", Arrays.toString(words));

	    StringBuilder current = new StringBuilder();

	    for (String word : words) {

	        // If a single word is longer than limit – add as is
	        if (word.length() > limit) {
	            // flush current if not empty
	            if (current.length() > 0) {
	                result.add(current.toString());
	                current.setLength(0);
	            }
	            result.add(word);
	            continue;
	        }

	        // If adding the word stays within the limit
	        if (current.length() == 0) {
	            current.append(word);
	        } else if (current.length() + 1 + word.length() <= limit) {
	            current.append(" ").append(word);
	        } else {
	            // flush and start new line
	            result.add(current.toString());
	            current.setLength(0);
	            current.append(word);
	        }
	    }

	    // add last accumulated string
	    if (current.length() > 0) {
	        result.add(current.toString());
	    }

	    return result;
	}

	public static byte[] htmlToPdf(String html) {
		html = "<div>" + html + "</div>";
		try {
			JasperDesign design = new JasperDesign();
			design.setName("htmlReport");
			// A4 size
			design.setPageWidth(595);
			design.setPageHeight(842);
			design.setLeftMargin(20);
			design.setRightMargin(20);
			design.setTopMargin(20);
			design.setBottomMargin(20);
			design.setColumnWidth(555);
			JRDesignBand band = new JRDesignBand();
			band.setHeight(20);
			JRDesignTextField textField = new JRDesignTextField();
			textField.setX(0);
			textField.setY(0);
			textField.setWidth(555);
			textField.setHeight(20);
			textField.setStretchWithOverflow(true);
			textField.setMarkup("html");
			JRDesignExpression expression = new JRDesignExpression();
			expression.setText("$P{htmlContent}");
			textField.setExpression(expression);
			band.addElement(textField);
			((JRDesignSection) design.getDetailSection()).addBand(band);
			JRDesignParameter param = new JRDesignParameter();
			param.setName("htmlContent");
			param.setValueClass(String.class);
			design.addParameter(param);
			JasperReport report = JasperCompileManager.compileReport(design);
			Map<String, Object> params = new HashMap<>();
			params.put("htmlContent", html);
			JasperPrint print = JasperFillManager.fillReport(
					report,
					params,
					new JREmptyDataSource(1)
			);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(print, out);
			return out.toByteArray();
		} catch (Exception ex) {
			throw new RuntimeException("HTML to PDF conversion failed", ex);
		}
	}

	public static List<String> extractErrorMessages(JSONObject errorObject) {
		JSONArray errorDetails = errorObject.optJSONArray(Constants.ERROR_DETAILS);
		Map<String, String> fieldReplacementMap = new HashMap<>();
		fieldReplacementMap.put("customerAddress[0]", "office ");
		fieldReplacementMap.put("customerAddress[1]", "present ");
		fieldReplacementMap.put("customerAddress[2]", "permanent ");
		fieldReplacementMap.put("customerAddress[3]", "communication ");
		fieldReplacementMap.put("coApplicantDetails[0]", "coapplicant ");
		fieldReplacementMap.put("phoneNumber[0]", "");
		fieldReplacementMap.put("customerShortName[0]", "");
		fieldReplacementMap.put("customerFirstName[0]", "");

		List<String> messages = new ArrayList<>();

		if (errorDetails != null) {
			for (Object err : errorDetails) {
				JSONObject errJson = new JSONObject(err.toString());
				String field = errJson.optString(Constants.FIELD_NAME, Constants.ERROR2);
				String message = errJson.optString(Constants.MESSAGE).toLowerCase();
				boolean replaced = false;
				for (Map.Entry<String, String> entry : fieldReplacementMap.entrySet()) {
					if (field.contains(entry.getKey())) {
						messages.add(field.replace(entry.getKey(), entry.getValue()) + " - " + message.trim());
						replaced = true;
						break;
					}
				}
				if (!replaced) {
					messages.add(field + " - " + message.trim());
				}
			}
		}

		return messages;
	}

	public static JSONObject convertResponseToJson(String json) throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> raw = (Map<String, Object>) mapper.readValue(json, Map.class);
		Object cleaned = cleanUp(raw);
		String cleanJson = mapper.writeValueAsString(cleaned);
		JSONObject apiResp = new JSONObject(cleanJson);
		logger.debug("convertResponseToJson full response 2 --> {}", apiResp);
		return apiResp;
	}

	public static Object cleanUp(Object input) {
		if (input instanceof Map) {
			Map<String, Object> original = (Map<String, Object>) input;
			Map<String, Object> cleaned = new LinkedHashMap<>();
			for (Map.Entry<String, Object> entry : original.entrySet()) {
				String key = entry.getKey();
				Object value = cleanUp(entry.getValue());

				if ("map".equals(key) && value instanceof Map) {
					cleaned.putAll((Map) value);
				} else if ("myArrayList".equals(key) && value instanceof List) {
					return value;
				} else {
					cleaned.put(key, value);
				}
			}
			return cleaned;
		} else if (input instanceof List) {
			List<Object> originalList = (List<Object>) input;
			List<Object> cleanedList = new ArrayList<>();
			for (Object item : originalList) {
				cleanedList.add(cleanUp(item));
			}
			return cleanedList;
		} else {
			return input;
		}
	}

	public static byte[] mergePdf(byte[] pdf1, byte[] pdf2) {

		try (PDDocument doc1 = PDDocument.load(pdf1);
			 PDDocument doc2 = PDDocument.load(pdf2);
			 ByteArrayOutputStream output = new ByteArrayOutputStream()) {

			PDFMergerUtility merger = new PDFMergerUtility();
			merger.appendDocument(doc1, doc2);

			doc1.save(output);

			return output.toByteArray();

		} catch (IOException e) {
			throw new RuntimeException("Failed to merge PDFs", e);
		}
	}
}
