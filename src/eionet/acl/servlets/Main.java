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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Vector;

import eionet.acl.AcrossApps;
import eionet.acl.Names;

import com.tee.uit.client.*;

import java.util.Hashtable;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.HashMap;
import eionet.directory.DirectoryService;
import eionet.acl.utils.Util;
//import com.tee.uit.security.AppUser;
import java.security.acl.NotOwnerException;
import java.lang.reflect.InvocationTargetException;
import com.tee.uit.security.SignOnException;

/**
 * 
 */
public class Main extends BaseAC implements Names {

	/** */
	private Hashtable selectedAclPermissions = null;
	private Hashtable selectedAppGroups = null;
	private Hashtable aclManagerMetadata = null;   
	private HashMap selectedAppAcls = null;   

	/** */
	private String selectedAclOwner = null;
	
	/** */
	private String selectedAppName = "";
	private String selectedAclName = "";
	
	/** */
	private boolean isSessionUserChanged = false;
	
	/** */
	private Vector selectedAclRows = null;
	
	/** */
	private Hashtable selectedAclObject = null;
	private Vector selectedAclChildrenAcls = null;
	
	/**
	 * 
	 */
	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {  
		
		String action=req.getParameter("ACTION");
		action = (action == null ? "" : action);
		HttpSession sess = req.getSession();
		
		//l(" ======== ACTION===== " + action);
		//if logout, clear all parameters    
		if (action.equals(LOGOUT_ACTION)) {
			doLogout(req);
			isSessionUserChanged=true;
			action="";
		}
		
		//if login is going on, no user needed
		if (action.equals(LOGIN_ACTION)) {
			try {
				doLogin(req, res);
			} catch (Exception e ) {
				//l ("exception in login");      
				handleError(req,res, "Error: " + e.toString(), LOGIN_ACTION );
				return;
			}
			//redirect to show applications
			action=SHOW_APPS_ACTION;
		}
		
		//check if session exist, if not redirect to login page
		if ( !guard(sess)) {
			if (isAllowed(null)) {
				//l("doLogin");        
				doLogin(req,res);
				//l("Login ok");                  
				action=SHOW_APPS_ACTION;
			}
			else {
				handleError(req,res, "No session", LOGIN_ACTION );
				return;
			}
		}    
		
		//login to remote application    
		//redirect to the loggged in application
		if (action.equals(APPLOGIN_ACTION)) {
			doAppLogin(req, res);
			action=SHOW_APP_ACTION;
			return;
		}
		
		// search across applications
		if (action.equals(Names.ACT_SEARCH_ACROSS_APPS)){
			try{
				req.removeAttribute(Names.ATT_ERR_APP);
				searchAcrossApps(req, res);
				return;
			}
			catch (Exception e){
				String errMessage = e.getMessage();
				if (errMessage==null) errMessage = e.toString();
				e.printStackTrace(System.out);
				if (e instanceof ServiceClientException){
					String errApp = (String)req.getAttribute(Names.ATT_ERR_APP);    		
					System.out.println("application=" + errApp);
					if (errMessage!=null && errApp!=null)
						errMessage = errMessage + " (application=" + errApp + ")";
				}
				
				handleError(req, res, errMessage, action);
				return;
			}
		}
		
		String requestedAppName = null;
		String requestedAclName = null;
		
		//new app selected
		if (action.equals("")) {
			try {
				requestedAppName = req.getParameter("app");
				if (requestedAppName==null)
					requestedAppName = (String)sess.getAttribute(APP_ATT);
				
				requestedAclName = req.getParameter("ACL");        
				if (requestedAclName==null)       
					requestedAclName = (String)sess.getAttribute(ACL_ATT);
				requestedAclName = requestedAclName==null ? "/" : requestedAclName;
				
				sess.setAttribute(APP_ATT, requestedAppName);
				sess.setAttribute(ACL_ATT, requestedAclName);                
				
				//put useful data to HttpSession        
				initAclData(sess, requestedAppName, requestedAclName);
			} catch (Exception e ) {
				e.printStackTrace(System.out);
				handleError(req,res, "Error getting ACL data from Application " + selectedAppName + " " + e.toString() , SHOW_APPS_ACTION);          
				return;
			}
		}
		
		//now we have to have session, if not re-direct to login
		if (sess==null) {
			handleError(req, res, "No session", LOGIN_ACTION);
			return;
		}
		
		//put all data, got from remote app as HttpRequest attributes
		req.setAttribute(ACL_DATA_ATT, selectedAclRows);
		req.setAttribute(ACL_CHILDREN_ATT, selectedAclChildrenAcls);
		req.setAttribute(ACL_INFO_ATT, selectedAclObject);
		req.setAttribute(GROUPS_PARAM_NAME, selectedAppGroups);
		req.setAttribute(PERMS_PARAM_NAME, selectedAclPermissions);
		
		//user is not the owner of selected ACL    
		req.setAttribute(NOTOWNER_ATT, selectedAclOwner);  
		
		//l("SETTING  = " + owner);      
		
		//HttpSession needed as request attribtue as well
		req.setAttribute(SESS_ATT, sess);
		
		//redirect to correct JSP
		dispatch(req,res,action);
	}
	
