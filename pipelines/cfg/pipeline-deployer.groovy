@Library('ALM_SF_DX_LIBRARY@master') _

pipeline {
    agent any
    stages{
        stage('Pre Configuration') {
            steps {
                preconfigDeployerTag()
            }
        }
        stage('Configuration') {
            steps {
                configDeployerTag()
            }
        }
        stage('Building Up Delta Package') {
            steps {
                buildDelta()
            }
        }             
        stage('Validation & Report') {
            when { environment name: 'delta_built', value: 'true'}
            steps {           
                testValidation()           
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
    }
}
