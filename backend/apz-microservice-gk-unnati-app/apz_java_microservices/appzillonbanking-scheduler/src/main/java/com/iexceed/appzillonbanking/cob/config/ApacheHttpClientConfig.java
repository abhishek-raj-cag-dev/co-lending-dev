package com.iexceed.appzillonbanking.cob.config;

import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.iexceed.appzillonbanking.cob.core.payload.ConnectionPooler;

@Configuration
@EnableScheduling
public class ApacheHttpClientConfig {

	@Bean
	@ConfigurationProperties(prefix = "connectionparams")
	public ConnectionPooler connectionPooler() {
		return new ConnectionPooler();
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingConnectionManager(ConnectionPooler connectionPooler) {
		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
		// set a total amount of connections across all HTTP routes
		poolingConnectionManager.setMaxTotal(connectionPooler.getMaxTotalConnections());
		// set a maximum amount of connections for each HTTP route in pool
		poolingConnectionManager.setDefaultMaxPerRoute(connectionPooler.getMaxRouteConnections());
		// increase the amounts of connections if the host is localhost
		HttpHost localhost = new HttpHost(connectionPooler.getHostIp(), connectionPooler.getHostPortNumber());
		poolingConnectionManager.setMaxPerRoute(new HttpRoute(localhost),
				connectionPooler.getMaxLocalHostConnections());
		return poolingConnectionManager;
	}

	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy(ConnectionPooler connectionPooler) {
		return (httpResponse, httpContext) -> {
			HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
			HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);
			while (elementIterator.hasNext()) {
				HeaderElement element = elementIterator.nextElement();
				String param = element.getName();
				String value = element.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					return Long.parseLong(value) * 1000; // convert to ms
				}
			}
			return connectionPooler.getDefaultKeepAliveTime();
		};
	}

	@Bean
	public Runnable idleConnectionMonitor(PoolingHttpClientConnectionManager pool, ConnectionPooler connectionPooler) {
		return new Runnable() {
			@Override
			@Scheduled(fixedDelay = 20000)
			public void run() {
				// only if connection pool is initialised
				if (pool != null) {
					pool.closeExpiredConnections();
					pool.closeIdleConnections(connectionPooler.getIdleConnectionWaitTime(), TimeUnit.MILLISECONDS);
				}
			}
		};
	}

	@Bean
	public TaskScheduler taskScheduler(ConnectionPooler connectionPooler) {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("idleMonitor");
		scheduler.setPoolSize(connectionPooler.getTaskSchedulerPoolSize());
		return scheduler;
	}

	@Bean
	public CloseableHttpClient httpClient(ConnectionPooler connectionPooler) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionPooler.getConnectionTimeout())
				.setConnectionRequestTimeout(connectionPooler.getRequestTimeout())
				.setSocketTimeout(connectionPooler.getSocketTimeout()).build();
		return HttpClients.custom().setDefaultRequestConfig(requestConfig)
				.setConnectionManager(poolingConnectionManager(connectionPooler))
				.setKeepAliveStrategy(connectionKeepAliveStrategy(connectionPooler)).build();
	}
}
