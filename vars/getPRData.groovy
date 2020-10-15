def call(){
    def url = env.API_REPOSITORY_URL+"/pullrequests/"+env.MR_NUMBER
    withCredentials([usernamePassword(credentialsId: env.SSH_GITLAB, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
        def response                   = sh(script: "curl -u "+GIT_USER+":"+GIT_PASS +" "+url , returnStdout: true)
        def varMap                     = packageParser.parsePackage(response)
	    env.gitSourceBranch            = varMap.source.branch.name
        env.gitTargetBranch            = varMap.destination.branch.name
        env.gitMergeRequestDescription = varMap.description
        env.gitSourceRepoHttpUrl       = varMap.destination.repository.links.html.href
    }
}