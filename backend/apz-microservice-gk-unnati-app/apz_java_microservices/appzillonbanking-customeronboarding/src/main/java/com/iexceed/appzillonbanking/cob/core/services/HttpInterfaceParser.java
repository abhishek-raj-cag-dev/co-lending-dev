package com.iexceed.appzillonbanking.cob.core.services;

import com.iexceed.appzillonbanking.cob.constants.CommonConstants;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.utils.AdapterUtil;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ExternalIntReqResMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class HttpInterfaceParser {

	@Autowired
	private RestService restService;

	@Autowired
	private ExternalIntReqResMapper externalInterfaceResponseMapper;

	@Autowired
	private AdapterUtil adapterUtil;


	private static final Logger logger = LogManager.getLogger(HttpInterfaceParser.class);
	private static final String ERROR_CONST = "Error occurred while executing the rest service, error = ";


	private static final String PATH_PARAMETERS_LEFT_FILLER = "{";
	private static final String PATH_PARAMETERS_RIGHT_FILLER = "}";


	public Mono<Object> callService(JSONObject request, JSONObject interfaceJsonContent, Header header) {
		logger.debug("Start : callService with request = " + request.toString());
		JSONObject nestedJson = new JSONObject();
		String restRequest = nestedJson.toString();
		String loanIdTemp = "loanIdTemp";
		String endpointurl = "endpointurl";
		String companyIdTemp = "companyIdTemp";
		String customerIdTemp = "customerIdTemp";
		String arrayObj = "arrayObj";
		try {

			String httpActionType = interfaceJsonContent.get("httpactiontype").toString();
			logger.debug("httpActionType = " + httpActionType);

			JSONObject requiredRestObject = adapterUtil.obtainRequestObject(interfaceJsonContent);
			logger.debug("requiredRestObject = " + requiredRestObject);

			adapterUtil.reinitializeGlobalVariables();
			externalInterfaceResponseMapper.reinitializeGlobalVariables();

			LinkedHashMap<String, ArrayList<String>> finalMap = new LinkedHashMap<String, ArrayList<String>>();
			finalMap = adapterUtil.obtainInterfaceNodes(requiredRestObject);

			LinkedHashMap<String, ArrayList<String>> elemestsMap = new LinkedHashMap<String, ArrayList<String>>();
			elemestsMap = externalInterfaceResponseMapper.obtainRestInterfaceNodes(requiredRestObject);

			finalMap.putAll(elemestsMap);
			logger.debug("finalMap = " + finalMap);

			ArrayList<JSONObject> parsedList = new ArrayList<JSONObject>();
			parsedList = adapterUtil.obtainInterfaceElements(requiredRestObject);
			JSONArray parsedArray = new JSONArray(parsedList.toString());
			logger.debug("parsedArray = " + parsedArray);

			// Setting the content type of the api
			String contentType = obtainContentType(interfaceJsonContent);
			logger.debug("contentType = " + contentType);

			JSONArray headerParams = obtainHeaderParams(interfaceJsonContent);
			logger.debug("headerParams = " + headerParams);

			logger.debug("name of intf :" + interfaceJsonContent.get("name").toString());
			String interfaceName = interfaceJsonContent.optString("name", "");
			
			
			//Only for sanction API logic - To add Dynamic headers and path params
			if (interfaceName.equalsIgnoreCase(Constants.COAPPLICANT_CREATION) ||
				    interfaceName.equalsIgnoreCase(Constants.LOAN_CREATION) ||
				    interfaceName.equalsIgnoreCase(Constants.LOAN_REJECTION) ||
				    interfaceName.equalsIgnoreCase(Constants.LOAN_DISBURSEMENT) ||
				interfaceName.equalsIgnoreCase(Constants.DISBURSEMENT_REPAY_SCHEDULE)
			|| interfaceName.equalsIgnoreCase(Constants.APPLICANT_ACTIVATION)) {
				
				Properties prop = null;
				String custCreationUrl = "";
				String custUpdationUrl="";
					prop = CommonUtils.readPropertyFile();
					custCreationUrl =prop.getProperty(CobFlagsProperties.PRAYAAS_CUSTCREATION.getKey());
					custUpdationUrl=prop.getProperty(CobFlagsProperties.PRAYAAS_CUSTUPDATION.getKey());
					logger.debug("Property file read successfully");
				    JSONObject requestObj = request.getJSONObject(CommonConstants.API_REQUEST).optJSONObject(Constants.REQUESTOBJ);
				    if (requestObj != null && requestObj.has("body")) {
				        JSONObject bodyObj = requestObj.optJSONObject("body");
				        if (bodyObj != null) {
				        	
				        	if((interfaceName.equalsIgnoreCase(Constants.LOAN_REJECTION) ||
								    interfaceName.equalsIgnoreCase(Constants.LOAN_DISBURSEMENT) ||
								    interfaceName.equalsIgnoreCase(Constants.DISBURSEMENT_REPAY_SCHEDULE)) && bodyObj.has(loanIdTemp)) {
										String endPoint = interfaceJsonContent.getString(endpointurl);
										logger.debug("endPoint beofre adding :" + endPoint);
										interfaceJsonContent.put(endpointurl, endPoint + bodyObj.optString(loanIdTemp, ""));
										 bodyObj.remove(loanIdTemp);
				        	}
				            // Only update and remove if the values exist
				            if (bodyObj.has(customerIdTemp) || bodyObj.has(companyIdTemp)) {
				                for (Object obj : headerParams) {
				                    JSONObject headerObj = (JSONObject) obj;
				                    String name = headerObj.optString("name");

				                    if ("customer_id".equalsIgnoreCase(name) && bodyObj.has(customerIdTemp)) {
				                        String custId = bodyObj.optString(customerIdTemp, "");
                                        logger.debug("custId :{}", custId);
										String isUpdateCall = bodyObj.optString("isUpdateCall", "N");
										logger.debug("isUpdateCall :{}", isUpdateCall);
				                        if(isUpdateCall.equalsIgnoreCase("Y") && interfaceName.equalsIgnoreCase(Constants.COAPPLICANT_CREATION)) {
				                        	String endPoint = interfaceJsonContent.getString(endpointurl);
				                        	String updateEndpoint = endPoint.replace(custCreationUrl, custUpdationUrl);
                                            logger.debug("updateEndpoint beofre adding :{}", updateEndpoint);
											interfaceJsonContent.put(endpointurl, updateEndpoint + bodyObj.optString(customerIdTemp, ""));
											interfaceJsonContent.put("httpactiontype", "PUT");
				                        }
				                        headerObj.put("value", custId);
				                        bodyObj.remove(customerIdTemp);
                                        logger.debug("Updated customer_id to member id: {}", custId);
				                    } else if ("COMPANYID".equalsIgnoreCase(name) && bodyObj.has(companyIdTemp)) {
				                        String compId = bodyObj.optString(companyIdTemp, "");
				                        headerObj.put("value", compId);
										if(bodyObj.has(customerIdTemp) && interfaceName.equalsIgnoreCase(Constants.APPLICANT_ACTIVATION)) {
											String endPoint = interfaceJsonContent.getString(endpointurl);
											String finalUrl = endPoint.replace("@CustomerId@", bodyObj.optString(customerIdTemp, ""));
											logger.debug("finalUrl beofre adding :{}", finalUrl);
											interfaceJsonContent.put(endpointurl, finalUrl);
										}
				                        bodyObj.remove(companyIdTemp);
                                        logger.debug("Updated Branch Id: {}", compId);
				                    } 
				                }

				                logger.debug("After removal customerId/companyId temp fields: " + requestObj);
				                interfaceJsonContent.put(Constants.HTTPREQHEADERPARAMS, headerParams);
				                nestedJson = requestObj;
				            }
				        }
				    }
				}
            if(interfaceName.equalsIgnoreCase(Constants.CUSTOMER_LOAN_CHECK)
                    || interfaceName.equalsIgnoreCase(Constants.EXISTING_GL_LOAN_DTLS_API)){
                JSONObject requestObj = request.getJSONObject(CommonConstants.API_REQUEST).optJSONObject(Constants.REQUESTOBJ);
                if(requestObj.has("customerId")){
                    String endPoint = interfaceJsonContent.getString(endpointurl);
                    String finalUrl = endPoint.replace("@CustomerId@", requestObj.optString("customerId", ""));
                    logger.debug("finalUrl: {}", finalUrl);
                    interfaceJsonContent.put(endpointurl, finalUrl);
                }
            }
			//@author Abhishek.Raj CAG
			if(interfaceName.equalsIgnoreCase(Constants.NESL_STAGE_FETCH_TRACKER)){
				JSONObject requestObj = request.getJSONObject(CommonConstants.API_REQUEST).optJSONObject(Constants.REQUESTOBJ);
				if(requestObj.has("applicationId")){
					String endPoint = interfaceJsonContent.getString(endpointurl);
					String finalUrl = endPoint.replace("@ApplicationId@", requestObj.optString("applicationId", ""));
					logger.debug("finalUrl: {}", finalUrl);
					interfaceJsonContent.put(endpointurl, finalUrl);
				}
			}
			//----END----


			if (interfaceName.equalsIgnoreCase(Constants.LOAN_REPAYMENT_SCHEDULE)) {
			    JSONObject requestObj = request.getJSONObject(CommonConstants.API_REQUEST).optJSONObject(Constants.REQUESTOBJ);
			    if (requestObj != null && requestObj.has("body")) {
			        JSONObject bodyObj = requestObj.optJSONObject("body");
			        if (bodyObj != null) {
 
			            // List of keys to move from body to header
			            String[] keysToMove = { "loanFrequency", "interestRate", "loanAmount", "tenure", "customerId", "productID" };
			            String endPoint = interfaceJsonContent.getString(endpointurl);
			            String finalUrl = endPoint
			                             .replace("@FREQ@", bodyObj.optString("loanFrequency", ""))
			                             .replace("@RATE@", bodyObj.optString("interestRate", ""))
			                             .replace("@AMT@", bodyObj.optString("loanAmount", ""))
			                             .replace("@TENURE@", bodyObj.optString("tenure", ""))
			                             .replace("@CUSTID@", bodyObj.optString("customerId", ""))
			                             .replace("@PROD@", bodyObj.optString("productID", ""));
 
			                for (String key : keysToMove) {
			                	bodyObj.remove(key);  // Remove from body
			                }
						logger.debug("finalUrl :" + finalUrl);
						interfaceJsonContent.put(endpointurl, finalUrl);
						
			            logger.debug("final headers: " + headerParams);
			            logger.debug("After removal temp fields: " + requestObj);
 
			            // Update nested payload
			            nestedJson = requestObj;
			        }
			    }
			}
			//ended
			logger.debug("nestedJson after changes = " + nestedJson);
			
			/**
			 * Code change to allow direct service call without request mapping in the
			 * external interface Added to support list of objects for external interface
			 * services.
			 */
			if (parsedArray.length() == 0 && Constants.JSON_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
				if (request.getJSONObject(CommonConstants.API_REQUEST).get(Constants.REQUESTOBJ) instanceof JSONObject) {
					nestedJson = request.getJSONObject(CommonConstants.API_REQUEST).getJSONObject(Constants.REQUESTOBJ);
					restRequest = nestedJson.toString();
				} else if (request.getJSONObject(CommonConstants.API_REQUEST).get(Constants.REQUESTOBJ) instanceof JSONArray) {
					restRequest = request.getJSONObject(CommonConstants.API_REQUEST).getJSONArray(Constants.REQUESTOBJ)
							.toString();
				}
			} else {
				if (request.getJSONObject(CommonConstants.API_REQUEST).get(Constants.REQUESTOBJ) instanceof JSONObject) {
					JSONObject simpleJson = externalInterfaceResponseMapper.obtainSimpleRestJson(request, parsedArray);
					logger.debug("simpleJson = " + simpleJson);

					nestedJson = adapterUtil.obtainNestedJson(simpleJson, finalMap);
					logger.debug("nestedJson = " + nestedJson);
					restRequest = nestedJson.toString();
				} else if (request.getJSONObject(CommonConstants.API_REQUEST).get(Constants.REQUESTOBJ) instanceof JSONArray) {
					restRequest = request.getJSONObject(CommonConstants.API_REQUEST).getJSONArray(Constants.REQUESTOBJ)
							.toString();
				}
			}
			String endPointUrl = interfaceJsonContent.get(endpointurl).toString();
			logger.debug("endPointUrl = " + endPointUrl);

			// Setting the Path Parameters
			endPointUrl = obtainPathParams(interfaceJsonContent, endPointUrl);
			logger.debug("endPointUrl after adding path params = " + endPointUrl);

			// Setting the Query Parameters
			endPointUrl = obtainQueryParams(interfaceJsonContent, endPointUrl);
			logger.debug("endPointUrl after adding query params = " + endPointUrl);

			if (request.getJSONObject(CommonConstants.API_REQUEST).has(arrayObj)
					&& null != request.getJSONObject(CommonConstants.API_REQUEST).getJSONArray(arrayObj)
					&& request.getJSONObject(CommonConstants.API_REQUEST).getJSONArray(arrayObj).length() > 0) {
				restRequest = request.getJSONObject(CommonConstants.API_REQUEST).getJSONArray(arrayObj).toString();
			}

			try {
				// Executing the REST API
				return restService.executeRestApi(restRequest, headerParams, httpActionType, contentType,
						endPointUrl, interfaceJsonContent, header);
			} catch (Exception e) {
				logger.error("Error occurred while executing the rest api, error = " + e.getMessage());
				return adapterUtil.setErrorMono("Rest API execution failed", "4");
				// status = "F";
			}
		} catch (FileNotFoundException e) {
			logger.error(ERROR_CONST, e);
			return adapterUtil.setErrorMono("Please upload the interface file in the configured path.", "2");
		} catch (JSONException e) {
			logger.error(ERROR_CONST, e);
			return adapterUtil.setErrorMono("Request/Response parameters are missing.", "5");
		} catch (Exception e) {
			logger.error(ERROR_CONST, e);
			return adapterUtil.setErrorMono("Error in interface configuration", "1");
		}
	}

	public String setContentType(JSONObject interfaceJsonContent) {

		String contentType = interfaceJsonContent.get("reqcontenttype").toString();
		logger.debug("interface contentType = " + contentType);

		if (contentType.equalsIgnoreCase("JSON")) {
			contentType = Constants.JSON_CONTENT_TYPE;
		} else if (contentType.equalsIgnoreCase("XML")) {
			contentType = "application/xml";
		} else if (contentType.equalsIgnoreCase("TEXT")) {
			contentType = "text/html";
		} else if (contentType.equalsIgnoreCase("FORMDATA")) {
			contentType = "multipart/form-data";
		} else {
			contentType = Constants.JSON_CONTENT_TYPE;
		}
		return contentType;
	}

	public String obtainContentType(JSONObject interfaceJsonContent) {

		String contentType = interfaceJsonContent.get("reqcontenttype").toString();
		logger.debug("interface contentType = " + contentType);

		if (contentType.equalsIgnoreCase("JSON")) {
			contentType = Constants.JSON_CONTENT_TYPE;
		} else if (contentType.equalsIgnoreCase("XML")) {
			contentType = "application/xml";
		} else if (contentType.equalsIgnoreCase("TEXT")) {
			contentType = "text/html";
		} else if (contentType.equalsIgnoreCase("FORMDATA")) {
			contentType = "multipart/form-data";
		} else {
			contentType = Constants.JSON_CONTENT_TYPE;
		}
		return contentType;
	}

	public JSONArray obtainHeaderParams(JSONObject interfaceJsonContent) {

		JSONArray headerParams = null;
		if (interfaceJsonContent.has(Constants.HTTPREQHEADERPARAMS)) {
			String headerParamsStr = interfaceJsonContent.get(Constants.HTTPREQHEADERPARAMS).toString();

			headerParams = new JSONArray(headerParamsStr);
			logger.debug("headerParams = " + headerParams);
		} else {
			logger.debug("Header parameters are not configured");
			headerParams = new JSONArray();
		}
		return headerParams;
	}

	public String obtainQueryParams(JSONObject interfaceJsonContent, String endPointUrl) {

		StringBuilder sb = new StringBuilder();
		Map<String, String> queryParams = externalInterfaceResponseMapper.fetchQueryParams();
		logger.debug("queryParams = " + queryParams);
		int i = 0;
		logger.debug("endPointUrl before adding query params = " + endPointUrl);

		for (Map.Entry<String, String> entry : queryParams.entrySet()) {
			logger.debug("Creating the query parameters string");
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			if (i != (queryParams.size() - 1)) {
				sb.append("&");
			}
			i++;
		}
		logger.debug("queryParamsBuilder string = " + sb.toString());
		if ((sb != null) && (sb.length() > 0)) {
			logger.debug("Appending the query parameters");

			if ((endPointUrl.substring(endPointUrl.length() - 1)).equalsIgnoreCase("/")) {
				logger.debug("removing the unwanted character");
				endPointUrl = endPointUrl.substring(0, endPointUrl.length() - 1) + "?" + sb.toString();
			} else {
				endPointUrl = endPointUrl + "?" + sb.toString();
			}
		}
		return endPointUrl;
	}

	public String obtainPathParams(JSONObject interfaceJsonContent, String endPointUrl) {

		logger.debug("endPointUrl before adding path params = " + endPointUrl);
		Map<String, String> pathParamsMap = externalInterfaceResponseMapper.fetchPathParams();
		logger.debug("pathParamsMap = " + pathParamsMap);

		for (Map.Entry<String, String> entry : pathParamsMap.entrySet()) {
			String pathParamName = entry.getKey();
			logger.debug("pathParamName = " + pathParamName);

			String pathParamValue = entry.getValue();
			logger.debug("pathParamValue = " + pathParamValue);

			if ((endPointUrl.contains(PATH_PARAMETERS_LEFT_FILLER))
					&& (endPointUrl.contains(PATH_PARAMETERS_RIGHT_FILLER))) {
				int lfirstIndex = endPointUrl.indexOf(PATH_PARAMETERS_LEFT_FILLER);
				logger.debug("appendPathParameters lfirstIndex = " + lfirstIndex);
				int rfirstIndex = endPointUrl.indexOf(PATH_PARAMETERS_RIGHT_FILLER);
				logger.debug("appendPathParameters rfirstIndex = " + rfirstIndex);

				String pathParamToBeReplaced = endPointUrl.substring(lfirstIndex + 1, rfirstIndex);
				logger.debug("Path Param to be replaced = " + pathParamToBeReplaced);

				if (pathParamToBeReplaced.equalsIgnoreCase(pathParamName)) {
					endPointUrl = endPointUrl.replace(
							PATH_PARAMETERS_LEFT_FILLER + pathParamToBeReplaced + PATH_PARAMETERS_RIGHT_FILLER,
							pathParamValue);
				}
			}
		}
		return endPointUrl;
	}

}
