package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.client.WeatherClient;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(WeatherClientProperties.class)
public class RestClientConfig {

    @Bean
    public WeatherClient weatherClient(WeatherClientProperties properties) {
        RestClient client = RestClient.builder()
            .baseUrl(properties.getApiUrl())
            .defaultUriVariables(Map.of("apiKey", properties.getApiKey()))
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(client))
            .build();

        return factory.createClient(WeatherClient.class);
    }
}