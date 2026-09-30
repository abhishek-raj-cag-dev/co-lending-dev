package com.iexceed.appzillonbanking.cob.core.payload;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationDocuments;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ResponseBody {

	@ApiModelProperty(example = "This field contains the actual api response")
	public String responseObj;
	
	public List<ApplicationDocuments> applicationDocuments = new ArrayList<>();

	public ResponseBody() {
		super();
	}
	
	public ResponseBody(String responseObj) {
		super();
		this.responseObj = responseObj;
	}

	@Override
	public String toString() {
		return "Response [responseObj=" + responseObj + " , applicationDocuments=" + applicationDocuments +"]";
	}
}
