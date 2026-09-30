package com.iexceed.appzillonbanking.cob.core.utils;

import com.iexceed.appzillonbanking.cob.core.payload.ExternalServiceConfig;
import com.iexceed.appzillonbanking.cob.core.payload.ExternalServiceDetails;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class XmlReader {

	private static final Logger logger = LogManager.getLogger(XmlReader.class);

	public static Map<String, ExternalServiceDetails> getExternalServiceDetailsFromXml(String xmlFileUrlLocation) {
		ExternalServiceConfig externalServiceConfig = null;
		Map<String, ExternalServiceDetails> urlXmlMap = null;
		try {
			externalServiceConfig = unmarshalling(xmlFileUrlLocation);
			List<ExternalServiceDetails> externalService = externalServiceConfig.getExternalService();
			urlXmlMap = new HashMap<>();
			for (ExternalServiceDetails e : externalService) {
				urlXmlMap.put(e.getName(), e);
			}
		} catch (JAXBException e) {
			logger.error("XML Binding error " + e.getMessage());
		}
		return urlXmlMap;
	}

	private static ExternalServiceConfig unmarshalling(String xmlFileUrlLocation) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(ExternalServiceConfig.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		URL url = null;
		try {
			url = new URL(xmlFileUrlLocation);
		} catch (MalformedURLException e) {
			logger.error("Unable to access cloud config server to load properties file " + e.getMessage());
		}

		return (ExternalServiceConfig) unmarshaller.unmarshal(url);
	}
}
