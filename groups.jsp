<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator, eionet.acl.utils.Util, eionet.acl.Names" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<%@ include file="headerinfo.txt" %>
	<title>Groups</title>
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
breadcrumbs.add("Groups");
request.setAttribute("breadcrumbs", breadcrumbs);
%>
<jsp:include page="location.jsp" flush="true" />
<%@ include file="menu.jsp" %>

<div id="workarea">

	<div id="operations">
		<ul>
			<li><a href="javascript:openPage('<%=Names.SAVE_GROUPS_ACTION%>')">Save groups</a></li>
		</ul>
	</div>
	<%
	if (err!= null){ %>
        <div id="errormessage">
            <h1>Error!</h1>
            <p><%=err%></p>
        </div><%
    }
    %>
	<h1>Localgroups of application: <%=thisAppName%></h1>
	
    <form id="f" action="main" method="post">
    	<fieldset style="display:none">
        	<input type="hidden" value="" name="ACTION"/>
        </fieldset>
    </form>
	<br/>
	<table width="630" cellspacing="7">
		<tbody>
			<tr>
				<th style="width:147">Group</th>
				<th>Members</th>
			</tr>
            <tr>
				<td colspan="2">
					<form id="add_group" action="main" method="post">
						<div>
							<input type="text" name="NAME" onchange="fldValid(this)" />
							<input type="submit" value="Add" title="Add new localgroup"/>
						</div>
						<fieldset style="display:none">
							<input type="hidden" name="ACTION" value="<%=Names.GROUP_ADD_ACTION%>" />
						</fieldset>
					</form>
                 </td>
            </tr>
            
			<!-- groups -->
			<%
			int ii=0;
			Iterator i = groups!=null ? groups.keySet().iterator() : null;
			while (i!=null && i.hasNext()) {
				String name=(String)i.next();
				Vector members = (Vector)groups.get(name);
		
				StringBuffer sb = new StringBuffer();
				int iii=0;

			 if (members != null)
					for (Iterator ix = members.iterator(); ix.hasNext();) {
						String member =(String)ix.next();
						sb.append(member);
						iii++;
						if (iii < members.size() )
							sb.append(", ");
			}
			
			StringBuffer delLink = new StringBuffer("main?ACTION=");
			delLink.append(Names.GROUP_DEL_ACTION).append("&amp;NAME=").append(name);
			
			StringBuffer editLink = new StringBuffer("main?ACTION=");
			editLink.append(Names.SHOW_GRP_ACTION).append("&amp;NAME=").append(name);
			
			ii++;
			%>
			<tr>
				<td>
                    <table>
                    	<tr valign="top">
		                    <td>
		                    	<a href="<%=delLink.toString()%>" title="Delete localgroup">
		                        	<img alt="Delete localgroup" src="images/delete.gif" height="13" width="13" />
		                        </a>
		                    </td>
		                    <td style="width:100">
		                    	<%=name%>
		                    </td>
		                    <td>
		                    	<a href="<%=editLink.toString()%>" title="Edit localgroup">
		                        	<img alt="Edit localgroup" src="images/edit.gif" height="13" width="13" />
		                        </a>
		                    </td>
                    	</tr>
                    </table>

				</td>
				<td>
					<%=sb.toString()%>
				</td>
			</tr>
		<% }  %>

	</tbody></table>
</div> <!-- workarea -->
</div> <!-- container -->
<%@ include file="footer.jsp" %>
</body>
</html>
