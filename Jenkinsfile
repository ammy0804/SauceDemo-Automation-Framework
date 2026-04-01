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
            bat 'docker-compose down'
            testNG()

            // THIS LINE is what makes the report visible in Jenkins
            archiveArtifacts artifacts: 'reports/SauceDemoReport.html', fingerprint: true, allowEmptyArchive: false
        }
    }
}