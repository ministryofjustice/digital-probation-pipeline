# digital-probation-pipeline
Reusable pipeline-as-code snippets. The use of these snippets depends on the definition of a shared library in Jenkins which is configured to point
 to this repository. At the current time, this is done in the digital-probation-tooling project which deploys Jenkins in Kubernetes. 

# Usage
The snippets can be used as follows. This shows the use of simplePipeline, via a library definition named "digital-probation-pipeline"

```groovy
@Library('digital-probation-pipeline')

simplePipeline
```

# Snippets


| Snippet        | Pipeline stages |
| ---------------|-----------------|
| simplePipeline | Unit Test, Coverage, OWASP Dependency Check, Sonar Check





