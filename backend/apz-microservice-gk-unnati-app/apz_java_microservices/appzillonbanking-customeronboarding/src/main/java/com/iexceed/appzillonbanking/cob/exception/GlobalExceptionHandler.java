package com.iexceed.appzillonbanking.cob.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;


/**
 * @author Ankit.G
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Gson gson = new Gson();

	@ExceptionHandler(ReplicationFailedException.class)
	public ResponseEntity<ResponseWrapper> handleReplicationFailed(ReplicationFailedException ex) {

		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		Response response = new Response();
		ResponseWrapper wrapper = new ResponseWrapper();

		if ("callReplicateApplicationDetails Data sink Completed Error in FetchApplication call"
				.equals(ex.getMessage())) {

			respHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
			respHeader.setHttpStatus(HttpStatus.OK);
			responseBody.setResponseObj("Deeplink completed (Error in Application Master Call)");
			response.setResponseHeader(respHeader);
			response.setResponseBody(responseBody);
			wrapper.setApiResponse(response);

			return new ResponseEntity<>(wrapper, HttpStatus.OK);
		}

		Map<String, String> errorMap = new LinkedHashMap<>();
		errorMap.put("Status", "FAIL");
		errorMap.put("message", ex.getMessage());
		errorMap.put("data", null);
		if (ex.getTableName() != null) {
			errorMap.put("failedTable", ex.getTableName());
		}
		String jsonString = gson.toJson(errorMap);

		respHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		respHeader.setHttpStatus(HttpStatus.BAD_REQUEST);

		responseBody.setResponseObj(jsonString);

		response.setResponseHeader(respHeader);
		response.setResponseBody(responseBody);

		wrapper.setApiResponse(response);

		return new ResponseEntity<>(wrapper, HttpStatus.BAD_REQUEST);
	}

}
