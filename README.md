# Arch as code

![](https://github.com/nahknarmi/arch-as-code/workflows/Build%20&%20Test/badge.svg) 
[![Test Coverage](https://api.codeclimate.com/v1/badges/bf154787f36e5afed62e/test_coverage)](https://codeclimate.com/github/nahknarmi/arch-as-code/test_coverage)
[![Maintainability](https://api.codeclimate.com/v1/badges/bf154787f36e5afed62e/maintainability)](https://codeclimate.com/github/nahknarmi/arch-as-code/maintainability)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=nahknarmi/arch-as-code)](https://dependabot.com)
[![Known Vulnerabilities](https://snyk.io/test/github/nahknarmi/arch-as-code/badge.svg)](https://snyk.io/test/github/nahknarmi/arch-as-code)

Arch as code project is to manage **product architecture as code**. 

By following this approach we will be able to **manage our architecture documents, models, decisions and diagrams** in the same way we do code and gain all the **tools and workflows supporting modern development**. Think PR reviews, static code analysis, continuous integration & continuous deployment.

Specifically we are making use of the [Structurizr](https://structurizr.com/) tool by Simon Brown as the basis for structuring and storing our architecture models, decisions, views and documentation.

# Getting Started 

## 1. Install arch-as-code cli tool 

### Mac OSX & Linux

```bash
mkdir -p ~/arch-as-code && curl -s https://api.github.com/repos/nahknarmi/arch-as-code/releases/latest | grep "browser_download_url" | cut -d : -f 2,3 | tr -d \" | xargs curl -L | tar --strip-components 1 -x -C ~/arch-as-code

export PATH=$PATH:~/arch-as-code/bin

arch-as-code --help
```

### Windows

Download the latest binary [here](https://github.com/nahknarmi/arch-as-code/releases/latest).


## 2. Export Structurizr Workspace details [dashboard](https://structurizr.com/dashboard):

Export the above details as environment variables:
1. Workspace id
1. Workspace api key
1. Workspace api secret

```
export STRUCTURIZR_WORKSPACE_ID=<WORKSPACE_ID>

export STRUCTURIZR_API_KEY=<WORKSPACE_API_KEY>

export STRUCTURIZR_API_SECRET=<WORKSPACE_API_SECRET>
```

## 3. Publish sample architecture docs to Structurizr
```
git clone https://github.com/nahknarmi/arch-as-code.git

cd arch-as-code

arch-as-code samples/markdown
```

# Development

## Build Pre-requisites
- Java 1.8 or greater.
- Create [Structurizr](https://structurizr.com/) credentials file under `.arch-as-code/structurizr/credentials.json`. 
You can find sample file under `src/main/resources/sample_credentials.json` and update with contents. 
You can find workspaces specific keys from [https://structurizr.com/dashboard](https://structurizr.com/) 

## Build

Builds and tests application code for publishing architecture data structure to Structurizr.

Tests operate against a "test" Structurizr workspace.

```bash
./gradlew build
```

## Continuous Integration & Continuous Deployment

Continuous integration is currently being done using [GitHub Actions](https://github.com/nahknarmi/arch-as-code/actions) (will change soon).

Continuous deployment (publishing documentation) is currently being done using [GitHub Actions](https://github.com/nahknarmi/arch-as-code/actions) (will change soon). 

GitHub Actions configuration is captured under `.github/workflows/`

## Structurizr Notes
- In order to get the API keys & secret you need to first create a workspace
- [Structurizr Java example](https://github.com/structurizr/java-quickstart)
- [API documentation](https://structurizr.com/help/web-api)
- [Structurizr open API docs](https://structurizr.com/static/assets/structurizr-api.yaml)
