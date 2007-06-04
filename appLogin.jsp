<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page import="eionet.acl.Names" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<% if (request.getSession().getAttribute(Names.USER_ATT)==null)
	out.println("No session");
else {
	String appName=request.getParameter("app");

%>


<head>
<%@ include file="headerinfo.txt" %>
<title>User Authentication</title>
<script type="text/javascript">
// <![CDATA[

	function fillFields(){
		
		var wl=window.location.toString();
		var pos=wl.indexOf("?app=");
		if (pos>=0){
			pos = pos+5;
			var appName = wl.substr(pos);
			if (appName!=null && appName.length>0)
				document.forms["l"].elements["app"].value = appName;
		}
		
		var t = document.forms["l"].elements["app_user"];
		if (t)
			t.focus();
	}


	function resetForm(){
		document.forms["l"].reset();

		var t = document.forms["l"].elements["app_user"];
		if (t)
			t.focus();
	}

	function keyDown(keyCode){
		if (keyCode == 13){
			submitForm();
		}
	}
// ]]>
</script>
</head>

<body class="popup" onload="fillFields()">
<form id="l" method="post" action="main">

	<fieldset style="display:none">
		<input type="hidden" name="ACTION" value="<%=Names.APPLOGIN_ACTION%>" />
		<input type="hidden" name="app" />
	</fieldset>
	
	<table width="100%">
		<tr><td colspan="2">Enter your credentials to log in to <%=appName%></td></tr>
	
		<tr>
			<th>Username:</th>
	        <th align="right"><input size="25" type="text" name="app_user" id="app_user"/></th>
		</tr>
		<tr>
			<th >Password:</th>
	                <th align="right"><input size="25" type="password" name="app_pwd" onkeydown="javascript:keyDown(event.keyCode)"/></th>
		</tr>
		<tr><td align="right" colspan="2">
			<input name="SUBMIT" type="submit" value="Login" /></td>
		</tr>
		<tr><td align="right" colspan="2"><input name="RESET" type="button" value="Clear Fields" onclick="resetForm()" /></td></tr>
	</table>
</form>
</body>
<% } %>
</html>
