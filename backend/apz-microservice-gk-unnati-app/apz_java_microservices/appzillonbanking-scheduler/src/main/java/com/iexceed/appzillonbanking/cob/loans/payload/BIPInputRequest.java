package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BIPInputRequest {
   
    	 @JsonProperty("image_urls")
    	 private List<Map<String, String>> imageUrls;

   		 @JsonProperty("business_type")
   		 private String businessType;

   		 @JsonProperty("request_id")
   		 private String requestId;

   		 private Options options; 
  
     
   	 @Getter
   	 @Setter
   	 public static class Options {

   	     @JsonProperty("is_black_white_check")
   	     private boolean isBlackWhiteCheck = false;

   	     @JsonProperty("confidence_check")
   	     private boolean confidenceCheck = true;

   	     @JsonProperty("fraud_check")
   	     private boolean fraudCheck = false;

   	     @JsonProperty("is_complete_image_check")
   	     private boolean isCompleteImageCheck = true;
   	 }

}
