pipeline {
    agent any

    tools {
        maven 'Maven 3.9.x' // Must match the name in Jenkins Global Tool Configuration
        jdk 'JDK 17'        // Must match the name in Jenkins Global Tool Configuration
    }

    stages {
        stage('Checkout') {
            steps {
                // Pulls the latest code from your GitHub
                git 'https://github.com/YourUsername/YourRepository.git'
            }
        }

        stage('Docker Grid Up') {
            steps {
                // Starts your Selenium Grid in the background
                sh 'docker-compose up -d'
            }
        }

        stage('Run Automation Tests') {
            steps {
                // Runs the specific Docker XML suite
                sh 'mvn test -DsuiteXmlFile=grid-docker.xml'
            }
            post {
                always {
                    // Stop the grid even if tests fail
                    sh 'docker-compose down'
                }
            }
        }
    }

    post {
        always {
            // Tells Jenkins to save your Extent Report so you can view it later
            archiveArtifacts artifacts: 'reports/*.html', fingerprint: true
            
            // Optional: If you use the TestNG Plugin, this shows the charts in Jenkins
            testng() 
        }
    }
}