<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">

<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,
		eionet.acl.utils.Util, eionet.acl.Names" %>

<%
	//HashMap apps = (HashMap)session.getAttribute(Names.APPLICATIONS_ATT);

	//String thisAppName = (String)session.getAttribute(Names.APP_ATT);
	//String err = (String)request.getAttribute(Names.ERROR_ATT);
  //String ctx = request.getContextPath();

  //Hashtable permissions = (Hashtable) request.getAttribute("PRMS");
	//Hashtable groups = (Hashtable) request.getAttribute("GROUPS");

%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
  <title>Groups</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
  <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />

<script type="text/javascript">


function openPage(action) {
	document.forms["f"].ACTION.value=action;
	document.forms["f"].submit();
}


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
    <div class="breadcrumbitemlast">Groups</div>
    <div class="breadcrumbtail"></div>
  </div>
</div> <!-- pagehead -->

<%@ include file="globalnav.jsp" %>
<div id="workarea">
  <div id="operations">
    <ul>
      <li><a href="javascript:openPage('<%=Names.SAVE_GROUPS_ACTION%>')">Save groups</a></li>
    </ul>
  </div>
	<% if (err!= null) { %>
        <div id="errormessage">
            <h1>Error!</h1>
            <p><%=err%></p>
        </div>
	<% } %>
	<h1>Localgroups of application: <%=thisAppName%></h1>
            <form name="f" action="main" method="post">
                <input type="hidden" value="" name="ACTION"/>
            </form>

            <table width='630' cellspacing='7'>
              <tbody>
                <tr>
                  <th width='147'>Group</th>
                  <th>Members</th>
                </tr>
                <tr>
                  <td colspan="2">
                    <form name='add_group' action='main' method='post'>
                      <input onchange='fldValid(this)' name='NAME' type='text' />
                      <img width='38' alt='Add new localgroup' src='images/new.png' onclick='add_group.submit();' height='18' />
                      <input value='<%=Names.GROUP_ADD_ACTION%>' name='ACTION' type='hidden' />
                    </form>
                  </td>
                </tr>
		<!-- groups -->
			<%
				int ii=0;
				for (Iterator i=groups.keySet().iterator(); i.hasNext();) {
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
				ii++;
			%>
                <tr>
                <td>
                    <table><tr valign="top">
                    <td>
                      <form name='grp_<%=ii%>' action='main' method='post'>
                        <img width='15' alt='Delete localgroup' src='images/delete.png' onclick='grp_<%=ii%>.submit();' height='15'></img>
                        <input value='<%=Names.GROUP_DEL_ACTION%>' name='ACTION' type='hidden' />
                        <input value='<%=name%>' name='NAME' type='hidden' />
                      </form>
                    </td>
                    <td width="100"><%=name%></td>
                    <td>
                      <form name='e_grp_<%=ii%>' action='main' method='post'>
                        <img width='15' alt='Edit localgroup' src='images/edit.png' onclick='e_grp_<%=ii%>.submit();' height='15' />
                        <input value='<%=Names.SHOW_GRP_ACTION%>' name='ACTION' type='hidden' />
                        <input value='<%=name%>' name='NAME' type='hidden' />
                      </form>
                    </td>
                    </tr>
                    </table>

					</td>
					<td><%=sb.toString()%></td>
			</tr>
		<% }  %>

	</tbody></table>
</div> <!-- workarea -->
</body></html>
