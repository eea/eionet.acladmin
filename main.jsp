<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,
		eionet.acl.utils.Util, eionet.acl.Names" %>
<%

	
	Hashtable aclInfo =(Hashtable) request.getAttribute(Names.ACL_INFO_ATT); 

	String aclDescr = "";

	if (aclInfo != null)
		aclDescr = (String)aclInfo.get("description");

	


	Vector acls = (Vector) request.getAttribute(Names.ACL_CHILDREN_ATT);
	Vector aclData = (Vector) request.getAttribute(Names.ACL_DATA_ATT); 
	String selAcl = request.getParameter("ACL");


	//no need to show description if it equals to the name??
	if  (aclDescr.equals(selAcl))
		aclDescr="";


	if (selAcl == null || selAcl.equals(""))
		selAcl = (String)session.getAttribute(Names.ACL_ATT);

	if (selAcl==null || selAcl.equals("") )
		selAcl="/";

	String parentAclName = Util.getParentAclName(selAcl);

	boolean changed=(request.getAttribute(Names.CHANGED_ATT)!=null);

	boolean isOwner=true;

	if ( request.getAttribute(Names.NOTOWNER_ATT) ==null || ((String)request.getAttribute(Names.NOTOWNER_ATT)).equals("false") )
		isOwner=false;



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
            document.forms["add_entry"].PERMISSIONS.value += prm;
	else {
            var i = document.forms["add_entry"].PERMISSIONS.value.indexOf(prm);
            var lngth=o.value.length;
            document.forms["add_entry"].PERMISSIONS.value = document.forms["add_entry"].PERMISSIONS.value.substring(0,i) + document.forms["add_entry"].PERMISSIONS.value.substring(i+lngth);
    }
	
}

		function changeAcl() {
                    if ( document.forms["change_acl"].CHANGED.value=='true' && confirm("Save current ACL?"))
                                    openPage('<%=Names.ACL_SAVE_ACTION%>');
                    else {
                    
                            var c = document.forms["change_acl"].cboAcl;
                            document.forms["change_acl"].ACL.value = c.options[c.selectedIndex].value;
                            document.change_acl.submit();
                    }
		}

		function openParent(aclName) {
                    if ( document.forms["change_acl"].CHANGED.value=='true' && confirm("Save current ACL?"))
                        openPage('<%=Names.ACL_SAVE_ACTION%>');
                    else {
                        document.forms["change_acl"].ACL.value = aclName;
                        document.change_acl.submit();
                    }
                    //changeAcl();
		}
		function openPage(action) {
                    document.forms["change_acl"].ACTION.value=action;
                    document.change_acl.submit();
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
<div class="breadcrumbitem eionetaccronym"><a href="http://www.eionet.europa.eu">Eionet</a></div>
<div class="breadcrumbitem"><a href="javascript:openPage('C')">Applications</a></div>
<div class="breadcrumbitemlast"><%=thisAppName%></div>
<div class="breadcrumbtail"></div>
</div>

</div> <!-- pagehead -->
<%@ include file="globalnav.jsp" %>
<div id="workarea">

<div id="operations">
    <ul>
    <%
    if (grpEdit){ %>
    	<li><a href="javascript:openPage('<%=Names.SHOW_GROUPS_ACTION%>')">Edit groups</a></li><%
    }    
    if (isOwner){ %>
    	<li><a href="javascript:openPage('<%=Names.ACL_SAVE_ACTION%>')">Save ACL</a></li><%
    }
    %>
    </ul>
</div>
    <% if (err!= null) { %>
        <div id="errormessage">
            <h1>Error!</h1>
            <p><%=err%></p>
        </div>
    <% } %>

    <h1>Service Name: <%=thisAppName%></h1>
    <form name="change_acl" id="change_acl" action="main" method="post">
            <input type="hidden" name="ACL" value="<%=selAcl%>" />
            <input type="hidden" name="CHANGED" value="<%=changed%>" />
            <input type="hidden" name="ACTION" value="" />

    <table>
    <tr>
        <th>ACL Path</th><td><%=selAcl%></td>
    </tr>
    <% if (!aclDescr.equals("")) { %>
    <tr>
        <th>Description</th><td><%=aclDescr%></td>
    </tr>
    <% } %>
    <% if (parentAclName != null) { %>
    <tr>
        <th>Parent</th>
        <td><a href="javascript:openParent('<%=parentAclName%>')"><%=parentAclName%> (Traverse up)</a></td>
    </tr>
    <% } %>

    <% if (acls!=null && acls.size()>0 ) { %>
     <tr>
            <th>Children ACLs: </th>

            <td><select onchange="javascript:changeAcl()" name="cboAcl" style="width:140"><option value=""></option>
                    <% 
                    for (int i=0; i<acls.size(); i++) {
                    //for (Iterator i=acls.keySet().iterator(); i.hasNext();) {
                            String a=(String)acls.elementAt(i);
                            String sel = ( a.equals(selAcl) ? " selected='selected' " : "");
                    %>
                    <option value="<%=a%>" <%=sel%>><%=a%></option>
                    <% } %>
            </select>
	</td></tr>
    <% } %>
    </table>
    </form>
    
    <% if (!selAcl.equals("") ) { %>						
    <table width="100%" border="0" cellspacing="0">
        <tr>
            <th width="15%">ACL Type</th>
            <th width="10%">Type</th>
            <th width="20%">Subject</th>
            <th width="35%">Permissions</th>
            <th width="15%"></th>
        </tr>
        <% if (isOwner) { %>
        <tr valign="top">
            <form name="add_entry" action="main" method="post" >
                <td class="bordertop borderleft borderbottom" >
                    <select name="ACL_TYPE" style="width:120">
                        <option value="object">Object</option>
                        <option value="doc">DOC</option>
                        <option value="dcc">DCC</option>
                    </select>
                </td>
                <td class="bordertop borderleft borderbottom" >
                    <select name="TYPE" style="width:120">
                        <option value="user">User</option>
                        <option value="localgroup">Localgroup</option>
                        <option value="circarole">Circarole</option>
                        <option value="anonymous">Anonymous</option>
                        <option value="authenticated">Authenticated</option>
                        <option value="owner">Owner</option>
                    </select>
                </td>
                <td class="bordertop borderleft borderbottom" >
                    <input type="text" size="20" name="NAME" />
                </td>
                <td class="bordertop borderleft borderbottom" >
                        <input type="hidden" size="20" name="PERMISSIONS" />
                        <% for (Iterator i = permissions.keySet().iterator(); i.hasNext();) { 
                                 String name = (String)i.next();
                                 String prmDesc = (String)permissions.get(name);
                        %>
                            <span title="<%=prmDesc%>"><input onclick="cP(this)" type="checkbox" value="<%=name + ','%>" name="<%=name%>" /><%=name%></span>
                        <% }  %> 
                        &nbsp;
                </td>
                <td class="borderright bordertop borderleft borderbottom" >
                    <input type="submit" value="Add" title="Add entry to ACL"/>
                    <input type="hidden" name="ACTION" value="<%=Names.ACL_ADD_ACTION%>" />
                </td>
            </form>
        </tr><!-- add row -->
        <% } %>

                        <% for (int i=0; aclData!=null && i<aclData.size(); i++) {
                                Hashtable aclEntry=(Hashtable)aclData.elementAt(i);
                                String type=(String)aclEntry.get("type");
                                String id=(String)aclEntry.get("id");
                                String perms=(String)aclEntry.get("perms");
                                String aclType=(String)aclEntry.get("acltype");
                                perms=Util.getPrmDescrs(perms, permissions);

                                //quick hack
                                if (type.equals("user") && id.equals(aUser)) {
                                        type=aUser; id="";
                                }
                                if (type.equals("user") && id.equals(uUser)) {
                                        type=uUser; id="";
						}
						if ( type.equals("user") && id.equals("owner")) {
							type="owner"; id="";
						}


					%>
					<tr valign="top">
						<td class="borderleft borderbottom" ><%=aclType%></td>
						<td class="borderleft borderbottom" ><%=type%></td>
						<td class="borderleft borderbottom" ><%=id%>&nbsp;</td>
						<td class="borderleft borderbottom" ><%=perms%>&nbsp;</td>
                                                <td class="borderright borderleft borderbottom" >
							<table>
							<tr>
							<td>
							<% if (isOwner) { %>
							<form action="main" name="show_prm_<%=i%>">
                                                            <input type="hidden" name="ACTION" value="<%=Names.SHOW_PERMISSIONS_ACTION%>" />
                                                            <input type="hidden" name="NAME" value="<%=id%>" />
                                                            <input type="hidden" name="TYPE" value="<%=type%>" />
                                                            <input type="hidden" name="ACL_TYPE" value="<%=aclType%>" />
                                                            <img onclick="show_prm_<%=i%>.submit()" border="0" height="15" width="15" src="images/edit.png" alt="Modify permissions" />
										
							</form>
							</td><td>
							<form action="main" name="del_<%=i%>" method="post" >
                                                            <img onclick="del_<%=i%>.submit();" height="15" width="15" src="images/delete.png" alt="Delete entry from ACL" />
                                                            <input type="hidden" name="ACTION" value="<%=Names.ACL_DEL_ACTION%>" />
                                                            <input type="hidden" name="TYPE" value="<%=type%>" />	
                                                            <input type="hidden" name="NAME" value="<%=id%>" />
                                                            <input type="hidden" name="ACL_TYPE" value="<%=aclType%>" />
							</form>
							<% } %>
							</td>
							</tr>
							</table>
						</td>
					</tr>
					<% } %>
					</table>

					<!-- isOwner -->
    <% } %>
</div> <!-- workarea -->
</body>
</html>
