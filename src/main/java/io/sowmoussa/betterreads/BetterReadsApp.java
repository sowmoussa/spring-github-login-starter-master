package io.sowmoussa.betterreads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sowmoussa.betterreads.connection.DataStaxAstraProperties;

@SpringBootApplication
@RestController
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterReadsApp {

	public static void main(String[] args) {
		SpringApplication.run(BetterReadsApp.class, args);
	}

	@RequestMapping("/user")
	public String user(@AuthenticationPrincipal OAuth2User principal) {
		System.out.println(principal);
		return principal.getAttribute("name");
	}
	
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		return builder -> builder.withCloudSecureConnectBundle(astraProperties.getSecureConnectBundle().toPath());
	}
}
