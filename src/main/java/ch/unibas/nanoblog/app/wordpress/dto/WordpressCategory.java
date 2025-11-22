package ch.unibas.nanoblog.app.wordpress.dto;

/**
 * Represents a WordPress category object.
 */
public record WordpressCategory(
        int id,
        String name,
        String slug
) {}
