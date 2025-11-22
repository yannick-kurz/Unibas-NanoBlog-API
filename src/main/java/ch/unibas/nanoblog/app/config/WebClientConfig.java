package ch.unibas.nanoblog.app.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    /**
     * Creates a dedicated WebClient for communicating with the WordPress API.
     * This ensures:
     * - A base URL is always set
     * - Timeouts prevent hanging requests (common with slow WP hosting)
     * - Logging is added for debugging
     */
    @Bean
    public WebClient wordpressClient(WebClient.Builder builder,
                                     @Value("${wordpress.api.url}") String apiUrl) {

        // Configure the underlying HTTP client (Netty)
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // max time to establish connection
                .responseTimeout(Duration.ofSeconds(5))               // max time to wait for response
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5))   // max time to read data
                                .addHandlerLast(new WriteTimeoutHandler(5))  // max time to write data
                );

        return builder
                .baseUrl(apiUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())   // Log outgoing requests
                .filter(logResponse())  // Log incoming responses
                .build();
    }

    /**
     * Logs the HTTP method + URL of every outgoing request.
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            System.out.println("➡️ Request: " + req.method() + " " + req.url());
            return reactor.core.publisher.Mono.just(req);
        });
    }

    /**
     * Logs the HTTP response status for easier debugging.
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            System.out.println("⬅️ Response: " + res.statusCode().value());
            return reactor.core.publisher.Mono.just(res);
        });
    }
}
