package com.iexceed.appzillonbanking.cob.core.domain.apz;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "TB_ASMI_USER_ROLE")
@IdClass(UserRoleId.class)
@Getter @Setter
public class UserRole {
	
	@Id
	private String userId;
	
	@Id
	private String roleId;
	
	@Id
	private String appId;
	
	@Column(name = "CREATE_USER_ID")
	private String createUserId;
	
	@Column(name = "CREATE_TS")
	private Timestamp createTs;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
}