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
        }
}