pipeline {
  agent any
  stages {
    stage('check') {
      steps {
        echo 'hello world'
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