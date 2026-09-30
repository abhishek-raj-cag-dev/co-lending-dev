package com.iexceed.appzillonbanking.cob.payload;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.domain.ab.LovMaster;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLovRequestFields {

	@ApiModelProperty(required = true, position = 1)
	@JsonProperty("lovMasterList")
	private List<LovMaster> lovMasterList;
}