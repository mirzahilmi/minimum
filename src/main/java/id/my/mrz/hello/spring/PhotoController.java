package id.my.mrz.hello.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PhotoController {
	private final Map<Long, Photo> db = new HashMap<>() {
		{
			put(1L, new Photo(1, "hello.png"));
		}
	};

	@GetMapping("/photos")
	public Collection<Photo> getPhotos() {
		return db.values();
	}

	@GetMapping("/photos/{id}")
	public Photo getPhoto(@PathVariable() long id) {
		Photo photo = db.get(id);
		if (photo == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		return photo;
	}
}
