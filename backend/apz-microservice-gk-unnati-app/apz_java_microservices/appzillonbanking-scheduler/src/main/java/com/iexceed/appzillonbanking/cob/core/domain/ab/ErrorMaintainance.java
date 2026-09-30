package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ABMI_ERROR_MAINTENANCE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorMaintainance {
	@Id
	@Column(name = "APZ_ERROR_CD")
	private String errorCode;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "APZ_ERROR_DESC")
	private String errorDesc;

	@Column(name = "APZ_ERROR_NAMESPACE")
	private String errorNameSpace;

	@Column(name = "APZ_ERROR_TYPE")
	private String errorType;

	@Column(name = "LANGUAGE")
	private String language;

	@Column(name = "HOST_ID")
	private String hostId;

	@Column(name = "HOST_ERROR_CD")
	private String hostErrorCode;

	@Column(name = "HOST_ERROR_DESC")
	private String hostErrorDesc;

	@Column(name = "RECORD_STATUS")
	private String recordStatus;

	@Column(name = "AUTH_STATUS")
	private String authStatus;

	@Column(name = "CHECKER_ID")
	private String checkerId;

	@Column(name = "MAKER_ID")
	private String makerId;

}
