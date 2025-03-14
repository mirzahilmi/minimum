package id.my.mrz.minimum.domain.tag.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import id.my.mrz.minimum.domain.tag.dto.TagResourceResponse;

@Entity
@Table(
    name = "tags")
public final class Tag {

    @Id
    @GeneratedValue(
        strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    protected Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagResourceResponse toTagResourceResponse() {
        return new TagResourceResponse(id, name);
    }
}
