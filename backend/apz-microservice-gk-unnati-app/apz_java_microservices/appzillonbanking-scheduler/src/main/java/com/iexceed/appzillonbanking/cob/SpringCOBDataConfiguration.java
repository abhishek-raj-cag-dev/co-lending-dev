package com.iexceed.appzillonbanking.cob;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.iexceed.appzillonbanking.cob.core.utils.JasyptConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("file:${dbProperties.path}")
@EnableJpaRepositories(entityManagerFactoryRef = "abCOBEntityManagerFactory",
		transactionManagerRef = "abCOBTransactionManager",
basePackages = {
		"com.iexceed.appzillonbanking.*.repository.ab",
		"com.iexceed.appzillonbanking.*.*.repository.ab"})
public class SpringCOBDataConfiguration {

	@Value("${springcob.datasource.hibernate.dialect}")
	private String dialect;

	@Value("${springcob.datasource.maximum-pool-size}")
	private int maximumPoolSize;

	@Value("${springcob.datasource.minimum-idle}")
	private int minimumIdle;

	@Primary
	@Bean(name = "abCOBDataSourceProps")
	@ConfigurationProperties(prefix = "springcob.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean(name = "abCOBDataSource")
	public DataSource dataSource() {
		JasyptConfig jasyptConfig = new JasyptConfig();
		String decryptedPassword = jasyptConfig.getPasswordEncryptor()
				.decrypt(getDatasourceProperties().getPassword());

		HikariDataSource hikariDataSource = DataSourceBuilder.create()
				.type(HikariDataSource.class)
				.url(getDatasourceProperties().getUrl())
				.username(getDatasourceProperties().getUsername())
				.password(decryptedPassword)
				.driverClassName(getDatasourceProperties().getDriverClassName())
				.build();
		hikariDataSource.setMaximumPoolSize(maximumPoolSize);
		hikariDataSource.setMinimumIdle(minimumIdle);
		hikariDataSource.setPoolName("abCOBPool");
		return hikariDataSource;
	}

	@Primary
	@Bean(name = "abCOBEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean barEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("abCOBDataSource") DataSource abCOBDataSource) {

				Map<String, Object> properties1 = new HashMap<>();
				properties1.put("hibernate.dialect", dialect);

		return builder.dataSource(abCOBDataSource)
				.packages("com.iexceed.appzillonbanking.*.domain.ab",
						"com.iexceed.appzillonbanking.*.*.domain.ab")
				.persistenceUnit("abdatacob")
				.properties(properties1)
				.build();
	}

	@Primary
	@Bean(name = "abCOBTransactionManager")
	public PlatformTransactionManager abTransactionManager(
			@Qualifier("abCOBEntityManagerFactory") EntityManagerFactory abCOBEntityManagerFactory) {
		return new JpaTransactionManager(abCOBEntityManagerFactory);
	}
}
