def call(){

    commentHandler.editLastMessage( "+ Running PMD Analysis" );
    
    def ruleSetPath         = "${env.PATH_SCRIPTS}/pmd/rules.xml";
    def reportPath          = "sfdxScannerReport.html";
    def deployFolderPath    = "${env.PATH_SALESFORCE}/srcToDeploy";

    def statusCode = sh( script: "sfdx scanner:run -f html -o ${reportPath} -t ${deployFolderPath} --pmdconfig ${ruleSetPath} -v", returnStatus: true );

    def pmdMsg = "+ PMD Analysis finished with status **${statusCode}**";

    if( fileExists( "$reportPath" ) ){
        archiveArtifacts allowEmptyArchive: true, artifacts: "${reportPath}", fingerprint: true;  
        pmdMsg += " [PMD Report]( ${env.BUILD_URL}artifact/${reportPath} )";
    }

    sh "mv ${reportPath} ${env.PATH_SALESFORCE}/artifacts_folder/"

    commentHandler.editLastMessage( pmdMsg );
}