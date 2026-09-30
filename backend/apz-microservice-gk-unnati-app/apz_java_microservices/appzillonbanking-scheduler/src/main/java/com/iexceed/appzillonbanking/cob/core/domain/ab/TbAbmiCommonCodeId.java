package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class TbAbmiCommonCodeId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "CODE_TYPE", nullable = false)
	private String codeType;

	@Column(name = "CM_CODE", nullable = false)
	private String code;
	
	public TbAbmiCommonCodeId() {
	}

	public TbAbmiCommonCodeId(String codeType, String code) {
		this.codeType=codeType;
		this.code=code;	
	}
	
	
}