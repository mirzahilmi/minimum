package id.my.mrz.minimum.domain.article.dto;

import id.my.mrz.minimum.domain.article.entity.ArticleDocument;
import id.my.mrz.minimum.domain.tag.entity.TagDocument;
import java.io.Serializable;
import java.util.List;

public final record ArticleDocumentSearchQuery(String query, List<String> tags)
    implements Serializable {
  public static final String QUERY_STRING_KEY = "query";

  public ArticleDocument toArticleDocument() {
    List<TagDocument> tagDocuments =
        tags != null ? tags.stream().map(tag -> new TagDocument(null, tag)).toList() : List.of();
    return new ArticleDocument(null, query, query, query, tagDocuments);
  }
}
