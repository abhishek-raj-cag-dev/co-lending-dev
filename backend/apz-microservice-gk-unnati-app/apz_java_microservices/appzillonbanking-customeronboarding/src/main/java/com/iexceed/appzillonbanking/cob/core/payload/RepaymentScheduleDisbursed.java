package com.iexceed.appzillonbanking.cob.core.payload;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class RepaymentScheduleDisbursed {

	    @SerializedName("S.NO")
	    String slNo;
	    
	    @SerializedName("date")
	    String date;

	    @SerializedName("principal")
	    String principal;
	    
	    @SerializedName("interest")
	    String interest;
	    
	    @SerializedName("total")
	    String total;
	    
	    @SerializedName("principalOs")
	    String principalOs;
	    
	    @SerializedName("interestOs")
	    String interestOs;
	    
	    @SerializedName("totalOs")
	    String totalOs;   
	
}
