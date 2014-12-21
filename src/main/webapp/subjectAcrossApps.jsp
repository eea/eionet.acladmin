<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@ page import="java.util.*" %>
<%@ page import="java.text.Collator" %>
<%@ page import="eionet.acladmin.*" %>
<%@ page import="eionet.acladmin.Names" %>

<%
	Hashtable resultHash = (Hashtable)request.getAttribute(Names.ATT_SEARCH_ACROSS_APPS_RESULT);
	String subjectType = request.getParameter(Names.PRM_SUBJECT_TYPE);
	String subjectID = request.getParameter(Names.PRM_SUBJECT_ID);
	if (subjectType==null) subjectType = "";
	if (subjectID==null) subjectID = "";
	
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<%@ include file="headerinfo.txt" %>
<title>User across applications</title>
<script type="text/javascript">
// <![CDATA[

function openPage(action) {
	document.forms["f"].ACTION.value=action;
	document.forms["f"].submit();
}
function openApp(appName) {
	document.forms["f"].app.value=appName;
	document.forms["f"].ACTION.value="";
	document.forms["f"].ACL.value="/";
	document.forms["f"].submit();
}
function doLogin(appName) {
	window.open("appLogin.jsp?app=" + appName,"login","height=200,width=460,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
}

// ]]>
</script>
</head>

<body>
<div id="container">
<%
Vector breadcrumbs = new Vector();
breadcrumbs.add("Applications|javascript:openPage('C')");
breadcrumbs.add("Search across applications");
request.setAttribute("breadcrumbs", breadcrumbs);
%>
<jsp:include page="location.jsp" flush="true" />
<%@ include file="menu.jsp" %>
<div id="workarea">

	<h1>Search across applications</h1>
	<table>
		<tr>
			<th>Subject type:</th>
			<td><%=subjectType%></td>
		</tr>
		<tr>
			<th>Subject ID:</th>
			<td><%=subjectID%></td>
		</tr>
	</table>
	<br/>

	<%
	if (resultHash==null || resultHash.size()==0){
		%>
		<strong>No results returned!</strong><%
	}
	else{
		
		Vector appNames = new Vector(resultHash.keySet());
		Collections.sort(appNames, Collator.getInstance());
		%>
		<table border="1" cellpadding="2">
			<tr style="background-color:#C0C0C0">
				<th>Application</th>
				<%
				if (subjectType.equals("user")){
					%>
					<th>Groups</th><%
				}
				%>
				<th>ACL</th>
				<th>Type</th>
				<th>Permissions</th>
			</tr>
			
			<%
			for (int i=0; i<appNames.size(); i++){
				
				int appDisplayed = 0;
			
				String appName = (String)appNames.get(i);
				Hashtable appHash = (Hashtable)resultHash.get(appName);
				if (appHash==null || appHash.size()==0)
					continue;
				
				HashSet groupsHash = (HashSet)appHash.get("groups");
				Hashtable aclsHash = (Hashtable)appHash.get("acls");
				if (subjectType.equals("localgroup")){
					if (aclsHash==null || aclsHash.size()==0)
						continue;
				}
				else if ((aclsHash==null || aclsHash.size()==0) && (groupsHash==null || groupsHash.size()==0))
					continue;
					
				Integer o = (Integer)appHash.get("total_acl_entries");
				int totalAclEntries = o==null ? 0 : o.intValue();
				String strTotalAclEntries = String.valueOf(totalAclEntries);
				
				// if subject type is 'user' and no acls found, we must create a dummy empty acl nevertheless,
				// because otherwise the table cannot be displayed correctly, because we must
				// display the groups anyway
				if ((aclsHash==null || aclsHash.size()==0) && subjectType.equals("user")){
					aclsHash = new Hashtable();
					Hashtable h = new Hashtable();
					h.put("acltype", "");
					h.put("permissions", "");
					Vector vec = new Vector();
					vec.add(h);
					aclsHash.put("", vec);
					totalAclEntries = 1;
					strTotalAclEntries = "1";
				}
								
				Vector aclNames = new Vector(aclsHash.keySet());
				Collections.sort(aclNames, Collator.getInstance());
				for (int j=0; j<aclNames.size(); j++){
					
					int aclDisplayed = 0;
					
					String aclName = (String)aclNames.get(j);
					Vector aclEntries = (Vector)aclsHash.get(aclName);
					if (aclEntries==null || aclEntries.size()==0)
						continue;
					
					String strEntriesSize = String.valueOf(aclEntries.size());
					for (int k=0; k<aclEntries.size(); k++){
						
						Hashtable aclEntry = (Hashtable)aclEntries.get(k);
						String aclType = (String)aclEntry.get("acltype");
						String perms = (String)aclEntry.get("permissions");
						%>
						<tr>
							<%
							if (appDisplayed<1){
								%>
								<td valign="top" rowspan="<%=strTotalAclEntries%>"><strong><%=appName%></strong></td>
								<%
								if (subjectType.equals("user")){
									%>
									<td rowspan="<%=strTotalAclEntries%>">
										<%
										if (groupsHash==null || groupsHash.size()==0){
											%>&nbsp;<%
										}
										else{
											Vector v = new Vector(groupsHash);
											Collections.sort(v, Collator.getInstance());
											for (int g=0; g<v.size(); g++){
												String groupName = (String)v.get(g);
												%>
												<a href="main?ACTION=search_across_apps&amp;subject_type=localgroup&amp;subject_id=<%=groupName%>"><%=groupName%></a>
												<%
												if (g<(v.size()-1)){
													%><br/><%
												}
											}
										}
										%>
									</td><%
								}
								appDisplayed++;
							}
							
							if (aclDisplayed<1){
								%>
								<td rowspan="<%=strEntriesSize%>"><%=aclName%></td>
								<%
								aclDisplayed++;
							}
							%>
							<td><%=aclType%></td>
							<td><%=perms%></td>
						</tr>
						<%						
					}
				}
			}

			%>
				
		</table>
		<%
	}
	%>
	</div> <!-- workarea ends here -->
	
	<form id="f" action="main" method="post">
		<fieldset style="display:none">
			<input type="hidden" name="app" value="" />
			<input type="hidden" name="ACTION" value="" />
			<input type="hidden" name="ACL" value="" />
		</fieldset>
	</form>
</div> <!-- container -->
<%@ include file="footer.jsp" %>
</body>
</html>
