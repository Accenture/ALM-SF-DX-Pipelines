@Library('ALM_SF_DX_LIBRARY@master') _

pipeline {
    agent any
    stages{
        stage('Pre Configuration') {
            steps {
                getWebhookData "$JSON_PACKAGE"
                preconfig()
            }
        }
        stage('Configuration') {
            steps {
                config()
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
        stage('Validation') {
            when { 
                environment name: 'delta_built', value: 'true'
            }
            steps {
                validationRun()
            }
        }
        stage('Deploy Delta Package') {
            when {
                environment name: 'delta_built', value: 'true'
                environment name: 'CHECK_DEPLOY', value: 'true'
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
