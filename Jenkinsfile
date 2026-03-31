pipeline {
    agent any

    tools {
        // These must match your Jenkins Global Tool Configuration names
        maven 'myMaven' 
        jdk 'myjava'    
    }

    stages {
        stage('Docker Grid Up') {
            steps {
                // Starts your Selenium Grid nodes
                bat 'docker-compose up -d'
            }
        }

        stage('Run Automation Tests') {
            steps {
                // Runs the tests through Maven
                bat 'mvn test -DsuiteXmlFile=grid-docker.xml'
            }
        }
    }

    post {
        always {
            // Shuts down the grid even if tests fail
            bat 'docker-compose down'
            
            // Saves your Extent Report in Jenkins
            archiveArtifacts artifacts: 'reports/*.html', fingerprint: true
            
            // Displays TestNG charts (Requires TestNG Results Plugin)
            testng() 
        }
    }
}