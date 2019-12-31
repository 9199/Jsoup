pipeline {
    agent any
    tools {
        maven 'maven'
    }
    stages {
        stage('Checkout') {
            steps {
                echo '从GitHub下载项目源码'
                checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'github-account', url: 'https://github.com/9199/Jsoup.git']]])
            }
        }
        stage('Build') {
            steps {
                echo '开始编译构建镜像'
                sh 'cd /home/Jsoup && mvn clean compile -U -DskipTests jib:dockerBuild'
            }
        }
        stage('Push') {
            steps {
                echo '将本地Docker镜像推送到Dockerhub镜像仓库'
                echo '给当前镜像设置带有Harbor地址和项目名称的Tag，这样才能推送到Harbor的library项目之下'
                sh 'docker tag bolingcavalry/hellojib:0.0.1-SNAPSHOT xiaotizi/hellojib:0.0.1-SNAPSHOT'
                echo '登录Dockerhub'
                sh 'docker login -u xiaotizi -p Hand1234'
                echo '登录账号成功，开始推送镜像'
                sh 'docker push xiaotizi/hellojib:0.0.1-SNAPSHOT'
            }
        }
        stage('Clean') {
            steps {
                echo '清理Maven工程'
                sh 'cd /home/Jsoup && mvn clean'
                echo '清理完毕'
            }
        }
    }
}
