@Library('ALM_SF_DX_LIBRARY@master') _

pipeline {
    agent any
    stages{
        stage('Pre Configuration'){
            steps{
                preconfigEasyCommitManual()
            }
        }
        stage('Configuration'){
            steps{
                configEasyCommit()
            }
        }
        stage('Generate Package'){
            steps{
                generatePackage()
            }
        }
        stage('Retrieve Changes'){
            steps{
                retrieveChanges()
            }
        }
        stage('Merge Changes'){
            steps{
                mergeChanges()
            }
        }
        stage('Commit Changes'){
            steps{
                commitChanges()
            }
        }
    }
}