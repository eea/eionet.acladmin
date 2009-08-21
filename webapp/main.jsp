<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
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
		            document.forms["add_entry"].elements["PERMISSIONS"].value += prm;
			else {
		            var i = document.forms["add_entry"].elements["PERMISSIONS"].value.indexOf(prm);
		            var lngth=o.value.length;
		            document.forms["add_entry"].elements["PERMISSIONS"].value = document.forms["add_entry"].elements["PERMISSIONS"].value.substring(0,i) + document.forms["add_entry"].elements["PERMISSIONS"].value.substring(i+lngth);
		    }
			
		}

		function changeAcl() {
			
			var c = document.forms["change_acl"].elements["cboAcl"];
            document.forms["change_acl"].elements["ACL"].value = c.options[c.selectedIndex].value;
            if (document.forms["change_acl"].elements["CHANGED"].value=='true' && confirm("Save current ACL?"))
				openPage('<%=Names.ACL_SAVE_ACTION%>');				
            else
                document.forms["change_acl"].submit();
		}

		function openParent(aclName) {
			
            document.forms["change_acl"].elements["ACL"].value = aclName;
            if (document.forms["change_acl"].elements["CHANGED"].value=='true' && confirm("Save current ACL?"))
                openPage('<%=Names.ACL_SAVE_ACTION%>');
            else
                document.forms["change_acl"].submit();
		}
		
		function openPage(action) {
            document.forms["change_acl"].elements["ACTION"].value=action;
            document.forms["change_acl"].submit();
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
if (selAcl!=null)
	breadcrumbs.add(selAcl.equals("/") ? "/root_level" : selAcl);
request.setAttribute("breadcrumbs", breadcrumbs);
%>
<jsp:include page="location.jsp" flush="true" />
<%@ include file="menu.jsp" %>
    
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
    <%
    if (err!= null){
	    %>
        <div id="errormessage">
            <h1>Error!</h1>
            <p><%=eionet.acl.utils.Util.replaceTags(err)%></p>
        </div><%
    }
    %>

    <h1>Service Name: <%=thisAppName%></h1>
    <br/>
    <form id="change_acl" action="main" method="post">
		<fieldset style="display:none">
		    <input type="hidden" name="ACL" value="<%=selAcl%>" />
		    <input type="hidden" name="CHANGED" value="<%=changed%>" />
		    <input type="hidden" name="ACTION" value=""/>
		</fieldset>
    	<table>
		    <tr>
		        <th style="text-align:right">ACL Path:</th>
		        <td><%=selAcl%></td>
		    </tr>
		    <%
		    if (!aclDescr.equals("")){ %>
			    <tr>
			        <th style="text-align:right">Description:</th><td><%=aclDescr%></td>
			    </tr><%
			}
		    if (parentAclName != null){ %>
			    <tr>
			        <th style="text-align:right">Parent:</th>
			        <td><a href="javascript:openParent('<%=parentAclName%>')"><%=parentAclName%> (Traverse up)</a></td>
			    </tr><%
			}	
		    if (acls!=null && acls.size()>0 ){ %>
			     <tr>
					<th style="text-align:right">Children ACLs:</th>				
					<td>
						<select onchange="javascript:changeAcl()" name="cboAcl" style="width:140">
							<option value=""></option>
							<% 
							for (int i=0; i<acls.size(); i++){
								String a=(String)acls.elementAt(i);
								String sel = ( a.equals(selAcl) ? " selected='selected' " : "");
								%>
								<option value="<%=a%>" <%=sel%>><%=a%></option><%
							}
							%>
						</select>
					</td>
				</tr><%
		    }
		    %>
    	</table>
    </form>
    
    <br/>
    
    <form id="add_entry" action="main" method="post">    
    	<%
    	if (!selAcl.equals("")){
	    	%>
    		<table width="100%" border="0" cellspacing="0">
						<col style="width:15%"/>
						<col style="width:10%"/>
						<col style="width:20%"/>
						<col style="width:35%"/>
						<col style="width:15%"/>
		        <tr>
		            <th>ACL Type</th>
		            <th>Type</th>
		            <th>Subject</th>
		            <th>Permissions</th>
		            <th></th>
		        </tr>        
        		<%
        		if (isOwner){
	        		%>
        			<tr valign="top">            
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
		                        <%
		                        for (Iterator i = permissions.keySet().iterator(); i.hasNext();){ 
	                                String name = (String)i.next();
	                                String prmDesc = (String)permissions.get(name);
		                        	%>
		                        	<input onclick="cP(this)" type="checkbox" value="<%=name + ','%>" name="<%=name%>" id="<%=name%>"/><label for="<%=name%>" title="<%=prmDesc%>"><%=name%></label><%
		                        }
		                        %>&nbsp;
		                </td>
		                <td class="borderright bordertop borderleft borderbottom" >
		                    <input type="submit" value="Add" title="Add entry to ACL"/>
		                    <input type="hidden" name="ACTION" value="<%=Names.ACL_ADD_ACTION%>" />
		                </td>
        			</tr><%
        		}

                for (int i=0; aclData!=null && i<aclData.size(); i++){
	                
                    Hashtable aclEntry=(Hashtable)aclData.elementAt(i);
                    String type=(String)aclEntry.get("type");
                    String id=(String)aclEntry.get("id");
                    String perms=(String)aclEntry.get("perms");
                    String aclType=(String)aclEntry.get("acltype");
                    perms=Util.getPrmDescrs(perms, permissions);

					//quick hack
                    if (type.equals("user") && id.equals(aUser)){
                        type=aUser;
                        id="";
                    }
                    if (type.equals("user") && id.equals(uUser)){
                        type=uUser;
                        id="";
                    }
					if (type.equals("user") && id.equals("owner")){
						type="owner";
						id="";
					}
					%>
					<tr valign="top">
						<td class="borderleft borderbottom" ><%=aclType%></td>
						<td class="borderleft borderbottom" ><%=type%></td>
						<td class="borderleft borderbottom" ><%=id%>&nbsp;</td>
						<td class="borderleft borderbottom" ><%=perms%>&nbsp;</td>
                        <td class="borderright borderleft borderbottom">
							<table>
								<tr>
									<td>
										<%
										if (isOwner){
											
											StringBuffer showLink = new StringBuffer("main?ACTION=");
											showLink.append(Names.SHOW_PERMISSIONS_ACTION).append("&amp;NAME=").append(id).append("&amp;TYPE=").append(type).append("&amp;ACL_TYPE=").append(aclType);
											
											StringBuffer delLink = new StringBuffer("main?ACTION=");
											delLink.append(Names.ACL_DEL_ACTION).append("&amp;NAME=").append(id).append("&amp;TYPE=").append(type).append("&amp;ACL_TYPE=").append(aclType);

											%>
											<a href="<%=showLink.toString()%>" title="Modify permissions">
												<img height="13" width="13" src="images/edit.gif" alt="Modify permissions"/>
											</a>
											</td>
											<td>
												<a href="<%=delLink.toString()%>" title="Delete entry from ACL" onclick="return confirm('Are you sure you want to delete this entry? The subject will lose the given permissions!');">
													<img height="13" width="13" src="images/delete.gif" alt="Delete entry from ACL"/>
												</a><%
										}
										%>
									</td>
								</tr>
							</table>
						</td>
					</tr><%
				} // end if (isOwner )
				%>
			</table><%
		} // end if if (!selAcl.equals(""))
		%>
	</form> <!-- id="add_entry" -->
	
</div> <!-- end workarea -->
</div> <!-- end container -->
<%@ include file="footer.jsp" %>
</body>
</html>
