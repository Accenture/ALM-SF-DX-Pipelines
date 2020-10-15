def call(){
    if( currentBuild.currentResult == Result.SUCCESS.toString() ){
    	commentHandler.updateStatus( env.COMMIT_STATUS_SUCCESS );
        dir( "${env.PATH_SALESFORCE}/${env.PROJECT_NAME}" ){
            sendEmail.sendEmailValidate( "success", env.gitUserEmail, env.RECIPIENTS_RELEASE_MANAGERS, "pmdReport.html" );
        }
    }
    else{
        commentHandler.updateStatus( env.COMMIT_STATUS_FAILED );
    }
    def message = "+ **Jenkins Build ${env.BUILD_DISPLAY_NAME} finished ${currentBuild.currentResult}**";
    println( message );
    commentHandler.editLastMessage( message );
}