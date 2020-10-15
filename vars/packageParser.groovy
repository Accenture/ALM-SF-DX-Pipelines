import groovy.json.JsonSlurperClassic
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def parsePackage(json){
    def varPackage = new JsonSlurperClassic().parseText("$json")
    return varPackage
}