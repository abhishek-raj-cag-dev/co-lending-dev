package com.iexceed.appzillonbanking.cob.loans.domain.user;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import org.hibernate.annotations.Type;

/**
 * The persistent class for the TB_ASMI_USER database table.
 * 
 */
@Entity
@Table(name = "TB_ASMI_USER")
@IdClass(TbAsmiUserPK.class)
public class TbAsmiUser {

	@Id
	private String appId;

	@Id
	private String userId;

	@Column(name = "ADD_INFO1")
	private String addInfo1;

	@Column(name = "ADD_INFO2")
	private String addInfo2;

	@Column(name = "ADD_INFO3")
	private String addInfo3;

	@Column(name = "ADD_INFO4")
	private String addInfo4;

	@Column(name = "ADD_INFO5")
	private String addInfo5;

	@Column(name = "AUTH_STATUS")
	private String authStatus;

	@Column(name = "CHECKER_ID")
	private String checkerId;

	@Column(name = "CHECKER_TS")
	private Timestamp checkerTs;

	@Column(name = "CREATE_TS")
	private Timestamp createTs;

	@Column(name = "CREATE_USER_ID")
	private String createUserId;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_OF_BIRTH")
	private Date dateOfBirth;

	private String externalidentifier;

	@Column(name = "FAIL_COUNT")
	private int failCount;

	@Column(name = "LANGUAGE")
	private String language;

	@Column(name = "LOGIN_STATUS")
	private String loginStatus;

	@Column(name = "MAKER_ID")
	private String makerId;

	@Column(name = "MAKER_TS")
	private Timestamp makerTs;

	private String pin;

	@Column(name = "PIN_CHANGE_TS")
	private Timestamp pinChangeTs;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "PROFILE_PIC")
	private String profilePic;

	@Column(name = "USER_ACTIVE")
	private String userActive;

	@Column(name = "USER_ADDR1")
	private String userAddr1;

	@Column(name = "USER_ADDR2")
	private String userAddr2;

	@Column(name = "USER_ADDR3")
	private String userAddr3;

	@Column(name = "USER_ADDR4")
	private String userAddr4;

	@Column(name = "USER_EML1")
	private String userEml1;

	@Column(name = "USER_EML2")
	private String userEml2;

	@Column(name = "USER_LOCK_TS")
	private Timestamp userLockTs;

	@Column(name = "USER_LOCKED")
	private String userLocked;

	@Column(name = "USER_LVL")
	private Integer userLvl;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "USER_PHNO1")
	private String userPhno1;

	@Column(name = "USER_PHNO2")
	private String userPhno2;

	@Column(name = "VERSION_NO")
	private Integer versionNo;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAddInfo1() {
		return addInfo1;
	}

	public void setAddInfo1(String addInfo1) {
		this.addInfo1 = addInfo1;
	}

	public String getAddInfo2() {
		return addInfo2;
	}

	public void setAddInfo2(String addInfo2) {
		this.addInfo2 = addInfo2;
	}

	public String getAddInfo3() {
		return addInfo3;
	}

	public void setAddInfo3(String addInfo3) {
		this.addInfo3 = addInfo3;
	}

	public String getAddInfo4() {
		return addInfo4;
	}

	public void setAddInfo4(String addInfo4) {
		this.addInfo4 = addInfo4;
	}

	public String getAddInfo5() {
		return addInfo5;
	}

	public void setAddInfo5(String addInfo5) {
		this.addInfo5 = addInfo5;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
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

	public Timestamp getCreateTs() {
		return createTs;
	}

	public void setCreateTs(Timestamp createTs) {
		this.createTs = createTs;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getExternalidentifier() {
		return externalidentifier;
	}

	public void setExternalidentifier(String externalidentifier) {
		this.externalidentifier = externalidentifier;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getMakerId() {
		return makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	public Timestamp getMakerTs() {
		return makerTs;
	}

	public void setMakerTs(Timestamp makerTs) {
		this.makerTs = makerTs;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public Timestamp getPinChangeTs() {
		return pinChangeTs;
	}

	public void setPinChangeTs(Timestamp pinChangeTs) {
		this.pinChangeTs = pinChangeTs;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getUserActive() {
		return userActive;
	}

	public void setUserActive(String userActive) {
		this.userActive = userActive;
	}

	public String getUserAddr1() {
		return userAddr1;
	}

	public void setUserAddr1(String userAddr1) {
		this.userAddr1 = userAddr1;
	}

	public String getUserAddr2() {
		return userAddr2;
	}

	public void setUserAddr2(String userAddr2) {
		this.userAddr2 = userAddr2;
	}

	public String getUserAddr3() {
		return userAddr3;
	}

	public void setUserAddr3(String userAddr3) {
		this.userAddr3 = userAddr3;
	}

	public String getUserAddr4() {
		return userAddr4;
	}

	public void setUserAddr4(String userAddr4) {
		this.userAddr4 = userAddr4;
	}

	public String getUserEml1() {
		return userEml1;
	}

	public void setUserEml1(String userEml1) {
		this.userEml1 = userEml1;
	}

	public String getUserEml2() {
		return userEml2;
	}

	public void setUserEml2(String userEml2) {
		this.userEml2 = userEml2;
	}

	public Timestamp getUserLockTs() {
		return userLockTs;
	}

	public void setUserLockTs(Timestamp userLockTs) {
		this.userLockTs = userLockTs;
	}

	public String getUserLocked() {
		return userLocked;
	}

	public void setUserLocked(String userLocked) {
		this.userLocked = userLocked;
	}

	public Integer getUserLvl() {
		return userLvl;
	}

	public void setUserLvl(Integer userLvl) {
		this.userLvl = userLvl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhno1() {
		return userPhno1;
	}

	public void setUserPhno1(String userPhno1) {
		this.userPhno1 = userPhno1;
	}

	public String getUserPhno2() {
		return userPhno2;
	}

	public void setUserPhno2(String userPhno2) {
		this.userPhno2 = userPhno2;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

@Data
	public static class UserSummary{
		String userId;
		String userName;
		String addInfo3;

		public UserSummary(String userId, String userName, String addInfo3) {
			this.userId = userId;
			this.userName = userName;
			this.addInfo3 = addInfo3;
		}
	}

}
