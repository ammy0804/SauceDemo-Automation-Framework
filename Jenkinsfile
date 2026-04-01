pipeline {
    agent any

    /* IMPORTANT: Ensure these names match 'Manage Jenkins' -> 'Global Tool Configuration' 
       If your Maven is named 'Maven' and JDK is 'JDK11', change the names below.
    */
    tools {
        maven 'myMaven' 
        jdk 'myjava'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                // Pulls code from your GitHub repository
                checkout scm
            }
        }

        stage('Docker Grid Cleanup & Up') {
            steps {
                // -v wipes old data, --remove-orphans kills ghost containers
                bat 'docker-compose down -v --remove-orphans'
                bat 'docker-compose up -d'
                // Give the Grid 15 seconds to fully initialize
                sleep 15
            }
        }

        stage('Run Automation Tests') {
            steps {
                // Runs the tests. ignore=true ensures reports generate even if a test fails.
                bat 'mvn test -DsuiteXmlFile=grid-docker.xml -Dmaven.test.failure.ignore=true'
            }
        }
    }

    post {
        always {
            // 1. FREE MEMORY: Stop the containers after execution
            bat 'docker-compose down'

            // 2. PUBLISH RESULTS: Using the exact syntax from your generator screenshot
            testNG()

            // 3. SAVE HTML REPORT: Archive the Extent Report so you can view it in Jenkins
            archiveArtifacts artifacts: 'reports/*.html', allowEmptyArchive: true
        }
    }
}