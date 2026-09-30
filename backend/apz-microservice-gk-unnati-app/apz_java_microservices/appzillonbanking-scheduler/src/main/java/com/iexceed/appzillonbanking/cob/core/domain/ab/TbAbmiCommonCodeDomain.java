package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_COMMON_CODES")
@IdClass(TbAbmiCommonCodeId.class)
@Getter @Setter
public class TbAbmiCommonCodeDomain {
	
	@Id
	private String codeType;
	
	@Id
	private String code;
	
	@Column(name = "CODE_DESC")
	private String codeDesc;
	
	@Column(name = "LANGUAGE")
	private String language;
	
	@Column(name = "ACCESS_TYPE")
	private String accessType;

	

	@Override
	public String toString() {
		return "TbAbmiCommonCodeDomain [codeType=" + codeType + ", code=" + code + ", codeDesc=" + codeDesc
				+ ", language=" + language + ", accessType=" + accessType + "]";
	}
}
