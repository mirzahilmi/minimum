package id.my.mrz.hello.spring.article;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface IArticleService {

  List<ArticleResourceResponse> fetchArticles();

  ArticleResourceResponse getArticle(long id);

  ArticleResourceResponse createArticle(ArticleCreateRequest payload);

  ArticleResourceResponse updateArticle(long id, ArticleCreateRequest payload);

  ArticleResourceResponse uploadThumbnail(long id, MultipartFile file) throws Exception;

  void delete(long id);
}
