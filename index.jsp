<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,
		eionet.acl.utils.Util, eionet.acl.Names" %>
<%
	HashMap appClients = (HashMap)session.getAttribute(Names.APPCLIENTS_ATT);
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
  <title>Admin permissions</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
  <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
  <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />

<script type="text/javascript">
<!--
	
	function openPage(action) {
		document.forms["f"].ACTION.value=action;
		document.forms["f"].submit();
	}
	function openApp(appName) {
		document.forms["f"].app.value=appName;
		document.forms["f"].ACTION.value="";
		document.forms["f"].ACL.value="/";
		document.forms["f"].submit();
	}
	function doLogin(appName) {
		window.open("appLogin.jsp?app=" + appName,"login","height=200,width=400,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
	}

//-->
</script>
<%@ include file="menu.jsp" %>
</head>
<body>
<div id="pagehead">
  <div id="identification">
    <a href="/"><img src="images/logo.png" alt="Logo" id="logo" /></a>
    <div class="sitetitle">Access Control List administration</div>
    <div class="sitetagline">You change who can do what in Reportnet</div>
  </div>

  <div class="breadcrumbtrail">
    <div class="breadcrumbhead">You are here:</div>
    <div class="breadcrumbitem"><a href="http://www.eionet.eu.int">EIONET</a></div>
    <div class="breadcrumbitemlast">Applications</div>
    <div class="breadcrumbtail"></div>
  </div>
</div> <!-- pagehead -->

<%@ include file="globalnav.jsp" %>
<div id="workarea">
    <% if (err!= null) { %>
	<div id="errormessage">
	    <h1>Error!</h1>
            <p><%=err%></p>
	</div>
    <% } %>
      <h1>Choose application</h1>
      <table cellspacing="7" width="630">
        <tbody>
        <tr>
          <th width="147">Application</th>
          <th>Host</th>
	</tr>

		<%  for (Iterator i = apps.keySet().iterator(); i.hasNext();) { 
 		    String appName = (String)i.next();
		 		//String appUrl = (String)apps.get(appName);
        String appUrl = (String) ((HashMap)apps.get(appName)).get("url");
				
				int pos = appUrl.indexOf("//");
				pos = appUrl.indexOf("/", pos+2);
				appUrl=appUrl.substring(0,pos);
				String appHref; //if client already exists, no login needed
				if (appClients.containsKey(appName))
					//appHref="main?app=" + appName;
					appHref="javascript:openApp('" + appName+ "')";
				else
					appHref="javascript:doLogin('" + appName + "')";

		%>
	<tr>
	        <td><strong><a href="<%=appHref%>"><%=appName%></a></strong></td>
		<td><strong><%=appUrl%></strong></td>
	</tr>
		<%  }  %>
        </tbody>
      </table>

<form name="f" action="main" method="post">
	<input type="hidden" name="app" value="" />
	<input type="hidden" name="ACTION" value="" />
	<input type="hidden" name="ACL" value="" />
</form>
<hr/>
</div> <!-- workarea -->
</body>
</html>
