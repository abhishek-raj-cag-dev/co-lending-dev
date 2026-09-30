package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_SANCTION_MASTER")
@Getter
@Setter
@ToString
public class SanctionMaster {

	@JsonProperty("id")
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "product")
	@JsonProperty("product")
	private String product;

	@Column(name = "min_value")
	@JsonProperty("min_value")
	private BigDecimal minValue;

	@Column(name = "max_value")
	@JsonProperty("max_value")
	private BigDecimal maxValue;

	@Column(name = "bm")
	@JsonProperty("bm")
	private String bm;

	@Column(name = "am")
	@JsonProperty("am")
	private String am;
	
	@Column(name = "rm")
	@JsonProperty("rm")
	private String rm;
	
	@Column(name = "dm")
	@JsonProperty("dm")
	private String dm;

}
