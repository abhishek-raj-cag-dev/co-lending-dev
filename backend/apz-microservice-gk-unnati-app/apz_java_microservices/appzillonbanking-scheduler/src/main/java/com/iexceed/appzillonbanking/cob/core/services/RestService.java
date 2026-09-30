package com.iexceed.appzillonbanking.cob.core.services;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.iexceed.appzillonbanking.cob.core.exception.CustomException;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.logs.service.LogExternalReqRes;

import reactor.core.publisher.Mono;

@Component
public class RestService {

	private static final Logger logger = LogManager.getLogger(RestService.class);

	@Autowired
	private WebClient webClient;

	@Autowired
	private LogExternalReqRes logService;

	Object patchResponse = "";

	public Mono<Object> executeRestApi(String restRequest, JSONArray headerParams, String methodType,
			String mediaTypeString, String endPointUrl, JSONObject interfaceJsonContent, Header header) {
		logger.debug("Start : executeRestApi with request = " + restRequest.toString());

		Mono<Object> webClientAPIResponse = Mono.empty();

		StringBuilder builder = new StringBuilder(endPointUrl);
		URI uri = URI.create(builder.toString());
		logger.debug("API URL::" + uri);
		int apiTimeout = 30;
		if (interfaceJsonContent.has(Constants.TIMEOUT) && !"".equalsIgnoreCase(interfaceJsonContent.getString(Constants.TIMEOUT))) {
			apiTimeout = Integer.parseInt(interfaceJsonContent.getString(Constants.TIMEOUT));
		}
		logger.debug("apiTimeout::" + apiTimeout);
		String respType = "";
		if (interfaceJsonContent.has("rescontenttype")) {
		respType = interfaceJsonContent.getString("rescontenttype");
		}
		
		if ("POST".equalsIgnoreCase(methodType)) {
			if("PDF".equalsIgnoreCase(respType)) {
				 // Increase the max buffer size to 10 MB
		        int maxInMemorySize = 10 * 1024 * 1024;  // 10 MB
		        
		        // Use default WebClient builder and configure buffer limit
		        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
		                .codecs(configurer -> configurer.defaultCodecs()
		                    .maxInMemorySize(maxInMemorySize))  // Set max buffer size
		                .build();
		        this.webClient = WebClient.builder()
                        .exchangeStrategies(exchangeStrategies)
                        .build();
				webClientAPIResponse = webClient.post().uri(uri)
						.headers(httpHeaders(headerParams, endPointUrl, mediaTypeString))
						.body(BodyInserters.fromValue(restRequest)).retrieve()
						.onStatus(HttpStatus::is4xxClientError, err -> httpStatusErrResponse(err)).bodyToMono(byte[].class)
		                .map(pdfBytes -> (Object) pdfBytes)
						.doOnSuccess(value -> {
							logTxnDtlsToDB(header, restRequest, value.toString(), LocalDateTime.now(), "S", "JSON",
									interfaceJsonContent);
						}).doOnError(error -> {
							logTxnDtlsToDB(header, restRequest, error.getMessage(), LocalDateTime.now(), "F", "JSON",
									interfaceJsonContent);
						});
			}else {
			webClientAPIResponse = webClient.post().uri(uri)
					.headers(httpHeaders(headerParams, endPointUrl, mediaTypeString))
					.body(BodyInserters.fromValue(restRequest)).retrieve()
					.onStatus(HttpStatus::is4xxClientError, err -> httpStatusErrResponse(err)).bodyToMono(Object.class)
					.timeout(Duration.ofSeconds(apiTimeout)).doOnSuccess(value -> {
						logTxnDtlsToDB(header, restRequest, value.toString(), LocalDateTime.now(), "S", "JSON",
								interfaceJsonContent);
					}).doOnError(error -> {
						logTxnDtlsToDB(header, restRequest, error.getMessage(), LocalDateTime.now(), "F", "JSON",
								interfaceJsonContent);
					});
			}
		} else if ("GET".equalsIgnoreCase(methodType)) {
			webClientAPIResponse = webClient.get().uri(uri)
					.headers(httpHeaders(headerParams, endPointUrl, mediaTypeString)).retrieve()
					.onStatus(HttpStatus::is4xxClientError, err -> httpStatusErrResponse(err)).bodyToMono(Object.class)
					.timeout(Duration.ofSeconds(apiTimeout)).doOnSuccess(value -> {
						logTxnDtlsToDB(header, restRequest, value.toString(), LocalDateTime.now(), "S", "JSON",
								interfaceJsonContent);
					}).doOnError(error -> {
						logTxnDtlsToDB(header, restRequest, error.getMessage(), LocalDateTime.now(), "F", "JSON",
								interfaceJsonContent);
					});
		} else if ("PUT".equalsIgnoreCase(methodType)) {
			webClientAPIResponse = webClient.put().uri(uri)
					.headers(httpHeaders(headerParams, endPointUrl, mediaTypeString))
					.body(BodyInserters.fromValue(restRequest)).retrieve()
					.onStatus(HttpStatus::is4xxClientError, err -> httpStatusErrResponse(err)).bodyToMono(Object.class)
					.timeout(Duration.ofSeconds(apiTimeout)).doOnSuccess(value -> {
						logTxnDtlsToDB(header, restRequest, value.toString(), LocalDateTime.now(), "S", "JSON",
								interfaceJsonContent);
					}).doOnError(error -> {
						logTxnDtlsToDB(header, restRequest, error.getMessage(), LocalDateTime.now(), "F", "JSON",
								interfaceJsonContent);
					});
		} else if ("PATCH".equalsIgnoreCase(methodType)) {
			webClientAPIResponse = webClient.patch().uri(uri)
					.headers(httpHeaders(headerParams, endPointUrl, mediaTypeString))
					.body(BodyInserters.fromValue(restRequest)).retrieve()
					.onStatus(HttpStatus::is4xxClientError, err -> httpStatusErrResponse(err)).bodyToMono(Object.class)
					.timeout(Duration.ofSeconds(apiTimeout)).doOnSuccess(value -> {
						logTxnDtlsToDB(header, restRequest, value.toString(), LocalDateTime.now(), "S", "JSON",
								interfaceJsonContent);
					}).doOnError(error -> {
						logTxnDtlsToDB(header, restRequest, error.getMessage(), LocalDateTime.now(), "F", "JSON",
								interfaceJsonContent);
					});
		} else if ("DELETE".equalsIgnoreCase(methodType)) {
			webClientAPIResponse = webClient.delete().uri(uri)
					.headers(httpHeaders(headerParams, endPointUrl, mediaTypeString)).retrieve()
					// .onStatus(HttpStatus::is5xxServerError, err -> httpStatusErrResponse(err))
					.onStatus(HttpStatus::is4xxClientError, err -> httpStatusErrResponse(err)).bodyToMono(Object.class)
					.timeout(Duration.ofSeconds(apiTimeout)).doOnSuccess(value -> {
						logTxnDtlsToDB(header, restRequest, value.toString(), LocalDateTime.now(), "S", "JSON",
								interfaceJsonContent);
					}).doOnError(error -> {
						logTxnDtlsToDB(header, restRequest, error.getMessage(), LocalDateTime.now(), "F", "JSON",
								interfaceJsonContent);
					});
		}

		return webClientAPIResponse;

	}

