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
public class RepaymentSchedule {

	    @SerializedName("Interest")
	    String interest;

	    @SerializedName("SL.NO")
	    String slNo;

	    @SerializedName("Total Due")
	    String totalDue;

	    @SerializedName("Outstanding")
	    String outstanding;

	    @SerializedName("Date")
	    String date;

	    @SerializedName("Principal")
	    String principal;
	    
	    
	
}
