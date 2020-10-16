def call(){
    checkBranch();
    removeMetadata();
    config.authorizeOrg();
    config.generateDescribe();
    createEmptyProject();
}

def checkBranch(){
    dir( env.PATH_SALESFORCE ){
        def dateToday  = new Date().format( 'ddMMyyy' );
        def branchName = "alignment/alignment_${env.SOURCE_BRANCH}_${dateToday}"
        sshagent( credentials: [ env.REPO_CREDENTIALS ] ){
            sh "git ls-remote --heads ${env.REPO_URL} ${branchName}";
            env.branchExists = sh( script: "git ls-remote --heads ${env.REPO_URL} ${branchName} | wc -l", returnStdout: true );
        }
        if( env.branchExists.trim() == '1' ){
            sh "git checkout ${branchName}";
            sh "git reset --hard origin/${branchName}";
        }
        else{
            sh "git checkout -B ${branchName} origin/${env.SOURCE_BRANCH}";
        }
    }
}

def removeMetadata(){
     dir( "${env.PATH_SALESFORCE}/${env.PROJECT_NAME}" ){
          listFolders = sh( script: "find . -type d -mindepth 1 -maxdepth 1", returnStdout: true );
          sh "find . -type d -mindepth 1 -maxdepth 1 -exec rm -rf {} +";
     }
}

def createEmptyProject(){
    sh "sfdx force:project:create -n srcRetrieved";
}