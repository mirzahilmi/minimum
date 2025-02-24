package id.my.mrz.minimum.domain.greeting.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Greeting API", description = "Simple greeting actions")
public final class GreetingController {
  public static final String template = "Hello, %s!";
  public final AtomicLong counter = new AtomicLong();

  final record Greeting(long id, String name) {}

  @GetMapping("/greeting")
  public Greeting greeting(@RequestParam(name = "name", defaultValue = "World") String name) {
    return new Greeting(counter.getAndIncrement(), String.format(template, name));
  }
}
