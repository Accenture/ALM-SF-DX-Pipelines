def call( packageFile ){
    retrievePackage( packageFile );
    mergeChanges();
}

def retrievePackage( packageFile ){
    dir( 'srcRetrieved/force-app/main/default' ){
        sh "find . -type d -mindepth 1 -maxdepth 1 -exec rm -rf {} +";
    }
    dir( 'srcRetrieved' ){
        def packagePath = "../${env.PATH_SALESFORCE}/${env.PROJECT_NAME}/$packageFile"
        withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY')] ){
            def statusCodeRetrieve = sh( script:"sfdx force:source:retrieve -u ${SFDC_USN} -x \"$packagePath\" --wait 99 > retrieve.json", returnStatus: true );
            echo "INFO: Metadata retrieved with status code: ${statusCodeRetrieve}";
            
            if( statusCodeRetrieve.toString().trim() != '0' ){
                archiveArtifacts allowEmptyArchive: true, artifacts: "retrieve.json", fingerprint: true;
                error "ERROR: Failed to retrieve metadata";
            }
        }
    }
}