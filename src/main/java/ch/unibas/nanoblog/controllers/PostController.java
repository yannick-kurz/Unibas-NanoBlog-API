package ch.unibas.nanoblog.controllers;

import ch.unibas.nanoblog.app.wordpress.WordpressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/posts")
public class PostController {
}
