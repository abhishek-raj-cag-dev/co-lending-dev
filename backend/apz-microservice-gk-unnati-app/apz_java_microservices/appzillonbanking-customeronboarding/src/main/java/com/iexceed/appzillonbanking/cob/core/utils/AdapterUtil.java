package com.iexceed.appzillonbanking.cob.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.constants.CommonConstants;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.logs.service.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class AdapterUtil {

	@Autowired
	ExternalIntReqResMapper externalIntReqResMapper;

	@Autowired
	LoggingService logsService;

	private static final Logger logger = LogManager.getLogger(AdapterUtil.class);

	private LinkedHashMap<String, ArrayList<String>> hmap = new LinkedHashMap<>();
	private ArrayList<JSONObject> elementsList = new ArrayList<>();

	/*
	 * Calling obtainInterfaceNodes method to obtain a JSONObject containing the
	 * details of REQUESTDATAMODEL Input : JSONObject containing the interface
	 * details Output: JSONObject containing the REQUESTDATAMODEL details
	 */
	public JSONObject obtainRequestObject(JSONObject fileJson) {

		JSONObject reqJson = new JSONObject();
		JSONArray reqJsonArray;

		Iterator<String> keys = fileJson.keys();
		while (keys.hasNext()) {
			String key = keys.next();

			if (fileJson.get(key) instanceof JSONArray) {
				reqJsonArray = fileJson.getJSONArray(key);

				for (int i = 0; i < reqJsonArray.length(); i++) {
					reqJson = (JSONObject) reqJsonArray.get(i);

					String modelType = reqJson.get("typeclass").toString();

					if (modelType.equalsIgnoreCase("REQUESTDATAMODEL")) {

						return reqJson;
					}
				}
			}
		}
		return reqJson;
	}

	/*
	 * Calling obtainInterfaceNodes method to obtain a map containing all the parent
	 * and child nodes Input : JSONObject containing the interface details Output:
	 * Map containing all the parent and child nodes
	 */
	public LinkedHashMap<String, ArrayList<String>> obtainInterfaceNodes(JSONObject fileJson) {

		JSONObject reqJson;
		JSONArray reqJsonArray;

		Iterator<String> keys = fileJson.keys();
		while (keys.hasNext()) {
			String key = keys.next();

			if (fileJson.get(key) instanceof JSONArray) {
				reqJsonArray = fileJson.getJSONArray(key);

				for (int i = 0; i < reqJsonArray.length(); i++) {
					reqJson = (JSONObject) reqJsonArray.get(i);

					if (reqJson.has(key)) {
						String type = reqJson.get("type").toString();

						if (type.equalsIgnoreCase("node")) {

							JSONArray tempJsonArray = (JSONArray) reqJson.get(key);
							JSONObject tempJson = new JSONObject();

							String parentNode = reqJson.has(Constants.ALIAS)
									? (reqJson.get(Constants.ALIAS).toString() + ":" + reqJson.get("name").toString())
									: (reqJson.get("name").toString());
							ArrayList<String> childNodeList = new ArrayList<>();

							for (int j = 0; j < tempJsonArray.length(); j++) {
								tempJson = (JSONObject) tempJsonArray.get(j);

								if ((tempJson.get("type").toString()).equalsIgnoreCase("node")) {

									if (tempJson.has(Constants.ALIAS)) {
										childNodeList.add(tempJson.get(Constants.ALIAS).toString() + ":"
												+ tempJson.get("name").toString());
									}

									else {
										childNodeList.add(tempJson.get("name").toString());
									}
								}
							}

							hmap.put(parentNode, childNodeList);
						}
						// Using recursion to parse the entire JSON object
						obtainInterfaceNodes(reqJson);
					}
				}
			}
		}

		return hmap;
	}

	/*
	 * Calling obtainInterfaceElements method to obtain a JSON containing all the
	 * elements of the interface Input : JSONObject containing the interface details
	 * Output: ArrayList containing all the elements of the interface
	 */
	public ArrayList<JSONObject> obtainInterfaceElements(JSONObject fileJson) throws JSONException {

		JSONObject reqJson;
		JSONArray reqJsonArray;
		Iterator<String> keys = fileJson.keys();

		while (keys.hasNext()) {
			String key = keys.next();
			if (fileJson.get(key) instanceof JSONArray) {
				reqJsonArray = fileJson.getJSONArray(key);

				for (int i = 0; i < reqJsonArray.length(); i++) {
					reqJson = (JSONObject) reqJsonArray.get(i);

					// Using recursion to parse the entire JSON object
					if (reqJson.has(key) && (!((reqJson.get("type")).toString().equalsIgnoreCase("element")))) {
						obtainInterfaceElements(reqJson);
					}

					if ((reqJson.get("type")).toString().equalsIgnoreCase("element")) {
						elementsList.add(reqJson);
					}
				}
			}
		}

		return elementsList;
	}

	public JSONObject formSimpleJson(JSONObject finalJson, String nodeName, String keyName, String value,
			JSONObject outputJson) {

		if (finalJson.has(nodeName)) {
			String currentContents = finalJson.get(nodeName).toString();
			finalJson.remove(nodeName);

			JSONArray jsonArr = new JSONArray();
			// support for json arrays
			/*
			 * if ((value != null) && (value.startsWith("[{")) && (value.endsWith("}]"))) {
			 * jsonArr = new JSONArray(value); JSONObject completeJson = new
			 * JSONObject(currentContents); completeJson.put(keyName, jsonArr);
			 * finalJson.put(nodeName, completeJson); }
			 * 
			 * // support for integer/string arrays else if ((value != null) &&
			 * (value.startsWith("[")) && (value.endsWith("]"))) {
			 */
			if ((value != null) && (value.startsWith("[")) && (value.endsWith("]"))) {
				value = value.replace("[", "").replace("]", "");
				Object[] arrContents = value.split(",");

				for (Object str : arrContents) {
					jsonArr.put(str);
				}

				JSONObject completeJson = new JSONObject(currentContents);
				completeJson.put(keyName, jsonArr);
				finalJson.put(nodeName, completeJson);
			}

			else {
				JSONObject completeJson = new JSONObject(currentContents);
				completeJson.put(keyName, value);
				finalJson.put(nodeName, completeJson);
			}
		}

		else {
			if (outputJson.length() == 0) {
				JSONObject tempJson = new JSONObject();
				tempJson.put(keyName, value);
				finalJson.put(nodeName, tempJson);
			}

			else {
				finalJson.put(nodeName, outputJson);
			}
		}

		return finalJson;
	}

	/*
	 * Calling obtainNestedJson method to obtain a nested JSON containing all the
	 * nodes and its elements in the required hierarchical structure Input :
	 * JSONObject object obtained from the method "obtainSimpleJson" and the map
	 * containing the details of parent and child nodes Output: JSONObject
	 * containing all the nodes and its elements in the required hierarchical
	 * structure
	 */
	public JSONObject obtainNestedJson(JSONObject simpleJson, LinkedHashMap<String, ArrayList<String>> nodeMap)
			throws JSONException {

		JSONObject finalNestedJson = new JSONObject();
		LinkedHashMap<String, ArrayList<String>> reversedMap = new LinkedHashMap<>();

		List<String> alKeys = new ArrayList<>(nodeMap.keySet());
		Collections.reverse(alKeys);

		for (String strKey : alKeys) {
			reversedMap.put(strKey, nodeMap.get(strKey));
		}

		for (Map.Entry<String, ArrayList<String>> mapData : reversedMap.entrySet()) {
			String parentNodeName = mapData.getKey();
			ArrayList<String> childValues = mapData.getValue();

			if (childValues.toString().equals("[]")) {
				if (simpleJson.has(parentNodeName)) {
					finalNestedJson.put(parentNodeName, simpleJson.get(parentNodeName));
				}
			}

			else {
				for (String childNodeName : childValues) {
					if (simpleJson.has(childNodeName)) {
						if (finalNestedJson.has(childNodeName)) {
							String childNodeContents = finalNestedJson.get(childNodeName).toString();
							JSONObject childJson = new JSONObject(childNodeContents);

							if (finalNestedJson.has(childNodeName)) {
								JSONObject parentJson = new JSONObject();

								if (simpleJson.has(parentNodeName)) {
									if (finalNestedJson.has(parentNodeName)) {
										finalNestedJson.remove(childNodeName);
										parentJson = finalNestedJson.getJSONObject(parentNodeName);
										parentJson.put(childNodeName, childJson);
										finalNestedJson.put(parentNodeName, parentJson);
									}

									else {
										finalNestedJson.remove(childNodeName);
										parentJson = simpleJson.getJSONObject(parentNodeName);
										parentJson.put(childNodeName, childJson);
										finalNestedJson.put(parentNodeName, parentJson);
									}
								}

								else {
									finalNestedJson.remove(childNodeName);

									if (finalNestedJson.has(parentNodeName)) {
										parentJson = finalNestedJson.getJSONObject(parentNodeName);
									}

									parentJson.put(childNodeName, childJson);
									finalNestedJson.put(parentNodeName, parentJson);
								}
							}
						}

						else {
							finalNestedJson.put(childNodeName, simpleJson.get(childNodeName));
						}
					}

					else {
						String currentChildContents = finalNestedJson.get(childNodeName).toString();

						if (finalNestedJson.has(parentNodeName)) {
							String currentParentContents = finalNestedJson.get(parentNodeName).toString();
							finalNestedJson.remove(childNodeName);

							JSONObject tempChildJson = new JSONObject(currentChildContents);
							JSONObject tempParentJson = new JSONObject(currentParentContents);

							tempParentJson.put(childNodeName, tempChildJson);
							finalNestedJson.put(parentNodeName, tempParentJson);
						}

						else {
							JSONObject tempChildJson = new JSONObject(currentChildContents);
							finalNestedJson.remove(childNodeName);
							JSONObject tempChildJson1 = new JSONObject();
							tempChildJson1.put(childNodeName, tempChildJson);
							finalNestedJson.put(parentNodeName, tempChildJson1);
						}
					}
				}
			}
		}

		return finalNestedJson;
	}

	public JSONObject formSimpleResponseJson(JSONObject finalJson, String nodeName, String keyName, String value,
			JSONObject outputJson) {

		if (finalJson.has(nodeName)) {
			String currentContents = finalJson.get(nodeName).toString();
			finalJson.remove(nodeName);

			// support for arrays
			if ((value != null) && (value.startsWith("[")) && (value.endsWith("]"))) {
				logger.debug("support for arrays");
				JSONArray newArr = new JSONArray(value);
				JSONArray combinedArr = new JSONArray();
				JSONObject completeJson = null;

				if ((currentContents != null) && (currentContents.startsWith("[")) && (currentContents.endsWith("]"))) {
					JSONArray existingArray = new JSONArray(currentContents);

					for (int i = 0; i < existingArray.length(); i++) {
						JSONObject tempJson = existingArray.getJSONObject(i);
						tempJson.put(keyName, (i < newArr.length()) ? newArr.get(i) : "");
						combinedArr.put(tempJson);
					}
				}

				else {
					completeJson = new JSONObject(currentContents);

					Iterator<String> keys = completeJson.keys();
					JSONArray existingArray;
					JSONObject existingJson = new JSONObject();

					while (keys.hasNext()) {
						String key = keys.next();

						// support for json arrays
						if (completeJson.get(key) instanceof JSONArray) {
							logger.debug("combining existing array with new array");
							existingArray = completeJson.getJSONArray(key);

							for (int i = 0; i < existingArray.length(); i++) {
								JSONObject tempJson = new JSONObject();
								tempJson.put(key, existingArray.get(i));
								tempJson.put(keyName, (i < newArr.length()) ? newArr.get(i) : "");
								combinedArr.put(tempJson);
							}
						}

						// support for json objects
						else {
							logger.debug("combining existing array with new object");
							existingJson.put(key, completeJson.get(key).toString());
							combinedArr.put(existingJson);
						}
					}
				}

				finalJson.put(nodeName, combinedArr);
			}

			else {
				JSONObject completeJson = new JSONObject(currentContents);
				completeJson.put(keyName, value);
				finalJson.put(nodeName, completeJson);
			}
		}

		else {
			if (outputJson.length() == 0) {
				JSONObject tempJson = new JSONObject();
				tempJson.put(keyName, value);
				finalJson.put(nodeName, tempJson);
			}

			else {
				finalJson.put(nodeName, outputJson);
			}
		}

		return finalJson;
	}

	public JSONObject setSuccessResp(String apiResponse) {
		logger.debug("Start : setSuccessResp");
		logger.debug("apiResponse = " + apiResponse);

		JSONObject response = new JSONObject();
		response.put(Constants.ERRORCODE, "0");
		response.put(Constants.ERRORMESSAGE, "");
		response.put(Constants.RESPONSEOBJ, apiResponse);

		logger.debug("End : setSuccessResp");
		return response;
	}



	/**
	 * Method to set Mono<Object> for successful response from the API.
	 * 
	 * @author akshay.upadhya
	 * @since 15.11.2021
	 * @param apiResponse
	 * @return
	 */
	public Mono<Object> setSuccessRespMono(String apiResponse) {
		logger.debug("Start : setSuccessResp, apiResponse = " + apiResponse);
		JSONObject response = new JSONObject();
		response.put(Constants.ERRORCODE, "0");
		response.put(Constants.ERRORMESSAGE, "");
		response.put(Constants.RESPONSEOBJ, apiResponse);
		return Mono.just(response);
	}

	public JSONObject setError(String errorMsg, String errorCode) {
		logger.debug("Start : setError");
		logger.debug("errorMsg = " + errorMsg);
		logger.debug("errorCode = " + errorCode);

		JSONObject response = new JSONObject();
		response.put(Constants.ERRORCODE, errorCode);
		response.put(Constants.ERRORMESSAGE, errorMsg);
		response.put(Constants.RESPONSEOBJ, "");

		logger.debug("End : setError");
		return response;
	}

	/**
	 * Method to generate a Mono<Object> for setting the error case condition
	 * 
	 * @author akshay.upadhya
	 * @since 15.11.2021
	 * @param errorMsg
	 * @param errorCode
	 * @return
	 */
	public Mono<Object> setErrorMono(String errorMsg, String errorCode) {
		logger.debug("Start : setError, errorMsg = " + errorMsg + ", errorCode = " + errorCode);
		JSONObject response = new JSONObject();
		response.put(Constants.ERRORCODE, errorCode);
		response.put(Constants.ERRORMESSAGE, errorMsg);
		response.put(Constants.RESPONSEOBJ, "");
		logger.debug("End : setError");
		return Mono.just(response);
	}

	public String readInterfaceContentFromServer(String fileName) throws IOException {

		String serverFilePath = CommonUtils.getExternalProperties("interFaceDir") + fileName;
		BufferedReader br = new BufferedReader(new FileReader(serverFilePath));
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return sb.toString();

	}

	public String readJSONContentFromServer(String fileName) throws IOException {
		String serverFilePath = CommonUtils.getExternalProperties("jsonDir") + fileName;
		logger.debug("serverFilePath :" + serverFilePath);
		BufferedReader br = new BufferedReader(new FileReader(serverFilePath));
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return sb.toString();

	}

	public String getPropertyValueFromServer(String keyName) {
		return CommonUtils.getExternalProperties(keyName);
	}

	/*
	 * Calling obtainInterfaceNodes method to obtain a JSONObject containing the
	 * details of RESPONSEDATAMODEL Input : JSONObject containing the interface
	 * details Output: JSONObject containing the RESPONSEDATAMODEL details
	 */
	public JSONObject obtainResponseObject(JSONObject fileJson) {

		JSONObject respJson = new JSONObject();
		JSONArray reqJsonArray = null;

		Iterator<String> keys = fileJson.keys();
		while (keys.hasNext()) {
			String key = keys.next();

			if (fileJson.get(key) instanceof JSONArray) {
				reqJsonArray = fileJson.getJSONArray(key);

				for (int i = 0; i < reqJsonArray.length(); i++) {
					respJson = (JSONObject) reqJsonArray.get(i);

					String modelType = respJson.get("typeclass").toString();

					if (modelType.equalsIgnoreCase("RESPONSEDATAMODEL")) {

						return respJson;
					}
				}
			}
		}
		return respJson;
	}

	public void reinitializeGlobalVariables() {
		logger.debug("Start : reinitializeGlobalVariables with counter");
		logger.debug("re-initializing global variables");

		hmap = new LinkedHashMap<>();
		elementsList = new ArrayList<>();

		logger.debug("hmap = " + hmap);
		logger.debug("elementsList = " + elementsList);

		logger.debug("End : reinitializeGlobalVariables");
	}

	public boolean isJSONValid(String jsonInString) {
		logger.debug("Start : isJSONValid");
		boolean validJson;
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			validJson = true;
		} catch (IOException e) {
			validJson = false;
		}
		logger.debug("End : isJSONValid with response = " + validJson);
		return validJson;
	}

	/**
	 * Generic Method to return the Mono<ResponseEntity> wrapper for the external
	 * API service call.
	 * 
	 * @author akshay.upadhya
	 * @since 15.11.2021
	 * @param userAccountResponse
	 * @param interfaceFileName
	 * @return
	 */

	public Mono<ResponseEntity<ResponseWrapper>> generateResponseWrapper(Mono<Object> responseObject,
			String intfFileName, Header header) {
		logger.debug("interfaceFileName =" + intfFileName);
		logger.debug("End : generateResponseWrapper method with response :: " + responseObject.toString());
		Mono<ResponseEntity<ResponseWrapper>> responseWrapperEntity = responseObject.map(val -> {
			ResponseWrapper responseWrapper = this.getResponseMapper(val, intfFileName, header);
			return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
		});
		return responseWrapperEntity;
	}

	public Mono<ResponseWrapper> generateRespWrapper(Mono<Object> responseObj, String intfFileName, Header header) {
		logger.debug("interfaceFileName =" + intfFileName);
		logger.debug("%s %s","End : generateRespWrapper Wrapper :: ", responseObj.toString());
		return responseObj.map(val -> {
			return this.getResponseMapper(val, intfFileName, header);
		});
	}

	public ResponseWrapper getResponseMapper(Object val, String interfaceFileName, Header header) {
		interfaceFileName = interfaceFileName + ".apzinterface";
		String extApiResponse = val.toString();
		logger.debug("Response from API::" + extApiResponse);
		// If val is an instanceof HashMap converting to String.
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		try {
			if (val instanceof Response) {
				logger.debug("Response is an instance of Response POJO, Hence adding the wrapper and returning as is");
				String extApiResp = new Gson().toJson(val);
				logger.debug("extApiResp::" + extApiResp);
				responseBody.setResponseObj("");
				response.setResponseBody(responseBody);
				responseWrapper.setApiResponse(new Gson().fromJson(extApiResp, Response.class));
				logger.debug("responseWrapper::" + responseWrapper);
				return responseWrapper;
			}
			if (val instanceof HashMap<?, ?>) {
				extApiResponse = new Gson().toJson(val);
				logger.debug("HashMap Instance::" + extApiResponse);
			}
			if (val instanceof ArrayList<?>) {
				extApiResponse = new ObjectMapper().writeValueAsString(val);
			}
			if (extApiResponse != null && !extApiResponse.trim().equals("")) {
				// Handling for the JSON Object as response.
				if (extApiResponse.startsWith("{") && extApiResponse.endsWith("}")) {
					JSONObject apiResponse;

					apiResponse = new JSONObject(extApiResponse);
					if (apiResponse.has(Constants.ERRORCODE) && !"0".equalsIgnoreCase(apiResponse.getString(Constants.ERRORCODE))) {
						responseBody.setResponseObj("");
						responseHeader.setErrorCode(apiResponse.getString(Constants.ERRORCODE));
						responseHeader.setResponseCode(CommonConstants.FAILURE);
						responseHeader.setResponseMessage(apiResponse.getString(Constants.ERRORMESSAGE));
						response.setResponseHeader(responseHeader);
						response.setResponseBody(responseBody);
						responseWrapper.setApiResponse(response);
					} else {
						logger.debug("External API execution is successful.");
						responseBody.setResponseObj(extApiResponse);
						responseHeader.setErrorCode("");
						responseHeader.setResponseCode(CommonConstants.SUCCESS);
						responseHeader.setResponseMessage("");
						JSONObject sucResp = new JSONObject(extApiResponse);
						logger.debug("API execution is completed, starting the response parsing logic");
						String interfaceFileContent = this.readInterfaceContentFromServer(interfaceFileName);
						logger.debug("interfaceFileContent = " + interfaceFileContent);

						JSONObject interfaceJsonContent = new JSONObject(interfaceFileContent);
						JSONObject interfaceResponseObject = this.obtainResponseObject(interfaceJsonContent);
						logger.debug("interfaceResponseObject = " + interfaceResponseObject);
						this.reinitializeGlobalVariables();
						if ((interfaceResponseObject.has("childs")) && this.isJSONValid(extApiResponse)) {
							logger.debug(
									"Response data model is configured for this interface, setting the required api response");
							JSONObject finalRespObj = externalIntReqResMapper.obtainRequiredRestApiResponse(sucResp,
									interfaceResponseObject);

							responseBody.setResponseObj(finalRespObj.toString());
							responseHeader.setErrorCode("");
							responseHeader.setResponseCode(CommonConstants.SUCCESS);
							responseHeader.setResponseMessage("");
						} else {
							logger.debug(
									"Response data model is not configured for this interface, returning the entire api response");
						}
					}

				} else if (extApiResponse.startsWith("[{") && extApiResponse.endsWith("}]")) {

					logger.debug("External API execution is successful and response is an Array.");
					JSONArray arrResp = new JSONArray(extApiResponse);

					if (arrResp.length() == 1) {
						JSONObject responseAPI = arrResp.getJSONObject(0);
						logger.debug("API execution is completed, starting the response parsing logic");
						String interfaceFileContent = this.readInterfaceContentFromServer(interfaceFileName);
						logger.debug("interfaceFileContent = " + interfaceFileContent);

						JSONObject interfaceJsonContent = new JSONObject(interfaceFileContent);
						JSONObject interfaceResponseObject = this.obtainResponseObject(interfaceJsonContent);
						logger.debug("interfaceResponseObject = " + interfaceResponseObject);
						this.reinitializeGlobalVariables();
						if ((interfaceResponseObject.has("childs")) && this.isJSONValid(extApiResponse)) {
							logger.debug(
									"Response data model is configured for this interface, setting the required api response");
							JSONObject finalRespObj = new JSONObject();
							finalRespObj = externalIntReqResMapper.obtainRequiredRestApiResponse(responseAPI,
									interfaceResponseObject);

							responseBody.setResponseObj(finalRespObj.toString());
						} else {
							logger.debug(
									"Response data model is not configured for this interface, returning the entire api response");
							responseBody.setResponseObj(extApiResponse);
						}
					} else {
						responseBody.setResponseObj(extApiResponse);
					}
					responseHeader.setResponseCode(CommonConstants.SUCCESS);
					responseHeader.setErrorCode("");
					responseHeader.setResponseMessage("");
				} else if ("[]".equalsIgnoreCase(extApiResponse) || "{}".equalsIgnoreCase(extApiResponse)) {
					logger.debug("empty response received");
					responseBody.setResponseObj(extApiResponse);
					responseHeader.setErrorCode("8");
					responseHeader.setResponseCode(CommonConstants.SUCCESS);
					responseHeader.setResponseMessage("Empty Object response received from the API");
				} else {
					logger.debug("Else case handler for response parser.");
					responseBody.setResponseObj("");
					responseHeader.setErrorCode("600");
					responseHeader.setResponseCode(CommonConstants.FAILURE);
					responseHeader.setResponseMessage(Errors.PROCESSINGREQUESTERROR.getErrorMessage());
				}
			} else {
				logger.debug("empty response received");
				responseBody.setResponseObj("");
				responseHeader.setErrorCode("7");
				responseHeader.setResponseCode(CommonConstants.FAILURE);
				responseHeader.setResponseMessage("Empty response received from the API");
			}
		} catch (Exception e) { 
			logger.error("Exception Occured::", e);
			responseBody.setResponseObj("");
			responseHeader.setErrorCode("7");
			responseHeader.setResponseCode(CommonConstants.FAILURE);
			responseHeader.setResponseMessage("API Execution Failed.");
		}
		response.setResponseHeader(responseHeader);
		response.setResponseBody(responseBody);
		responseWrapper.setApiResponse(response);
		return responseWrapper;
	}
}
