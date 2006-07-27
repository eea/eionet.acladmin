<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,
		eionet.acl.utils.Util, eionet.acl.Names" %>
<%	
	HashMap appClients = (HashMap)session.getAttribute(Names.APPCLIENTS_ATT);
%>

<%
response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
<%@ include file="headerinfo.txt" %>
  <title>Admin permissions</title>
<script type="text/javascript">
// <![CDATA[

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
		window.open("appLogin.jsp?app=" + appName,"login","height=200,width=460,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
	}
// ]]>
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

<hr/>

	<h1>Search across applications</h1>
	<form name="index.jsp" action="main" method="post">
	
		<%
		if (session.getAttribute(Names.ATT_ACROSS_APPS)==null){
			%>
			<h4>Username and password for searching across applications:</h4>
			<table>
				<tr>
					<td align="right"><label for="username">Username:</label></td>
					<td><input id="username" type="text" name="apps_user" /></td>
				</tr>
				<tr>
					<td align="right"><label for="password">Password:</label></td>
					<td><input id="password" type="password" name="apps_pwd" /></td>
				</tr>
			</table>
			<%
		}
		%>
		<h4>Search for:</h4>
		<table>
			<tr>
				<td align="right">
					<label for="subject_type">Subject type:</label>
				</td>
				<td>
					<select id="subject_type" name="subject_type">
						<option selected value="user">user</option>
						<option value="localgroup">localgroup</option>
					</select>
				</td>
			</tr>
			<tr>
				<td align="right"><label for="subject_id">Subject ID:</label></td>
				<td><input id="subject_id" type="text" name="subject_id" /></td>
			</tr>
			<%
			if (session.getAttribute(Names.ATT_ACROSS_APPS)!=null){
				%>
				<tr>
					<td>&nbsp;</td>
					<td><input type="checkbox" name="<%=Names.PRM_RELOAD_ACROSS_APPS%>"/>&nbsp;Reload</td>
				</tr>
				<%
			}
			%>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" name="submit" value="Search"/></td>
			</tr>

		</table>
		
		<input type="hidden" name="ACTION" value="search_across_apps"/>
	</form>

	<form name="f" action="main" method="post">
		<input type="hidden" name="app" value="" />
		<input type="hidden" name="ACTION" value="" />
		<input type="hidden" name="ACL" value="" />
	</form>

</div> <!-- workarea -->
</body>
</html>
