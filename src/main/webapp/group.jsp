<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator, eionet.acl.utils.Util, eionet.acl.Names" %>

<%
	String aclName = request.getParameter("NAME");
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
</head>
<body>

<div id="container">
	<%
	Vector breadcrumbs = new Vector();
	breadcrumbs.add("Applications|javascript:openPage('C')");
	if (session.getAttribute(Names.APP_ATT)!=null)
		breadcrumbs.add(session.getAttribute(Names.APP_ATT) + "|main?action=&amp;ACL=/");
	breadcrumbs.add("Groups|javascript:openPage('Y')");
	breadcrumbs.add(aclName==null ? "?" : aclName);
    request.setAttribute("breadcrumbs", breadcrumbs);
	%>
    <jsp:include page="location.jsp" flush="true" />
    <%@ include file="menu.jsp" %>
<div id="workarea">
  <div id="operations">
    <ul>
      <li><a href="javascript:openPage('<%=Names.SHOW_GROUPS_ACTION%>')">OK</a></li>
    </ul>
  </div>
	<% if (err!= null) { %>
        <div id="errormessage">
            <h1>Error!</h1>
            <p><%=eionet.acl.utils.Util.replaceTags(err)%></p>
        </div>
	<% } %>

  <h1>Localgroup Name: <%=aclName%></h1>
  <br/>
  <table cellspacing="7" border="0">
  <tbody>
  <tr>
    <th>Member</th>
    <th>Delete</th>
  </tr>
  <tr valign="top">
    <td>
      <form id="add_member" action="main" method="post">
      	<div>
	        <input name="MEMBER" type="text" />&nbsp;
	        <input type="submit" value="Add" title="Add user to localgroup"/>
	        <input value="<%=Names.MEMBER_ADD_ACTION%>" name="ACTION" type="hidden" />
	        <input value="<%=aclName%>" name="NAME" type="hidden" />
	    </div>
      </form>
    </td>
    <td></td>
  </tr>
  
	<!-- members -->
      
	<% 
	Vector members = (Vector)groups.get(aclName);
	for (int i=0; members!=null && i<members.size(); i++){
	  
		String name = (String)members.get(i);
		String fontColour = name.equals(aUser) || name.equals(uUser) ? "#0000EE" : "#646666";
		
		StringBuffer delLink = new StringBuffer("main?ACTION=");
		delLink.append(Names.MEMBER_DEL_ACTION).append("&amp;MEMBER=").append(name).append("&amp;NAME=").append(aclName);
		%>
		<tr>
			<td style="color:<%=fontColour%>">
				<%=name%>
			</td>
			<td>
				<a href="<%=delLink.toString()%>" title="Delete member from localgroup" onclick="return confirm('Are you sure you want to remove this member?');">
					<img src="images/delete.gif" alt="Delete member from localgroup" height="13" width="13" />
				</a>
			</td>		
		</tr><%
	}
	%> 

  </tbody>
  </table>

  <form id="f" method="post" action="main">
  	<fieldset style="display:none">
    	<input type="hidden" name = "ACTION" value ="" />
    </fieldset>
  </form>
</div> <!-- workarea -->
</div> <!-- container -->
<%@ include file="footer.jsp" %>
</body>
</html>
