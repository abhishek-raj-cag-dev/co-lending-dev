package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeCalculatorDairyRequestFields {
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("businessLocation")
	private String businessLocation;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cowsList")
	private List<CowsListRequestFields> cowsList;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("buffaloesList")
	private List<BuffaloesListRequestFields> buffaloesList;

    @ApiModelProperty(required = true, example = "")
    @JsonProperty("dairyState")
    private String dairyState;

}
