package com.iexceed.appzillonbanking.cob.core.services;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iexceed.appzillonbanking.cob.constants.CommonConstants;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.utils.AdapterUtil;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;

import reactor.core.publisher.Mono;

@Component
public class InterfaceAdapter {

	@Autowired
	private HttpInterfaceParser httpInterfaceParser;

	@Autowired
	private SoapInterfaceParser soapInterfaceParser;

	@Autowired
	private AdapterUtil adapterUtil;

	private static final Logger logger = LogManager.getLogger(InterfaceAdapter.class);

	public Mono<Object> executeInterfaceAdapter(Object interfaceRequest, String interfaceName, Header header) {

		logger.debug("Start : interface adapter flow");
		JSONObject apiResponse = new JSONObject();

		Mono<Object> apiResponseMono = Mono.just(new JSONObject());

		try {
			ObjectMapper mapperObj = new ObjectMapper();

			mapperObj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			JSONObject requestWrapper = new JSONObject(mapperObj.writeValueAsString(interfaceRequest));

			JSONObject request = new JSONObject();
			request.put(CommonConstants.API_REQUEST, requestWrapper);
			logger.debug("Start : interface adapter request = " + request);

			String interfaceFileName = interfaceName + ".apzinterface";
			logger.debug("interfaceFileName =" + interfaceFileName);

			String interfaceFileContent = adapterUtil.readInterfaceContentFromServer(interfaceFileName);
			logger.debug("interfaceFileContent = " + interfaceFileContent);

			JSONObject interfaceJsonContent = new JSONObject(interfaceFileContent);
			String serviceType = interfaceJsonContent.get("servicetype").toString();

			if (serviceType.equalsIgnoreCase("HTTP")) {

				logger.debug("Service type is http");
				logger.debug("service start : " + LocalDateTime.now());
				apiResponseMono = httpInterfaceParser.callService(request, interfaceJsonContent, header);
				logger.debug("service end : " + LocalDateTime.now());
			} else if (serviceType.equalsIgnoreCase("SOAP")) {

				logger.debug("Service type is soap : not in use");
//				apiResponse = soapInterfaceParser.callService(request, interfaceJsonContent, header);
			} else {
				logger.debug("Invalid Service Type");
				apiResponseMono = adapterUtil
						.setErrorMono("Invalid Service Type. Please configure the interface type as HTTP/SOAP", "3");
			}
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException occurred while calling the service file not found, error = ", e);
			apiResponseMono = adapterUtil.setErrorMono("Please upload the interface file in the configured path.", "2");
		} catch (JSONException e) {
			logger.error("JSONException occurred while calling the service, error = ", e);
			apiResponseMono = adapterUtil.setErrorMono(Constants.REQ_RESP_MISSING, "5");
		} catch (Exception e) {
			logger.error("Exception occurred while calling the service, error = ", e);
			apiResponseMono = adapterUtil.setErrorMono(Constants.REQ_RESP_MISSING, "5");

		}
		logger.debug("End : adapter flow with response = " + apiResponse);
		return apiResponseMono;
	}

	public Mono<Object> callExternalService(Header header, Object restApiRequest, String interfaceName) {

		logger.debug("Start : callExternalService : "+restApiRequest.toString());
		return executeInterfaceAdapter(restApiRequest, interfaceName, header);
	}
}
