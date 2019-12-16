def call(Map pipelineParams) {

    pipeline {
        agent {
            kubernetes {
                yamlFile 'BuildAgent.yaml'
            }
        }

        options {
            timeout(time: 5, unit: 'MINUTES')
        }

        stages {
            stage('Unit Test') {
                steps {
                    container('gradle') {
                        sh "echo HELLO"
                        sh "gradle clean test"
                    }
                }
            }
            stage('Coverage') {
                steps {
                    container('gradle') {
                        sh "gradle jacocoTestReport jacocoTestCoverageVerification"
                    }
                }
            }
            stage('OWASP Dependency check') {
                steps {
                    container('dependency-check') {
                        sh './dependency-check.sh > /dev/null'
                    }
                }
            }
            stage('Sonar scanner') {
                steps {
                    container('sonar-scanner') {
                        withCredentials([string(credentialsId: 'hmpps-pcs-tooling-sonarqube-user-token', variable: 'SONAR_TOKEN')]) {
                            withEnv(["SONAR_PROJECT_KEY=digital-probation-java-skeleton:${env.GIT_BRANCH}"]) {
                                sh './sonar-scan.sh'
                            }
                        }
                    }
                }
            }
        }
        post {
            always {
                echo 'Verify that the dependency check reports have been deleted by counting files in the ./dependency-check-report directory before and after deletion'
                echo 'An eyeball check of this build log should show that the directory no longer exists, even if the agent somehow remains alive after the job has finished'
                sh '''
                ls -l ./dependency-check-report/ | wc -l
                rm -rf ./dependency-check-report/
                ls -l ./dependency-check-report/ | wc -l
                  '''
            }
        }
    }
}
