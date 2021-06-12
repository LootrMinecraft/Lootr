#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk "jdk8u292-b10"
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean'
            }
        }
        stage('Build and Deploy') {
            steps {
                echo 'Building and Deploying to Maven'
                sh './gradlew build publish'
            }
        }
    }
    post {
        always {
            archive 'build/libs/**.jar'
        }
    }
}
