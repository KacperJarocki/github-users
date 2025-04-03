package pl.app.github.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.app.github.entities.GithubRepository;
import pl.app.github.entities.GithubBranches;

class GithubServiceTests {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private GithubService githubService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturn404WhenUserDoesNotExist() {
    String username = "nonexistentUser";
    when(restTemplate.getForEntity(anyString(), eq(GithubRepository[].class), eq(username)))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    assertThrows(HttpClientErrorException.class, () -> githubService.getUserRepositories(username));
  }

  @Test
  void shouldReturnEmptyListWhenUserHasNoRepositories() {
    String username = "userWithoutRepos";
    when(restTemplate.getForEntity(anyString(), eq(GithubRepository[].class), eq(username)))
        .thenReturn(new ResponseEntity<>(new GithubRepository[] {}, HttpStatus.OK));

    List<GithubRepository> result = githubService.getUserRepositories(username);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyListWhenUserOnlyHasForkedRepos() {
    String username = "userWithOnlyForks";

    GithubRepository forkedRepo = new GithubRepository(
        "forked-repo",
        new GithubRepository.Owner(username),
        true,
        new ArrayList<>());

    when(restTemplate.getForEntity(anyString(), eq(GithubRepository[].class), eq(username)))
        .thenReturn(new ResponseEntity<>(new GithubRepository[] { forkedRepo }, HttpStatus.OK));

    when(restTemplate.getForEntity(anyString(), eq(GithubBranches[].class), eq(username), eq("forked-repo")))
        .thenReturn(new ResponseEntity<>(new GithubBranches[] {}, HttpStatus.OK));

    List<GithubRepository> result = githubService.getUserRepositoriesNotForked(username);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnOnlyOwnRepositoriesWhenUserHasMixedRepos() {
    String username = "userWithMixedRepos";

    GithubRepository ownRepo = new GithubRepository(
        "my-repo",
        new GithubRepository.Owner(username),
        false,
        new ArrayList<>());

    GithubRepository forkedRepo = new GithubRepository(
        "forked-repo",
        new GithubRepository.Owner(username),
        true,
        new ArrayList<>());

    when(restTemplate.getForEntity(anyString(), eq(GithubRepository[].class), eq(username)))
        .thenReturn(new ResponseEntity<>(new GithubRepository[] { ownRepo, forkedRepo }, HttpStatus.OK));

    GithubBranches testBranch = new GithubBranches("main", "abc123");
    when(restTemplate.getForEntity(anyString(), eq(GithubBranches[].class), eq(username), eq("my-repo")))
        .thenReturn(new ResponseEntity<>(new GithubBranches[] { testBranch }, HttpStatus.OK));
    when(restTemplate.getForEntity(anyString(), eq(GithubBranches[].class), eq(username), eq("forked-repo")))
        .thenReturn(new ResponseEntity<>(new GithubBranches[] { testBranch }, HttpStatus.OK));
    List<GithubRepository> result = githubService.getUserRepositoriesNotForked(username);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("my-repo", result.get(0).getRepositoryName());
  }
}
