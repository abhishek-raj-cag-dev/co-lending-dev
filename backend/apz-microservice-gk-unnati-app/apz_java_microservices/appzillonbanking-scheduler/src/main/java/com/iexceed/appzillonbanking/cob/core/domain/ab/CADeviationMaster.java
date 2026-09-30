package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_abob_ca_deviation_master")
@Getter
@Setter
@ToString
public class CADeviationMaster {

	@Id
	@Column(name = "deviation_id")
	private String deviationId;

	@Column(name = "product")
	private String product;

	@Column(name = "deviation_description")
	private String deviationDescription;

	@Column(name = "auto_approve")
	private String autoApprove;

	@Column(name = "bcm")
	private String bcm;

	@Column(name = "acm")
	private String acm;

	@Column(name = "active")
	private String active;

}
