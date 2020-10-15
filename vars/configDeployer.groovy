def call(){
	setMergeParameters();
	getMergeInfo();
    config();
}

def getMergeInfo(){
	withCredentials( [ string( credentialsId: env.PRIVATE_TOKEN, variable: 'TOKEN' ) ] ){
		def mapHeaders = [ Authorization: TOKEN ];
		def response   = httpRequest.doGetHttpRequestHeaders( "$env.gitMergeRequestUrl", mapHeaders );
		getMergeDetails( response.body );
	}
}

def setMergeParameters(){
	env.gitMergeRequestId = env.MR_NUMBER;
    
	if( env.REPOSITORY_URL.contains( "api.bitbucket.org" ) ){
	}
	else if( env.IS_BB_SERVER ){
		def matcher = ( env.REPOSITORY_URL =~ /git@(.*)\/(.*)\/(.*)\.git/ );
		def hostURL = "https://" + matcher[ 0 ][ 1 ];
		def projectName = matcher[ 0 ][ 2 ];
		def repoName = matcher[ 0 ][ 3 ];
		env.gitMergeRequestUrl = "${hostURL}/rest/api/1.0/projects/${projectName}/repos/${repoName}/pull-requests/${env.gitMergeRequestId}";
	}
	else{
		withCredentials( [ string( credentialsId: env.PRIVATE_TOKEN, variable: 'TOKEN' ) ] ){
			env.gitMergeRequestUrl = "${env.GITLAB_HOST}/api/v4/projects/${env.PROJECT_ID}/merge_requests/${env.MR_NUMBER}?private_token=${TOKEN}";
		}
	}
}

def getMergeDetails( jsonBody ){
	def mapResponse       		   = packageParser.parsePackage( jsonBody );
	println( mapResponse );
	env.gitMergeRequestTitle       = mapResponse.title;
	env.gitMergeRequestDescription = mapResponse.description;
	env.gitMergeRequestState       = mapResponse.state;
	env.gitSourceBranch 		   = mapResponse.fromRef.displayId;
	env.gitTargetBranch		       = mapResponse.toRef.displayId;
	env.gitSourceBranchLastCommit  = mapResponse.fromRef.latestCommit;
	env.gitUserName                = mapResponse.author.user.name;
	env.gitUserEmail               = mapResponse.author.user.emailAddress;

	mapResponse.fromRef.repository.links.clone.each{ link ->
		if( link.get( "name" ) == "http" ){
			env.gitSourceRepoHttpUrl = link.get( "href" );
		}
		else if( link.get( "name" ) == "ssh" ){
			env.gitSourceRepoSshUrl = link.get( "href" );
		}
	}

	def matcher    = ( env.gitSourceRepoHttpUrl =~ /\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)\\/([A-Za-z0-9].*)\\/([A-Za-z0-9].*)/ );
	env.repoHost   = "https://"+ matcher[ 0 ][ 1 ];
	env.owner      = matcher[ 0 ][ 3 ];
	String project = matcher[ 0 ][ 4 ];
	env.project    = project.split( "\\." )[ 0 ];
}