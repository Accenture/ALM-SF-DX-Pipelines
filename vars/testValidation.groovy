def call(){    
	parallel ('Integration Tests' : {
		stage ('Running Validation'){
			validationRun();
		}
	},'PMD Analysis' : {
		stage('PMD Analysis'){
			pmdAnalysis();
		}
	},'Nomenclature Validation': {
		stage('Nomenclature Validation'){
			nomenclatureValidation();
		}
	})
}
