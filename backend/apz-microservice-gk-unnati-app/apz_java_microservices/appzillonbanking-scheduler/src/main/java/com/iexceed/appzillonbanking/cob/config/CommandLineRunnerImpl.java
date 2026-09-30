package com.iexceed.appzillonbanking.cob.config;

import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

	@Autowired
	private CommonProperties commonProperties;

	@Autowired
	private ExternalProperties externalProperties;

	@PostConstruct
	public void initProperties() {
		CommonUtils.initializeCommonProperties(commonProperties.getCommon());
		CommonUtils.initializeExternalProperties(externalProperties.getCommon());
	}

	@Override
	public void run(String... args) throws Exception {
		initProperties();
	}
}
