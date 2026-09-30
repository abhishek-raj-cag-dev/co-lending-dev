package com.iexceed.appzillonbanking.cob.core.utils;

import com.iexceed.appzillonbanking.cob.core.services.RestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

@Component
public class ExternalIntReqResMapper {

    private static final Logger logger = LogManager.getLogger(RestService.class);

    private LinkedHashMap<String, String> queryParamsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, String> pathParamsMap = new LinkedHashMap<>();

    /*
     * Calling obtainSimpleJson method to obtain a JSON containing all the nodes and
     * its elements. The values for different interface elements are also fetched in
     * this method. Input : Java object from input and the JSON array containing the
     * interface element details and the property file path Output: JSONObject
     * containing all the nodes and its elements of the interface with the values
     * assigned from any of the four ways
     */
    public JSONObject obtainSimpleRestJson(JSONObject reqObj, JSONArray parsedArray) throws Exception {

        JSONObject finalJson = new JSONObject();
        String finalJsonString = "";
        logger.debug("obtainSimpleRestJson reqObj = " + reqObj);

        BaseDynamicValue gcObj = new BaseDynamicValue();
        CustomDynamicValue cuObj = new CustomDynamicValue();
        AdapterUtil adapterUtil = new AdapterUtil();

        for (int i = 0, size = parsedArray.length(); i < size; i++) {
            JSONObject outputJson = new JSONObject();
            JSONObject reqJson = parsedArray.getJSONObject(i);
            if (reqJson.has("defaultvalue") && reqJson.has("relatednode")) {
                String defaultValue = reqJson.getString("defaultvalue");
                String entireKey;
                String[] key;
                String keyName = reqJson.getString("name");
                String value = "";

                String completeString = "";
                JSONObject getValueJson;
                int diff = 0;

                entireKey = defaultValue.substring((defaultValue.indexOf('~')) + 1);
                String nodeName = reqJson.getString("relatednode");

                if (defaultValue.toLowerCase().contains("object")) {
                    key = entireKey.split("\\.");
                    int length = key.length;

                    for (int j = 0; j < length; j++) {
                        diff = length - j;
                        if (diff == 1) {
                            if (completeString.length() > 0) {
                                getValueJson = new JSONObject(completeString);

                                if (getValueJson.get(key[j]) instanceof JSONArray) {
                                    JSONArray tempArray1 = new JSONArray();
                                    String tempValue1 = getValueJson.get(key[j]).toString();
                                    String[] arrValues1 = tempValue1.split(",");

                                    for (String arrValue : arrValues1) {
                                        tempArray1.put(arrValue);
                                    }

                                    value = tempArray1.toString();
                                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                } else {
                                    value = getValueJson.get(key[j]).toString();
                                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                }
                            } else {
                                if (reqObj.get(key[j]) instanceof JSONArray) {
                                    JSONArray tempArray3 = reqObj.getJSONArray(key[j]);
                                    value = tempArray3.toString();
                                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                } else {
                                    value = reqObj.get(key[j]).toString();
                                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                }
                            }

                        } else {

                            if (j == 0) {
                                if (reqObj.has(key[j])) {
                                    if (reqObj.get(key[j]) instanceof JSONArray) {
                                        completeString = reqObj.getJSONArray(key[j]).toString();
                                    } else if (reqObj.get(key[j]) instanceof JSONObject) {
                                        completeString = reqObj.getJSONObject(key[j]).toString();
                                    }
                                }
                            } else {
                                // handling arrays
                                if (completeString.startsWith("[{")) {
                                    JSONArray tempArr = new JSONArray(completeString);

                                    for (int k = 0; k < tempArr.length(); k++) {
                                        JSONObject tempJson = tempArr.getJSONObject(k);

                                        if (tempJson.has(key[j])) {
                                            completeString = tempJson.get(key[j]).toString();
                                            break;
                                        }
                                    }
                                } else {
                                    JSONObject tempObj = new JSONObject(completeString);
                                    completeString = tempObj.get(key[j]).toString();
                                }
                            }
                        }
                    }
                } else if (defaultValue.toLowerCase().contains("file")) {
                    value = adapterUtil.getPropertyValueFromServer(entireKey);
                    if (value == null) {
                        logger.debug("Required parameters are not configured in the property file");
                        throw new IOException("Please configure the required parameters in the property file");
                    }

                    outputJson.put(keyName, value);
                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                } else if (defaultValue.toLowerCase().contains("basejavacode")) {
                    value = gcObj.generateValue(defaultValue);
                    if (value == null) {
                        logger.debug("Error in Javacode element configuration");
                        throw new Exception("Error in Interface Configuration");
                    }
                    outputJson.put(keyName, value);
                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                } else if (defaultValue.toLowerCase().contains("customjavacode")) {
                    value = cuObj.generateValue(defaultValue);
                    if (value == null) {
                        logger.debug("Error in Javacode element configuration");
                        throw new Exception("Error in Interface Configuration");
                    }

                    outputJson.put(keyName, value);
                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                } else if (defaultValue.toLowerCase().contains("const")) {
                    value = entireKey;
                    outputJson.put(keyName, value);
                    finalJson = adapterUtil.formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                } else if (defaultValue.toLowerCase().contains("queryparam")) {
                    key = entireKey.split("\\.");
                    int length = key.length;

                    for (int j = 0; j < length; j++) {
                        diff = length - j;
                        if (diff == 1) {
                            if (completeString.length() > 0) {
                                getValueJson = new JSONObject(completeString);

                                if (getValueJson.get(key[j]) instanceof JSONArray) {
                                    JSONArray tempArray1 = new JSONArray();
                                    String tempValue1 = getValueJson.get(key[j]).toString();
                                    String[] arrValues1 = tempValue1.split(",");

                                    for (String arrValue : arrValues1) {
                                        tempArray1.put(arrValue);
                                    }

                                    value = tempArray1.toString();
                                    queryParamsMap.put(keyName, value);
                                } else {
                                    value = getValueJson.get(key[j]).toString();
                                    queryParamsMap.put(keyName, value);
                                }
                            } else {
                                if (reqObj.get(key[j]) instanceof JSONArray) {
                                    JSONArray tempArray3 = reqObj.getJSONArray(key[j]);
                                    value = tempArray3.toString();
                                } else {
                                    value = reqObj.get(key[j]).toString();
                                }
                            }

                        } else {

                            if (j == 0) {
                                if (reqObj.has(key[j])) {
                                    if (reqObj.get(key[j]) instanceof JSONArray) {
                                        completeString = reqObj.getJSONArray(key[j]).toString();
                                    } else if (reqObj.get(key[j]) instanceof JSONObject) {
                                        completeString = reqObj.getJSONObject(key[j]).toString();
                                    }
                                }
                            } else {
                                // handling arrays
                                if (completeString.startsWith("[{")) {
                                    JSONArray tempArr = new JSONArray(completeString);

                                    for (int k = 0; k < tempArr.length(); k++) {
                                        JSONObject tempJson = tempArr.getJSONObject(k);

                                        if (tempJson.has(key[j])) {
                                            completeString = tempJson.get(key[j]).toString();
                                            break;
                                        }
                                    }
                                } else {
                                    JSONObject tempObj = new JSONObject(completeString);
                                    completeString = tempObj.get(key[j]).toString();
                                }
                            }
                        }
                    }

                    logger.debug("Skipping to the next iteration since query params should not be considered for request body formation");
                    continue;
                } else if (defaultValue.toLowerCase().contains("pathparam")) {
                    key = entireKey.split("\\.");
                    int length = key.length;

                    for (int j = 0; j < length; j++) {
                        diff = length - j;
                        if (diff == 1) {
                            if (completeString.length() > 0) {
                                getValueJson = new JSONObject(completeString);

                                if (getValueJson.get(key[j]) instanceof JSONArray) {
                                    JSONArray tempArray1 = new JSONArray();
                                    String tempValue1 = getValueJson.get(key[j]).toString();
                                    String[] arrValues1 = tempValue1.split(",");

                                    for (String arrValue : arrValues1) {
                                        tempArray1.put(arrValue);
                                    }

                                    value = tempArray1.toString();
                                    pathParamsMap.put(keyName, value);
                                    // finalJson = formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                } else {
                                    value = getValueJson.get(key[j]).toString();
                                    pathParamsMap.put(keyName, value);
                                    // finalJson = formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                }
                            } else {
                                if (reqObj.get(key[j]) instanceof JSONArray) {
                                    JSONArray tempArray3 = reqObj.getJSONArray(key[j]);
                                    value = tempArray3.toString();
                                    // finalJson = formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                } else {
                                    value = reqObj.get(key[j]).toString();
                                    // finalJson = formSimpleJson(finalJson, nodeName, keyName, value, outputJson);
                                }
                            }

                        } else {

                            if (j == 0) {
                                if (reqObj.has(key[j])) {
                                    if (reqObj.get(key[j]) instanceof JSONArray) {
                                        completeString = reqObj.getJSONArray(key[j]).toString();
                                    } else if (reqObj.get(key[j]) instanceof JSONObject) {
                                        completeString = reqObj.getJSONObject(key[j]).toString();
                                    }
                                }
                            } else {
                                // handling arrays
                                if (completeString.startsWith("[{")) {
                                    JSONArray tempArr = new JSONArray(completeString);

                                    for (int k = 0; k < tempArr.length(); k++) {
                                        JSONObject tempJson = tempArr.getJSONObject(k);

                                        if (tempJson.has(key[j])) {
                                            completeString = tempJson.get(key[j]).toString();
                                            break;
                                        }
                                    }
                                } else {
                                    JSONObject tempObj = new JSONObject(completeString);
                                    completeString = tempObj.get(key[j]).toString();
                                }
                            }
                        }
                    }

                    logger.debug("Skipping to the next iteration since path params should not be considered for request body formation");
                    continue;
                }

                if (finalJson.has(nodeName)) {
                    String currentContents = finalJson.get(nodeName).toString();
                    finalJson.remove(nodeName);

                    JSONArray jsonArr = new JSONArray();

                    // support for arrays
                    if ((value != null) && (value.startsWith("[")) && (value.endsWith("]"))) {
                        value = value.replace("[", "").replace("]", "");
                        Object[] arrContents = value.split(",");

                        for (Object str : arrContents) {
                            jsonArr.put(str);
                        }

                        JSONObject completeJson = new JSONObject(currentContents);

                        completeJson.put(keyName, jsonArr);
                        finalJson.put(nodeName, completeJson);
                    } else {
                        JSONObject completeJson = new JSONObject(currentContents);

                        completeJson.put(keyName, value);
                        finalJson.put(nodeName, completeJson);
                    }
                } else {
                    finalJson.put(nodeName, outputJson);
                }
            } else {
                logger.debug("Please configure the default value and related node parameters");
                throw new Exception("Please configure the default value and related node parameters");
            }
        }

        finalJsonString = (finalJson.toString());
        finalJson = new JSONObject(finalJsonString);
        /*
         * if (finalJson.has("AppzillonRequest")) { JSONObject tempFinalJson =
         * finalJson.getJSONObject("AppzillonRequest");
         * finalJson.remove("AppzillonRequest");
         *
         * Iterator<String> keys = tempFinalJson.keys(); while (keys.hasNext()) { String
         * nodeName = keys.next(); String nodeValue =
         * tempFinalJson.get(nodeName).toString(); finalJson.put(nodeName, nodeValue); }
         * }
         */
        if (finalJson.has("AppzillonRequest")) {
            JSONObject tempFinalJson = finalJson.getJSONObject("AppzillonRequest");
            finalJson = tempFinalJson;
        }
        return finalJson;
    }

    public void reinitializeGlobalVariables() {
        logger.debug("Start : reinitializeGlobalVariables");
        logger.debug("re-initializing global variables");

        queryParamsMap = new LinkedHashMap<>();
        pathParamsMap = new LinkedHashMap<>();
        logger.debug("queryParamsMap = " + queryParamsMap);
        logger.debug("pathParamsMap = " + pathParamsMap);

        logger.debug("End : reinitializeGlobalVariables");
    }

    public LinkedHashMap<String, String> fetchQueryParams() {
        return queryParamsMap;
    }

    public LinkedHashMap<String, String> fetchPathParams() {
        return pathParamsMap;
    }

    public LinkedHashMap<String, ArrayList<String>> obtainRestInterfaceNodes(JSONObject fileJson) {
        JSONObject reqJson;
        JSONArray reqJsonArray;

        LinkedHashMap<String, ArrayList<String>> hmap = new LinkedHashMap<>();
        Iterator<String> keys = fileJson.keys();

        while (keys.hasNext()) {
            String key = keys.next();

            if (fileJson.get(key) instanceof JSONArray) {
                reqJsonArray = fileJson.getJSONArray(key);

                for (int i = 0; i < reqJsonArray.length(); i++) {
                    reqJson = (JSONObject) reqJsonArray.get(i);
                    String type = reqJson.get("type").toString();

                    // Path param and query param should not be considered in request body
                    if (type.equalsIgnoreCase("ELEMENT") && (!((reqJson.get("defaultvalue").toString().toLowerCase()).contains("queryparam"))) && (!((reqJson.get("defaultvalue").toString().toLowerCase()).contains("pathparam")))) {
                        String parentNode = reqJson.has("alias") ? (reqJson.get("alias").toString() + ":" + reqJson.get("relatednode").toString()) : (reqJson.get("relatednode").toString());
                        String childNode = reqJson.has("alias") ? (reqJson.get("alias").toString() + ":" + reqJson.get("name").toString()) : (reqJson.get("name").toString());

                        if (hmap.containsKey(parentNode)) {
                            ArrayList<String> childNodeList = hmap.get(parentNode);
                            childNodeList.add(childNode);

                            hmap.remove(parentNode);
                            hmap.put(parentNode, childNodeList);
                        } else {
                            ArrayList<String> childNodeList = new ArrayList<>();
                            childNodeList.add(childNode);
                            hmap.put(parentNode, childNodeList);
                        }
                    }
                }
            }
        }
        return hmap;
    }

    public JSONObject obtainRequiredRestApiResponse(JSONObject apiResponse, JSONObject interfaceResponseObject) throws Exception {
        logger.debug("Start : obtainRequiredRestApiResponse");
        AdapterUtil adapterUtil = new AdapterUtil();

        LinkedHashMap<String, ArrayList<String>> finalMap = adapterUtil.obtainInterfaceNodes(interfaceResponseObject);

        LinkedHashMap<String, ArrayList<String>> elementsMap = obtainRestInterfaceNodes(interfaceResponseObject);

        finalMap.putAll(elementsMap);
        logger.debug("response finalMap = " + finalMap);

        ArrayList<JSONObject> parsedList = adapterUtil.obtainInterfaceElements(interfaceResponseObject);

        JSONArray parsedArray = new JSONArray(parsedList.toString());
        logger.debug("response parsedArray = " + parsedArray);

        JSONObject simpleJson = obtainResponseJson(apiResponse, parsedArray);
        logger.debug("response final Json = " + simpleJson);

        JSONObject nestedResponseJson = adapterUtil.obtainNestedJson(simpleJson, finalMap);
        logger.debug("nestedResponseJson = " + nestedResponseJson);

        logger.debug("End : obtainRequiredRestApiResponse");
        return nestedResponseJson;
    }

    public JSONObject obtainResponseJson(JSONObject restResponseObj, JSONArray parsedArray) throws Exception {

        JSONObject finalJson = new JSONObject();
        AdapterUtil adapterUtil = new AdapterUtil();
        logger.debug("obtainSimpleResponseJson reqObj = " + restResponseObj);

        for (int i = 0, size = parsedArray.length(); i < size; i++) {
            JSONObject outputJson = new JSONObject();
            JSONObject reqJson = parsedArray.getJSONObject(i);
            logger.debug("current iteration = " + parsedArray.getJSONObject(i));
            if (reqJson.has("defaultvalue") && reqJson.has("relatednode")) {
                String defaultValue = reqJson.getString("defaultvalue");
                String entireKey;
                String[] key;
                String keyName = reqJson.getString("name");
                String value = "";

                String completeString = "";
                JSONObject getValueJson;
                int diff = 0;

                entireKey = defaultValue.substring((defaultValue.indexOf('~')) + 1);
                String nodeName = reqJson.getString("relatednode");

                if (defaultValue.toLowerCase().contains("object")) {
                    key = entireKey.split("\\.");
                    int length = key.length;

                    for (int j = 0; j < length; j++) {
                        diff = length - j;
                        if (diff == 1) {
                            if ((completeString.length() > 0) && (completeString.startsWith("{"))) {
                                getValueJson = new JSONObject(completeString);

                                if ((getValueJson.has(key[j])) && (getValueJson.get(key[j]) instanceof JSONArray)) {
                                    JSONArray tempArray1 = new JSONArray();
                                    String tempValue1 = getValueJson.get(key[j]).toString();
                                    String[] arrValues1 = tempValue1.split(",");

                                    for (String arrValue : arrValues1) {
                                        tempArray1.put(arrValue);
                                    }

                                    value = tempArray1.toString();
                                    finalJson = adapterUtil.formSimpleResponseJson(finalJson, nodeName, keyName, value, outputJson);
                                } else {
                                    value = getValueJson.has(key[j]) ? getValueJson.get(key[j]).toString() : "";
                                    finalJson = adapterUtil.formSimpleResponseJson(finalJson, nodeName, keyName, value, outputJson);
                                }
                            } else if ((completeString.length() > 0) && (completeString.startsWith("[{"))) {
                                JSONArray getValueJsonArr = new JSONArray(completeString);
                                JSONArray requiredArr = new JSONArray();

                                for (int k = 0; k < getValueJsonArr.length(); k++) {
                                    JSONObject tempJson = getValueJsonArr.getJSONObject(k);

                                    if (tempJson.has(key[j])) {
                                        requiredArr.put(tempJson.get(key[j]));
                                    }
                                }

                                value = requiredArr.toString();
                                logger.debug("final arr = " + requiredArr);
                                finalJson = adapterUtil.formSimpleResponseJson(finalJson, nodeName, keyName, value, outputJson);
                            } else {
                                if ((restResponseObj.has(key[j])) && (restResponseObj.get(key[j]) instanceof JSONArray)) {
                                    JSONArray tempArray3 = restResponseObj.getJSONArray(key[j]);
                                    value = tempArray3.toString();
                                    finalJson = adapterUtil.formSimpleResponseJson(finalJson, nodeName, keyName, value, outputJson);
                                } else {
                                    if (restResponseObj.has(key[j])) {
                                        value = restResponseObj.get(key[j]).toString();
                                        finalJson = adapterUtil.formSimpleResponseJson(finalJson, nodeName, keyName, value, outputJson);
                                    }
                                }
                            }

                        } else {

                            if (j == 0) {
                                if (restResponseObj.has(key[j])) {
                                    if (restResponseObj.get(key[j]) instanceof JSONArray) {
                                        completeString = restResponseObj.getJSONArray(key[j]).toString();
                                    } else if (restResponseObj.get(key[j]) instanceof JSONObject) {
                                        completeString = restResponseObj.getJSONObject(key[j]).toString();
                                    }
                                }
                            } else {
                                // handling arrays
                                if (completeString.startsWith("[{")) {
                                    JSONArray tempArr = new JSONArray(completeString);
                                    JSONArray requiredArr = new JSONArray(completeString);

                                    for (int k = 0; k < tempArr.length(); k++) {
                                        JSONObject tempJson = tempArr.getJSONObject(k);

                                        if (tempJson.has(key[j])) {
                                            requiredArr.put(tempJson.get(key[j]));
                                        }
                                    }

                                    logger.debug("requiredArr = " + requiredArr);
                                    completeString = requiredArr.toString();
                                } else {
                                    JSONObject tempObj = new JSONObject(completeString);
                                    completeString = tempObj.get(key[j]).toString();
                                }
                            }
                        }
                    }
                }

                if (finalJson.has(nodeName)) {
                    String currentContents = finalJson.get(nodeName).toString();
                    finalJson.remove(nodeName);

                    // support for arrays
                    if ((value != null) && (value.startsWith("[")) && (value.endsWith("]"))) {
                        JSONArray jsonArr = new JSONArray(value);

                        if ((currentContents != null) && (currentContents.startsWith("[")) && (currentContents.endsWith("]"))) {
                            JSONArray completeJson = new JSONArray(currentContents);
                            finalJson.put(nodeName, completeJson);
                        } else {
                            JSONObject completeJson = new JSONObject(currentContents);
                            completeJson.put(keyName, jsonArr);
                            finalJson.put(nodeName, completeJson);
                        }
                    } else {
                        JSONObject completeJson = new JSONObject(currentContents);

                        completeJson.put(keyName, value);
                        finalJson.put(nodeName, completeJson);
                    }
                } else {
                    finalJson.put(nodeName, outputJson);
                }
            } else {
                logger.debug("Please configure the default value and related node parameters");
                throw new Exception("Please configure the default value and related node parameters");
            }
        }

        if (finalJson.has("AppzillonResponse")) {
            JSONObject tempFinalJson = finalJson.getJSONObject("AppzillonResponse");
            finalJson.remove("AppzillonResponse");

            Iterator<String> keys = tempFinalJson.keys();
            while (keys.hasNext()) {
                String nodeName = keys.next();
                String nodeValue = tempFinalJson.get(nodeName).toString();
                finalJson.put(nodeName, nodeValue);
            }
        }

        return finalJson;
    }

}
