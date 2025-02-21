package id.my.mrz.hello.spring.domain.article.entity;

import id.my.mrz.hello.spring.domain.article.dto.ArticleResourceResponse;
import id.my.mrz.hello.spring.domain.tag.entity.Tag;
import id.my.mrz.hello.spring.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "articles")
public final class Article {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String title;
  private String slug;
  private String content;
  private String thumbnail;

  // scary cascade type
  @ManyToOne(cascade = CascadeType.ALL)
  private User user;

  public User getUser() {
    return user;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "tag_id", referencedColumnName = "id")
  private List<Tag> tags;

  protected Article() {}

  public Article(String title, String slug, String content, User user, List<Tag> tags) {
    this.title = title;
    this.slug = slug;
    this.content = content;
    this.thumbnail = "";
    this.user = user;
    this.tags = tags;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public ArticleResourceResponse toArticleResourceResponse() {
    return new ArticleResourceResponse(
        Id,
        title,
        slug,
        content,
        thumbnail,
        tags.stream().map(tag -> tag.toTagResourceResponse()).toList());
  }
}
