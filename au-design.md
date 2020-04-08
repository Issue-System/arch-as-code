# Architecture Update Design

## Driving idea:
An AU is defined as it's own branch of the arch-as-code workspace that contains a yaml documenting that AU, and has updates to the model.

## Key:
```
[O]   = provided as a arch-as-code command line argument, or from something like a ~/.arch-as-code/credentials.json file
[M]   = must be entered manually by editing the yaml
[P1]  = obtained from P1 automatically
[A]   = automatically generated programmatically
[VCS] = not captured in yaml at all-- information comes for free from the version control that the arch-as-code repo will be under (such as the name of a branch or commit, etc.)
...   = array
?     = more analysis required
```

## Information that goes in the Architecture (model yaml)
```
model updates [M]
```

## Information that would be contained via VCS
```
arch.jira [VCS] (would be in the PR title/description, or the branch name)
model updates' descriptions (would be in the commit history, PR description, etc.)
```

## Architecture Update Yaml Format:
```
name [O]
identifier [A]
milestone [P1]
jira epic ->
    ticket [A] (when feature stories are created)
    link [A] (when feature stories are created)
authors ->
    author... ->
        name [O]
        email [O]
PCAs ->
    PCA... ->
        name [M] (Future: [A] sourced from a "product master" google sheet)
        email [M] (Future: [A] sourced from a "product master" google sheet)
P2 ->
    link [P1]
    jira ->
        ticket [P1]
        link [P1]
P1 ->
    link [P1]
    jira ->
        ticket [P1]
        link [P1]
    executive-summary [P1]
useful-links -> (might not be necessary to duplicate from the P1)
    link... ->
        description [P1] / [M]
        link [P1] / [M]
milestone-dependencies -> (might not be necessary to duplicate from the P1)
    dependency... ->
        description [P1]
        links ->
            link... [P1]
requirements -> 
    requirement... ->
        id / alias [P1] (or [M])
        title [P1] (or [M])
        TDDs ->
            - TDD-id / TDD-alias [M]
            - TDD-id / TDD-alias [M]
            - TDD-id / TDD-alias [M]
TDDs ->
    Component... ->
        component-id / component-alias [M]
        TDDs -> 
            - TDD ->
                id / alias [M]
                text [M]
            - TDD ->
                id / alias [M]
                text [M]
            - TDD ->
                id / alias [M]
                text [M]
Capabilities ->
    E2Es ??? [M]
    ACCs ??? [M]/[or A from its own spreadsheet]
    Stories ->
        TDDs ->
            - id / alias [M]
            - id / alias [M]
            - id / alias [M]
        Requirements ->
            - id / alias [M]
            - id / alias [M]
            - id / alias [M]
        ???
```

## Things to validate:
 - TDDs
    - must refer to valid components
    - must be referred to by >=1 requirement (no orphan TDDs)
    - all requirements must have >=1 TDD
 - Stories
    - must refer to >=1 valid requirements
    - must refer to >=1 valid tdds
    - all tdds must have >=1 story
    - all requirements must have >=1 story

## How to visualize:
 - Some way to visualize the model in each branch is necessary
 - Diffs not necessary for now
