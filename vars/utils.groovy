import java.util.regex.Matcher;
import java.util.regex.Pattern;

def getPRTests(){
    def testMR = "";
    echo "env.TEST_TO_RUN : $env.TEST_TO_RUN";
    if( env.TEST_TO_RUN != null && env.TEST_TO_RUN.length()>0 ){
        testMR = env.TEST_TO_RUN;
    }
    else if( env.gitMergeRequestDescription != null ){
        testMR = env.gitMergeRequestDescription.split('testsToBeRun')[1].split("```")[0];
        testMR = testMR.replaceAll("\\s{1,}", " ");
        if( testMR && testMR.contains('Elimina este mensaje') ){
            print "Default Description";
            testMR = '';
        }
        else{
            testMR = testMR.split( ' ' ).join( ',' );
        }
    }
    else{
        testMR = '';
    }
    print "Tests Var: " + testMR;
    return testMR;
}

def handleValidationErrors( postType, postStatusCode, statusMessage ){
    echo "INFO: ${postType} returned status code: ${postStatusCode}";
    def message = '';
    if( postStatusCode == "0"){
        if( statusMessage == "Succeeded" ){
            message = ( postType == "Validation" ) ? "+ Validation Succeded." : "+ Package Deployed.";
            message += " [${postType} Log]( ${env.BUILD_URL}artifact/validate.json )";
        	commentHandler.editLastMessage( message );
        }
        else if ( statusMessage == "InProgress" ){ 
            message = "+ **${postType} The current build status is taking too long. Check the deployment status in your target org.** [${postType} Log]( ${env.BUILD_URL}artifact/validate.json )";
            errorMessage = "ERROR: ${postType} Failed. Error code: Timeout.";
		    commentHandler.editLastMessage( message );
		    error "$errorMessage";
        }
        else{ 
            message = "+ **${postType} Failed with the following build status: ${statusMessage}.** [${postType} Log]( ${env.BUILD_URL}artifact/validate.json )";
            errorMessage = "ERROR: ${postType} Failed. Build status: ${statusMessage}.";
            commentHandler.editLastMessage( message );
        	error "$errorMessage";
    	}
    }else{
        message = "+ **${postType} Failed with error code: ${postStatusCode}.** [${postType} Log]( ${env.BUILD_URL}artifact/validate.json )";
        errorMessage = "ERROR: ${postType} Failed. Error code: ${postStatusCode}.";
        commentHandler.editLastMessage( message );
        dir( "${env.PATH_SALESFORCE}/${env.PROJECT_NAME}" ){
            def logFile = ( postType == "Validation" ) ? "validate.json" : "deploy.json";
            sendEmail.sendEmailValidate( "error", env.gitUserEmail, "${env.RECIPIENTS_RELEASE_MANAGERS}", logFile );
        }
        error "$errorMessage";
    }
}

def handleDeltaStatus(statusCode) {
    def message;
    env.delta_built = false;
    switch( statusCode ){
        case "0":
            echo 'INFO: Delta package build correclty';
            env.delta_built = true;
            createDeltaArtifacts();
            commentHandler.editLastMessage( "+ Delta Package Built Successfully" );
            break
        case "11":
            echo 'WARNING: Delta package build with warnings';
            env.delta_built = true;
            createDeltaArtifacts();
            commentHandler.editLastMessage( "+ Delta Package Built With Warnings (Unknown Folders), please notify the RM" );
            break
        case "123":
            commentHandler.updateStatus( env.COMMIT_STATUS_FAILED );
            commentHandler.editLastMessage( "**ERROR! Could not find remote/source branch, ABORTING**" );
            error 'ERROR: Could not find remote/source branch, exiting...';
            break;
        case "2":
            commentHandler.updateStatus( env.COMMIT_STATUS_FAILED );
            currentBuild.result = 'ABORTED';
            echo 'WARNING: Branches up to date, could not build delta package';
            commentHandler.editLastMessage( "+ Delta Package was not built, not validating" );
            commentHandler.postMessage( "WARNING! Delta Package not built" );
            break;
        case "3":
            commentHandler.updateStatus( env.COMMIT_STATUS_FAILED );
            currentBuild.result = 'ABORTED';
            commentHandler.editLastMessage( "**ERROR! Merge Conflicts found, ABORTING**" );
            error 'FATAL: Merge Conflicts found';
            break;
        case "4":
            echo 'WARNING: No changes detected in src folder';
            commentHandler.editLastMessage( "+ Delta Package was not built, not validating" );
            break;
        default:
            commentHandler.updateStatus( env.COMMIT_STATUS_FAILED );
            commentHandler.editLastMessage( "**ERROR! Unhandled Error, ABORTING**" );
            error 'FATAL: Unhandled Errors...';
    }
}

def createDeltaArtifacts(){
    def date    = sh (script: 'date "+%y%m%d_%H%M"', returnStdout: true).trim();
    def source  = sh(script: "echo ${env.gitSourceBranch} | tr '/' '-'", returnStdout: true).trim();
    def target  = sh(script: "echo ${env.gitTargetBranch} | tr '/' '-'", returnStdout: true).trim();
    def path    = "${source}-${target}";
    def file    = "${date}__${path}";

    dir( "${env.PATH_SALESFORCE}/${env.PROJECT_NAME}/artifacts_folder" ){
        sh "zip -r ${file}.zip ../srcToDeploy";
        archiveArtifacts allowEmptyArchive: true, artifacts: "${file}.zip", fingerprint: true;
    }
}

def getURLPath ( httpUrl, start, end) {
    def matcher = (httpUrl =~ /\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)/);
    def match = matcher[start][end];
    return match;
}