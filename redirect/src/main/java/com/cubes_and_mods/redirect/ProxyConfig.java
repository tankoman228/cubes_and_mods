package com.cubes_and_mods.redirect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.http.HttpMethod;

//For imports to become working
//https://github.com/spring-cloud-samples/spring-cloud-gateway-sample/blob/main/pom.xml

@Configuration
class ProxyConfig {
	
	@Value("${services.res.uri}")
    private String resUri;

    @Value("${services.usr.uri}")
    private String usrUri;

    @Value("${services.buy.uri}")
    private String buyUri;
   

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("usr", r -> r.path("/usr/**").filters(f -> f.stripPrefix(1)).uri(usrUri))
                .route("res", r -> r.path("/res/**").filters(f -> f.stripPrefix(1)).uri(resUri))
                .route("buy", r -> r.path("/buy/**").filters(f -> f.stripPrefix(1)).uri(buyUri))
                .build();
	}
}