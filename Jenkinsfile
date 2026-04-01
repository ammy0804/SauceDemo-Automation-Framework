pipeline {
    agent any

    stages {
        // Stage 1: Get the code from GitHub
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        // Stage 2: THE NEW STAGE (Add/Replace here)
        stage('Cleanup & Docker Grid Up') {
            steps {
                // -v: deletes old volumes (cleans DB/Grid cache)
                // --remove-orphans: kills manual containers from previous runs
                bat 'docker-compose down -v --remove-orphans'
                bat 'docker-compose up -d'
            }
        }

        // Stage 3: Run the tests
        stage('Run Automation Tests') {
            steps {
                // This will now hit a fresh, clean grid
                bat 'mvn test -DsuiteXmlFile=grid-docker.xml'
            }
        }
    }

post {
    always {
        // Clean up docker containers
        bat 'docker-compose down'
        
        // Corrected syntax for TestNG plugin
        testNG testResults: '**/testng-results.xml'
        
        // Archive the Extent Report so you can see it in Jenkins
        archiveArtifacts artifacts: 'reports/*.html', allowEmptyArchive: true
    }
}
}