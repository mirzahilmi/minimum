package id.my.mrz.hello.spring.photo;

import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public final class PhotoService {
  private final PhotoRepository repository;

  public PhotoService(PhotoRepository repository) {
    this.repository = repository;
  }

  public List<PhotoResponse> fetch() {
    List<PhotoResponse> photos = new LinkedList<>();
    repository
        .findAll()
        .forEach(
            photo ->
                photos.add(new PhotoResponse(photo.getId(), photo.getFilename(), photo.getData())));
    return photos;
  }

  public PhotoResponse get(long id) {
    Photo photo = repository.findById(id).orElseThrow();
    return new PhotoResponse(photo.getId(), photo.getFilename(), photo.getData());
  }

  public PhotoResponse create(PhotoCreateRequest photo) {
    Photo entity = new Photo(photo.filename(), photo.data());
    repository.save(entity);
    return new PhotoResponse(entity.getId(), entity.getFilename(), entity.getData());
  }

  public void delete(long id) {
    repository.deleteById(id);
  }
}
