<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator, eionet.acl.utils.Util, eionet.acl.Names" %>
<%

	Hashtable groups = (Hashtable) request.getAttribute(Names.GROUPS_PARAM_NAME);
	HashMap apps = (HashMap)session.getAttribute(Names.APPLICATIONS_ATT);

	String thisAppName = (String)session.getAttribute(Names.APP_ATT);
	String ctx = request.getContextPath();
	String err = (String)request.getAttribute(Names.ERROR_ATT);

	String aUser = "authenticated";
	String uUser = "anonymous";

	Hashtable permissions = (Hashtable) request.getAttribute(Names.PERMS_PARAM_NAME);

	boolean grpEdit=false;
	if (thisAppName!=null && thisAppName.length()>0 && groups!=null && groups.size()>0){
		grpEdit=true;
	}

%>

<script type="text/javascript" src="util.js"></script>

<div id="leftcolumn" class="localnav">
	<h2>Contents</h2>
	<ul>
		<li><a href="javascript:openPage('<%=Names.SHOW_APPS_ACTION%>')" title='Show Applications'>Applications </a></li>
		<li><a href="javascript:openPage('<%=Names.LOGOUT_ACTION%>')">Logout </a></li>
	</ul>
</div>
