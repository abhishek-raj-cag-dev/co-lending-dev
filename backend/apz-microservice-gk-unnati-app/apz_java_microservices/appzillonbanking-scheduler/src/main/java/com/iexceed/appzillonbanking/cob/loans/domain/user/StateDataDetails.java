package com.iexceed.appzillonbanking.cob.loans.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_st_state")
@Getter
@Setter
public class StateDataDetails {

	@Id
	@JsonProperty("stateId")
	@Column(name = "id")
	private Integer stateId;

	@JsonProperty("stateName")
	@Column(name = "name")
	private String stateName;

	@Override
	public String toString() {
		return "StateDataDetails [stateId=" + stateId + ", stateName=" + stateName + "]";
	}

}