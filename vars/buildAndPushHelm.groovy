def call(){
	container('helm') {
		sh "helm package chart/${COMPONENT_NAME}"
	}
	container('curl') {
		script {
			withCredentials([usernamePassword(credentialsId: 'artifactorycloud', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
				def chart = readYaml file: "chart/${COMPONENT_NAME}/Chart.yaml"
				sh "curl -u ${USERNAME}:${PASSWORD} -T ${COMPONENT_NAME}-${chart.version}.tgz '${ARTIFACTORY_URL}/${HELM_DEV_REPOSITORY}/${COMPONENT_NAME}-${chart.version}.tgz'"
			}
		}					
	}	
}