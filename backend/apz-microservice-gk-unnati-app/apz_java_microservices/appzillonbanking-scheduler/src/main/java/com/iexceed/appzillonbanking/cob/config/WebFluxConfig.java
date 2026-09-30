package com.iexceed.appzillonbanking.cob.config;

import java.time.Duration;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

	@Bean
	public WebClient getWebClient() throws SSLException {
		
		SslContext sslContext = SslContextBuilder
	            .forClient()
	            .trustManager(InsecureTrustManagerFactory.INSTANCE)
	            .build();
		
		HttpClient httpClient = HttpClient.create()
				.secure(t -> t.sslContext(sslContext))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
				.doOnConnected(val -> {
					val.addHandlerLast(new ReadTimeoutHandler(30));
					val.addHandlerLast(new WriteTimeoutHandler(30));
				})
				.responseTimeout(Duration.ofSeconds(30));

		httpClient.compress(true);
		httpClient.keepAlive(false);
		httpClient.wiretap(true);
		ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
		return WebClient.builder()
				.clientConnector(connector)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
	}
	
	 @Override
	    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
	        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
	    }
	
}
