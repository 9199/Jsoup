pipeline {
  agent any
  stages {
      stage('Stage 1') {
          steps {
              echo 'Hello world!'
          }
      }
  }
  environment {
    NAME_SPACE = 'smartmi-uat'
    PROJECT_NAME = 'oms-interface'
    DOCKER_USER = '2000090957'
    DOCKER_PWD = 'zhimi.com'
    DOCKER_REGISTRY = 'hub.kce.ksyun.com'
  }
}