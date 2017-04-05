/*
------------------------------------------------------------------------------------------------------------------------
----
---- Specific function
----
------------------------------------------------------------------------------------------------------------------------
 */

class GlobalVariables implements Serializable {
    def Build_Env;      // define build environment
    def WORKSPACE;      // Current workspace
    def SoapUI_Port;
    def DB_Suffix;
    def COMPUTERNAME;
    def IsSmokeTest;
	def nunitFilter;
    def openCoverFilter;
}

return new GlobalVariables();