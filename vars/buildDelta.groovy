def call(){
    def statusCode;
	dir( "${env.PATH_SALESFORCE}" ){
		withEnv( [ "PYTHONIOENCODING=UTF-8" ] ){
			statusCode = sh returnStatus: true, script: "python3.7 ../${env.PATH_SCRIPTS}/mergerDX/merger.py merge_delta -s ${env.gitSourceBranch} -t ${env.gitTargetBranch} -a ${env.API_VERSION} ";
	    }
		echo "INFO: Build Delta returned status code: ${statusCode}";
	}
	utils.handleDeltaStatus( statusCode.toString().trim() );
}