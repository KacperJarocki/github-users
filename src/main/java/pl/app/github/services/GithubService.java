package pl.app.github.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.app.github.entities.GithubBranches;
import pl.app.github.entities.GithubRepository;
import pl.app.github.exceptions.UserNotFoundException;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class GithubService {
  private final RestTemplate restTemplate;

  public GithubService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<GithubRepository> getUserRepositories(String username) {
    try {
      ResponseEntity<GithubRepository[]> response = restTemplate.getForEntity(
          "https://api.github.com/users/{username}/repos",
          GithubRepository[].class,
          username);

      List<GithubRepository> repositories = Arrays.asList(response.getBody());
      for (GithubRepository repository : repositories) {
        this.setBranchesForRepository(repository);
      }
      return repositories;

    } catch (HttpClientErrorException.NotFound e) {
      throw new UserNotFoundException(username);
    }
  }

  public void setBranchesForRepository(GithubRepository githubRepository) {
    ResponseEntity<GithubBranches[]> branchesResponse = restTemplate.getForEntity(
        "https://api.github.com/repos/{owner}/{repo}/branches",
        GithubBranches[].class,
        githubRepository.getOwnerLogin(), githubRepository.getRepositoryName());
    List<GithubBranches> branches = Arrays.asList(branchesResponse.getBody());
    githubRepository.setBranches(branches);
  }

  public int hello() {
    return 1;
  }

  public List<GithubRepository> getUserRepositoriesNotForked(String username) {
    return getUserRepositories(username).stream()
        .filter(repo -> !repo.isAFork())
        .collect(Collectors.toList());

  }
}
