package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.tag.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "articles")
public final class Article {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long Id;

  private String title;
  private String slug;
  private String content;

  @OneToMany
  @JoinColumn(name = "tags", referencedColumnName = "id")
  private List<Tag> tags;

  public Article(String title, String slug, String content, List<Tag> tags) {
    this.title = title;
    this.slug = slug;
    this.content = content;
    this.tags = tags;
  }

  public long getId() {
    return Id;
  }

  public void setId(long id) {
    Id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public ArticleResourceResponse toArticleResourceResponse() {
    return new ArticleResourceResponse(Id, title, slug, content, tags);
  }
}
