package com.iexceed.appzillonbanking.cob.admin.domain.ab;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ABOB_MAKER_CHECKER")
@IdClass(TbAbmiMakerCheckerIds.class)
public class TbAbmiMakerChecker {

	@Id
	private String id;

	@Id
	private int versionNo;

	@Column(name = "FEATURE_ID")
	private String featureId;

	@Column(name = "USERACTION")
	private String userAction;

	@Column(name = "PAYLOAD")
	private String payload;

	@Column(name = "AUTH_STATUS")
	private String authStatus;

	@Column(name = "MAKER_ID")
	private String makerId;

	@Column(name = "MAKER_TS")
	private LocalDateTime makerTs;

	@Column(name = "CHECKER_ID")
	private String checkerId;

	@Column(name = "CHECKER_TS")
	private Timestamp checkerTs;

	public TbAbmiMakerChecker() {
	}

	public TbAbmiMakerChecker(String id, String featureId, String userAction, String payload, String authStatus,
			String makerId, LocalDateTime makerTs, String checkerId, Timestamp checkerTs, int versionNo) {
		this.id = id;
		this.featureId = featureId;
		this.userAction = userAction;
		this.payload = payload;
		this.authStatus = authStatus;
		this.makerId = makerId;
		this.makerTs = makerTs;
		this.checkerId = checkerId;
		this.checkerTs = checkerTs;
		this.versionNo = versionNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public String getMakerId() {
		return makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	public LocalDateTime getMakerTs() {
		return makerTs;
	}

	public void setMakerTs(LocalDateTime makerTs) {
		this.makerTs = makerTs;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	public Timestamp getCheckerTs() {
		return checkerTs;
	}

	public void setCheckerTs(Timestamp checkerTs) {
		this.checkerTs = checkerTs;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	public String toString() {
		return "BankTxnLimit [id=" + id + ", featureId=" + featureId + ", userAction="+ userAction
				+ ", payload=" + payload + ", authStatus=" + authStatus + ", makerId=" + makerId
				+ ", makerTs=" + makerTs + ", checkerId=" +checkerId+ ", checkerTs=" +checkerTs
				+ ", versionNo=" +versionNo+" ]";
	}
}
