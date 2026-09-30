package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class KycDedupeRequestFields {

	@ApiModelProperty(required = true, example = "1")
	@JsonProperty("customerType")
	private int customerType;
	
	@ApiModelProperty(required = false, example = "1789879879797")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("customerId")
	private String customerId;
	
	
	@ApiModelProperty(required = true, example = "voter")
	@JsonProperty("kycType")
	private String kycType;
	
	@ApiModelProperty(required = true, example = "XCX2872851")
	@JsonProperty("kycId")
	private String kycId;
	
	@ApiModelProperty(required = false, example = "dd/mm/yyyy")
	@JsonProperty("dob")
	private String dob;

	@ApiModelProperty(required = false, example = "Name")
	@JsonProperty("customerName")
	private String customerName;

	@ApiModelProperty(required = true, example = "NEW")
	@JsonProperty("productType")
	private String productType;
}
