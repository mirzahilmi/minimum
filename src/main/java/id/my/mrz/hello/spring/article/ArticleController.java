package id.my.mrz.hello.spring.article;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
  static final String THUMBNAIL_KEY = "thumbnail";

  private final IArticleService articleService;

  public ArticleController(IArticleService articleService) {
    this.articleService = articleService;
  }

  @GetMapping
  ResponseEntity<List<ArticleResourceResponse>> getArticles() {
    logger.info("Fetching all articles");
    List<ArticleResourceResponse> articles = articleService.fetchArticles();
    logger.debug("Fetched {} articles", articles.size());
    return ResponseEntity.ok(articles);
  }

  @GetMapping("/{id}")
  ResponseEntity<ArticleResourceResponse> getArticle(@PathVariable long id) {
    logger.info("Fetching article with id: {}", id);
    ArticleResourceResponse article = articleService.getArticle(id);
    logger.debug("Fetched article with id: {} and title: {}", id, article.getTitle());
    return ResponseEntity.ok(article);
  }

  @PostMapping
  ResponseEntity<ArticleResourceResponse> postArticle(
      @RequestBody @Valid ArticleCreateRequest payload) {
    logger.info("Creating article with title: {}", payload.getTitle());
    ArticleResourceResponse article = articleService.createArticle(payload);
    logger.info("Article created successfully with id: {}", article.getId());
    logger.debug("Created article details: {}", article);
    return ResponseEntity.created(URI.create("/articles/" + article.getId())).body(article);
  }

  @PutMapping("/{id}")
  ResponseEntity<ArticleResourceResponse> putArticle(
      @PathVariable long id, @RequestBody @Valid ArticleCreateRequest payload) {
    logger.info("Updating article with id: {}", id);
    ArticleResourceResponse article = articleService.updateArticle(id, payload);
    logger.info("Article updated successfully with id: {}", id);
    logger.debug("Updated article details: {}", article);
    return ResponseEntity.ok(article);
  }

  @PatchMapping("/{id}/thumbnail")
  ResponseEntity<ArticleResourceResponse> patchThumbnail(
      @PathVariable long id, @RequestPart(THUMBNAIL_KEY) MultipartFile file) throws Exception {
    logger.info("Uploading thumbnail for article with id: {}", id);
    ArticleResourceResponse response = articleService.uploadThumbnail(id, file);
    logger.info("Thumbnail uploaded successfully for article with id: {}", id);
    logger.debug(
        "Thumbnail file details - Name: {}, Size: {}", file.getOriginalFilename(), file.getSize());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteArticle(@PathVariable long id) {
    logger.info("Deleting article with id: {}", id);
    articleService.delete(id);
    logger.info("Article deleted successfully with id: {}", id);
    return ResponseEntity.noContent().build();
  }
}
