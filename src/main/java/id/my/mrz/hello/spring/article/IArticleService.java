package id.my.mrz.hello.spring.article;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface IArticleService {

  List<ArticleResourceResponse> fetchArticles();

  ArticleResourceResponse getArticle(long id);

  ArticleResourceResponse createArticle(ArticleCreateRequest payload);

  ArticleResourceResponse updateArticle(long id, ArticleCreateRequest payload);

  void delete(long id);
}
