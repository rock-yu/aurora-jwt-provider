pipeline {
    agent {
        docker {
            image "jdk11-gradle:6.7"
            alwaysPull true
            label 'search'
            customWorkspace "workspace/${JOB_NAME}"
        }
    }
    options {
        disableConcurrentBuilds()
    }
    environment {
        JOB_ID="${BUILD_TAG}"
    }
    stages {
        stage('Build') {
            steps {
                sh "./gradlew clean build --no-build-cache"
            }
        }

        stage('Publish Library') {
            steps {
                sh "./gradlew publish"
            }
        }
    }
}
