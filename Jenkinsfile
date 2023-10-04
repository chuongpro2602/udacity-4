pipeline {

    agent {
        docker { image 'maven:3.6.3-openjdk-8-slim' }
    }

    environment {
        PROJECT_REPOSITORY_DIRECTORY = "starter_code"
        APPLICATION_WAR_FILE = "target/starter_code-0.0.1.war"
        APPLICATION_CONTEXT = "$WORKSPACE/$PROJECT_REPOSITORY_DIRECTORY"
    }

    options {
        skipDefaultCheckout()
        skipStagesAfterUnstable()
    }

    stages {

        stage ('Checkout') {

            steps {
                checkout scm
            }

        }

        stage ('Build') {

            steps {

                script {
                    sh 'cd $APPLICATION_CONTEXT && mvn -B -DskipTests clean package'
                	 sh 'echo "Building the application"'
                }

            }

        }

        stage ('Test') {

            steps {

                script {
                    sh 'cd $APPLICATION_CONTEXT && mvn clean test cobertura:cobertura -Dcobertura.report.format=xml'
                }

            }

            post {
                always {
                    junit '**/target/*-reports/TEST-*.xml'
                    step([$class: 'CoberturaPublisher', coberturaReportFile: 'starter_code/target/site/cobertura/coverage.xml'])
                }
            }

        }

        stage ('Deploy') {

            steps {

                script {

                    def deploy_application = false

                    try {
                      
                        timeout(time: 5, unit: 'MINUTES') {

                            deploy_application = input id: 'deploy_application',
                                message: 'Deploy ?',
                                ok: 'Yes.',
                                parameters: [
                                    [
                                        $class: 'BooleanParameterDefinition',
                                        defaultValue: true,
                                        successfulOnly: true,
                                        description: 'Y/N',
                                        name: 'Y'
                                    ]
                                ]

                        }

                    } catch (error) {

                        def user = error.getCauses()[0].getUser()
                        if( 'SYSTEM' == user.toString() ) {
                            echo ' Build timeout.'
                            currentBuild.result = 'ABORTED'
                            throw error
                        }
                        currentBuild.result = 'UNSTABLE'

                    }

                    if ( deploy_application == true ) {
                        // Deploying the application
                        sh 'cd $APPLICATION_CONTEXT && mvn clean package'

                        deploy adapters: [
                            tomcat9(url: 'http://myContainer:8080',
                            credentialsId: 'tomcat')
                        ],
                            war: 'starter_code/target/*.war',
                            contextPath: '/'

                    }

                }   // Script

            }       // Steps

        } // Stage

    }   // Stages

}       // Pipeline
