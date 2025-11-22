package app.wordpress;

import ch.unibas.nanoblog.app.wordpress.WordpressService;
import ch.unibas.nanoblog.app.wordpress.dto.WordpressCategory;
import ch.unibas.nanoblog.app.wordpress.dto.WordpressPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WordpressServiceTest {

    private ExchangeFunction exchangeFunction;
    private WordpressService service;

    @BeforeEach
    void setup() {
        exchangeFunction = mock(ExchangeFunction.class);
        WebClient client = WebClient.builder().exchangeFunction(exchangeFunction).build();
        service = new WordpressService(client);
    }

    /**
     * Utility: create a mocked ClientResponse with JSON content
     */
    private ClientResponse jsonResponse(HttpStatus status, String json) {
        return ClientResponse
                .create(status)
                .header("Content-Type", "application/json")
                .body(json)
                .build();
    }

    // ------------------------------
    // getPostsByCategorySlug tests
    // ------------------------------

    @Test
    void getPostsByCategorySlug_success() {
        // WordPress returns a JSON array of posts
        String json = """
            [
              {"id": 1, "title": "Post A"},
              {"id": 2, "title": "Post B"}
            ]
            """;

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.OK, json)));

        List<WordpressPost> posts =
                service.getPostsByCategorySlug("news").block();

        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertEquals(1, posts.get(0).id());
    }

    @Test
    void getPostsByCategorySlug_4xx_error() {
        String json = "Not found";

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.NOT_FOUND, json)));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getPostsByCategorySlug("missing").block());

        assertTrue(ex.getMessage().contains("WordPress 4xx error"));
    }

    @Test
    void getPostsByCategorySlug_5xx_error() {
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, "")));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getPostsByCategorySlug("fail").block());

        assertTrue(ex.getMessage().contains("WordPress server error"));
    }

    // ------------------------------
    // getCategoryBySlug tests
    // ------------------------------

    @Test
    void getCategoryBySlug_success() {
        String json = """
            [
              {"id": 10, "name": "Tech"}
            ]
            """;

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.OK, json)));

        WordpressCategory category =
                service.getCategoryBySlug("tech").block();

        assertNotNull(category);
        assertEquals(10, category.id());
    }

    @Test
    void getCategoryBySlug_4xx_error() {
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.NOT_FOUND, "missing")));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getCategoryBySlug("invalid").block());

        assertTrue(ex.getMessage().contains("Category not found"));
    }

    @Test
    void getCategoryBySlug_5xx_error() {
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, "")));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getCategoryBySlug("oops").block());

        assertTrue(ex.getMessage().contains("WordPress category lookup failed"));
    }

    // ------------------------------
    // getCategoryId tests
    // ------------------------------

    @Test
    void getCategoryId_success() {
        String json = """
            [
              {"id": 77, "name": "Science"}
            ]
            """;

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.OK, json)));

        Integer id = service.getCategoryId("science").block();

        assertEquals(77, id);
    }
}
