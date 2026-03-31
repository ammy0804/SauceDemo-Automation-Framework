pipeline {
    agent any

    tools {
        maven 'myMaven' 
        jdk 'myjava'    
    }

    stages {
        stage('Cleanup & Docker Grid Up') {
            steps {
                bat 'docker-compose down'
                bat 'docker-compose up -d'
            }
        }

        stage('Run Automation Tests') {
            steps {
                // The flag below tells Jenkins: "Don't crash if a test fails; let the report handle it."
                bat 'mvn test -DsuiteXmlFile=grid-docker.xml -Dmaven.test.failure.ignore=true'
            }
        }
    }

    post {
        always {
            bat 'docker-compose down'
            
            // This captures your beautiful HTML Extent Report
            archiveArtifacts artifacts: 'reports/*.html', fingerprint: true
            
            // This generates the trend charts in Jenkins
            testNG() 
        }
    }
}