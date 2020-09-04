def call(){
    container('curl') {
        script{
            withCredentials([usernamePassword(credentialsId: 'artifactorycloud', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                unstash 'Chart.yaml'
                def chart = readYaml file: "chart/${COMPONENT_NAME}/Chart.yaml"
                sh "curl -i -u ${USERNAME}:${PASSWORD} -X POST '${ARTIFACTORY_URL}/api/docker/${DOCKER_DEV_REPOSITORY}/v2/promote' -H 'Content-Type: application/json' -d '{\"targetRepo\":\"${DOCKER_INTEGRACION_REPOSITORY}\",\"dockerRepository\":\"${COMPONENT_NAME}\",\"tag\":\"${chart.version}\"}'"
            }
        }
    }
}