def call(String json_package) {
    identify( packageParser.parsePackage( json_package ) );
}

def identify(varMap){
     println(varMap);
    if (varMap.containsKey( "project_id" )){
        println( "Gitlab" );
        env.gitUserName                 = varMap.user_username;                
        def lastCommit                  = varMap.commits.get(varMap.commits.size()-1);
        env.gitUserEmail                = lastCommit.author.email;
        env.branchLastCommit            = lastCommit.id;
        env.gitSourceRepoHttpUrl        = varMap.project.http_url;
        env.project                     = varMap.project_id;
        env.owner                       = "";

    }else if( varMap.containsKey( "date" ) ){
        println( "Bitbucket Server" );
        env.gitUserEmail                = varMap.actor.emailAddress;
        def lastCommit                  = varMap.changes.get( varMap.changes.size() - 1 );
        env.branchLastCommit            = lastCommit.toHash;
        
        varMap.repository.links.clone.each{ link ->
            if( link.get( "name" ) == "http" ){
                env.gitSourceRepoHttpUrl = link.get( "href" );
            }
        }

        def matcher    = ( env.gitSourceRepoHttpUrl =~ /\\/\\/([a-zA-Z.]*)\\/([A-Za-z0-9].*)\\/([A-Za-z0-9].*)\\/([A-Za-z0-9].*)/ );
        env.repoHost   = "https://"+ matcher[ 0 ][ 1 ];
        env.owner      = matcher[ 0 ][ 3 ];
        String project = matcher[ 0 ][ 4 ];
        env.project    = project.split( "\\." )[ 0 ];
    }else{
        //TODO  BB Cloud condition (( varMap.containsKey( "date" )){
        println( "Bitbucket Cloud" );
        //TODO env.gitUserEmail       = varMap.actor.emailAddress        
        def lastCommit                  = varMap.push.get( varMap.changes.size() - 1 );
        env.branchLastCommit            = lastCommit.toHash;
        varMap.repository.links.clone.each{ link ->
            if( link.get( "name" ) == "http" ){
                env.gitSourceRepoHttpUrl = link.get( "href" );
            }
            else if( link.get( "name" ) == "ssh" ){
                env.gitSourceRepoSshUrl = link.get( "href" );
            }
        }
        env.owner                       = utils.getURLPath ( env.gitSourceRepoHttpUrl, 0 , 2 );
        env.project                     = utils.getURLPath ( env.gitSourceRepoHttpUrl, 0 , 3 );  
    }
    /*TODO GitHub Integration }else if( varMap.containsKey( "date" )){
    */
    /*TODO Handling not implemented compatible server  }else{


    }
    */
    //env.repoHost                        = utils.getURLPath ( env.gitSourceRepoHttpUrl , 0 , 1 );
}
