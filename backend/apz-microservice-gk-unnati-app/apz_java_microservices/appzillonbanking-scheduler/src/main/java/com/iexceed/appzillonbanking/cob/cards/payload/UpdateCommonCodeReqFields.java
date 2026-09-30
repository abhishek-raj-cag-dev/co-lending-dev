package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UpdateCommonCodeReqFields {

	@ApiModelProperty(required = true)
	@JsonProperty("cardImages")
	List<String> cardImages;
	
	@ApiModelProperty(required = true, example = "cardImage.png")
	@JsonProperty("defaultImage")
	String defaultImage;
}