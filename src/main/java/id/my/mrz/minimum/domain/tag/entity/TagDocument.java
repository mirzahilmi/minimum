package id.my.mrz.minimum.domain.tag.entity;

import org.springframework.data.annotation.Id;

import id.my.mrz.minimum.domain.tag.dto.TagDocumentSearchQuery;
import id.my.mrz.minimum.domain.tag.dto.TagResourceResponse;

public final class TagDocument {
    @Id
    private Long id;
    private String name;

    public TagDocument(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagResourceResponse toTagResourceResponse(TagDocument tag) {
        return new TagResourceResponse(id, name);
    }

    public static TagDocument of(TagDocumentSearchQuery query) {
        return new TagDocument(null, query.name());
    }
}
