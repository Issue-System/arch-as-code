# Architecture Update Design

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

## VCS Information
```
arch.jira [VCS]
```

## Architecture Update Yaml Format:
```
name [O]
identifier [A]
milestone [P1]
authors ->
    author... ->
        name [O]
        email [O]
PCAs ->
    PCA... ->
        name [M]
        email [M]
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
    summary [P1]
useful-links -> 
    link... ->
        description [P1]
        link [P1]
milestone-dependencies ->
    dependency... ->
        description [P1]
        links ->
            link... [P1]
model-updates ->
    model-update... ->
        identifier [M]
        description [M]
        views ->
            view-id... [M]
        src-mapping... ->
            ???
ITDs -> 
    ???
TDDs ->
    TDD ->
        component-id / component-alias [M]
        text [M]
        traces ->
            ???
    ...
ACs ->
    AC... ->
        id [M]
        text [M]
```
