def call (){
    env.PATH_SCRIPTS    = 'scripts';
    env.PATH_SALESFORCE = 'AP_SALESFORCE';
    env.project         = env.PROJECT_NAME;
    
    dir( env.PATH_SCRIPTS ){
        git credentialsId: env.SCRIPTS_CREDENTIALS, url: "${env.SCRIPTS_URL}", branch: "${env.SCRIPTS_SOURCE_BRANCH}";
    }

    dir( env.PATH_SALESFORCE ){
        git credentialsId: env.REPO_CREDENTIALS, url: "${env.REPOSITORY_URL}";
        sh "git config http.sslVerify false && git config core.quotePath false";
    }
}