def call(Map pipelineParams) {

    pipeline {
        agent {
            kubernetes {
                yaml """metadata:
                          labels:
                            agent-type: DependencyCheck
                        spec:
                          containers:
                            - name: jnlp
                              env:
                                - name: CONTAINER_ENV_VAR
                                  value: jnlp
                              securityContext:
                                runAsUser: 1000
                        
                            - name: gradle
                              image: \'gradle:jdk11\'
                              command:
                                - cat
                              tty: true
                              securityContext:
                                runAsUser: 1000
                        
                            - name: dependency-check
                              image: \'owasp/dependency-check:5.2.0\' # Leave this at v5.2.0
                              command:
                                - cat
                              tty: true
                              securityContext:
                                runAsUser: 1000
                              env:
                                - name: CONTAINER_ENV_VAR
                                  value: dependency-check
                              volumeMounts:
                                - name: owasp-dependency-check-data
                                  mountPath: /usr/share/dependency-check/data
                        
                            - name: sonar-scanner
                              image: \'sonarsource/sonar-scanner-cli:4.2\'
                              command:
                                - cat
                              tty: true
                              securityContext:
                                runAsUser: 1000
                              env:
                                - name: CONTAINER_ENV_VAR
                                  value: sonar-scanner
                                - name: SONAR_HOST_URL
                                  valueFrom:
                                    configMapKeyRef:
                                      name: jenkins-config
                                      key: sonarqube-url
                        
                          volumes:
                            - name: owasp-dependency-check-data
                              persistentVolumeClaim:
                                claimName: jenkins-shared-persistent-data"""
            }
        }

        options {
            // Fails the job if it takes longer than this
            timeout(time: 60, unit: 'MINUTES')
        }

        stages {
            stage('Unit Test') {
                steps {
                    container('gradle') {
                        sh "gradle clean build"
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
