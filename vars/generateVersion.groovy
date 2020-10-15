def call(){
    def tagName = 'release_' + new Date().format( 'yyyyMMdd-hhmmss' ).toString();
    withCredentials( [ string( credentialsId: env.PRIVATE_TOKEN, variable: 'TOKEN' ) ] ){
        def statusCode = sh( script: "python3.7 callGitServer/call_git_server.py release --no-ssl --force-https " +
            "-to ${TOKEN} -tn T_${tagName} -rb version/V_${tagName} -t ${env.branchLastCommit} -r ${env.repoHost} " +
            "-o ${env.owner} -p ${env.project} -bs ${env.IS_BB_SERVER}", returnStatus: true );
        
        def emailTemplate = '';
        def emailStatus = '';
        if( statusCode.toString() == '0' ){
            emailTemplate = 'success.html';
            emailStatus = 'success';
        }else{
            emailTemplate = 'error.html'; 
            emailStatus = 'error';
            error 'ERROR: Tag/Version branch generation error, check output';
        }
    }
}

