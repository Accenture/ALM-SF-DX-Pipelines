import java.util.regex.Matcher;
import java.util.regex.Pattern;

def call(){
    installRequirements();

    def propertiesFile = "pipelines/resources/config.properties";
    env.API_VERSION    = getProperty( propertiesFile, "apiPackage" );
    
    commentHandler.postMessage( "### Starting Build [${env.BUILD_DISPLAY_NAME}](${env.RUN_DISPLAY_URL})" );
    
    authorizeOrg();
    generateDescribe();
}

def installRequirements(){
    sh "pip3 install -r ${env.PATH_SCRIPTS}/requirements.txt"
}

def authorizeOrg(){

    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY') ] ){
        def statusCode = sh( script: "sfdx force:auth:logout -u ${SFDC_USN} -p", returnStatus: true );
        echo "Logout completed with status code: ${statusCode}";
    }

    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY'), file( credentialsId: env.SFDX_KEY, variable: 'sfdxKey' ) ] ){
        statusCode = sh( script: "sfdx force:auth:jwt:grant --clientid ${SFDC_CONSUMER_KEY} -s --jwtkeyfile '${sfdxKey}' --username ${SFDC_USN} --instanceurl ${env.SFDX_URL} --json > login.json ", returnStatus: true );
        echo "Login completed with status code: ${statusCode}";
        handleLogin( statusCode );
    }
}

def getProperty(file,key){
    def props = readProperties  file: file;
    echo "$props";
    return props[key];
}

def generateDescribe(){
    def statusCode;
    withCredentials( [usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY') ] ){
        statusCode = sh(script: "sfdx force:mdapi:describemetadata -u ${SFDC_USN} -f ${env.PATH_SALESFORCE}/${env.PROJECT_NAME}/describe.log --apiversion ${env.API_VERSION}", returnStatus: true);
        if(statusCode != 0){
            error 'FATAL: Cannot create describe log. ABORTING';
        }
    }
	return statusCode
}    

def handleLogin( status ){
    if( status != 0 ){
        archiveArtifacts allowEmptyArchive: true, artifacts: "login.json", fingerprint: true;
        def message = "+  **Org Login Failed.** [Login Error]( ${env.BUILD_URL}artifact/login.json )";
        commentHandler.editLastMessage( message );
        error 'FATAL: Error loging into Org. ABORTING';
    }
}
