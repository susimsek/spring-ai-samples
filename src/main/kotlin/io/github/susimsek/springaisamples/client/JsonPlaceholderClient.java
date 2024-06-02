package io.github.susimsek.springaisamples.client;

import io.github.susimsek.springaisamples.model.Post;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface JsonPlaceholderClient {

    @PostExchange("/posts")
    Post createPost(@RequestBody Post post);
}