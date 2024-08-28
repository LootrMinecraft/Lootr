#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk "jdk-21"
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
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
            archive {
                excludes 'fabric/build/libs/**-dev-shadow.jar', 'neoforge/build/libs/**-dev-shadow.jar', 'common/build/libs/**-transform**.jar'
                includes 'fabric/build/libs/**.jar', 'neoforge/build/libs/**.jar', 'common/build/libs/**.jar'
                }
            }
        }
    }
} 
