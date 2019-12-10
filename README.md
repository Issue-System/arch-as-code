
# Intro

This project shows examples of integrating with:
- Google Docs API
- Structurizr API


# Pre-requisites
- Java 1.8
- Download Google Doc API credentials see - https://developers.google.com/docs/api/quickstart/java and put it under src/main/resources/StoredCredentials.json
- Create structurizr credentials file under /src/main/resources/credentials.json with structure found in src/main/resources/sample_credentials.json and update with contents from https://structurizr.com/dashboard 

# Build

```bash
./gradle build
```


# Run

```bash
./gradle run
```


# Structurizr Notes
- In order to get the API keys & secret you need to first create a workspace
- Java example - https://github.com/structurizr/java-quickstart
- API documentation - https://structurizr.com/help/web-api
- Structurizr open API docs - https://structurizr.com/static/assets/structurizr-api.yaml

# Google Docs API
- Java API example - https://developers.google.com/docs/api/quickstart/java 
