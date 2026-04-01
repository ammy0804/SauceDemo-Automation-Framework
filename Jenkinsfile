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
            // Clean up again so the next build starts fresh
            bat 'docker-compose down'
            testNG(pattern: '**/testng-results.xml')
        }
    }
}