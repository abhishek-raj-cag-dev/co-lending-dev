package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BIPRequestWrapper {

    @JsonProperty("apiRequest")
    private BIPMasterRequest apiRequest;

    @Getter
    @Setter
    @ToString
    public static class BIPMasterRequest {
        @JsonProperty("requestObj")
        private BIPRequestObj requestObj;
        
    	@JsonProperty("interfaceName")
    	private String interfaceName;
        
        @JsonProperty("appId")
        private String appId;
        
    }

    @Getter
    @Setter
    @ToString
    public static class BIPRequestObj {
    	
    	@ApiModelProperty(required = false, example = "APZDEP")
    	@JsonProperty("appId")
    	private String appId;

    	@ApiModelProperty(required = true, example = "NEW00063")
    	@JsonProperty("applicationId")
    	private String applicationId;

    	@ApiModelProperty(required = false, example = "1")
    	@JsonProperty("versionNum")
    	private int versionNum;
    	
    	@JsonProperty("documentType")
		private String documentType;
    	
    	@JsonProperty("userId")
		private String userId;
    	
    	@JsonProperty("custType")
		private String custType;
    	
    	@JsonProperty("files")
		private List<String> files;
    	
		@JsonProperty("botBusinessType")
		private String botBusinessType;
    }

}