	/**
	 * doPost()
	 */
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		doGet(req, res);
	}
	
	
	private void initAclData(HttpSession session, String requestedAppName, String requestedAclName) throws Exception {
		
		//init only, if something changed=user has selected a nwe application or ACL to modify
		//selAcl=previously selected ACL, selApp = previously selected app
		if ((requestedAppName!=null && !(selectedAclName.equals(requestedAclName) && selectedAppName.equals(requestedAppName))) || isSessionUserChanged )
			try {
				HashMap apps = (HashMap)session.getAttribute(Names.APPLICATIONS_ATT);
				//remote XML/RPC clients data in appClients Hash
				HashMap appClients = (HashMap)session.getAttribute(Names.APPCLIENTS_ATT);
				
				//the application, edited at the moment
				HashMap app = (HashMap)apps.get(requestedAppName);
				
				//entry values for auth and anonymous users -> will be changed
				authUser = (String)app.get("authUser");
				unauthUser = (String)app.get("unauthUser");      
				
				ServiceClientIF srv = (ServiceClientIF)appClients.get(requestedAppName);
				Vector prms = new Vector();
				
				if (! selectedAppName.equals(requestedAppName) || isSessionUserChanged) {
					//new application selected, start from root ACL
					requestedAclName = "/";
					session.setAttribute(ACL_ATT, requestedAclName);
					
					prms.removeAllElements();
					
					//l("Here we go!");
					
					//get groups from remote application
					selectedAppGroups = (Hashtable)srv.getValue("getLocalGroups", prms);
					//l("groups from " + selApp + "  "  + groups);
					
					//aclManagerInfo
					aclManagerMetadata= (Hashtable)srv.getValue("getAclManagerInfo", prms);        
					
					//String flagName=(String)aclAdminData.get("flags");
					//permission flags
					//a bit hard-coded... will be more flags collections in the future
					//permissions = (Hashtable)((Hashtable)aclAdminData.get("flags")).get(flagName);
				}
				
				prms.removeAllElements();
				prms.add(requestedAclName);
				
				//get list of children ACLs of the selected ACL
				selectedAclChildrenAcls=(Vector)srv.getValue("getChildrenAcls", prms);    
				
				selectedAclName = requestedAclName;
				//owner=true;      
				try {
					//get ACL info from remote service
					selectedAclObject = (Hashtable)srv.getValue("getAclInfo", prms);    
					if (selectedAclObject!=null){
						selectedAclRows = (Vector)selectedAclObject.get("entries");	          
						selectedAclOwner= (String)selectedAclObject.get("owner");
						
						String flagName=(String)selectedAclObject.get("flags");
						Hashtable flags = (Hashtable)aclManagerMetadata.get("flags");
						if (flags!=null)
							selectedAclPermissions = (Hashtable)flags.get(flagName);
					}
				}
				catch (Exception e ) {
					e.printStackTrace(System.out);
					throw new ServiceClientException(e, e.toString());
				}
				
				selectedAppName = requestedAppName;
				
			} catch (ServiceClientException se ) {
				se.printStackTrace(System.out);
				if ( se.toString().indexOf("AuthenticationException") != -1) {
					//authentication failed in the remote server
					appClients.remove(requestedAppName);
					throw new ServiceClientException(se, "Authentication in the remote application failed");
				}
				throw new Exception ("Error getting ACL data from the remote service " +  requestedAppName + 
						" " + se.toString());
			}
			
			isSessionUserChanged=false;
	}
	
	/**
	 * redirect to correct JSP
	 */
	private void dispatch(HttpServletRequest req, HttpServletResponse res, String action) throws ServletException, IOException  {
		
		Object appNameAttr = null;
		if (req.getSession()!=null)
			appNameAttr = req.getSession().getAttribute(APP_ATT);
		
		String jspName = appNameAttr==null ? INDEX_JSP : MAIN_JSP;
		
		if (action.equals (SAVE_PERMS_ACTION))
			SaveHandler.savePermissions(req);
		else   if (action.equals (MEMBER_ADD_ACTION) || action.equals (MEMBER_DEL_ACTION) ||
				action.equals (GROUP_ADD_ACTION) || action.equals (GROUP_DEL_ACTION)) {
			SaveHandler.handleGroups(req,action);
			if (action.equals (MEMBER_ADD_ACTION) || action.equals (MEMBER_DEL_ACTION))          
				action=SHOW_GRP_ACTION;
			else
				action=SHOW_GROUPS_ACTION;        
		}
		else if (action.equals (ACL_ADD_ACTION) || action.equals(ACL_DEL_ACTION) ) {
			SaveHandler.handleAclEntry(req, action);
			action = "";
		}
		else if (action.equals(SAVE_GROUPS_ACTION) || action.equals(ACL_SAVE_ACTION)) {
			try {
				SaveHandler.callRemoteMethod(req,action );
			} catch (Exception e ){
				req.setAttribute(Names.ERROR_ATT, "Error saving data: " + e.getMessage());         
				//throw new ServletException("Error calling remote method " + e.toString(), e);
			}
			action = "";
		}
		
		
		if ( action.equals( SHOW_APPS_ACTION ))
			jspName= "index.jsp";
		else       if ( action.equals( SHOW_APP_ACTION ))
			jspName= "main.jsp";
		else  if ( action.equals( SHOW_GROUPS_ACTION ))
			jspName= "groups.jsp";
		else  if ( action.equals( SHOW_GRP_ACTION) )
			jspName= "group.jsp";
		else  if ( action.equals( SHOW_PERMISSIONS_ACTION) )
			jspName= "permissions.jsp";
		
		req.setAttribute(SESS_ATT, req.getSession());
		req.getRequestDispatcher(jspName).forward(req,res);
		
	}
	
	private void doLogout(HttpServletRequest req) {
		
		selectedAppGroups=null;
		selectedAclPermissions=null;
		
		if (appClients != null)      
			appClients.clear();
		
		HttpSession session = req.getSession();
		session.removeAttribute(USER_ATT);
		session.removeAttribute(APPCLIENTS_ATT);
		session.removeAttribute(ATT_ACROSS_APPS);
		
		req.removeAttribute(GROUPS_PARAM_NAME);
		req.removeAttribute(PERMS_PARAM_NAME);
		req.removeAttribute(SESS_ATT);
		
	}
	
	private boolean guard(HttpSession sess)  {
		if ( sess.getAttribute(USER_ATT)==null)
			return false;
		else
			return true;
	}
}