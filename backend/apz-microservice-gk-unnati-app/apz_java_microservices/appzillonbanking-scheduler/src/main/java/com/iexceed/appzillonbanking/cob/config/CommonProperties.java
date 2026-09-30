package com.iexceed.appzillonbanking.cob.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:application.properties")
@Getter
public class CommonProperties {

	private final Map<String, String> common = new HashMap<>();
	
	@Value("${litFileFormat}")
	private String litFileFormat;
	
	@Value("${cobFlags}")
	private String cobFlags;

	@Value("${scheduler.pushbackfetch.crontime}")
	private String crontime;
	
	public Map<String, String> getCommon() {
		common.put("litFileFormat", litFileFormat);
		common.put("cobFlags", cobFlags);	
		common.put("crontime", crontime);	
		return common;
	}
}