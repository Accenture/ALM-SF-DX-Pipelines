def call(){
    env.PATH_SCRIPTS    = 'scripts';
    env.PATH_SALESFORCE = 'AP_SALESFORCE';
    env.project         = env.PROJECT_NAME;
    
    dir( env.PATH_SCRIPTS ){
        git credentialsId: env.SCRIPTS_CREDENTIALS, url: "${env.SCRIPTS_URL}", branch: "${env.SCRIPTS_SOURCE_BRANCH}";
    }

    dir( env.PATH_SALESFORCE ){
        git credentialsId: env.REPO_CREDENTIALS, url: "${env.gitSourceRepoSshUrl}", branch: "${env.SOURCE_BRANCH}";
        sh "git config http.sslVerify false && git config core.quotePath false";
    }
}