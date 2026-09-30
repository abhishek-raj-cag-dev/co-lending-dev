package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_DB_KIT_MASTER")
@Getter
@Setter
@ToString
public class DBKITMaster {
    @Id
	@Column(name = "ID")
	private int id;

	@Column(name = "STAGE_NAME")
	private String stageName;
	
	@Column(name = "REJECT_REASONS")
	private String rejectReasons;
	
	@Column(name = "QUERIES")
	private String queries;

    @Column(name = "APP_ID")
    private String appId;

	@Column(name = "CATEGORY")
	private String category;
}
