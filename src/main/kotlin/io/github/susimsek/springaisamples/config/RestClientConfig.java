package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.client.JsonPlaceholderClient;
import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.logging.interceptor.RestClientLoggingInterceptor;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(WeatherClientProperties.class)
public class RestClientConfig {

    @Bean
    @Scope("prototype")
    RestClient.Builder restClientBuilder(
        RestClientBuilderConfigurer restClientBuilderConfigurer,
        ObjectProvider<RestClientLoggingInterceptor> restClientLoggingInterceptorProvider) {
        RestClient.Builder builder = RestClient.builder()
            .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS));
        RestClientLoggingInterceptor loggingInterceptor = restClientLoggingInterceptorProvider.getIfAvailable();
        if (loggingInterceptor != null) {
            builder = builder.requestInterceptor(loggingInterceptor);
        }
        return restClientBuilderConfigurer.configure(builder);
    }

    @Bean
    public WeatherClient weatherClient(
        RestClient.Builder restClientBuilder,
        WeatherClientProperties properties) {
        RestClient client = restClientBuilder
            .baseUrl(properties.getApiUrl())
            .defaultUriVariables(Map.of("apiKey", properties.getApiKey()))
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(client))
            .build();

        return factory.createClient(WeatherClient.class);
    }

    @Bean
    public JsonPlaceholderClient jsonPlaceholderClient(RestClient.Builder restClientBuilder) {
        RestClient client = restClientBuilder
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(client))
            .build();

        return factory.createClient(JsonPlaceholderClient.class);
    }

}