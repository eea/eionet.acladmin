<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,
		eionet.acl.utils.Util, eionet.acl.Names" %>

<%
	//HashMap apps = (HashMap)session.getAttribute(Names.APPLICATIONS_ATT);
	
	//String thisAppName = (String)session.getAttribute(Names.APP_ATT);

	//String err = (String)request.getAttribute(Names.ERROR_ATT);
  //String ctx = request.getContextPath();

  //Hashtable permissions = (Hashtable) request.getAttribute("PRMS");
	//Hashtable groups = (Hashtable) request.getAttribute("GROUPS");

	String aclName = request.getParameter("NAME");

	//entry names for anonymous and auth access 
	//String aUser =  (String)((HashMap)apps.get(thisAppName)).get("authUser");
	//String uUser =  (String)((HashMap)apps.get(thisAppName)).get("unauthUser");

%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<%@ include file="headerinfo.txt" %>
  <title>Group Members</title>
  <script type="text/javascript">
// <![CDATA[
	function openPage(action) {
		document.forms["f"].ACTION.value=action;
		document.forms["f"].submit();
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
    <div class="breadcrumbitem"><a href="javascript:openPage('<%=Names.SHOW_APPS_ACTION%>')">Applications</a></div>
    <div class="breadcrumbitem"><a href="javascript:openPage('')"><%=thisAppName%></a></div>
    <div class="breadcrumbitem"><a href="javascript:openPage('<%=Names.SHOW_GROUPS_ACTION%>')">Groups</a></div>
    <div class="breadcrumbitemlast">Members</div>
    <div class="breadcrumbtail"></div>
</div>
</div> <!-- pagehead -->

<%@ include file="globalnav.jsp" %>
<div id="workarea">
  <div id="operations">
    <ul>
      <li><a href="javascript:openPage('<%=Names.SHOW_GROUPS_ACTION%>')">OK</a></li>
    </ul>
  </div>
	<% if (err!= null) { %>
        <div id="errormessage">
            <h1>Error!</h1>
            <p><%=err%></p>
        </div>
	<% } %>

  <h1>Localgroup Name: <%=aclName%></h1>
  <table cellspacing="7" border="0">
  <tbody>
  <tr>
    <th>Member</th>
    <th>Delete</th>
  </tr>
  <tr valign='top'>
    <td>
      <form name='add_member' action='main' method='post'>
        <input name='MEMBER' type='text' />&nbsp;
        <input type="submit" value="Add" title="Add user to localgroup"/>
        <input value='<%=Names.MEMBER_ADD_ACTION%>' name='ACTION' type='hidden' />
        <input value='<%=aclName%>' name='NAME' type='hidden' />
      </form>
    </td>
    <td></td>
  </tr>
      <!-- members -->
      <% 
          int ii=0;
          Vector members = (Vector)groups.get(aclName);
          if (members != null)
              for (Iterator i = members.iterator(); i.hasNext();) { 
                  String name=(String)i.next();
                  ii++;

                  String fontColour;
                  if ( (name.equals(aUser) || name.equals(uUser))  ) 
                      fontColour = "#0000EE";
                  else
                      fontColour = "#646666";

      %>
  <tr>
      <td><font color="<%=fontColour%>"><%=name%></font></td>
      <td>
          <form action='main' name='member_<%=ii%>' method='post'>
            <img width='15' alt='Delete member from localgroup' src='images/delete.png' onclick='member_<%=ii%>.submit();' height='15' />
            <input value='<%=Names.MEMBER_DEL_ACTION%>' name='ACTION' type='hidden' />
            <input value='<%=name%>' name='MEMBER' type='hidden' />
            <input value='<%=aclName%>' name='NAME' type='hidden' />
          </form>
      </td>

  </tr>
          <% }  %> 

  </tbody>
  </table>

  <form name="f" method="post" action="main">
    <input type="hidden" name = "ACTION" value ="" />
  </form>
</div> <!-- workarea -->
</body>
</html>
