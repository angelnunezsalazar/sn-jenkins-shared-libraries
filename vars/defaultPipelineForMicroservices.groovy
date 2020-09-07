def call(body) {
    
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent none
        options {
            skipDefaultCheckout(true)
        }
        environment{
            ARTIFACTORY_URL="${pipelineParams.ARTIFACTORY_URL}"
            COMPONENT_NAME="${pipelineParams.COMPONENT_NAME}"
            HELM_DEV_REPOSITORY="${pipelineParams.HELM_DEV_REPOSITORY}"
            DOCKER_DEV_REPOSITORY="${pipelineParams.DOCKER_DEV_REPOSITORY}"
            DOCKER_DEV_REGISTRY="angelnunez-${DOCKER_DEV_REPOSITORY}.jfrog.io"
            DOCKER_DEV_IMAGE="${DOCKER_DEV_REGISTRY}/${COMPONENT_NAME}"
            DOCKER_INTEGRACION_REPOSITORY="docker-integracion-local"
            DOCKER_INTEGRACION_REGISTRY="angelnunez-${DOCKER_INTEGRACION_REPOSITORY}.jfrog.io"
        }
        stages {
            stage('Build Stages'){
                agent any
                stages{
                    stage('Checkout Code') {
                        steps {
                            checkout scm
                            stash includes: "chart/${COMPONENT_NAME}/Chart.yaml", name: 'Chart.yaml'
                        }
                    }
                    stage('Generate and Publish Docker Image') {
                        steps {
                            buildAndPushDocker()
                        }
                    }
                    stage('Generate and Publish Helm Chart') {
                        steps {
                            buildAndPushHelm()
                        }
                    }
                }
            }
            stage('Integration Stages'){
                when {
                    expression {
                        timeout(time: 3, unit: 'DAYS') {
                            input message: 'Promocionar a Integración?', submitter: getUsersForRole('calidad')
                            return true
                        }
                    }
                    beforeAgent true
                }
                agent any
                stages{
                    stage('Promocionar a Integración') {
                        steps {
                            promoteDocker()
                        }
                    }
                    stage('Desplegar a Integracion') {
                        steps {
                            upgradeHelm()
                        }
                    }
                }
            }
        }
    }
}