package com.iexceed.appzillonbanking.cob.payload;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class DeleteNomineeRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "16190967474451390")
	@JsonProperty("nomineeDtlsId")
	private BigDecimal nomineeDtlsId;
	
	@ApiModelProperty(required = true, position = 2, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
	
	@ApiModelProperty(required = true, position = 3, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 4, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
}