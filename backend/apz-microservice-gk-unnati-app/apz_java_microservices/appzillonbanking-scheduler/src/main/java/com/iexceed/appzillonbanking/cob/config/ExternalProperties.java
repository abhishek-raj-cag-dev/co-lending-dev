package com.iexceed.appzillonbanking.cob.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("file:${externalPropFile.path}")
@ConfigurationProperties("ab")
public class ExternalProperties {

	private final Map<String, String> common = new HashMap<>();
	
	public Map<String, String> getCommon() {
		return common;
	}
}