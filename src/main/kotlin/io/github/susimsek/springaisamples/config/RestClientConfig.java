package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.logging.wrapper.HttpLoggingWrapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
@EnableConfigurationProperties({
    RestClientProperties.class, WeatherClientProperties.class})
@RequiredArgsConstructor
public class RestClientConfig {

    private final RestClientProperties restClientProperties;

    @Bean
    @Scope("prototype")
    RestClient.Builder restClientBuilder(
        RestClientBuilderConfigurer restClientBuilderConfigurer,
        ObjectProvider<HttpLoggingWrapper> httpLoggingWrapperProvider) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
            .withConnectTimeout(restClientProperties.getConnectTimeout())
            .withReadTimeout(restClientProperties.getReadTimeout());
        RestClient.Builder builder = RestClient.builder()
            .requestFactory(ClientHttpRequestFactories.get(settings));
        HttpLoggingWrapper httpLoggingWrapper = httpLoggingWrapperProvider.getIfAvailable();
        if (httpLoggingWrapper != null) {
            builder = builder.requestInterceptor(httpLoggingWrapper.createRestClientInterceptor());
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
}