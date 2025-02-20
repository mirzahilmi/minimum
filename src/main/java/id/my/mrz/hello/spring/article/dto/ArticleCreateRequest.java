package id.my.mrz.hello.spring.article.dto;

import id.my.mrz.hello.spring.tag.dto.TagCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

public final class ArticleCreateRequest implements Serializable {
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

  @Valid private final List<TagCreateRequest> tags;

  public ArticleCreateRequest(
      String title, String slug, String content, List<TagCreateRequest> tags) {
    this.title = title;
    this.slug = slug;
    this.content = content;
    this.tags = tags;
  }

  public String getTitle() {
    return title;
  }

  public String getSlug() {
    return slug;
  }

  public String getContent() {
    return content;
  }

  public List<TagCreateRequest> getTags() {
    return tags;
  }
}
