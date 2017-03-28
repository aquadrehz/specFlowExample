/*
------------------------------------------------------------------------------------------------------------------------
----
---- Specific function
----
------------------------------------------------------------------------------------------------------------------------
 */

def checkOutAndSetupVariable(globalVariable) {
    // Cleanup ENV
    killSoapUIProcess();
    killChromeProcess();

    // globalVariable.WORKSPACE = pwd()    // globalVariable.WORKSPACE = "${env.WORKSPACE}"
    // globalVariable.COMPUTERNAME = java.net.InetAddress.getLocalHost().getHostName()

    // Replace SQL and trigger
    configFile = 'SQL/Procedure/Create_GetSalaryFileImportFromPepldoc.sql'
    replaceConfigSQLFile(configFile, globalVariable)
    configFile = 'SQL/Procedure/Create_GetNewCustomersFromPust.sql'
    replaceConfigSQLFile(configFile, globalVariable)
    configFile = 'SQL/Trigger/Create_UpdateSalaryFileImportInPeplDoc.sql'
    replaceConfigSQLFile(configFile, globalVariable)

    // Replay Data
    def configFile = 'SPE.Tests.ReplayData/App.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // Report Service
    configFile = 'ReportService/Web.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // PuST
    configFile = 'PuST.Data/App.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'PuST.Web/Web.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // SPE
    configFile = 'SPE.Data/App.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'SPE.BatchManager/Web.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'SPE.BatchProcess/App.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // SPE.BatchProcess.Tests.Integration
    configFile = 'SPE.BatchProcess.Tests.Integration/app.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // SPS.Test.System
    configFile = 'SPS.Test.System/app.SystemTest01.config'
    replaceConfigFile(configFile, globalVariable)

    // SPE.API
    configFile = 'SPE.API/Web.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'SPE.API.Tests.Integration/app.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // SPE.Import
    configFile = 'SPE.Imports/App.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'SPE.Imports.Tests.Integration/app.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // SPE.PeplDoc
    configFile = 'SPE.PeplDoc.Data/App.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'SPE.PeplDoc/Web.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'SPE.PeplDoc.Tests.System/App.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)

    // SPE.SaFiHub
    configFile = 'SPE.SaFiHub/Web.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
    configFile = 'SPE.SaFiHub.Tests.Integration/app.'+globalVariable.Build_Env+'.config'
    replaceConfigFile(configFile, globalVariable)
}

def runUnitTestTestSystem(globalVariable) {
    // Run test with covertura + nunit
    bat '"%OpenCover%" -target:"%Nunit%" -targetargs:"' +
            'Bowling.SpecFlow\\bin\\'+globalVariable.Build_Env+'\\Bowling.SpecFlow.dll ' +
            '/xml=nunit-result.xml /noshadow /framework:net-4.5" -register -mergebyhash  -output:"outputCoverage.xml"'

    // Parse the unit test result
    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '1', failureThreshold: '1', unstableNewThreshold: '1', unstableThreshold: '1'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-result.xml', skipNoTestFiles: false, stopProcessingIfError: true]]])

    // Generate open cover report
    bat '"%ReportGenerator%"  -reports:outputCoverage.xml -targetDir:CodeCoverageHTML'

    // Publish code coverage report
    publishHTML(target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'CodeCoverageHTML', reportFiles: 'index.htm', reportName: 'Code Coverage Report'])
}

def runIntegrationTestSPESystem(globalVariable) {
    killSoapUIProcess();
    resetReplayDatabase(globalVariable);
    resetSPEDatabase(globalVariable);

    try
    {
        def option = ''
        if(globalVariable.IsSmokeTest) {
            option = '/include:BaseLine'
        }
        bat '"%Nunit%" SPE.BatchProcess.Tests.Integration\\bin\\'+globalVariable.Build_Env+'\\SPE.BatchProcess.Tests.Integration.dll /xml=nunit-integration-result.xml /noshadow /framework:net-4.5 /nothread '+option

    } catch(err) {};

    // Parse the integration test result
    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-integration-result.xml', skipNoTestFiles: false, stopProcessingIfError: true]]])
}

def runIntegrationTestAPI(globalVariable) {
    killSoapUIProcess();
    resetReplayDatabase(globalVariable);
    resetSPEDatabase(globalVariable);
    resetPuSTDatabase(globalVariable);
    publishSPEWeb(globalVariable);
    publishPuSTWeb(globalVariable);
    publishAPI(globalVariable);
    try
    {
        bat '"%Nunit%" SPE.API.Tests.Integration\\bin\\'+globalVariable.Build_Env+'\\SPE.API.Tests.Integration.dll /xml=nunit-api-integration-result.xml /noshadow /framework:net-4.5'
    } catch(err) {};

    // Parse the integration test result
    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-api-integration-result.xml', skipNoTestFiles: false, stopProcessingIfError: true]]])
}

