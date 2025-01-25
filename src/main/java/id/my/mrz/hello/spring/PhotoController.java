package id.my.mrz.hello.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
public class PhotoController {
	private final Map<String, Photo> db = new HashMap<>() {
		{
			put("random", new Photo("random", "hello.png"));
		}
	};

	@GetMapping("/photos")
	public Collection<Photo> getPhotos() {
		return db.values();
	}

	@GetMapping("/photos/{id}")
	public Photo getPhoto(@PathVariable String id) {
		Photo photo = db.get(id);
		if (photo == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		return photo;
	}

	@DeleteMapping("/photos/{id}")
	public void deletePhoto(@PathVariable String id) {
		db.remove(id);
	}

	@PostMapping("/photos")
	public Photo postPhoto(@RequestBody @Valid Photo photo) {
		String id = UUID.randomUUID().toString();
		System.out.println(photo.fileName());
		Photo payload = new Photo(id, photo.fileName());
		db.put(id, payload);
		return payload;
	}
}
