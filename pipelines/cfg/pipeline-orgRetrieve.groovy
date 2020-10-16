@Library('ALM_SF_DX_LIBRARY@master') _

pipeline {
    agent any
    stages{
        stage('Pre Configuration'){
            steps{
                preconfigBackup()
            }
        }
        stage('Configuration'){
            steps{
                configBackup()
            }
        }
        stage('Retrieve Metadata'){
            steps{
                retrievePackage( 'package.xml' )
            }
        }
        stage('Commit Changes'){
            steps{
                commitChangesBackup()
            }
        }
    }
}