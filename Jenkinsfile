pipeline {
agent any

    stages {
        stage('Test') {
            steps {
                sh 'robot Tests'
            }
        }
    }

    post {
        always {
            script {
                step(
                    [
                        $class                    : 'RobotPublisher',
                        outputPath                : WORKSPACE,
                        outputFileName            : "*.xml",
                        reportFileName            : "report.html",
                        logFileName               : "log.html",
                        disableArchiveOutput      : false,
                        passThreshold             : 100,
                        unstableThreshold         : 95.0,
                        otherFiles                : "*.png"
                    ]
                )
            }
        }

        success {
                emailext body: '''${SCRIPT, template="./templates/robot_framework_template.groovy"}''',
                mimeType: 'text/html',
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                to: 'EMAIL'
        }
		
        failure {
                emailext body: '''${SCRIPT, template="./templates/robot_framework_template.groovy"}''',
                mimeType: 'text/html',
                subject: "FAIL: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                to: 'EMAIL'
        }
    }
}