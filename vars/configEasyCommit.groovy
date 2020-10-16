def call(){
    checkBranch();
    authorizeOrg();
    generateDescribe();
}

def installRequirements(){
    sh "pip3 install -r ${env.PATH_SCRIPTS}/requirements.txt";
}

def checkBranch(){
    dir( env.PATH_SALESFORCE ){
        sshagent( credentials: [ env.REPO_CREDENTIALS ] ){
            sh "git ls-remote --heads ${env.REPO_URL} feature/${env.BRANCH_NAME}";
            env.branchExists = sh( script: "git ls-remote --heads ${env.REPO_URL} feature/${env.BRANCH_NAME} | wc -l", returnStdout: true );
        }
        if( env.branchExists.trim() == '1' ){
            sh "git checkout feature/${env.BRANCH_NAME}";
            sh "git reset --hard origin/feature/${env.BRANCH_NAME}";
            sh "git status";
        }
        else{
            sh "git checkout -B feature/${env.BRANCH_NAME} origin/${env.SOURCE_BRANCH}";
        }
    }
}

def authorizeOrg(){
    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY')] ){
        def statusCode = sh( script: "sfdx force:auth:logout -u ${SFDC_USN} -p", returnStatus: true );
        echo "Logout completed with status code: ${statusCode}";
    }

    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY'), file( credentialsId: env.SFDX_KEY, variable: 'sfdxKey' ) ] ){
        statusCode = sh( script: "sfdx force:auth:jwt:grant --clientid ${SFDC_CONSUMER_KEY} --jwtkeyfile '${sfdxKey}' --username ${SFDC_USN} --instanceurl ${env.SFDX_URL} --json > login.json", returnStatus: true);
        echo "Login completed with status coded ${statusCode}";
        handleLogin( statusCode );
    }
}

def generateDescribe(){
    def statusCode;
    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY')] ){
        statusCode = sh(script: "sfdx force:mdapi:describemetadata -u ${SFDC_USN} -f describe.log --apiversion ${env.API_VERSION}", returnStatus: true);
        if(statusCode != 0){
            error 'FATAL: Cannot create describe log. ABORTING';
        }
    }
	return statusCode
}

def handleLogin( status ){
    if( status != 0 ){
        archiveArtifacts allowEmptyArchive: true, artifacts: "login.json", fingerprint: true;
        error 'FATAL: Error loging into Org. ABORTING';
    }
}