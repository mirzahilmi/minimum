package id.my.mrz.hello.spring.photo;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/photos")
public class PhotoController {
	private final PhotoService service;

	public PhotoController(PhotoService service) {
		this.service = service;
	}

	@GetMapping("/")
	public List<PhotoResponse> getPhotos() {
		return service.fetch();
	}

	@GetMapping("/{id}")
	public PhotoResponse getPhoto(@PathVariable long id) throws ResponseStatusException {
		PhotoResponse photo;
		try {
			photo = service.get(id);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return photo;
	}

	@DeleteMapping("/{id}")
	public void deletePhoto(@PathVariable long id) {
		service.delete(id);
	}

	@PostMapping("/")
	public PhotoResponse postPhoto(@RequestPart(name = "attachment") MultipartFile photoFile) throws IOException {
		PhotoCreateRequest photo = new PhotoCreateRequest(
				photoFile.getOriginalFilename(),
				photoFile.getBytes());
		return service.create(photo);
	}

	@GetMapping("/{id}/content")
	public ResponseEntity<byte[]> servePhoto(@PathVariable long id) {
		PhotoResponse photo;
		try {
			photo = service.get(id);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition
				.attachment()
				.filename(photo.filename())
				.build());

		return new ResponseEntity<>(photo.data(), headers, HttpStatus.OK);
	}
}
