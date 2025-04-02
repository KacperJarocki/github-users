package pl.app.github.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GithubController {

  @GetMapping("/github/{username}")
  public ResponseEntity getRepositories(@PathVariable String username) {
    return ResponseEntity.ok(username);
  }
}
