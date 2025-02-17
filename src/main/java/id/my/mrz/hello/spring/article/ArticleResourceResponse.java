package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.tag.Tag;
import java.io.Serializable;
import java.util.List;

final class ArticleResourceResponse implements Serializable {
  private final long id;
  private final String title;
  private final String slug;
  private final String content;
  private final String thumbnail;
  private final List<Tag> tags;

  ArticleResourceResponse(
      long id, String title, String slug, String content, String thumbnail, List<Tag> tags) {
    this.id = id;
    this.title = title;
    this.slug = slug;
    this.content = content;
    this.thumbnail = thumbnail;
    this.tags = tags;
  }

  long getId() {
    return id;
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

  String getThumbnail() {
    return thumbnail;
  }

  List<Tag> getTags() {
    return tags;
  }
}
