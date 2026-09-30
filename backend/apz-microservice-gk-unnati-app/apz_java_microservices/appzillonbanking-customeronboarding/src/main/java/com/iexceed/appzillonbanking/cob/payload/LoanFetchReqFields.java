package com.iexceed.appzillonbanking.cob.payload;
 
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@ToString
public class LoanFetchReqFields {
 
	@ApiModelProperty(required = true, position = 1, example = "")
	@JsonProperty("company")
	private String company;
 
	@ApiModelProperty(required = true, position = 2, example = "Digi@2019")
	@JsonProperty("password")
	private String password;
 
	@ApiModelProperty(required = true, example = "MOBAPI")
	@JsonProperty("userName")
	private String userName;
 
	@ApiModelProperty(required = true, example = "ENTITY.ID")
	@JsonProperty("columnName")
	private String columnName;
 
	@ApiModelProperty(required = true, example = "21393959")
	@JsonProperty("criteriaValue")
	private String criteriaValue;
 
	@ApiModelProperty(required = true, example = "EQ")
	@JsonProperty("operand")
	private String operand;

    @ApiModelProperty(required = true, example = "10047485")
    @JsonProperty("transactionId")
    private String transactionId;
 
}

 