pipeline {
    agent any

    tools {
        maven 'myMaven' 
        jdk 'myjava'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Docker Grid Cleanup & Up') {
            steps {
                bat 'docker-compose down -v --remove-orphans'
                bat 'docker-compose up -d'
                sleep 15
            }
        }

        stage('Run Automation Tests') {
            steps {
                // ignore=true ensures the pipeline continues to reporting even if tests fail
                bat 'mvn test -DsuiteXmlFile=grid-docker.xml -Dmaven.test.failure.ignore=true'
            }
        }
    }

  post {
        always {
            // Stop containers
            bat 'docker-compose down'
            
            // Publish TestNG results
            testNG()

            // ARCHIVE THE REPORT: This is the standard way without extra plugins
            archiveArtifacts artifacts: 'reports/SauceDemoReport.html', fingerprint: true, allowEmptyArchive: true
        }
    }
}