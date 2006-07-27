<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,
		eionet.acl.utils.Util, eionet.acl.Names" %>

<%


	String aclEntryType = request.getParameter("TYPE");
	String entryName=request.getParameter("NAME");

	String aclType = request.getParameter("ACL_TYPE");

	String aclName = (String)session.getAttribute(Names.ACL_ATT);

	Hashtable aclInfo =(Hashtable) request.getAttribute(Names.ACL_INFO_ATT); 
	String aclDescr = (String)aclInfo.get("description");


	Vector aclData = (Vector) request.getAttribute(Names.ACL_DATA_ATT); 

	String aclPermissions = "";

	//!! hard - code !! 
     if (aclEntryType.equals("anonymous") || aclEntryType.equals("authenticated") || aclEntryType.equals("owner") ) {
        entryName=aclEntryType;
        aclEntryType="user";
     }


	for (int i=0; i< aclData.size(); i++) {
		Hashtable aclE=(Hashtable)aclData.elementAt(i);
		if( ((String)aclE.get("id")).equals(entryName)) {
			aclPermissions = (String)aclE.get("perms");
			break;
		}

	}

	//String aclPermissions = (String)correctHash.get(entryName);

if (aclPermissions==null)
	aclPermissions="";

	if (! Util.isNullStr(aclPermissions))
		aclPermissions = aclPermissions + ",";


%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<%@ include file="headerinfo.txt" %>
  <title>Permissions</title>
<script type="text/javascript">
// <![CDATA[

function cP (o) {
	var prm = o.value
	if(o.checked)
		document.forms["f"].PERMISSIONS.value += prm;
	else {
		var i = document.forms["f"].PERMISSIONS.value.indexOf(prm);
		var lngth=o.value.length;
		document.forms["f"].PERMISSIONS.value = document.forms["f"].PERMISSIONS.value.substring(0,i) + document.forms["f"].PERMISSIONS.value.substring(i+lngth);
	}

}

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
    <div class="breadcrumbitem"><a href="main"><%=thisAppName%></a></div>
    <div class="breadcrumbitemlast">Permissions</div>
    <div class="breadcrumbtail"></div>
  </div>
</div> <!-- pagehead -->

<%@ include file="globalnav.jsp" %>

<div id="workarea">
  <div id="operations">
    <ul>
      <li><a href="javascript:openPage('<%=Names.SAVE_PERMS_ACTION%>')">OK</a></li>
    </ul>
  </div>
  <h1>Permissions</h1>
  <table>
  <tr>
      <th>ACL Entry Name</th><td><%=entryName%></td>
  </tr>
  <tr>
      <th>ACL Entry Type</th><td><%=aclType%></td>
  </tr>
  <tr>
      <th>ACL Name</th><td><em><%=aclDescr%></em></td>
  </tr>
  </table>

  <table cellspacing="7">
    <thead>
    <tr>
      <th width="10"></th>
      <th>Permission</th>
      <th>Description</th>
    </tr>
    </thead>
    <tbody>
        <!-- permissions -->
        <% for (Iterator i = permissions.keySet().iterator(); i.hasNext();) { 
           String name = (String)i.next();

                 String prmDesc = (String)permissions.get(name);
                 String chkValue=name+",";

                 boolean hasPrm = aclPermissions.indexOf(name) != -1;
                 String chkChecked = (hasPrm ? "checked='checked'" : "");
        %>
    <tr valign="top">
      <td><input onclick="cP(this)" type="checkbox" value="<%=chkValue%>" name="<%=name%>" <%=chkChecked %> /></td>
      <td><%=name%></td>
      <td><strong><%=prmDesc%></strong></td>
    </tr>
        <% }  %> 
    </tbody>
  </table>

  <form name="f" method="post" action="main">
    <input type="hidden" name="ACTION" value="<%=Names.SAVE_PERMS_ACTION%>"/>
    <input type="hidden" name="TYPE" value="<%=aclEntryType%>"/>
    <input type="hidden" name="NAME" value="<%=entryName%>"/>
    <input type="hidden" name="PERMISSIONS" value="<%=aclPermissions%>"/>
    <input type="hidden" name="ACL_TYPE" value="<%=aclType%>"/>
  </form>
</div> <!-- workarea -->
</body>
</html>
