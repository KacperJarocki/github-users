package pl.app.github.entities;

import java.util.List;

public class GithubRepository {
  String repositoryName;
  String ownerLogin;
  List<GithubBranches> branches;
  Boolean fork;
}
