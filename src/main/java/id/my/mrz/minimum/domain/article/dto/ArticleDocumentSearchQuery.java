package id.my.mrz.minimum.domain.article.dto;

import id.my.mrz.minimum.domain.tag.dto.TagDocumentSearchQuery;
import java.io.Serializable;
import java.util.List;

public final record ArticleDocumentSearchQuery(
    String title, String slug, String content, List<TagDocumentSearchQuery> tags)
    implements Serializable {}
