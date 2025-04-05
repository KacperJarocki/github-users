package pl.app.github.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.github.entities.GithubRepository;
import pl.app.github.exceptions.UserNotFoundException;
import pl.app.github.services.GithubService;

@RestController
@RequestMapping("/api")
public class GithubController {

  private GithubService githubService;

  GithubController(GithubService githubService) {
    this.githubService = githubService;
  }

  @GetMapping("/github/{username}")
  public ResponseEntity getRepositories(@PathVariable String username) {
    try {
      List<GithubRepository> repositories = githubService.getUserRepositoriesNotForked(username);
      return ResponseEntity.ok(repositories);
    } catch (UserNotFoundException e) {
      Map<String, Object> error = new HashMap<>();
      error.put("status", 404);
      error.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
  }
}
