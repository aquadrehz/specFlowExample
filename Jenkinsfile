parallel (
		"Unit Test001": {
			node {
				ws("workspace/${JOB_NAME}") {
					
					  def x = "${env.WORKSPACE}";
					   echo 'en '+x;
					   echo 'psd '+pwd();
					
					
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
					globalVariable.Filter = "${env.Filter}";

					stage('Build') {
						ShareLibrary.buildTestSystem(globalVariable);
					}

					stage('System Test') {
						ShareLibrary.runUnitTest(globalVariable);
					}
					
					stage('Unit Test') {
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

					stage('Build') {
						ShareLibrary.buildTestSystem(globalVariable);
					}

					stage('System Test') {
						ShareLibrary.runUnitTest(globalVariable);
					}
					
					stage('Unit Test') {
						ShareLibrary.runSystemTest(globalVariable);
					}
				}
			}
		}
)

