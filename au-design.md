# Architecture Update Design

## Driving idea:
An AU is defined as its own branch of the arch-as-code workspace that contains a yaml documenting that AU, and has updates to the model.

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
name: "" [O]
milestone: "" [P1]
authors:
  - name: "" [O]
    email: "" [O]
  - name: "" [O]
    email: "" [O]
PCAs:
  - name: "" [M] (Future: [A] sourced from a "product master" google sheet)
    email: "" [M] (Future: [A] sourced from a "product master" google sheet)
  - name: "" [M] (Future: [A] sourced from a "product master" google sheet)
    email: "" [M] (Future: [A] sourced from a "product master" google sheet)
P2:
    link: "" [P1]
    jira:
        ticket: "" [P1]
        link: "" [P1]
P1:
    link: "" [P1]
    jira:
        ticket: "" [P1]
        link: "" [P1]
    executive-summary: "" [P1]
useful-links: ??? (might not be necessary to duplicate from the P1)
milestone-dependencies: ??? (might not be necessary to duplicate from the P1)
decisions: (ITDs, IFDs, SSDs, etc.)
    "decision-id":
        text: "" [P1] (or [M])
        TDD-references:
          - "TDD-id" [M]
          - "TDD-id" [M]
    "decision-id":
        text: "" [P1] (or [M])
        TDD-references:
          - "TDD-id" [M]
          - "TDD-id" [M]
TDDs:
    "Component-id":
        "TDD-id": [M]
            text: "tdd-description" [M]
        "TDD-id": [M]
            text: "tdd-description" [M]
    "Component-id":
        "TDD-id": [M]
            text: "tdd-description" [M]
        "TDD-id": [M]
            text: "tdd-description" [M]
functional-requirements: (E2E steps, ACCs, ACs from P1, ACs added manually, etc.)
    "functional-requirement-id":  [M]
        text: "functional requirement text" [M]
        source: "some text, or link to functional requirement" [M]
        TDD-references:
          - "TDD-id" [M]
          - "TDD-id" [M]
    "e2e-5-step-4":  [M]
        text: "must send random xml when random button is pressed" [M]
        source: "link to e2e jira ticket? or spreadsheet containing accs/e2es?" [M]
        TDD-references:
          - "TDD-id" [M]
          - "TDD-id" [M]
capabilities:
    epic:
        title: "" [M]
        jira:               (Potential improvement: automatically create)
            ticket: "" [M]
            link: "" [M]
    feature-stories:
      - title: "" [M]
        jira:
          ticket: "" [A] (after our tool creates jira stories)
          link: "" [A] (after our tool creates jira stories)
        tdd-references:
            - "id" [M]
            - "id" [M]
        functional-requirement-references:
            - "id" [M]
            - "id" [M]
```

## Things to validate:
(KEY: E for Error, W for Warning, T for validate-tdd command, S for validate-story command)   
 - Decisions  
   - [E][T][✓] must have >=1 TDD  
 - TDDs  
   - [E][T][✓] must refer to valid components  
   - [E][T][✓] must be referred to by >=1 decision or functional requirement (no orphan TDDs)
 - Functional Requirements
   - [E][T][✓] if they have TDDs, those TDDs must be valid
 - Stories  
   - [E][S][✓] must refer to >=1 valid functional requirements  
   - [E][S][✓] must refer to >=1 valid tdds  
   - [E][S][✓] all tdds must have >=1 story   
   - [E][S][✓] all functional requirements must have >=1 story   
   - [W][S][ ] check for duplicate stories (both stories have same TDDs and requirements)

## How to visualize:
 - Some way to visualize the model in each branch is necessary
 - Diffs not necessary for now