def runIntegrationTestImports(globalVariable) {
    killSoapUIProcess();
    resetReplayDatabase(globalVariable);
    resetSPEDatabase(globalVariable);
    resetPuSTDatabase(globalVariable);
    resetPeplDocDatabase(globalVariable);
    publishImport(globalVariable);
    try
    {
        bat '"%Nunit%" SPE.Imports.Tests.Integration\\bin\\'+globalVariable.Build_Env+'\\SPE.Imports.Tests.Integration.dll /xml=nunit-import-integration-result.xml /noshadow /framework:net-4.5 /nothread'
    } catch(err) {};

    // Parse the integration test result
    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-import-integration-result.xml', skipNoTestFiles: false, stopProcessingIfError: true]]])
}

def runIntegrationTestSaFiHub(globalVariable) {
    killSoapUIProcess();
    resetReplayDatabase(globalVariable);
    resetPeplDocDatabase(globalVariable);
    publishSaFiHub(globalVariable);
    try
    {
        bat '"%Nunit%" SPE.SaFiHub.Tests.Integration\\bin\\'+globalVariable.Build_Env+'\\SPE.SaFiHub.Tests.Integration.dll /xml=nunit-safihub-integration-result.xml /noshadow /framework:net-4.5'
    } catch(err) {};

    // Parse the integration test result
    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-safihub-integration-result.xml', skipNoTestFiles: false, stopProcessingIfError: true]]])
}

def runIntegrationTestPeplDoc(globalVariable) {
    killSoapUIProcess();
    resetReplayDatabase(globalVariable);
    resetPeplDocDatabase(globalVariable);
    try
    {
        bat '"%Nunit%" SPE.PeplDoc.Tests.Integration\\bin\\'+globalVariable.Build_Env+'\\SPE.PeplDoc.Tests.Integration.dll /xml=nunit-pepldoc-integration-result.xml /noshadow /framework:net-4.5'
    } catch(err) {};

    // Parse the integration test result
    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-pepldoc-integration-result.xml', skipNoTestFiles: false, stopProcessingIfError: true]]])
}

def runSystemTestSPE(globalVariable) {
    killChromeProcess();
    killSoapUIProcess();
    resetReplayDatabase(globalVariable);
    resetSPEDatabase(globalVariable);
    publishReportService(globalVariable);
    publishSPEWeb(globalVariable);
    try
    {
        bat '"%Nunit%" SPS.Test.System\\bin\\'+globalVariable.Build_Env+'\\SPS.Test.System.dll /xml=nunit-spe-system-result.xml /noshadow /framework:net-4.5 /nothread'
    } catch(err) {};

    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '1', failureThreshold: '1', unstableNewThreshold: '1', unstableThreshold: '1'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-spe-system-result.xml', skipNoTestFiles: false, stopProcessingIfError: false]]])
}

def runSystemTestPeplDoc(globalVariable) {
    killChromeProcess();
    killSoapUIProcess();
    resetPeplDocDatabase(globalVariable);
    publishPeplDocWeb(globalVariable);
    try
    {
        bat '"%Nunit%" SPE.PeplDoc.Tests.System\\bin\\'+globalVariable.Build_Env+'\\SPE_PeplDoc_Tests_System.dll /xml=nunit-pepldoc-system-result.xml /noshadow /framework:net-4.5'
    } catch(err) {};

    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '1', failureThreshold: '1', unstableNewThreshold: '1', unstableThreshold: '1'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-pepldoc-system-result.xml', skipNoTestFiles: false, stopProcessingIfError: false]]])
}

