package com.iexceed.appzillonbanking.cob.core.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RPCStatDetails {

	@JsonProperty("stageID")
	private int stageID;

	@JsonProperty("custType")
	private String custType;

	@JsonProperty("query")
	private List<String> query;

	@JsonProperty("editedFields")
	private List<String> editedFields;
}