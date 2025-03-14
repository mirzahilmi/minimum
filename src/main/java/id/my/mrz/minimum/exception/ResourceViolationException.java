package id.my.mrz.minimum.exception;

import java.util.List;

public final class ResourceViolationException extends RuntimeException {
    private final List<String> tags;

    public ResourceViolationException(String message) {
        super(message);
        this.tags = List.of();
    }

    public ResourceViolationException(String message, List<String> tags) {
        super(message);
        this.tags = tags;
    }

    public ResourceViolationException(String message, Throwable cause) {
        super(message, cause);
        this.tags = List.of();
    }

    public ResourceViolationException(String message, List<String> tags, Throwable cause) {
        super(message, cause);
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }
}
