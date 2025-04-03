package pl.app.github.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRepository {
  @JsonProperty("name")
  String repositoryName;

  String ownerLogin;
  List<GithubBranches> branches;
  @JsonProperty("fork")
  Boolean fork;

  @JsonProperty("owner")
  private void unpackOwner(Owner owner) {
    this.ownerLogin = owner.login;
  }

  public GithubRepository(
      @JsonProperty("name") String repositoryName,
      @JsonProperty("owner") Owner owner,
      @JsonProperty("fork") Boolean fork,
      List<GithubBranches> branches) {
    this.repositoryName = repositoryName;
    this.ownerLogin = owner.login;
    this.fork = fork;
    this.branches = branches;
  }

  public static class Owner {
    public String login;

    public Owner(String username) {
      this.login = username;
    }
  }

  public Boolean checkName(String name) {
    return ownerLogin.equalsIgnoreCase(name);
  }

  public String getOwnerLogin() {
    return this.ownerLogin;
  }

  public String getRepositoryName() {
    return this.repositoryName;
  }

  public void setBranches(List<GithubBranches> branches) {
    this.branches = branches;
  }

  public List<GithubBranches> getBranches() {
    return this.branches;
  }

  public Boolean isAFork() {
    return this.fork;
  }
}
