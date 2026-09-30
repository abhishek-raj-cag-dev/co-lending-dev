package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NomineeDetailsPayload {

	
	@JsonProperty("nomineeName")
	private String nomineeName;
	
	@JsonProperty("nomineeRelationship")
	private String nomineeRelationship;
	
	@JsonProperty("nomineeDob")
	private String nomineeDob;
	
	@JsonProperty("guardianName")
	private String guardianName;
	
	@JsonProperty("guardianRelationship")
	private String guardianRelationship;
	
	@JsonProperty("guardianDob")
	private String guardianDob;
	
	@JsonProperty("nomineeMobile")
	private String nomineeMobile;
	
	@JsonProperty("nomineeEmail")
	private String nomineeEmail;
	
	@JsonProperty("guardianLandMark")
	private String guardianLandMark;
	
	@JsonProperty("guardianMobile")
	private String guardianMobile;
	
	@JsonProperty("guardianEmail")
	private String guardianEmail;

	@Override
	public String toString() {
		return "NomineeDetailsPayload{" +
				"nomineeName='" + nomineeName + '\'' +
				", nomineeRelationship='" + nomineeRelationship + '\'' +
				", nomineeDob='" + nomineeDob + '\'' +
				", guardianName='" + guardianName + '\'' +
				", guardianRelationship='" + guardianRelationship + '\'' +
				", guardianDob='" + guardianDob + '\'' +
				", nomineeMobile='" + nomineeMobile + '\'' +
				", nomineeEmail='" + nomineeEmail + '\'' +
				", guardianLandMark='" + guardianLandMark + '\'' +
				", guardianMobile='" + guardianMobile + '\'' +
				", guardianEmail='" + guardianEmail + '\'' +
				'}';
	}
}