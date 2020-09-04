def call(){
    container('helm') {
        script {
            withCredentials([usernamePassword(credentialsId: 'artifactorycloud', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {									
                unstash 'Chart.yaml'
                def chart = readYaml file: "chart/${COMPONENT_NAME}/Chart.yaml"
                sh "helm repo add artifactory ${ARTIFACTORY_URL}/helm --username ${USERNAME} --password ${PASSWORD}"
                sh "helm pull artifactory/${COMPONENT_NAME} --version ${chart.version} --untar"
                sh "helm upgrade --install ${COMPONENT_NAME} ./${COMPONENT_NAME} -f ./${COMPONENT_NAME}/env/values-integracion.yaml"
            }
        }				
    }
}