package ch.unibas.nanoblog.app.wordpress;

import ch.unibas.nanoblog.app.wordpress.dto.WordpressCategory;
import ch.unibas.nanoblog.app.wordpress.dto.WordpressPost;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class WordpressService {

    private final WebClient webClient;

    /**
     * The service uses a dedicated WebClient bean defined in WebClientConfig.
     */
    public WordpressService(WebClient wordpressClient) {
        this.webClient = wordpressClient;
    }

    /**
     * Retrieves all posts that belong to a given category slug.
     *
     * - Uses typed DTOs instead of raw JSON strings
     * - Includes error handling for 4xx and 5xx responses
     * - Returns a Mono<List> which fits WebFlux best
     */
    public Mono<List<WordpressPost>> getPostsByCategorySlug(String categorySlug) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/wp/v2/posts")
                        .queryParam("categories", categorySlug)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("WordPress 4xx error: " + msg)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("WordPress server error")))
                .bodyToFlux(WordpressPost.class)
                .collectList();
    }

    /**
     * Retrieves category information from a slug.
     *
     * WordPress returns an array, so bodyToFlux + singleOrEmpty() is required.
     */
    public Mono<WordpressCategory> getCategoryBySlug(String slug) {
        return webClient.get()
                .uri("/wp/v2/categories?slug={slug}", slug)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("Category not found: " + msg)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("WordPress category lookup failed")))
                .bodyToFlux(WordpressCategory.class)
                .singleOrEmpty();
    }

    /**
     * Convenience method: resolves the ID from a category slug.
     */
    public Mono<Integer> getCategoryId(String slug) {
        return getCategoryBySlug(slug).map(WordpressCategory::id);
    }
}
