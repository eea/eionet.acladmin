<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="eionet.acl.Names" %>

<%
  String ctx = request.getContextPath();

	String err = (String)request.getAttribute(Names.ERROR_ATT);

	//HashMap apps = (HashMap)session.getAttribute(Names.APPLICATIONS_ATT);


%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>Admin permissions</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
  <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />

<script type="text/javascript">

function showhelp(text) {
	if (text != '')
		alert(text);
	else
		alert('No examples for this unit type!');
}

function setFocus(){
	var t;
	t=document.getElementById("j_username");
	t.focus();
}

}


	function changeParamInString(sUrl, sName, sValue){
		var  i, j,  sBeg, sEnd, sStr;

		//KL 021009 -> in some reason does not work anymore :(
		//sValue=escape(sValue);

		i = sUrl.indexOf(sName + '=');
		if (i > 0) {
			sBeg=sUrl.substr(0, i); 
			sStr=sUrl.substr(i);
			j = sStr.indexOf('&');
			if (j > 0)
			   sEnd = sStr.substr(j);
			else
			   sEnd= '';

			sUrl=sBeg + sName + '=' + sValue + sEnd ;

			}
		else
			{

			j = sUrl.indexOf('?');
			if (j>0)
				sUrl = sUrl + '&' + sName + '=' + sValue;
			else
				sUrl = sUrl + '?' + sName + '=' + sValue;
			}
		//return sUrl ;
		redirect(sUrl);
		}

	function redirect(url){
		//document.URL=url;
		document.location=url;

	}

var browser = document.all ? 'E' : 'N';

var picklist = new Array();


</script>
</head>
<body onload="setFocus()">
	<% if (err!= null) { %>
        <div id="errormessage">
            <h1>Error!</h1>
            <p><%=err%></p>
        </div>
	<% } %>
<br/><br/><br/><br/>
<form name="f" action="main" method="post">
	<input name="ACTION" type="hidden" value="<%=Names.LOGIN_ACTION%>" />
	<table>
        <tr>
          <td width='200'></td>
          <td>
            <b>UserName:</b>
          </td>
          <td>
            <input name='j_username' type='text' />
          </td>
        </tr>
        <tr>
          <td width='200'></td>
          <td>
            <b>Password:</b>
          </td>
          <td>
            <input name='j_passwd' type='password' />
          </td>
        </tr>
        <tr height='30'>
          <td colspan='3'></td>
        </tr>
        <tr>
          <td width='200'></td>
          <td>
            <input value='OK' name='ok_btn' type='submit' />
          </td>
        </td>
        <td>
          <input value='Cancel' name='cancel_btn' type='button' onclick='javascript:window.close()' />
        </td>
        </tr>
	</table>
</body>
</html>
