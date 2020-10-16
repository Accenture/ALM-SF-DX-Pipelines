def call(){
    parseRequest();
    initWorkspace();
}

def parseRequest(){
    echo "${JSON_PACKAGE}";
    def requestBody  = packageParser.parsePackage( "${JSON_PACKAGE}" );
    env.BRANCH_NAME  = requestBody.EasyCommit__User_Story_Code__c;
    env.BRANCH_DESC  = requestBody.EasyCommit__User_Story_Description__c;
    env.API_VERSION  = requestBody.EasyCommit__API_Version__c + ".0";
    env.JSON_PACKAGE = requestBody.EasyCommit__JSON_Package__c;
    println( env.JSON_PACKAGE );
}

def initWorkspace(){
    env.PATH_SCRIPTS    = 'scripts';
    env.PATH_SALESFORCE = 'AP_SALESFORCE';

    dir( env.PATH_SCRIPTS ){
        git credentialsId: env.SCRIPTS_CREDENTIALS, url: "${env.SCRIPTS_URL}", branch: "${env.SCRIPTS_SOURCE_BRANCH}";
    }

    dir( env.PATH_SALESFORCE ){
        git credentialsId: env.REPO_CREDENTIALS, url: "${env.REPO_URL}", branch: "${env.SOURCE_BRANCH}";
        sh "git config http.sslVerify false && git config core.quotePath false";
    }

    dir( 'artifacts_folder' ){
        deleteDir()
    }
    dir( 'srcRetrieved' ){
        deleteDir()
    }
}