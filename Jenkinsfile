pipeline {
    agent any

    tools {
        maven 'myMaven' 
        jdk 'myjava'    
    }

    stages {
        stage('Cleanup & Docker Grid Up') {
            steps {
                // First, we force-stop any old containers to avoid the "Conflict" error
                bat 'docker-compose down'
                
                // Now we start a fresh grid
                bat 'docker-compose up -d'
            }
        }

        stage('Run Automation Tests') {
            steps {
                // Runs the tests
                bat 'mvn test -DsuiteXmlFile=grid-docker.xml'
            }
        }
    }

    post {
        always {
            // Clean up containers after the run
            bat 'docker-compose down'
            
            // Save the HTML Extent Report
            archiveArtifacts artifacts: 'reports/*.html', fingerprint: true
            
            // FIX: Capital 'NG' to match what your Jenkins log requires
            testNG() 
        }
    }
}