	private static Mono<Error> httpStatusErrResponse(ClientResponse response) {

		logger.debug("Inside method for handling the httpStatusErrResponse" + response);
		return response.bodyToMono(String.class).flatMap(body -> {
			logger.debug("Inside 4xx or 5xx Error Response Body is {}" + body);
			JSONObject apiErrRespJSON = new JSONObject();
			int statusCode = response.rawStatusCode();
			apiErrRespJSON.put("errorCode", String.valueOf(statusCode));
			apiErrRespJSON.put("errorMessage", new JSONObject(body));
			return Mono.error(new CustomException(apiErrRespJSON.toString()));
		});
	}

	private void logTxnDtlsToDB(Header header, String request, String response, LocalDateTime startDateTime,
			String status, String requestType, JSONObject interfaceJsonContent) {
		logService.logTransactionToDb(header, request, response, startDateTime, status,
				requestType, interfaceJsonContent);
	}

	private Consumer<HttpHeaders> httpHeaders(JSONArray headersArr, String endPointUrl, String mediaTypeString) {
		return headers -> {
			logger.debug("Headers Array::" + headersArr);
			for (int i = 0; i < headersArr.length(); i++) {
				JSONObject tempObj = headersArr.getJSONObject(i);
				String name = tempObj.get("name").toString();
				String value = tempObj.get("value").toString();
				// Added the check to avoid adding duplicate headers
				headers.set(name, value);
			}
			headers.set("Content-Type", mediaTypeString);
			headers.set("Accept", mediaTypeString);
			logger.debug("Final Header Parameters value::" + headers.toString());
		};
	}
}
