package io.hexlet.spring.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Post {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(Post post) {
        if (post.getTitle() != null) {
            this.setTitle(post.getTitle());
        }
        if (post.getContent() != null) {
            this.setContent(post.getContent());
        }
        if (post.getAuthor() != null) {
            this.setAuthor(post.getAuthor());
        }
    }
}
