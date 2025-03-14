package id.my.mrz.minimum.domain.article.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import id.my.mrz.minimum.domain.article.dto.ArticleResourceResponse;
import id.my.mrz.minimum.domain.tag.dto.TagResourceResponse;
import id.my.mrz.minimum.domain.tag.entity.TagDocument;

@Document(
    indexName = "articles")
public final class ArticleDocument {
    @Id
    private Long id;
    private String title;
    private String slug;
    private String content;
    private List<TagDocument> tags;

    public ArticleDocument(
        Long id, String title, String slug, String content, List<TagDocument> tags) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<TagDocument> getTags() {
        return tags;
    }

    public void setTags(List<TagDocument> tags) {
        this.tags = tags;
    }

    public ArticleResourceResponse toArticleResourceResponse() {
        List<TagResourceResponse> tags =
            this.getTags().stream().map(tag -> tag.toTagResourceResponse(tag)).toList();
        return new ArticleResourceResponse(id, title, slug, content, null, tags);
    }
}
