def call(){
    def statusCode = "";
	dir( "${env.PATH_SALESFORCE}/${env.PROJECT_NAME}" ){
        statusCode = sh( script: "python3.7 ..${env.PATH_SCRIPTS}/mergerDX/merger.py merge_delta -s ${env.gitSourceBranch} -t ${env.gitTargetBranch}~1 -sf ${env.PROJECT_NAME} -a ${env.API_VERSION} ", returnStatus: true );
        echo "INFO: Build Delta returned status code: ${statusCode}";
	}
    utils.handleDeltaStatus( statusCode.toString().trim() );
}