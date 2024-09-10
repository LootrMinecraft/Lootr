#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk "jdk-21"
    }
    stages {
        stage('Prepare') {
            steps {
                echo 'Preparing Project'
                checkout scmGit(
                    branches: [[name: '${SOURCE_BRANCH}']],
                    extensions: [
                       cloneOptions(noTags:false)
                    ]
                )
            }
        }
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
            archiveArtifacts artifacts: 'fabric/build/libs/**.jar, neoforge/build/libs/**.jar, common/build/libs/**.jar', excludes: 'fabric/build/libs/**-dev-shadow.jar, neoforge/build/libs/**-dev-shadow.jar, common/build/libs/**-transform**.jar', followSymlinks: false, onlyIfSuccessful: true
        }
    }
} 
