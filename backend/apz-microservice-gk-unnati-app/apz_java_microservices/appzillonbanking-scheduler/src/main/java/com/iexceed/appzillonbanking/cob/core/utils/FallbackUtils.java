package com.iexceed.appzillonbanking.cob.core.utils;

import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import reactor.core.publisher.Mono;

public class FallbackUtils {

	public static Mono<Object> genericFallbackMonoObject() {
		ResponseHeader responseHeader = new ResponseHeader();
		CommonUtils.generateHeaderForGenericError(responseHeader);
		return getFallbackResponseMonoObject(responseHeader);
	}

	private static Mono<Object> getFallbackResponseMonoObject(ResponseHeader responseHeader) {
		ResponseBody responseBody = new ResponseBody();
		responseBody.setResponseObj("");
		Response fallbackResponse = new Response();
		fallbackResponse.setResponseHeader(responseHeader);
		fallbackResponse.setResponseBody(responseBody);
		return Mono.just(fallbackResponse);
	}

	private static Mono<Response> getFallbackResponseMono(ResponseHeader responseHeader) {
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResponseObj("");
        Response fallbackResponse = new Response();
        fallbackResponse.setResponseHeader(responseHeader);
        fallbackResponse.setResponseBody(responseBody);
        return Mono.just(fallbackResponse);
    }
	
	public static Mono<Response> genericFallbackMono() {
		ResponseHeader responseHeader = new ResponseHeader();
		CommonUtils.generateHeaderForGenericError(responseHeader);
		return getFallbackResponseMono(responseHeader);
	}
}