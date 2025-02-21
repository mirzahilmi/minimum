package id.my.mrz.hello.spring.domain.article.dto;

import id.my.mrz.hello.spring.domain.tag.dto.TagDocumentSearchQuery;
import java.io.Serializable;
import java.util.List;

public record ArticleDocumentSearchQuery(
    String title, String slug, String content, List<TagDocumentSearchQuery> tags)
    implements Serializable {}
