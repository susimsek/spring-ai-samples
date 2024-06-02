package io.github.susimsek.springaisamples.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Post {
    private Long id;
    private String title;
    private String body;
    private Long userId;
}