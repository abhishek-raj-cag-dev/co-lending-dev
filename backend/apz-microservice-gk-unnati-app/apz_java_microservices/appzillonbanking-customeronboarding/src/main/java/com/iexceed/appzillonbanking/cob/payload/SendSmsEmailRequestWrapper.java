package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSmsEmailRequestWrapper {

	@JsonProperty("apiRequest")
	private SendSmsAndEmailApiRequest apiRequest;
}

