/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-5 / UIT project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.acl.servlets;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Vector;

import com.tee.uit.client.*;
import java.util.Hashtable;
import javax.servlet.http.HttpSession;

import java.util.Iterator;
import java.util.HashMap;
import java.util.StringTokenizer;

import eionet.directory.DirectoryService;
import eionet.acl.AcrossApps;
import eionet.acl.Names;
import eionet.directory.DirServiceException;
import com.tee.uit.security.AppUser;
import com.tee.uit.security.AccessControlListIF;
import com.tee.uit.security.AccessController;
import eionet.acl.utils.Util;
import com.tee.uit.security.SignOnException;

/**
 * Base servlet for all servlets in Acl Admin Tool
 */
public abstract class BaseAC extends HttpServlet {
	
	private ResourceBundle props;
	
	protected HashMap apps;
	protected HashMap appClients; //holds ServiceClients (RPC clients )
	
	protected final String ACL_readPermission = "r";
	protected final String ACL_appAclName = "/";
	protected String authUser;
	protected String unauthUser;
	protected HttpSession session;
	
	protected HashMap acls;
	
	/*protected boolean xisLogged(HttpServletRequest req) {
	 boolean ok = false;
	 session = getSession(req);
	 if ( session!= null && session.getAttribute( Names.USER_ATT ) != null ) {
	 ok = true;
	 } 
	 return ok;
	 } */
	
	/**
	 * returns the current Http session
	 */
	protected HttpSession getSession(HttpServletRequest req ) {
		session = (HttpSession)req.getAttribute(Names.SESS_ATT);
		return session;
	}
	
	/**
	 * Returns property value as STRING from the acladmin.properties file
	 */
	protected String getProperty(String property) throws Exception {
		if (props == null)
			try {
				props = ResourceBundle.getBundle("acladmin");
			} catch (MissingResourceException mre) {
				throw new Exception("Properties file acladmin.properties is not existing in the classpath");
			}
			return props.getString(property);
	}
	
	/** 
	 * doGet() 
	 */
	public abstract void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException ;
	
	/**
	 * dpPost()
	 */
	public abstract void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException;
	
	/**
	 * Login to AclAdmin
	 */
	protected void doLogin(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		String u = req.getParameter("j_username");
		String p = req.getParameter("j_passwd");
		
		//here we set session as a request attribute    
		HttpSession session = req.getSession();
		
		
		try {
			//DirectoryService.sessionLogin(u, p);
			AppUser aclUser = new AppUser();
			
			if(!Util.isNullStr(u))
				aclUser.authenticate(u,p);
			
			//Check, if the user has permission to use the AclAdmin Tool
			//acls = AccessController.getAcls();   
			
			//AccessControlListIF aclAdminAcl = (AccessControlListIF)acls.get(ACL_appAclName);
			
			//boolean isAllowed=false;
			
			
			/*
			 if (aclAdminAcl != null)
			 isAllowed = aclAdminAcl.checkPermission(u,ACL_readPermission );
			 else        
			 throw new ServletException("No root Acl found: " + ACL_appAclName);
			 */
			if ( !isAllowed(u))
				throw new ServletException("Not allowed to use the Acl Admin tool");
			
			
			session.setAttribute(Names.USER_ATT, aclUser);        
			
			if ( apps == null) {
				String appNames = null;
				try {
					appNames = getProperty("applications");
				} catch (MissingResourceException me ) {
					throw new Exception("AppNames property does not exist in acladmin.properties");
				}
				if (Util.isNullStr(appNames))
					throw new Exception("AppNames property is empty in acladmin.properties");
				
				parseApps(appNames);
			}
			//l(" APPS OK ");
			req.setAttribute(Names.SESS_ATT, session); 
			session.setAttribute(Names.APPLICATIONS_ATT, apps); 
			session.setAttribute(Names.APPCLIENTS_ATT, new HashMap());  //clients remains empty at init phase
			
		} catch (Exception dire ){
			
			session.setAttribute(Names.USER_ATT, null);    
			session.setAttribute(Names.APPLICATIONS_ATT, null);
			req.setAttribute(Names.SESS_ATT, null);
			
			//handleError(req, res,"Authentication failed " + dire.toString(), Names.ERROR_ACTION);
			throw new ServletException("Authentication failed " + dire.toString());
			//l("=================== 1");      
			//return;
		}
		
	}
	
	
	/**
	 * handle error and direct to the correct JSP
	 */
	protected void handleError(HttpServletRequest req, HttpServletResponse res, String errMsg, String action) throws ServletException, IOException  {
		
		req.setAttribute(Names.ERROR_ATT, errMsg);
		String jspName = Names.LOGIN_JSP;  //default
		
		if (action.equals(Names.LOGIN_ACTION))
			jspName = Names.LOGIN_JSP;
		else if (action.equals(Names.SHOW_APPS_ACTION))
			jspName = Names.INDEX_JSP; 
		else if (action.equals("")) {
			jspName = Names.MAIN_JSP;
		}
		else if (action.equals(Names.ACT_SEARCH_ACROSS_APPS)){
			jspName = Names.INDEX_JSP;
		}
		
		req.getRequestDispatcher(jspName).forward(req,res);
		
	}
	
