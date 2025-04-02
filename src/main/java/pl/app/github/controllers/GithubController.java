package pl.app.github.controllers;

import java.util.List;

import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Git;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pl.app.github.entities.GithubRepository;
import pl.app.github.services.GithubService;

@RestController
public class GithubController {

  private GithubService githubService;

  GithubController(GithubService githubService) {
    this.githubService = githubService;
  }

  @GetMapping("/github/{username}")
  public ResponseEntity getRepositories(@PathVariable String username) {
    List<GithubRepository> repositories = githubService.getUserRepositories(username);
    return ResponseEntity.ok(repositories);
  }
}
