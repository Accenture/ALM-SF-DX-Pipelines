def call(){   
    def packageBody = env.JSON_PACKAGE.toString().replaceAll( "\n", "" );
    println( packageBody )
    
    sh "python3.7 ${env.PATH_SCRIPTS}/generatePackage/generatePackage.py -j '${packageBody}' -a ${env.API_VERSION}";
    sh "if [ ! -d \"artifacts_folder\" ]; then mkdir artifacts_folder; fi";
    sh "mv package.xml artifacts_folder/package.xml";
    archiveArtifacts allowEmptyArchive: true, artifacts: 'artifacts_folder/package.xml', fingerprint: true;
}