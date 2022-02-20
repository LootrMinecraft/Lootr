#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk "jdk-16.0.1+9"
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'git submodule init'
                sh 'git submodule update'
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
