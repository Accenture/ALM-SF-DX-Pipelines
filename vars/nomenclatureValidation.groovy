def call(){
    echo "INFO: TargetBranch: ${env.gitTargetBranch}"
    
    def statusCode = "";
    dir( "${env.PATH_SALESFORCE}/${env.PROJECT_NAME}" ){          
        statusCode = sh( script: "python3.7 ../../${env.PATH_SCRIPTS}/nomenclature/nomenclature.py execute", returnStatus: true );
        echo "INFO: statusCode: ${statusCode}";
        def date   = sh( script: 'date "+%y%m%d_%H%M"', returnStdout: true ).trim();
        def source = sh( script: "echo ${env.gitSourceBranch} | tr '/' '-'", returnStdout: true ).trim();
        def target = sh( script: "echo ${env.gitTargetBranch} | tr '/' '-'", returnStdout: true ).trim();
        def path   = "${source}-${target}";
        def file   = "${date}__${path}";
        sh "mv output.html artifacts_folder/Nomenclatura-${file}.html";
        archiveArtifacts allowEmptyArchive: true, artifacts: "artifacts_folder/Nomenclatura-${file}.*", fingerprint: true;
    }
    handleNomenclatureStatus( statusCode.toString().trim() );
}

def handleNomenclatureStatus( statusCode ){
    switch( statusCode ) {
        case "0":
            echo 'INFO: Nomenclature Validation succesfully passed';
            commentHandler.editLastMessage( "+ Nomenclature validation build succesfully." );
            break;
        case "140":
            commentHandler.editLastMessage( "+ Nomenclature validation failed. Please review the report." );
            break;
        case "141":
            commentHandler.editLastMessage( "+ Nomenclature validation build with **WARNINGS**. Please review the report." );
            break;
        default:
            echo "INFO: Unhandled error. Error Code: ${statusCode}";
            commentHandler.updateStatus( env.COMMIT_STATUS_FAILED );
            commentHandler.editLastMessage( "**ERROR! Unhandled Error, ABORTING**" );
            error 'FATAL: Unhandled Errors...';
    }
}