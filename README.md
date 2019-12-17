# digital-probation-pipeline
Reusable pipeline-as-code snippets. The use of these snippets depends on the definition of a shared library in Jenkins which is configured to point
 to this repository. At the current time, this is done in the digital-probation-tooling project which deploys Jenkins in Kubernetes. 

# Usage
The snippets can be used as follows. This shows the use of simplePipeline, via a library definition named "digital-probation-pipeline". A branch
 name can be added as shown and could be drawn from environment variables.

```groovy
library(
  identifier: 'digital-probation-pipeline@${your branch name}'
)

simplePipeline()
```

# Snippets


| Snippet        | Pipeline stages |
| ---------------|-----------------|
| simplePipeline | Unit Test, Coverage, OWASP Dependency Check, Sonar Check





