<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="eionet.acl.Names" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<% if (request.getSession().getAttribute(Names.USER_ATT)==null)
	out.println("No session");
else {
	String appName=request.getParameter("app");

%>


<head>
<title>User Authentication</title>
  <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
  <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />

<script type="text/javascript">

function fillFields() {
			var wl=window.location.toString();

		  var pos=wl.indexOf("?app=");
			pos = pos+5;
			var appName = wl.substr(pos);

			var t = document.forms["l"].elements["app"];

			t.value=appName;

			t=document.getElementById("app_user");
			t.focus();

		}


	function resetForm(){
		if (document.all){
			document.all('l').reset();
		}
		else{
			document.forms["l"].reset();
		}

		t=document.getElementById("app_user");
		t.focus();

	}

	function keyDown(keyCode){
		if (keyCode == 13){
			submitForm();
		}
	}

</script>
</head>

<body class="popup" onload="fillFields()">
<form name="l" name="LOGIN" method="post" action="main">
<input type="hidden" name="ACTION" value='<%=Names.APPLOGIN_ACTION%>' />
<input type="hidden" name="app" />

<table width="100%">
	<tr><td colspan=2>Enter your credentials to log in to <%=appName%></td></tr>

	<tr>
		<th >Username:</th>
                <th align='right'><input size='25' type='text' name='app_user'/></th>
		</script>
	</tr>
	<tr>
		<th >Password:</th>
                <th align='right'><input size='25' type='password' name='app_pwd' onkeydown='javascript:keyDown(event.keyCode)'/></th>
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
