package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkMfiConsumer {

	@JacksonXmlProperty(localName = "INDV")
	private String indv;

	@JacksonXmlProperty(localName = "SCORE")
	private String score;

	@JacksonXmlProperty(localName = "GROUP")
	private String group;

	@JacksonXmlProperty(localName = "CNS-INDV")
	private String cnsIndv;

	@JacksonXmlProperty(localName = "CNS-SCORE")
	private String cnsScore;

	@JacksonXmlProperty(localName = "IOI")
	private String ioi;

}
