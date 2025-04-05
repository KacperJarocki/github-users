package pl.app.github.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import pl.app.github.entities.*;
import pl.app.github.exceptions.UserNotFoundException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
public class GithubServiceIntegrationTest {

  private GithubService githubService;
  private RestTemplate restTemplate;
  private MockRestServiceServer mockServer;

  @BeforeEach
  void setup() {
    restTemplate = new RestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    githubService = new GithubService(restTemplate);
  }

  @Test
  void shouldReturnRepositoriesWithBranchesWhenUserExistsAndHasRepos() {
    String username = "testuser";

    String reposResponse = """
            [
              {
                "name": "my-repo",
                "fork": false,
                "owner": {
                  "login": "testuser"
                }
              }
            ]
        """;

    String branchesResponse = """
            [
              {
                "name": "main",
                "commit": {
                  "sha": "abc123"
                }
              }
            ]
        """;

    mockServer.expect(requestTo("https://api.github.com/users/testuser/repos"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(reposResponse, MediaType.APPLICATION_JSON));

    mockServer.expect(requestTo("https://api.github.com/repos/testuser/my-repo/branches"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(branchesResponse, MediaType.APPLICATION_JSON));

    List<GithubRepository> result = githubService.getUserRepositoriesNotForked(username);

    assertEquals(1, result.size());
    assertEquals("my-repo", result.get(0).getRepositoryName());
    assertEquals("testuser", result.get(0).getOwnerLogin());
    assertEquals("main", result.get(0).getBranches().get(0).getName());
    assertEquals("abc123", result.get(0).getBranches().get(0).getLastCommitSha());

    mockServer.verify();
  }

  @Test
  void shouldReturnEmptyListWhenUserHasOnlyForkedRepos() {
    String username = "testuser";

    String reposResponse = """
            [
              {
                "name": "forked-repo",
                "fork": true,
                "owner": {
                  "login": "testuser"
                }
              }
            ]
        """;

    String branchesResponse = """
            [
              {
                "name": "main",
                "commit": {
                  "sha": "abc123"
                }
              }
            ]
        """;

    mockServer.expect(requestTo("https://api.github.com/users/testuser/repos"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(reposResponse, MediaType.APPLICATION_JSON));

    mockServer.expect(requestTo("https://api.github.com/repos/testuser/forked-repo/branches"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(branchesResponse, MediaType.APPLICATION_JSON));
    List<GithubRepository> result = githubService.getUserRepositoriesNotForked(username);

    assertTrue(result.isEmpty());
    mockServer.verify();
  }

  @Test
  void shouldReturnEmptyListWhenUserHasNoRepos() {
    String username = "emptyuser";

    String reposResponse = "[]";

    mockServer.expect(requestTo("https://api.github.com/users/emptyuser/repos"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(reposResponse, MediaType.APPLICATION_JSON));

    List<GithubRepository> result = githubService.getUserRepositoriesNotForked(username);

    assertTrue(result.isEmpty());
    mockServer.verify();
  }

  @Test
  void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
    String username = "notexist";

    mockServer.expect(requestTo("https://api.github.com/users/notexist/repos"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));

    assertThrows(UserNotFoundException.class, () -> githubService.getUserRepositoriesNotForked(username));
    mockServer.verify();
  }
}
