package id.my.mrz.minimum.domain.article.controller;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import id.my.mrz.minimum.domain.article.dto.ArticleCreateRequest;
import id.my.mrz.minimum.domain.article.dto.ArticleDocumentSearchQuery;
import id.my.mrz.minimum.domain.article.dto.ArticleResourceResponse;
import id.my.mrz.minimum.domain.article.service.IArticleService;
import id.my.mrz.minimum.domain.user.entity.Principal;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(
    name = "Article API",
    description = "Article resource actions")
@SecurityRequirement(
    name = "bearerAuth")
public final class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    public static final String THUMBNAIL_KEY = "thumbnail";
    private final IArticleService articleService;

    public ArticleController(IArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/api/v1/articles")
    public ResponseEntity<List<ArticleResourceResponse>> getArticles(
        @ParameterObject ArticleDocumentSearchQuery query) {
        List<ArticleResourceResponse> articles = List.of();

        logger.info("Fetching all articles");
        if (query != null)
            articles = articleService.searchArticle(query);
        else
            articles = articleService.fetchArticles();
        logger.debug("Fetched {} articles", articles.size());

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/api/v1/articles/{id}")
    public ResponseEntity<ArticleResourceResponse> getArticle(@PathVariable long id) {
        logger.info("Fetching all articles");
        ArticleResourceResponse article = articleService.getArticle(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping("/api/v1/articles")
    public ResponseEntity<ArticleResourceResponse> postArticle(
        Principal principal, @RequestBody @Valid ArticleCreateRequest payload)
        throws AccessDeniedException {

        logger.info(
            "Creating article with title: {} for user: {}", payload.getTitle(), principal.getId());
        ArticleResourceResponse article = articleService.createArticle(principal.getId(), payload);
        logger.info("Article created successfully with id: {}", article.getId());
        logger.debug("Created article details: {}", article);

        return ResponseEntity.created(URI.create("/articles/" + article.getId())).body(article);
    }

    @PutMapping("/api/v1/articles/{id}")
    public ResponseEntity<ArticleResourceResponse> putArticle(
        @PathVariable long id, Principal principal,
        @RequestBody @Valid ArticleCreateRequest payload)
        throws AccessDeniedException {
        logger.info("Updating article with id: {}", id);
        ArticleResourceResponse article =
            articleService.updateArticle(id, principal.getId(), payload);
        logger.info("Article updated successfully with id: {}", id);
        logger.debug("Updated article details: {}", article);

        return ResponseEntity.ok(article);
    }

    @PatchMapping(
        value = "/api/v1/articles/{id}/thumbnail",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleResourceResponse> patchThumbnail(
        @PathVariable long id, Principal principal, @RequestPart(THUMBNAIL_KEY) MultipartFile file)
        throws Exception {
        logger.info("Uploading thumbnail for article with id: {}", id);
        ArticleResourceResponse response =
            articleService.uploadThumbnail(id, principal.getId(), file);
        logger.info("Thumbnail uploaded successfully for article with id: {}", id);
        logger.debug(
            "Thumbnail file details - Name: {}, Size: {}", file.getOriginalFilename(),
            file.getSize());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/v1/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id, Principal principal)
        throws AccessDeniedException {
        logger.info("Deleting article with id: {}", id);
        articleService.delete(id, principal.getId());
        logger.info("Article deleted successfully with id: {}", id);

        return ResponseEntity.noContent().build();
    }
}
