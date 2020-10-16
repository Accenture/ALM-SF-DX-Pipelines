def call( status ){
    def progressCode = ( status == 'success' ) ? 'Completed' : 'Cancel';
    def easyCommitNumber = 'EC-0000';

    withCredentials( [ usernamePassword(credentialsId: env.SFDX_CREDENTIALS, usernameVariable: 'SFDC_USN', passwordVariable: 'SFDC_CONSUMER_KEY') ] ){
        statusCode = sh( script: "sfdx force:data:record:update -u ${SFDC_USN} -s EasyCommit__Easy_Commit__c -w \"Name='${easyCommitNumber}'\" -v \"EasyCommit__Status__c='${progressCode}'\" ", returnStatus: true);
        echo "Update completed with status coded ${statusCode}";
    }
}