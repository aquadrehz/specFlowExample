parallel (
agent any
    "Unit Test001": {
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
            globalVariable.Build_Env = 'UnitTest01';
            globalVariable.WORKSPACE = pwd()
            globalVariable.SoapUI_Port = "8099"
            globalVariable.DB_Suffix = "UnitTest01"
            globalVariable.COMPUTERNAME = ShareLibrary.mapHostname("${NODE_NAME}")
            globalVariable.IsSmokeTest = false;

            stage('Build') {
                ShareLibrary.buildTestSystem(globalVariable);
            }

            stage('Unit Test') {
                ShareLibrary.runUnitTestTestSystem(globalVariable);
            }
        }
    },
    "Unit Test002": {
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
                globalVariable.Build_Env = 'UnitTest02';
                globalVariable.WORKSPACE = pwd()
                globalVariable.SoapUI_Port = "8099"
                globalVariable.DB_Suffix = "UnitTest02"
                globalVariable.COMPUTERNAME = ShareLibrary.mapHostname("${NODE_NAME}")
                globalVariable.IsSmokeTest = false;

                stage('Build') {
                    ShareLibrary.buildTestSystem(globalVariable);
                }

                stage('Unit Test') {
                    ShareLibrary.runUnitTestTestSystem(globalVariable);
                }
            }
        
    }
)

