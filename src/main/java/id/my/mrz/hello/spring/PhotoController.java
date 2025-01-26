package id.my.mrz.hello.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PhotoController {
	private final Map<String, Photo> db = new HashMap<>();

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
	public Photo postPhoto(@RequestPart(name = "attachment") MultipartFile photo) throws IOException {
		String id = UUID.randomUUID().toString();
		Photo payload = new Photo(id, photo.getBytes(), photo.getOriginalFilename());
		db.put(id, payload);
		return payload;
	}

	@GetMapping("/photos/{id}/content")
	public ResponseEntity<byte[]> servePhoto(@PathVariable String id) {
		Photo photo = db.get(id);
		if (photo == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition
				.attachment()
				.filename(photo.filename())
				.build());

		return new ResponseEntity<>(photo.file(), headers, HttpStatus.OK);
	}
}
