<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,eionet.acl.utils.Util, eionet.acl.Names" %>

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

if (aclPermissions==null)
	aclPermissions="";

	if (!Util.isNullStr(aclPermissions)){
		if (!aclPermissions.endsWith(","))
			aclPermissions = aclPermissions + ",";
		if (!aclPermissions.startsWith(","))
			aclPermissions = "," + aclPermissions;
	}


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
		document.forms["f"].elements["PERMISSIONS"].value += prm;
	else {
		var i = document.forms["f"].elements["PERMISSIONS"].value.indexOf(prm);
		var lngth=o.value.length;
		document.forms["f"].elements["PERMISSIONS"].value = document.forms["f"].elements["PERMISSIONS"].value.substring(0,i) + document.forms["f"].elements["PERMISSIONS"].value.substring(i+lngth);
	}

}

function openPage(action) {
	document.forms["f"].ACTION.value=action;
	document.forms["f"].submit();
}

// ]]>
</script>

</head>
<body>
<div id="container">
	<%
	Vector breadcrumbs = new Vector();
	breadcrumbs.add("Applications|javascript:openPage('C')");
	if (session.getAttribute(Names.APP_ATT)!=null)
		breadcrumbs.add(session.getAttribute(Names.APP_ATT) + "|main?action=&amp;ACL=/");
	if (aclName!=null)
		breadcrumbs.add((aclName.equals("/") ? "/root_level" : aclName) + "|main?action=&amp;ACL=" + aclName);
	breadcrumbs.add("Permissions");
    request.setAttribute("breadcrumbs", breadcrumbs);
	%>
    <jsp:include page="location.jsp" flush="true" />
    <%@ include file="menu.jsp" %>
<div id="workarea">
  <div id="operations">
    <ul>
      <li><a href="javascript:openPage('<%=Names.SAVE_PERMS_ACTION%>')">OK</a></li>
    </ul>
  </div>
  <h1>Permissions</h1>
  
  <br/>
  
  <table>
  <tr>
      <th style="text-align:right">ACL Entry Name:</th><td><%=entryName%></td>
  </tr>
  <tr>
      <th style="text-align:right">ACL Entry Type:</th><td><%=aclType%></td>
  </tr>
  <tr>
      <th style="text-align:right">ACL Name:</th><td><em><%=aclDescr%></em></td>
  </tr>
  </table>
  
	<br/>
	
  <table cellspacing="7">
    <thead>
    <tr>
      <th style="width:10"></th>
      <th>Permission</th>
      <th>Description</th>
    </tr>
    </thead>
    <tbody>
        <!-- permissions -->
        <%
        for (Iterator i = permissions.keySet().iterator(); i.hasNext();){ 
	        
			String name = (String)i.next();

			String prmDesc = (String)permissions.get(name);
			String chkValue = "," + name + ",";

            boolean hasPrm = aclPermissions.indexOf(chkValue) != -1;
            
            String chkChecked = (hasPrm ? "checked=\"checked\"" : "");
        %>
    <tr valign="top">
      <td><input onclick="cP(this)" type="checkbox" value="<%=chkValue%>" name="<%=name%>" <%=chkChecked %> /></td>
      <td><%=name%></td>
      <td><strong><%=prmDesc%></strong></td>
    </tr>
        <% }  %> 
    </tbody>
  </table>

  <form id="f" method="post" action="main">
  	<fieldset style="display:none">
	    <input type="hidden" name="ACTION" value="<%=Names.SAVE_PERMS_ACTION%>"/>
	    <input type="hidden" name="TYPE" value="<%=aclEntryType%>"/>
	    <input type="hidden" name="NAME" value="<%=entryName%>"/>
	    <input type="hidden" name="PERMISSIONS" value="<%=aclPermissions%>"/>
	    <input type="hidden" name="ACL_TYPE" value="<%=aclType%>"/>
	</fieldset>
  </form>
</div> <!-- workarea -->
</div> <!-- container -->
<%@ include file="footer.jsp" %>
</body>
</html>
