# GitHub Repository Service

This project provides a service for interacting with GitHub repositories. It allows retrieving repositories for a given GitHub user and filtering repositories that are not forks. It also fetches the branches of repositories that are not forks.

## Features

- Get all repositories of a given user.
- Filter out repositories that are forks.
- Retrieve branches for repositories that are not forks.
- Handle errors and provide appropriate responses in case of missing users or other issues.

## Setup

### Prerequisites

- Java 21 or later
- Spring Boot 2.7 or later
- Gradle (for building the project)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/github-repository-service.git
   cd github-repository-service
2. Build the project using Gradle:
```bash
./gradlew build
```
Or, if you have Gradle installed globally, you can use
```bash
gradle build
```
3. Run the application:
```Bash
./gradlew bootRun
```
or:
```bash
gradle bootRun
```
### Endpoint

Get all repositories of a user: GET /api/github/{username}

Returns all repositories of the user that are not forks.

Example:

```bash
curl http://localhost:8080/api/github/testuser
```
If the user does not exist, it returns a 404 error with a message:
```json
{
  "status": 404,
  "message": "User 'testuser' not found"
}
```

### Tests
This project includes integration tests for various scenarios, including:

- Valid user with repositories.
- User with only forked repositories.
- User with no repositories.
- Non-existent user (returns 404).

Running Tests
To run the tests:

Clone the repository.

Navigate to the project directory.

Run the tests with Gradle:
```bash
./gradlew test
```
Or, if you have Gradle installed globally:

```bash
gradle test
```
