def call(String json_package) {
    identify(packageParser.parsePackage(json_package))
}

def identify(varMap){
    echo "$varMap"
    if( varMap.containsKey( "pullrequest" ) ){
        env.gitMergeRequestTitle       = varMap.pullrequest.title;
        env.gitMergeRequestDescription = varMap.pullrequest.description;
        env.gitMergeRequestState       = varMap.pullrequest.state;
        env.gitSourceBranch            = varMap.pullrequest.source.branch.name;
        env.gitTargetBranch            = varMap.pullrequest.destination.branch.name;
        env.gitUserName                = varMap.actor.nickname;
        env.gitSourceRepoHttpUrl       = getHttpUrl(varMap.repository.links.html.href);
        
        env.COMMIT_STATUS_SUCCESS     = "SUCCESSFUL";
        env.COMMIT_STATUS_FAILED      = "FAILED";
        env.COMMIT_STATUS_IN_PROGRESS = "INPROGRESS";
        env.COMMIT_STATUS_STOPPED     = "STOPPED";
        //Bitbucket cloud has no value for pending status
        env.COMMIT_STATUS_PENDING     = "INPROGRESS";
        env.owner                     = utils.getURLPath ( env.gitSourceRepoHttpUrl , 0 , 2 );
        env.project                   = utils.getURLPath ( env.gitSourceRepoHttpUrl , 0 , 3 ).split(".")[ 0 ];
    }
    else if( varMap.containsKey( "pullRequest" ) ){
        env.gitMergeRequestId          = varMap.pullRequest.id;
        env.gitMergeRequestTitle       = varMap.pullRequest.title;
        env.gitMergeRequestDescription = varMap.pullRequest.description;
        env.gitMergeRequestState       = varMap.pullRequest.state;
        env.gitSourceBranch            = varMap.pullRequest.fromRef.displayId;
        env.gitTargetBranch            = varMap.pullRequest.toRef.displayId;
        env.gitSourceBranchLastCommit  = varMap.pullRequest.fromRef.latestCommit;
        env.gitUserName                = varMap.actor.name;
        env.gitUserEmail               = varMap.actor.emailAddress;
        varMap.pullRequest.links.self.each{ link ->
            env.gitMergeRequestUrl = link.get( "href" );
        }
        varMap.pullRequest.fromRef.repository.links.clone.each{ link ->
            if( link.get( "name" ) == "http" ){
                env.gitSourceRepoHttpUrl = link.get( "href" );
            }
            else if( link.get( "name" ) == "ssh" ){
                env.gitSourceRepoSshUrl = link.get( "href" );
            }
        }
        
        env.COMMIT_STATUS_SUCCESS     = "SUCCESSFUL";
        env.COMMIT_STATUS_FAILED      = "FAILED";
        env.COMMIT_STATUS_IN_PROGRESS = "INPROGRESS";
        //Bitbucket server has no value for stopped or pending status
        env.COMMIT_STATUS_STOPPED     = "INPROGRESS";
        env.COMMIT_STATUS_PENDING     = "INPROGRESS";

        def matcher    = ( env.gitSourceRepoHttpUrl =~ /\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)\\/([A-Za-z0-9].*)\\/([A-Za-z0-9].*)/ );
        env.repoHost   = "https://"+ matcher[ 0 ][ 1 ];
        env.owner      = matcher[ 0 ][ 3 ];
        String project = matcher[ 0 ][ 4 ];
        env.project    = project.split("\\.") [0];
    }
    else{
        env.COMMIT_STATUS_SUCCESS     = "success";
        env.COMMIT_STATUS_FAILED      = "failed";
        env.COMMIT_STATUS_IN_PROGRESS = "running";
        env.COMMIT_STATUS_STOPPED     = "canceled";
        env.COMMIT_STATUS_PENDING     = "pending";
    }
}

def getSshUrl(httpUrl){
    regex = "/\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)/"
    def matcher = (httpUrl =~ /\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)/)
    domain = matcher[0][1]
    projectUrl = matcher[0][2]
    sshUrl = "git@" + domain + ':' + projectUrl + '.git'
    return sshUrl
}

def getHttpUrl(httpUrl){
    regex = "/\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)/"
    def matcher = (httpUrl =~ /\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)/)
    domain = matcher[0][1]
    projectUrl = matcher[0][2]
    sshUrl = "https:"+"/"+"/" + domain + "/" + projectUrl + '.git'
    return sshUrl    
}