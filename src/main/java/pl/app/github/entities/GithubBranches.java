package pl.app.github.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubBranches {

  @JsonProperty("name")
  String name;

  @JsonProperty("commit")
  private void unpackCommit(Commit commit) {
    this.lastCommitSha = commit.sha;
  }

  String lastCommitSha;

  private static class Commit {
    public String sha;
  }

  public String getLastCommitSha() {
    return this.lastCommitSha;
  }

  public GithubBranches(String name, String lastCommitSha) {
    this.name = name;
    this.lastCommitSha = lastCommitSha;
  }
}
