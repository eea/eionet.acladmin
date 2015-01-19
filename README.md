ACLAdmin
========

Pre-install steps
-----------------

1. Create local.properties like default.properties as an example.

2. Configure/remove applcations managed by AclADmin. Adding an application called "appName" means adding/modifying the following 4 parameters:
    2.1 applications=appName
    2.2 appName.router.url=URL of appName	
    2.3 appName.anonymous.access=anonymous
    2.4 appName.authenticated.access=authenticated

3. Copy the acl folder into ${app.home} folder and edit it's contents accordingly.

4. Modify the ${app.home} property value in local.properties file


Building the war
----------------

Execute command: mvn install

Deployment
----------
By default Tomcat is configured to copy the META-INF/context.xml file in the WAR file to /etc/tomcat/Catalina/localhost/ROOT.xml. Since it is empty, the application will fall back to properties files in the webapp. If you want to load the properties file from an external location, you can activate it in the ROOT.xml file post deployment. The documentation says that this file won't be overwritten again, but this is only for restarts. It will happen when you redeploy the WAR file. On production systems you must then add the `deployXML="false"` to the `<Host>` element to prevent it.
```xml
<Host ... deployXML="false" ...
```
For more, see http://tomcat.apache.org/tomcat-7.0-doc/config/host.html
