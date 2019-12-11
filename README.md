# Arch as code

![](https://github.com/nahknarmi/arch-as-code/workflows/Build%20&%20Test/badge.svg) 

[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=nahknarmi/arch-as-code)](https://dependabot.com)

[![Known Vulnerabilities](https://snyk.io/test/github/nahknarmi/arch-as-code/badge.svg)](https://snyk.io/test/github/nahknarmi/arch-as-code)


Arch as code project is to manage **product architecture as code**. 

By following this approach we will be able to **manage our architecture documents, models, decisions and diagrams** in the same way we do code and gain all the **tools and workflows supporting modern development**. Think PR reviews, static code analysis, continuous integration & continuous deployment.

Specifically we are making use of the [Structurizr](https://structurizr.com/) tool by Simon Brown as the basis for structuring and storing our architecture models, decisions, views and documentation.

## Build Pre-requisites
- Java 1.8 or greater.
- Create [Structurizr](https://structurizr.com/) credentials file under /src/main/resources/credentials.json with structure found in `src/main/resources/sample_credentials.json` and update with contents from [https://structurizr.com/dashboard](https://structurizr.com/) 

## Build

Builds and tests application code for publishing architecture data structure to Structurizr.

Tests operate against a "test" Structurizr workspace.

```bash
./gradle build
```


## Publish Artifacts

Publishes documentation to "production" Structurizr workspace. 

By default the documentation is store under `documentation/products/`.

```bash
./gradle run
```

## Continuous Integration & Continuous Deployment

Continuous integration is currently being done using [GitHub Actions](https://github.com/nahknarmi/arch-as-code/actions) (will change soon). 

GitHub Actions configuration is captured under `.github/workflows/`

## Structurizr Notes
- In order to get the API keys & secret you need to first create a workspace
- [Structurizr Java example](https://github.com/structurizr/java-quickstart)
- [API documentation](https://structurizr.com/help/web-api)
- [Structurizr open API docs](https://structurizr.com/static/assets/structurizr-api.yaml)
