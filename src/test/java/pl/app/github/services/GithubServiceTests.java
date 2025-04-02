package pl.app.github.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class GithubServiceTests {
  GithubService githubService;

  GithubServiceTests() {
    githubService = new GithubService(new RestTemplate());
  }

  @Test
  void helloFromService() {
    assertEquals(
        1,
        githubService.hello());
  }

  @Test
  void getAllRepositoriesOfExistingUser() {
    assertEquals(githubService.GetUserRepositories("kacperjarocki").get(1).checkName("kacperjarocki"), true);
  }
}
