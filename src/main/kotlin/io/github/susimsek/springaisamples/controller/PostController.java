package io.github.susimsek.springaisamples.controller;

import io.github.susimsek.springaisamples.client.JsonPlaceholderClient;
import io.github.susimsek.springaisamples.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final JsonPlaceholderClient jsonPlaceholderClient;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = jsonPlaceholderClient.createPost(post);
        return ResponseEntity.ok(createdPost);
    }
}