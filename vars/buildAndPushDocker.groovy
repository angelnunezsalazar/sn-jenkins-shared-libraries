def call(){
    container('docker') {
        script {
            def chart = readYaml file: "chart/${COMPONENT_NAME}/Chart.yaml"
            withCredentials([usernamePassword(credentialsId: 'artifactorycloud', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                sh "docker login ${DOCKER_DEV_REGISTRY} --username='${USERNAME}' --password='${PASSWORD}'"
                sh "docker build -t ${DOCKER_DEV_IMAGE}:${chart.version} ."
                sh "docker push ${DOCKER_DEV_IMAGE}:${chart.version}"
            }
        }
    }

}