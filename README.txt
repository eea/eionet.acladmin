Documentation:

Pre-install steps:
===========================

1. Create local.properties like default.properties as an example.

2. Configure/remove applcations managed by AclADmin. Adding an application called "appName" means adding/modifying the following 4 parameters:
    2.1 applications=appName
    2.2 appName.router.url=URL of appName	
    2.3 appName.anonymous.access=anonymous
    2.4 appName.authenticated.access=authenticated

3. Copy the acl folder into ${app.home} folder and edit it's contents accordingly.

4. Modify the ${app.home} property value in local.properties file


Building the war:
=================
Execute command: mvn install
