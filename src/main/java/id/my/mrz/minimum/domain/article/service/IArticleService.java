package id.my.mrz.minimum.domain.article.service;

import id.my.mrz.minimum.domain.article.dto.ArticleCreateRequest;
import id.my.mrz.minimum.domain.article.dto.ArticleDocumentSearchQuery;
import id.my.mrz.minimum.domain.article.dto.ArticleResourceResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface IArticleService {

  List<ArticleResourceResponse> fetchArticles();

  ArticleResourceResponse getArticle(long id);

  List<ArticleResourceResponse> searchArticle(ArticleDocumentSearchQuery query);

  ArticleResourceResponse createArticle(long userId, ArticleCreateRequest payload);

  ArticleResourceResponse updateArticle(long id, long userId, ArticleCreateRequest payload);

  ArticleResourceResponse uploadThumbnail(long id, long userId, MultipartFile file)
      throws Exception;

  void delete(long id, long userId);
}
