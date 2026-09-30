package com.iexceed.appzillonbanking.cob.core.payload;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BCMPIStatDetails {

    @JsonProperty("stageID")
	private int stageID;

	@JsonProperty("custType")
	private String custType;

	@JsonProperty("query")
	private List<String> query;

	@JsonProperty("editedFields")
	private List<String> editedFields;

	@JsonProperty("rejectReasons")
	private List<String> rejectReasons;

	@JsonProperty("timeStamp")
	private List<LocalDateTime> timeStamp;
}
