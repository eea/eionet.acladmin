<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, java.util.Iterator,
		eionet.acl.utils.Util, eionet.acl.Names" %>
<%

	Hashtable groups = (Hashtable) request.getAttribute(Names.GROUPS_PARAM_NAME);
	HashMap apps = (HashMap)session.getAttribute(Names.APPLICATIONS_ATT);


	String thisAppName = (String)session.getAttribute(Names.APP_ATT);
  String ctx = request.getContextPath();
	String err = (String)request.getAttribute(Names.ERROR_ATT);

	String aUser = "authenticated" ; // (String)((HashMap)apps.get(thisAppName)).get("authUser");
	String uUser = "anonymous" ;// (String)((HashMap)apps.get(thisAppName)).get("unauthUser");

	Hashtable permissions = (Hashtable) request.getAttribute(Names.PERMS_PARAM_NAME);

	boolean grpEdit=false;

	if ( groups!= null && groups.keySet().iterator().hasNext()) {
			grpEdit=true;
	}

%>

<script type="text/javascript" src="util.js"></script>
