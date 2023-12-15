pipeline {
  agent any
  environment{
        PATH ="/usr/share/man/man1/mvn.1.gz:$PATH"
  }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    sh 'mvn clean install'
                    // Add your build commands here
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo 'Testing...'
                   }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo 'Deploying...'
                    // Add your deployment commands here
                }
            }
        }
    }
}
