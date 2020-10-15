def call (){
    dir (env.project){            
        if (! fileExists(".git")){
            git credentialsId: env.SSH_GITLAB, url: "${env.REPOSITORY_URL}"
            sh "git config http.sslVerify false && git config core.quotePath false"
        }else{
            sh "git reset --hard"
            git credentialsId: env.SSH_GITLAB, url: "${env.REPOSITORY_URL}"
        }
    }
}