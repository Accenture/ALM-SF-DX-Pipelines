def call(){
    dir( env.PATH_SALESFORCE ){
        sh "git config --global user.email \"easycommit@jenkins.com\"";
        sh "git config --global user.name \"EasyCommit\"";
        sh "git add ${env.PROJECT_NAME}";
        sh "git commit -m \"${env.BRANCH_DESC}\"";
        sshagent( credentials: [ env.REPO_CREDENTIALS ] ){
            if( env.branchExists.trim() == '1' ){
                sh "git push";
            }
            else{
                sh "git push --set-upstream origin feature/${env.BRANCH_NAME}";
            }
        }
    }
}