package ch.unibas.nanoblog.app.wordpress.dto;

/**
 * Represents a WordPress post object returned by the WP REST API.
 *
 * Only essential fields are included — more can be added as needed.
 */
public record WordpressPost(
        int id,
        String date,
        Rendered title,
        Rendered content
) {
    /**
     * WP returns the title and content fields nested inside a "rendered" property.
     */
    public record Rendered(String rendered) {}
}
