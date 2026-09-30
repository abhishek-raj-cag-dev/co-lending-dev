package com.iexceed.appzillonbanking.cob;

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
@EnableJpaRepositories(entityManagerFactoryRef = "userCOBEntityManagerFactory",
		transactionManagerRef = "userCOBTransactionManager",
basePackages = {
				"com.iexceed.appzillonbanking.*.repository.user",
				"com.iexceed.appzillonbanking.*.*.repository.user" })
public class UserCOBDataConfiguration {

	@Value("${usercob.datasource.maximum-pool-size}")
	private int maximumPoolSize;

	@Value("${usercob.datasource.minimum-idle}")
	private int minimumIdle;

	@Bean(name = "userCOBDataSourceProps")
	@ConfigurationProperties(prefix = "usercob.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "userCOBDataSource")
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
		hikariDataSource.setPoolName("userCOBPool");
		return hikariDataSource;
	}

	@Bean(name = "userCOBEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean barEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("userCOBDataSource") DataSource userCOBDataSource) {
		return builder.dataSource(userCOBDataSource)
				.packages("com.iexceed.appzillonbanking.*.domain.user",
						"com.iexceed.appzillonbanking.*.*.domain.user")
				.persistenceUnit("userdatacob")
				.build();
	}

	@Bean(name = "userCOBTransactionManager")
	public PlatformTransactionManager userTransactionManager(
			@Qualifier("userCOBEntityManagerFactory") EntityManagerFactory userCOBEntityManagerFactory) {
		return new JpaTransactionManager(userCOBEntityManagerFactory);
	}
}
