package com.iexceed.appzillonbanking.cob.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_CGOB_SMS_TEMPLATE")
@IdClass(SmsTemplatePK.class)
@Getter
@Setter
public class SmsTemplate {

	@Id
	private String actionType;

	@Id
	private String language;

	@Id
	private String appId;

	@Column(name = "CHANNEL")
	private String channel;

	@Column(name = "SMS")
	private String sms;

	@Column(name = "SMS_DYNAMIC_PARAM")
	private String smsDynamicParam;

	@Column(name = "EMAIL_TITLE")
	private String emailTitle;

	@Column(name = "EMAIL_BODY")
	private String emailBody;

	@Column(name = "EMAIL_DYNAMIC_PARAM")
	private String emailDynamicParam;

	@Override
	public String toString() {
		return "SmsTemplate [actionType=" + actionType + ", language=" + language + ", appId=" + appId + ", channel="
				+ channel + ", sms=" + sms + ", smsDynamicParam=" + smsDynamicParam + ", emailTitle=" + emailTitle
				+ ", emailBody=" + emailBody + ", emailDynamicParam=" + emailDynamicParam + "]";
	}

}