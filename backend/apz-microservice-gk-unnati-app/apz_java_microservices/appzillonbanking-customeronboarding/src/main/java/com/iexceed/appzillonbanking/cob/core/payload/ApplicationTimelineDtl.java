package com.iexceed.appzillonbanking.cob.core.payload;

import java.util.List;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplicationTimelineDtl {

	@JsonProperty("actionTaken")
	private String actionTaken;

	@JsonProperty("stage")
	private String stage;
	
	@JsonProperty("timeStamp")
	private String timeStamp;	
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@Transient
	@JsonProperty("rpcStatRemaks")
	private List<RPCStatDetails> rpcStatRemaks;

	@Transient
	@JsonProperty("dbKitStatDetails")
	private List<BCMPIStatDetails> dbKitStatDetails;

	@Transient
	@JsonProperty("srcStatRemaks")
	private List<SrcStatDetails> srcStatRemarks;

	@Transient
	@JsonProperty("caStatDetails")
	private List<CAStatDetails> caStatDetails;
}
