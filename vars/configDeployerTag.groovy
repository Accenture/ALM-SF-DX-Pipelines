def call(){
    config()
    setMergeInfo()
}

def setMergeInfo(){
	env.gitSourceBranch		= "$env.BRANCH_NAME"
	env.gitTargetBranch		= "$env.TAG"
    env.gitSourceRepoSshUrl	= "$env.REPOSITORY_URL"
}