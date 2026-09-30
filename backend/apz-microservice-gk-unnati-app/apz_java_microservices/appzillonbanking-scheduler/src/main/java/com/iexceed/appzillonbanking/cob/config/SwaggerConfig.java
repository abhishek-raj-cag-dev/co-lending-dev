package com.iexceed.appzillonbanking.cob.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2).forCodeGeneration(true).apiInfo(apiInfo())
				.enable(true)
				.useDefaultResponseMessages(false)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.iexceed.appzillonbanking.cob"))
				.paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Appzillon Banking Services").description("Appzillon Banking Services ")
				.contact(
						new Contact("i-exceed technology solutions private limited", "i-exceed.com", "hr@i-exceed.com"))
				.license("Proprietary").licenseUrl("https://i-exceed.com/contact-us/")
				.termsOfServiceUrl("https://i-exceed.com/contact-us/").version("1.0.0").build();
	}
}