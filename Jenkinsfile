pipeline {
    agent any
    tools {
        maven 'myMaven' 
        jdk 'myjava'
    }
    stages {
        stage('Checkout SCM') {
            steps { checkout scm }
        }
        stage('Docker Grid Cleanup & Up') {
            steps {
                bat 'docker-compose down -v --remove-orphans'
                bat 'docker-compose up -d'
                sleep 20
            }
        }
        stage('Run Automation Tests') {
            steps {
                // The -Dmaven.test.failure.ignore=true is critical here
                bat 'mvn test -DsuiteXmlFile=grid-docker.xml -Dmaven.test.failure.ignore=true'
            }
        }
    }
    post {
        always {
            bat 'docker-compose down'
            testNG()
            // Using standard archive because you don't have the publishHTML plugin
            archiveArtifacts artifacts: 'reports/SauceDemoReport.html', allowEmptyArchive: true
        }
    }
}