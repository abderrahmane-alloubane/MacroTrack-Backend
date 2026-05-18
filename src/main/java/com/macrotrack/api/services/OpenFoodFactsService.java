package com.macrotrack.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Service
public class OpenFoodFactsService {

    private static final Logger log = LoggerFactory.getLogger(OpenFoodFactsService.class);

    private final WebClient webClient;

    private static final String BASE_URL = "https://world.openfoodfacts.org";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    public OpenFoodFactsService() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.USER_AGENT, "MySpringApp/1.0 (myapp@example.com)")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    // GET product by barcode (read operation - no auth needed)
    @Cacheable(value = "products", key = "#barcode")
    public String getProductByBarcode(String barcode) {
        return webClient.get()
                .uri("/api/v2/product/{barcode}.json", barcode)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String searchProducts(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cgi/search.pl")
                        .queryParam("search_terms", query)
                        .queryParam("page_size", 100)
                        .queryParam("json", 1)
                        .queryParam("action", "process")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(REQUEST_TIMEOUT)
                .block();
    }
}