def runSystemTestPuST(globalVariable) {
    killChromeProcess();
    resetPuSTDatabase(globalVariable);
    publishReportService(globalVariable);
    publishPuSTWeb(globalVariable);
    resetPuSTDatabase(globalVariable);

    try
    {
        bat '"%Nunit%" PuST_Tests_System\\bin\\'+globalVariable.Build_Env+'\\PuST_Tests_System.dll /xml=nunit-pust-system-result.xml /noshadow /framework:net-4.5'
    } catch(err) {};

    step([$class: 'XUnitBuilder', testTimeMargin: '3000', thresholdMode: 1, thresholds: [[$class: 'FailedThreshold', failureNewThreshold: '1', failureThreshold: '1', unstableNewThreshold: '1', unstableThreshold: '1'], [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']], tools: [[$class: 'NUnitJunitHudsonTestType', deleteOutputFiles: true, failIfNotNew: true, pattern: 'nunit-pust-system-result.xml', skipNoTestFiles: false, stopProcessingIfError: false]]])
}

/*
------------------------------------------------------------------------------------------------------------------------
----
---- Reusable function
----
------------------------------------------------------------------------------------------------------------------------
 */

def identifyProject(branchName) {
    if( branchName =~ /-refactor/ ) {
        return "SPE";
    } else if( branchName =~ /-SPE/ ) {
        return "SPE";
    } else if( branchName =~ /-PuST/ ) {
        return "PuST";
    } else {
        return "SPE";
    }
}

def replaceConfigFile(fileName, globalVariable) {
    echo "replaceConfigFile: "+fileName

    def fileText = readFile(fileName)
    writeFile file: fileName + ".bak", text: fileText

    fileText = fileText.replaceAll('%WORKSPACE%', globalVariable.WORKSPACE.replace("\\","\\\\"))
    fileText = fileText.replaceAll('%SoapUI_Port%', globalVariable.SoapUI_Port)
    fileText = fileText.replaceAll('%DB_Suffix%', globalVariable.DB_Suffix)
    fileText = fileText.replaceAll('%COMPUTERNAME%', globalVariable.COMPUTERNAME)
    writeFile file: fileName, text: fileText
}

def replaceConfigSQLFile(fileName, globalVariable) {
    echo "replaceConfigSQLFile: "+fileName
    def fileText = readFile(fileName)
    writeFile file: fileName + ".bak", text: fileText

    fileText = fileText.replaceAll('SET @SpeDB = \'SPE_Refactor\'', 'SET @SpeDB = \'SPE_'+globalVariable.DB_Suffix+'\'')
    fileText = fileText.replaceAll('SET @PuSTDB = \'PuST_Refactor\'', 'SET @PuSTDB = \'PuST_'+globalVariable.DB_Suffix+'\'')
    fileText = fileText.replaceAll('SET @PeplDocDB = \'PeplDoc_Refactor\'', 'SET @PeplDocDB = \'PeplDoc_'+globalVariable.DB_Suffix+'\'')
    // file.write(fileText, 'utf-8');
    writeFile file: fileName, text: fileText, encoding: 'utf-8'
}

def killChromeProcess() {
    try
    {
        bat 'taskkill /F /IM chrome.exe'
    } catch(err) {

    }
    try
    {
        bat 'taskkill /F /IM chromedriver.exe'
    } catch(err) {

    }
    try
    {
        bat 'taskkill /F /IM phantomjs.exe'
    } catch(err) {

    }
}

def killSoapUIProcess() {
    try
    {
        bat 'jps -m'
        bat 'for /f "tokens=1" %%i in (\'jps -m^|find "SoapUIMockServiceRunner"\') do ( taskkill /F /PID %%i )'
    } catch(err) {

    }
}

def buildSPSSystem(globalVariable) {
    // Restore package
    bat '"%Nuget%" restore SPE_Solution/SmartPensionEngine.sln'
    // Run the ms build
    bat '"%MSBuild%" /p:Configuration='+globalVariable.Build_Env+' SPE_Solution/SmartPensionEngine.sln'
}

def buildTestSystem(globalVariable) {
    // Restore package
    bat '"%Nuget%" restore ./specFlowExample.sln'
	echo '>>>>>>>>>>>>>>>"%Nuget%" restore ./specFlowExample.sln'

    // Run the ms build
    bat '"%MSBuild%" /p:Configuration='+globalVariable.Build_Env+' ./specFlowExample.sln'
	echo '>>>>>>>>>>>>>>>"%MSBuild%" /p:Configuration='+globalVariable.Build_Env+' ./specFlowExample.sln'
}

def resetReplayDatabase(globalVariable) {
    try {
        // Drop database
        // sqlcmd -E -S <ServerName> -Q "ALTER DATABASE <DBName> SET SINGLE_USER WITH ROLLBACK IMMEDIATE"
        // sqlcmd -E -S <ServerName> -Q "DROP DATABASE <DBName>"
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "ALTER DATABASE [SPE_Replay_'+globalVariable.DB_Suffix+'] SET SINGLE_USER WITH ROLLBACK IMMEDIATE"'
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "drop database [SPE_Replay_'+globalVariable.DB_Suffix+']"'

        // Migrate database
        dir('SPE.Tests.ReplayData\\bin\\'+globalVariable.Build_Env+'\\') {
            bat 'copy %Migrate% migrate.exe'
            bat 'migrate.exe SPE.Tests.ReplayData.dll /startUpConfigurationFile=SPE.Tests.ReplayData.dll.config'
        }

    } catch(err) {
        echo "Exception thrown:\n ${err}"
        echo "Stacktrace:"
        err.printStackTrace()
    }
}

def resetSPEDatabase(globalVariable) {
    try {
        // Drop database
        // sqlcmd -E -S <ServerName> -Q "ALTER DATABASE <DBName> SET SINGLE_USER WITH ROLLBACK IMMEDIATE"
        // sqlcmd -E -S <ServerName> -Q "DROP DATABASE <DBName>"
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "ALTER DATABASE [SPE_'+globalVariable.DB_Suffix+'] SET SINGLE_USER WITH ROLLBACK IMMEDIATE"'
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "drop database [SPE_'+globalVariable.DB_Suffix+']"'
    } catch(err) {
        echo "Exception thrown:\n ${err}"
        echo "Stacktrace:"
        err.printStackTrace()
    }

    try
    {
        // Migrate database
        dir('SPE.Data\\bin\\'+globalVariable.Build_Env+'\\') {
            bat 'copy %Migrate% migrate.exe'
            bat 'migrate.exe SPE.Data.dll /startUpConfigurationFile=SPE.Data.dll.config'
        }
    } catch(err) {
        echo "Exception thrown:\n ${err}"
        echo "Stacktrace:"
        err.printStackTrace()
    }
}

def resetPuSTDatabase(globalVariable) {
    try
    {
        // Drop database
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "ALTER DATABASE [PuST_'+globalVariable.DB_Suffix+'] SET SINGLE_USER WITH ROLLBACK IMMEDIATE"'
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "drop database [PuST_'+globalVariable.DB_Suffix+']"'
    } catch(err) {
        echo "Exception thrown:\n ${err}"
        echo "Stacktrace:"
        err.printStackTrace()
    }

    try
    {
        // Migrate database
        dir('PuST.Data\\bin\\'+globalVariable.Build_Env+'\\') {
            bat 'copy %Migrate% migrate.exe'
            bat 'migrate.exe PuST.Data.dll /startUpConfigurationFile=PuST.Data.dll.config'
        }
    } catch(err) {
        echo "Exception thrown:\n ${err}"
        echo "Stacktrace:"
        err.printStackTrace()
    }
}

def resetPeplDocDatabase(globalVariable) {
    try
    {
        // Drop database
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "ALTER DATABASE [PeplDoc_'+globalVariable.DB_Suffix+'] SET SINGLE_USER WITH ROLLBACK IMMEDIATE"'
        bat 'sqlcmd -S .\\SQLEXPRESS -Q "drop database [PeplDoc_'+globalVariable.DB_Suffix+']"'
    } catch(err) {
        echo "Exception thrown:\n ${err}"
        echo "Stacktrace:"
        err.printStackTrace()
    }

    try
    {
        // Migrate database
        dir('SPE.PeplDoc.Data\\bin\\'+globalVariable.Build_Env+'\\') {
            bat 'copy %Migrate% migrate.exe'
            bat 'migrate.exe SPE.PeplDoc.Data.dll /startUpConfigurationFile=SPE.PeplDoc.Data.dll.config'
        }
    } catch(err) {
        echo "Exception thrown:\n ${err}"
        echo "Stacktrace:"
        err.printStackTrace()
    }
}

def publishReportService(globalVariable) {
    bat '"%MSBuild%" ReportService\\ReportService.csproj /p:DeployOnBuild=true /p:PublishProfile='+globalVariable.Build_Env+'.pubxml'
}

def publishSPEWeb(globalVariable) {
    bat '"%MSBuild%" SPE.BatchManager\\SPE.BatchManager.csproj  /p:DeployOnBuild=true /p:PublishProfile='+globalVariable.Build_Env+'.pubxml'
}

def publishPuSTWeb(globalVariable) {
    bat '"%MSBuild%" PuST.Web\\PuST.Web.csproj  /p:DeployOnBuild=true /p:PublishProfile='+globalVariable.Build_Env+'.pubxml'
}

def publishAPI(globalVariable) {
    bat '"%MSBuild%" SPE.API\\SPE.API.csproj  /p:DeployOnBuild=true /p:PublishProfile='+globalVariable.Build_Env+'.pubxml'
}

def publishImport(globalVariable) {
    bat '"%MSBuild%" SPE.Imports\\SPE.Imports.csproj  /p:DeployOnBuild=true /p:PublishProfile='+globalVariable.Build_Env+'.pubxml'
}

def publishSaFiHub(globalVariable) {
    bat '"%MSBuild%" SPE.SaFiHub\\SPE.SaFiHub.csproj  /p:DeployOnBuild=true /p:PublishProfile='+globalVariable.Build_Env+'.pubxml'
}

def publishPeplDocWeb(globalVariable) {
    bat '"%MSBuild%" SPE.PeplDoc\\SPE.PeplDoc.csproj  /p:DeployOnBuild=true /p:PublishProfile='+globalVariable.Build_Env+'.pubxml'
}

def mapHostname(nodeName) {
    def result
    switch (nodeName) {
        case 'master':
            result = 'DESKTOP-B01VJR7'
            break
        case 'physicalNode':
            result = 'WIN-N3GP2VTUL2B'
            break
        case 'deployNode':
            result = 'WIN-N3GP2VTUL2B'
            break
        default:
            result = 'DESKTOP-B01VJR7'
            break
    }
    return result
}

return this;