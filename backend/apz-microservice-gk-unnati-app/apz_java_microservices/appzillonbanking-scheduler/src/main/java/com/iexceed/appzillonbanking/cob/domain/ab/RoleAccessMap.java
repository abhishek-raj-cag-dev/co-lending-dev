package com.iexceed.appzillonbanking.cob.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_ROLE_ACCESS_MAP")
@IdClass(RoleAccessMapId.class)
@Getter @Setter
public class RoleAccessMap {
	
	@Id
	private String appId;
	
	@Id
	private String roleId;

	@JsonProperty("accessPermission")
	@Column(name = "ACCESS_PERMISSION")
	private String accessPermission;	
	
	@JsonProperty("allowedFeature")
	@Column(name = "ALLOWED_FEATURES")
	private String allowedFeature;
}