@Library('ALM_SF_DX_LIBRARY@master') _

pipeline {
    agent any
    stages{
        stage('Pre Configuration') {
            steps {
                preconfigDeployer()
            }
        }
        stage('Configuration') {
            steps {
                configDeployer()
            }
        }
        stage('Building Up Delta Package') {
            steps {
                buildDelta()
            }
        }               
        stage('Report') {
            when { 
                environment name: 'delta_built', value: 'true'
            }
            steps {
                pmdAnalysis()
            }
        }
        stage('AlignOrg') {
            when { 
                environment name: 'delta_built', value: 'true'
                environment name: 'CHECK_ALIGN', value: 'true'
            }
            steps {
                align( 'deploy' )
            }
        }
        stage('Validation') {
            when { 
                environment name: 'delta_built', value: 'true'
                environment name: 'CHECK_ALIGN', value: 'false'
            }
            steps {
                validationRun()
            }
        }      
        stage('Deploy Delta Package') {
            when {
                environment name: 'delta_built', value: 'true'
                environment name: 'CHECK_DEPLOY', value: 'true'
                environment name: 'CHECK_ALIGN', value: 'false'
            }
            steps {
                deployDelta()
            }
        }
        stage('Post Build Steps') {
            steps{
                postBuild()
            }
        }
    }
}
