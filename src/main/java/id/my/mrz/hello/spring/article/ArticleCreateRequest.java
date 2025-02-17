package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.tag.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

final class ArticleCreateRequest implements Serializable {
  @NotNull(message = "title is required")
  @NotBlank(message = "title cannot be blank")
  private final String title;

  @NotNull(message = "slug is required")
  @NotBlank(message = "slug cannot be blank")
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "slug must follow URI format standard")
  private final String slug;

  @NotNull(message = "title is required")
  @NotBlank(message = "title cannot be blank")
  private final String content;

  private final List<Tag> tags;

  ArticleCreateRequest(String title, String slug, String content, List<Tag> tags) {
    this.title = title;
    this.slug = slug;
    this.content = content;
    this.tags = tags;
  }

  String getTitle() {
    return title;
  }

  String getSlug() {
    return slug;
  }

  String getContent() {
    return content;
  }

  List<Tag> getTags() {
    return tags;
  }
}
