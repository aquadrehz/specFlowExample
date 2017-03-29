parallel (
		"Unit Test001": {
			node {
				ws("workspace/${JOB_NAME}") {
					def globalVariable;
					def GlobalVariables;
					def ShareLibrary;
					def branchName = "${env.BRANCH_NAME}";
					checkout([$class: 'GitSCM', branches: [[name: branchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CheckoutOption', timeout: 60], [$class: 'CloneOption', depth: 1, noTags: true, reference: '', shallow: true]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'aquadrehz', url: 'https://github.com/aquadrehz/specFlowExample.git']]])

					GlobalVariables = load 'Jenkinsfiles/GlobalVariables.groovy'
					ShareLibrary = load 'Jenkinsfiles/ShareLibrary.groovy'
					ShareLibrary.killSoapUIProcess();
					globalVariable = GlobalVariables;
					globalVariable.Build_Env = 'UnitTest001';
					globalVariable.WORKSPACE = pwd()
					globalVariable.SoapUI_Port = "8099"
					globalVariable.DB_Suffix = "UnitTest001"
					globalVariable.COMPUTERNAME = ShareLibrary.mapHostname("${NODE_NAME}")
					globalVariable.IsSmokeTest = false;
					globalVariable.Filter = "gutter"

					stage('Build001') {
						ShareLibrary.buildTestSystem(globalVariable);
					}

					stage('System Test001') {
						ShareLibrary.runUnitTest(globalVariable);
					}
					
					stage('Unit Test001') {
						ShareLibrary.runSystemTest(globalVariable);
					}
				}
			}
		},
		"Unit Test002": {
			node {				
				ws("workspace/${JOB_NAME}") {
					def globalVariable;
					def GlobalVariables;
					def ShareLibrary;
					def branchName = "${env.BRANCH_NAME}";
					checkout([$class: 'GitSCM', branches: [[name: branchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CheckoutOption', timeout: 60], [$class: 'CloneOption', depth: 1, noTags: true, reference: '', shallow: true]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'aquadrehz', url: 'https://github.com/aquadrehz/specFlowExample.git']]])

					GlobalVariables = load 'Jenkinsfiles/GlobalVariables.groovy'
					ShareLibrary = load 'Jenkinsfiles/ShareLibrary.groovy'
					ShareLibrary.killSoapUIProcess();
					globalVariable = GlobalVariables;
					globalVariable.Build_Env = 'UnitTest002';
					globalVariable.WORKSPACE = pwd()
					globalVariable.SoapUI_Port = "8099"
					globalVariable.DB_Suffix = "UnitTest002"
					globalVariable.COMPUTERNAME = ShareLibrary.mapHostname("${NODE_NAME}")
					globalVariable.IsSmokeTest = false;
					globalVariable.Filter = "";

					stage('Build002') {
						ShareLibrary.buildTestSystem(globalVariable);
					}

					stage('System Test002') {
						ShareLibrary.runUnitTest(globalVariable);
					}
					
					stage('Unit Test002') {
						ShareLibrary.runSystemTest(globalVariable);
					}
				}
			}
		}
)

