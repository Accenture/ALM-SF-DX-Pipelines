def sendEmail( emailType, recipient, recipientCc ){
	withCredentials( [ usernamePassword( credentialsId: env.EMAIL_CREDENTIALS, usernameVariable: 'USN', passwordVariable: 'PWD' ) ] ){
		sh ( script: "python3.7 ../${env.PATH_SCRIPTS}/sendemail/send_email.py -u ${USN} -p ${PWD} -sa ${env.EMAIL_SERVER} ${emailType} -r ${recipient} -rCC ${recipientCc}", 
            returnStatus: true )
	}
}

def sendEmailValidate( status, recipient, recipientCc, file_path ){
	withCredentials( [ usernamePassword( credentialsId: env.EMAIL_CREDENTIALS, usernameVariable: 'USN', passwordVariable: 'PWD' ) ] ){
		sh ( script: "python3.7 ../${env.PATH_SCRIPTS}/sendemail/send_email.py -u ${USN} -p ${PWD} -sa ${env.EMAIL_SERVER} validate -r ${recipient} -rCC ${recipientCc} -f ${file_path} -s ${status}", 
            returnStatus: true )
	}
}

def sendEmailValidate( status, recipient, recipientCc ){
	withCredentials( [ usernamePassword( credentialsId: env.EMAIL_CREDENTIALS, usernameVariable: 'USN', passwordVariable: 'PWD' ) ] ){
		sh ( script: "python3.7 ../${env.PATH_SCRIPTS}/sendemail/send_email.py -u ${USN} -p ${PWD} -sa ${env.EMAIL_SERVER} validate -r ${recipient} -rCC ${recipientCc} -s ${status}", 
            returnStatus: true )
	}
}