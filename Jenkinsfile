pipeline {
    agent any

    environment {
        GIT_REPO_URL   = 'https://github.com/aptitude-pro/aptitude-be.git'
        GIT_BRANCH     = 'main'

        APP_NAME       = 'aptitude-be'
        IMAGE_NAME     = 'aptitude-be'
        IMAGE_TAG      = "${BUILD_NUMBER}"
        CONTAINER_NAME = 'aptitude-be'

        APP_PORT       = '8080'
        HOST_PORT      = '8081'
    }

    stages {
        stage('Checkout from GitHub') {
            steps {
                git branch: "${GIT_BRANCH}",
                    url: "${GIT_REPO_URL}"
            }
        }

        stage('Build Jar') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean bootJar -x test'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
            }
        }

        stage('Deploy Container') {
            steps {
                sh '''
            docker rm -f $CONTAINER_NAME || true

            docker run -d \
              --name $CONTAINER_NAME \
              --env-file /secrets/backend.env \
              -p $HOST_PORT:$APP_PORT \
              $IMAGE_NAME:$IMAGE_TAG
                '''
            }
        }
    }

    post {
        success {
            echo "배포 성공: ${IMAGE_NAME}:${IMAGE_TAG}"
            echo "접속: http://localhost:8081"
        }
        failure {
            echo '배포 실패'
        }
    }
}