package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FetchRoleRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "initiator")
	@JsonProperty("roleId")
	private String roleId;
	
	@ApiModelProperty(required = true, position = 2, example = "john")
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(position = 3, example = "")
	@JsonProperty("branchCode")
	private String branchCode;
	
	@ApiModelProperty(position = 4, example = "")
	@JsonProperty("fetchType")
	private String fetchType;

	@ApiModelProperty(position = 5, example = "")
	@JsonProperty("searchVal")
	private String searchVal;

	@ApiModelProperty(position = 6, example = "")
	@JsonProperty("fetchRole")
	private String fetchRole;

	@ApiModelProperty(position = 7, example = "1")
	@JsonProperty("pageNo")
	private int pageNo;

	@ApiModelProperty(position = 8, example = "freshCases")
	@JsonProperty("filterType")
	private String filterType;

	@ApiModelProperty(position = 9, example = "unnati")
	@JsonProperty("productType")
	private String productType;

    @JsonProperty("filterList")
    private List<String> filterList;

}
