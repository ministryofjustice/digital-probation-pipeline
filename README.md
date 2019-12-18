# digital-probation-pipeline
The purpose of this repository is to store shared definitions of Jenkins pipeline code, with the intention of achieving consistency of build and
 deployment pipelines across the MOJ estate. 
 
The project follows the conventional layout described here https://jenkins.io/doc/book/pipeline/shared-libraries/
 
 The use of these snippets depends on the definition of a shared library in Jenkins which is configured to point
 to this repository. At the current time, this is done in the digital-probation-tooling project which deploys Jenkins in Kubernetes. 

# Usage
A pipeline definition example is given below, showing the use of simplePipeline, via a library definition named "digital-probation
-pipeline". A branch name can be added as shown and could be drawn from environment variables.

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





