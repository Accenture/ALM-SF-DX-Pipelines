def call(){
	sh "sfdx force:project:create -n srcRetrieved";
	sh "rm -rf srcRetrieved/force-app/main/default && mkdir srcRetrieved/force-app/main/default"
	dir( 'srcRetrieved' ){
		withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY')] ){
			def statusCodeRetrieve = sh( script:"sfdx force:source:retrieve -u ${SFDC_USN} -x ../artifacts_folder/package.xml --apiversion ${env.API_VERSION} --wait 30", returnStatus: true );
			echo "INFO: Metadata retrieved with status code: ${statusCodeRetrieve}";
		}
	}
	sh "zip -r srcRetrieved.zip srcRetrieved/force-app/main/default"
	sh "mv srcRetrieved.zip artifacts_folder";
	archiveArtifacts allowEmptyArchive: true, artifacts: "artifacts_folder/srcRetrieved.zip", fingerprint: true;
}