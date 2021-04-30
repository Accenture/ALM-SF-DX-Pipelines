def call( operation ){
    
    def statusCode;
    def statusMessage;

    dir( "${env.PATH_SALESFORCE}" ){
        if( operation == 'validate' ){
            statusCode    = validatePackage( 'srcToDeploy' );
            statusMessage = handleResults( 'validate.json' )
        }
        else{
            statusCode    = deployPackage( 'srcToDeploy' );
            statusMessage = handleResults( 'deploy.json' )
        }
    }

    utils.handleValidationErrors( 'Validation', statusCode.toString().trim(), statusMessage);
}

def validatePackage( folder ){
    def statusCode;
    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY') ] ){
        statusCode = sh( script:"sfdx force:source:deploy -u ${SFDC_USN} -p ${folder} --apiversion ${env.API_VERSION} --wait 90 -c --json > validate.json", returnStatus: true );
        echo "INFO: Validation returned status code: ${statusCode}";
    }
    return statusCode;
}

def deployPackage( folder ){
    def statusCode;
    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY') ] ){
        statusCode = sh( script:"sfdx force:source:deploy -u ${SFDC_USN} -p ${folder} --apiversion ${env.API_VERSION} --wait 90 --json > deploy.json", returnStatus: true );
        echo "INFO: Deployment returned status code: ${statusCode}";
    }
    return statusCode;
}

def handleResults( resultsFile ){
    archiveArtifacts allowEmptyArchive: true, artifacts: "${resultsFile}", fingerprint: true;
    def sfdxResponse = readJSON file: "${resultsFile}";
    if( resultsFile == 'validate.json' ){
        statusMessage = sfdxResponse.result.status;
    }
    else{
        statusMessage = sfdxResponse.result.deployedSource ? 'Succeeded' : 'Failed';
    }
    
    dir( "artifacts_folder" ){
        sh "mv ../${resultsFile} .";
    }
    
    return statusMessage;
}