package com.iexceed.appzillonbanking.cob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class AppzillonBankingCustomerOnBoardingApplication {

	public static void main(String[] args) {
		System.out.println("### BUILD MARKER 20260831-1 ###");
		SpringApplication.run(AppzillonBankingCustomerOnBoardingApplication.class, args);
	}

}
