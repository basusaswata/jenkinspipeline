 def call(body) {

        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

        node {
            // Clean workspace before doing anything
            deleteDir()

            try {
                stage ('Clone') {
		    bat "echo '#######checking out from git...'"
                    checkout scm
                }
                stage ('Build') {
                    bat "echo '############ building ${config.projectName} ...'"
			bat "mvn clean install"
                }
                stage ('Tests') {
                    parallel 'static': {
                        bat "echo 'shell scripts to run static tests...'"
			    bat "mvn test"
                    },
                    'unit': {
                        bat "echo 'shell scripts to run unit tests...'"
                    },
                    'integration': {
                        bat "echo 'shell scripts to run integration tests...'"
                    }
                }
                stage ('Deploy') {
                    bat "echo 'deploying to server ${sbServer1}...'"
					
                }
            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }
        }
    }
