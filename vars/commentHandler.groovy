def updateStatus( status ){
    def description = "";
    if( env.repoHost.contains( "api.bitbucket.org" ) || env.IS_BB_SERVER ){
        switch( status ){
            case env.COMMIT_STATUS_SUCCESS:
                description = "--description \"Execution completed successfully\"";
                break;
            case env.COMMIT_STATUS_FAILED:
                description = "--description \"Execution failed\"";
                break;
            case env.COMMIT_STATUS_IN_PROGRESS:
                description = "--description \"Execution in progress\"";
                break;
        }
    }
    withCredentials( [ string( credentialsId: env.PRIVATE_TOKEN, variable: 'TOKEN' ) ] ){
        sh( script: "python3.7 ${env.PATH_SCRIPTS}/callGitServer/call_git_server.py status --no-ssl --force-https -t ${TOKEN} -s ${status} " +
            "-c ${env.gitSourceBranchLastCommit} ${description} -r ${env.repoHost} -o ${env.owner} -p ${env.project} -bs ${env.IS_BB_SERVER}"
            , returnStatus: true );
    }
}

def postMessage( message ){
    withCredentials( [ string( credentialsId: env.PRIVATE_TOKEN, variable: 'TOKEN' ) ] ){
        sh( script: "python3.7 ${env.PATH_SCRIPTS}/callGitServer/call_git_server.py comment --no-ssl --force-https -t ${TOKEN} " +
            "-m \"${message}\" -mr ${env.gitMergeRequestId} -r ${env.repoHost} -o ${env.owner} -p ${env.project} -bs ${env.IS_BB_SERVER}"
            , returnStatus: true );
    }
}

def editLastMessage( message ){
    withCredentials( [ string( credentialsId: env.PRIVATE_TOKEN, variable: 'TOKEN' ) ] ){
        sh( script: "python3.7 ${env.PATH_SCRIPTS}/callGitServer/call_git_server.py comment --no-ssl --force-https -t ${TOKEN} " +
            "-m \"${message}\" -mr ${env.gitMergeRequestId} -e -r ${env.repoHost} -o ${env.owner} -p ${env.project} -bs ${env.IS_BB_SERVER}",
             returnStatus: true );
    }
}

def editLastCommentWithList( messages ){
    def messagesString = '';
    for( message in messages ){
        messagesString += "\"${message}\" ";
    }
    editLastMessage( messagesString );
}