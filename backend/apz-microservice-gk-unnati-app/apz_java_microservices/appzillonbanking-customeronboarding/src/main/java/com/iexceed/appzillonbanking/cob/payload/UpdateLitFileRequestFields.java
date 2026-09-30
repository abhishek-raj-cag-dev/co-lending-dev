package com.iexceed.appzillonbanking.cob.payload;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class UpdateLitFileRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "EN")
	@JsonProperty("languageCode")
	private String languageCode;
	
	@ApiModelProperty(required = true, position = 2, example = "")
	@JsonProperty("litList")
	private List<LITDomain> litList;
}