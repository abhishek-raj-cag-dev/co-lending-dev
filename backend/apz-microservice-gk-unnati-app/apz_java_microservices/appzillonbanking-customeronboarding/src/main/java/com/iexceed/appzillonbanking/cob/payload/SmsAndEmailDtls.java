package com.iexceed.appzillonbanking.cob.payload;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SmsAndEmailDtls {
	
	private String custId;
	
	private String CustName;

	private String actionType;

	private String mobileNo;

	private String emailId;

	private String smsBody;

	private String emailTitle;

	private String emailBody;

	private boolean smsReq;

	private boolean emailReq;

	private boolean hasAttachment;
	
	private String attachmentContent;
	
	private String attachmentType;

	@Override
	public String toString() {
		return "SmsAndEmailDtls [custId=" + custId + ", CustName=" + CustName + ", actionType=" + actionType
				+ ", mobileNo=" + mobileNo + ", emailId=" + emailId + ", smsBody=" + smsBody + ", emailTitle="
				+ emailTitle + ", emailBody=" + emailBody + ", smsReq=" + smsReq + ", emailReq=" + emailReq
				+ ", hasAttachment=" + hasAttachment + ", attachmentContent=" + attachmentContent + ", attachmentType="
				+ attachmentType + "]";
	}

	
}
