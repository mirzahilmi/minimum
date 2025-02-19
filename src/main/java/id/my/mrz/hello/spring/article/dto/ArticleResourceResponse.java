package id.my.mrz.hello.spring.article.dto;

import id.my.mrz.hello.spring.tag.Tag;
import java.io.Serializable;
import java.util.List;

public final class ArticleResourceResponse implements Serializable {
  private final long id;
  private final String title;
  private final String slug;
  private final String content;
  private final String thumbnail;
  private final List<Tag> tags;

  public ArticleResourceResponse(
      long id, String title, String slug, String content, String thumbnail, List<Tag> tags) {
    this.id = id;
    this.title = title;
    this.slug = slug;
    this.content = content;
    this.thumbnail = thumbnail;
    this.tags = tags;
  }

  public long getId() {
    return id;
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

  public String getThumbnail() {
    return thumbnail;
  }

  public List<Tag> getTags() {
    return tags;
  }
}
