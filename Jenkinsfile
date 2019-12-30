pipeline {
  agent any
  stages {
      stage('Check') {
          steps {
              echo 'Check Success!'
          }
      }
      stage('Build') {
          steps {
              echo 'Build Success!'
          }
      }
      stage('Image') {
          steps {
              echo 'Image Success!'
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