	/**
	 * check, if session exists and user is logged in 
	 */
	protected void  guard(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			HttpSession session = getSession(req);  
			
			if ( session==null)
				throw new SecurityException ("No session ");
			
			AppUser aclUser = (AppUser)session.getAttribute(Names.USER_ATT);
			
			if (aclUser == null)
				throw new SecurityException ("No user ");
			else if (aclUser.getUserName() == null)
				throw new SecurityException ("No user name ");      
			
		} catch (SecurityException e ) {
			if (session!=null) {
				session.setAttribute(Names.USER_ATT, null);
				session.setAttribute(Names.APPLICATIONS_ATT, null);
				session.setAttribute(Names.APPCLIENTS_ATT, null);
			}
			handleError(req, res, "No session", Names.LOGIN_ACTION);
			return;
			
		}
		//l(" GUARD passed successfully");    
	}
	
	
	/**
	 * parses applications property and puts application names + rpc router URL insto HashMap
	 */
	private void parseApps(String appNames) throws Exception {
		
		StringTokenizer st = new StringTokenizer(appNames, ",");
		apps = new HashMap();
		
		while (st.hasMoreTokens())  {
			String appName = st.nextToken();
			String appUrl = getProperty(appName + ".router.url");
			
			//!!! hard coded for a while...
			authUser = "authenticated"; //getProperty(appName + ".authenticated.access");
			unauthUser = "anonymous"; //getProperty(appName + ".anonymous.access");
			HashMap appData = new HashMap();
			
			appData.put("url", appUrl);
			appData.put("authUser", authUser);
			appData.put("unauthUser", unauthUser);      
			//apps.put(appName, appUrl);
			apps.put(appName, appData);      
		}
	}  
	
	protected static void l(String s ) {
		System.out.println("========================================================================");
		System.out.println(s);
		System.out.println("========================================================================");    
	}
	
	/**
	 * login to the remote application
	 */
	protected void doAppLogin(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		String appName = req.getParameter("app");
		
		HttpSession session = req.getSession();
		HashMap apps = (HashMap)session.getAttribute(Names.APPLICATIONS_ATT);
		appClients = (HashMap)session.getAttribute(Names.APPCLIENTS_ATT);
		String appUrl  = (String) ((HashMap)apps.get(appName)).get("url");
		
		String u = req.getParameter("app_user");
		String p = req.getParameter("app_pwd");
		
		//l("doApplogin, user=" + u);
		
		try {    
			ServiceClientIF appClient = ServiceClients.getServiceClient(Names.RPC_SERVICE_NAME, appUrl);
			
			if (!Util.isNullStr(u))
				appClient.setCredentials(u,p);
			
			//if authentication succeeded, rpcClient is put to hash, so there is no need to login later 
			//if the session is alive
			appClients.put(appName, appClient);
			//l("put appClients=" + appName);      
			
		} catch (Exception e ) {
			throw new ServletException("Error initializing service client for " + appName + " " + e.toString());
		}
		
		req.setAttribute(Names.SESS_ATT, session);
		printPage(res, "<html><script type=\"text/javascript\">window.opener.location='main?app=" +
				appName + "';window.close()</script></html>");
	}
	
	/**
	 * Prints the given page (text/html/xml) to the <CODE>HttpServletResponse</CODE> writer.
	 */
	protected void printPage(HttpServletResponse res, String page) {
		res.setContentType("text/html");
		try {
			java.io.PrintWriter out = res.getWriter();
			out.print(page);
			out.close();   
		} catch (IOException e) {
			System.out.println("Writing page to response stream failed");
		}
	}
	
	/**
	 * 
	 * @param userName
	 * @return
	 * @throws SignOnException 
	 */
	protected boolean isAllowed(String userName) throws SignOnException{
		
		if (acls==null)
			acls = AccessController.getAcls();   
		
		AccessControlListIF rootAcl = (AccessControlListIF)acls.get(ACL_appAclName);
		
		// to use AclAdmin the user has to have read permission in root ACL (but only if the latter is present)
		if (rootAcl!=null)
			return rootAcl.checkPermission(userName, ACL_readPermission );
		else
			return true;
	}
	
	/**
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void searchAcrossApps(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		// just make sure there is no previous result in request,
		// even though I cannot see a way it can get in there
		req.removeAttribute(Names.ATT_SEARCH_ACROSS_APPS_RESULT);
		
		String subjectID = req.getParameter(Names.PRM_SUBJECT_ID);
		String subjectType = req.getParameter(Names.PRM_SUBJECT_TYPE);
		if (Util.isNullStr(subjectType) || Util.isNullStr(subjectID))
			throw new Exception("Subject type or ID not supplied");
		
		if (!subjectType.equals("user") && !subjectType.equals("localgroup"))
			throw new Exception("Unsupported subject type: " + subjectType);
		
		HttpSession session = req.getSession();
		if (session==null)
			throw new Exception("No session found in request");
		
		AcrossApps acrossApps = (AcrossApps)session.getAttribute(Names.ATT_ACROSS_APPS);
		if (acrossApps==null){
			String usr = req.getParameter(Names.PRM_ACROSS_APPS_USERNAME);
			String psw = req.getParameter(Names.PRM_ACROSS_APPS_PASSWORD);
			if (Util.isNullStr(usr) || Util.isNullStr(psw))
				throw new Exception("Username or password not supplied");
			
			acrossApps = new AcrossApps(apps, usr, psw);
			try{
				acrossApps.init();
			}
			catch (ServiceClientException sce){
				String errApp = acrossApps.getCurInitApp();
				if (errApp!=null) req.setAttribute(Names.ATT_ERR_APP, errApp);
				throw sce;
			}
			
			session.setAttribute(Names.ATT_ACROSS_APPS, acrossApps);
		}
		else if (req.getParameter(Names.PRM_RELOAD_ACROSS_APPS)!=null){ // reload
			try{
				acrossApps.reload();
			}
			catch (ServiceClientException sce){
				String errApp = acrossApps.getCurInitApp();
				if (errApp!=null) req.setAttribute(Names.ATT_ERR_APP, errApp);
				throw sce;
			}
			
			session.setAttribute(Names.ATT_ACROSS_APPS, acrossApps);
		}
		
		Hashtable resultHash = acrossApps.getSubjectEntries(subjectID, subjectType);
		if (resultHash!=null && resultHash.size()>0)
			req.setAttribute(Names.ATT_SEARCH_ACROSS_APPS_RESULT, resultHash);
		
		// dispatch
		String jspName = Names.JSP_SUBJECT_ACROSS_APPS;
		RequestDispatcher dispatcher = req.getRequestDispatcher(jspName);
		if (dispatcher==null)
			throw new Exception("getRequestDispatcher(" + jspName + ") returned null");
		
		dispatcher.forward(req,res);
	}
}