## Developing

### Build Pre-requisites

- JDK 11 or greater
- Create [Structurizr](https://structurizr.com/) credentials file under
  `.arch-as-code/structurizr/credentials.json`. You can find sample file
  under `src/main/resources/sample_credentials.json` and update with
  contents. You can find workspaces specific keys from
  [https://structurizr.com/dashboard](https://structurizr.com/dashboard)

### Build

Builds and tests application code for publishing architecture data
structure to Structurizr.

Tests operate against a "test" Structurizr workspace.

```bash
./gradlew build
```

### Run

Runs Bootstrap.java. Equivalent to executing the binary of a release.

```bash
./gradlew run --args='-h'
```

For example, to initialize a workspace, run:

```bash
mkdir /tmp/temporaryWorkSpace
./gradlew run --args="init -i ${WORKSPACE_ID} -k ${WORKSPACE_API_KEY} -s ${WORKSPACE_API_SECRET} /tmp/temporaryWorkSpace"
```

## Continuous Integration & Continuous Deployment

Continuous integration is currently being done using
[GitHub Actions](https://github.com/trilogy-group/arch-as-code/actions).

Continuous deployment (publishing documentation) is currently being done
using
[GitHub Actions](https://github.com/trilogy-group/arch-as-code/actions).

GitHub Actions configuration is captured under `.github/workflows/`

## Structurizr notes

- [Structurizr Java example](https://github.com/structurizr/java-quickstart)
- [API documentation](https://structurizr.com/help/web-api)
- [Structurizr open API docs](https://structurizr.com/static/assets/structurizr-api.yaml)

