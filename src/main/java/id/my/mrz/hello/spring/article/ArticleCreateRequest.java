package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.tag.Tag;
import java.util.List;

final class ArticleCreateRequest {
  private final String title;
  private final String slug;
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
