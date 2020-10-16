def call(){
    dir( env.PATH_SALESFORCE ){
        sh "git config --global user.email \"easycommit@jenkins.com\"";
        sh "git config --global user.name \"EasyCommit\"";
        sh "git add ${env.PROJECT_NAME}";
        sh "git commit -m \"Metadata alignment\"";
        sshagent( credentials: [ env.REPO_CREDENTIALS ] ){
            def dateToday  = new Date().format( 'ddMMyyy' );
            def branchName = "alignment/alignment_${env.SOURCE_BRANCH}_${dateToday}"
            sh "git push --set-upstream origin ${branchName}";
        }
    }
}