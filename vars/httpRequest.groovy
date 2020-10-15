HttpResponse doGetHttpRequest( String requestUrl ){
    URL url = new URL( requestUrl )
    HttpURLConnection connection = url.openConnection()
    connection.setRequestMethod( "GET" )
    connection.setRequestProperty( "Private-Token", "6MV5vvqxn4RMsKHi6Dxy" )
    println "DEBUG: Request (GET):\n\tURL: ${requestUrl}"
    connection.connect()
    HttpResponse resp = new HttpResponse( connection )
    println "DEBUG: Response:\n\tHTTP Status: ${resp.statusCode}\n\tMessage: ${resp.message}"
    return resp
}

HttpResponse doGetHttpRequestHeaders( String requestUrl, Map headers ){
    if( headers.size() > 0 ){
        URL url = new URL(requestUrl)
        HttpURLConnection connection = url.openConnection()
        connection.setRequestMethod( "GET" )
        if( headers.get( "user" ) != null && headers.get( "password" ) != null ){
            String authString = headers.get( "user" ) + ":"
            authString = authString + headers.get( "password" )
            authString = authString.bytes.encodeBase64().toString()
            connection.setRequestProperty( "Authorization", "Basic " + authString )
        }
        if( headers.get( "Private-Token" ) != null){
            connection.setRequestProperty( "Private-Token", headers.get( "Private-Token" ) )
        }
        if( headers.get( "Authorization" ) != null){
            connection.setRequestProperty( "Authorization", "Bearer " + headers.get( "Authorization" ) )
        }
        println "DEBUG: Request (GET):\n\tURL: ${requestUrl}"
        connection.connect()
        HttpResponse resp = new HttpResponse( connection )
        println "DEBUG: Response:\n\tHTTP Status: ${resp.statusCode}\n\tMessage: ${resp.message}"
        return resp
    }
}

class HttpResponse {

    String body
	String message
	Integer statusCode
	boolean failure = false
	
    public HttpResponse(HttpURLConnection connection){
		this.statusCode = connection.responseCode
		this.message = connection.responseMessage
		if(statusCode == 200 || statusCode == 201){
			this.body = connection.content.text//this would fail the pipeline if there was a 400
		}else{
			this.failure = true
			this.body = connection.getErrorStream().text
		}
		connection = null //set connection to null for good measure, since we are done with it
	}
	public json(){
		if( this.body != null ){
			return packageParser.parsePackage( this.body )
		}
	}
}