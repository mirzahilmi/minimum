package id.my.mrz.hello.spring.article;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/articles")
final class ArticleController {
  static final String THUMBNAIL_KEY = "thumbnail";

  private final IArticleService articleService;

  public ArticleController(IArticleService articleService) {
    this.articleService = articleService;
  }

  @GetMapping
  ResponseEntity<List<ArticleResourceResponse>> getArticles() {
    List<ArticleResourceResponse> articles = articleService.fetchArticles();
    return ResponseEntity.ok(articles);
  }

  @GetMapping("/{id}")
  ResponseEntity<ArticleResourceResponse> getArticle(@PathVariable long id) {
    ArticleResourceResponse article = articleService.getArticle(id);
    return ResponseEntity.ok(article);
  }

  @PostMapping
  ResponseEntity<ArticleResourceResponse> postArticle(@RequestBody ArticleCreateRequest payload) {
    ArticleResourceResponse article = articleService.createArticle(payload);
    return ResponseEntity.created(URI.create("/articles/" + article.id())).body(article);
  }

  @PutMapping("/{id}")
  ResponseEntity<ArticleResourceResponse> putArticle(
      @PathVariable long id, @RequestBody ArticleCreateRequest payload) {
    ArticleResourceResponse article = articleService.updateArticle(id, payload);
    return ResponseEntity.ok(article);
  }

  @PatchMapping("/{id}/thumbnail")
  ResponseEntity<ArticleResourceResponse> patchThumbnail(
      @PathVariable long id, @RequestPart(THUMBNAIL_KEY) MultipartFile file) throws Exception {
    ArticleResourceResponse response = articleService.uploadThumbnail(id, file);
    return ResponseEntity.ok(response);
  }

  ResponseEntity<Void> deleteArticle(@PathVariable long id) {
    articleService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
