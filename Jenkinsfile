pipeline {
    agent any

    tools {
        // Change these to match the names Jenkins suggested in your error log!
        maven 'myMaven' 
        jdk 'myjava'    
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/ammy0804/SauceDemo-Automation-Framework.git'
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