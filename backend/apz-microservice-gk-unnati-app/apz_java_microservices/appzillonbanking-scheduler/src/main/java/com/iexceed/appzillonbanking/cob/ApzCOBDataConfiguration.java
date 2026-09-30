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
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("file:${dbProperties.path}")
@EnableJpaRepositories(entityManagerFactoryRef = "apzCOBEntityManagerFactory",
		transactionManagerRef = "apzCOBTransactionManager",
basePackages = {
				"com.iexceed.appzillonbanking.*.repository.apz",
				"com.iexceed.appzillonbanking.*.*.repository.apz" })
public class ApzCOBDataConfiguration {

	@Value("${apzcob.datasource.hibernate.dialect}")
	private String dialect;

	@Value("${apzcob.datasource.maximum-pool-size}")
	private int maximumPoolSize;

	@Value("${apzcob.datasource.minimum-idle}")
	private int minimumIdle;

	@Bean(name = "apzCOBDataSourceProps")
	@ConfigurationProperties(prefix = "apzcob.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "apzCOBDataSource")
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
		hikariDataSource.setPoolName("apzCOBPool");
		return hikariDataSource;
	}

	@Bean(name = "apzCOBEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean barEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("apzCOBDataSource") DataSource apzCOBDataSource) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", dialect);

		return builder.dataSource(apzCOBDataSource).packages("com.iexceed.appzillonbanking.*.domain.apz", "com.iexceed.appzillonbanking.*.*.domain.apz").persistenceUnit("apzdatacob").properties(properties).build();
	}

	@Bean(name = "apzCOBTransactionManager")
	public PlatformTransactionManager apzTransactionManager(
			@Qualifier("apzCOBEntityManagerFactory") EntityManagerFactory apzCOBEntityManagerFactory) {
		return new JpaTransactionManager(apzCOBEntityManagerFactory);
	}
}
