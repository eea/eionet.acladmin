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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Hashtable;
import eionet.acl.utils.Util;
import java.util.Vector;
import eionet.acl.Names;
import javax.servlet.http.HttpSession;

import com.tee.uit.client.*;
//import eionet.acl.AclUser;

/**
* Handler of storing methods for the ACL Admin Tool
*/
public class SaveHandler {

  /**
  * calls XML/RPC method for storing groups or ACL information
  */
  static void callRemoteMethod(HttpServletRequest req, String action) throws ServiceClientException  {

    HttpSession sess = (HttpSession)req.getAttribute(Names.SESS_ATT);
    String appName = (String)sess.getAttribute(Names.APP_ATT); //req.getParameter("app");
    HashMap appClients = (HashMap)sess.getAttribute(Names.APPCLIENTS_ATT);

    ServiceClientIF client = (ServiceClientIF)appClients.get(appName);    
    Vector prms = new Vector();

    String methodName = null;

    //call correct XML/RPC method
    if (action.equals( Names.SAVE_GROUPS_ACTION)) {
      Hashtable groups = (Hashtable)req.getAttribute(Names.GROUPS_PARAM_NAME);    
      prms.add(groups);
      methodName = "setLocalGroups";
    }
    else if (action.equals( Names.ACL_SAVE_ACTION)) {
      String aclName = (String)sess.getAttribute(Names.ACL_ATT);
      Hashtable aclInfo=(Hashtable)req.getAttribute(Names.ACL_INFO_ATT);
      prms.add(aclInfo);
      methodName = "setAclInfo";      
    }

    client.getValue(methodName, prms);

  }

  /**
  * localgroups handling
  * several checkings, the username cannot be emoty, group cannot exist etc..
  */
  static void handleGroups(HttpServletRequest req, String action) {
     String grpName= (String)req.getParameter("NAME");
     String user= (String)req.getParameter("MEMBER");
     Hashtable groups = (Hashtable)req.getAttribute("GROUPS");
     Vector members = (Vector)groups.get(grpName);    

     if (action.equals( Names.MEMBER_ADD_ACTION) ) {
      if (Util.isNullStr(user))
        req.setAttribute(Names.ERROR_ATT, "The username cannot be empty.");      
      else if ( members.contains(user))
        req.setAttribute(Names.ERROR_ATT, "The user already exists in the ACL.");      
      else      
        members.add(user);      
      }
     else if (action.equals( Names.MEMBER_DEL_ACTION))
      members.remove(user);      
     else if (action.equals( Names.GROUP_ADD_ACTION))  {
      if (Util.isNullStr(grpName))
        req.setAttribute(Names.ERROR_ATT, "The group name cannot be empty.");      
      else if ( groups.containsKey(grpName))
        req.setAttribute(Names.ERROR_ATT, "The localgroup already exists.");      
      else      
        groups.put(grpName, new Vector());
     }
     else if (action.equals( Names.GROUP_DEL_ACTION))   
        groups.remove(grpName);
     

  }
  static void handleAclEntry(HttpServletRequest req, String action) {
     String aclName = (String)(req.getSession().getAttribute(Names.ACL_ATT));

     String entryName= (String)req.getParameter("NAME");
     String aclEntryType= (String)req.getParameter("TYPE");

     String aclPerms = (String)req.getParameter("PERMISSIONS");

      String aclType = (String)req.getParameter("ACL_TYPE");

    if (  !Util.isNullStr(aclPerms) && aclPerms.lastIndexOf(',') == aclPerms.length()-1 )
      aclPerms = aclPerms.substring(0, aclPerms.length()-1);

     //!!! quick hard-coded fix !!!
     //later auth and anonymous will be handled as separate mechanisms
     //now just defined usernames
     if (aclEntryType.equals("anonymous") || aclEntryType.equals("authenticated") || aclEntryType.equals("owner")  ) {
        entryName=aclEntryType;
        aclEntryType="user";
     }
      
     Vector aclData = (Vector)req.getAttribute(Names.ACL_DATA_ATT);
     Hashtable acl=Util.getAclEntry(aclData, aclEntryType, entryName, aclType);
        
    if (action.equals(Names.ACL_ADD_ACTION) ) {
      if ( Util.isNullStr(entryName) )
        req.setAttribute(Names.ERROR_ATT, "The entry name cannot be empty.");
      else if ( acl != null) 
        req.setAttribute(Names.ERROR_ATT, "The entry already exists in the ACL.");      
      else if ( entryName.equals("owner") && aclType.equals("object"))
        req.setAttribute(Names.ERROR_ATT, "Owner entry can be used only in DOC and DCC ACL's.");      
      else {
        acl=new Hashtable();
        acl.put("id", entryName);
        acl.put("type", aclEntryType);
        acl.put("perms", aclPerms);
        acl.put("acltype", aclType);

        if ( aclEntryType.equals("localgroup") && !checkGroup(entryName, req) )
          req.setAttribute(Names.ERROR_ATT, "Localgroup " + entryName + " does not exist.");        
        else
          aclData.add(acl);
      }
    }
    else //action.equals(Names.ACL_DEL_ACTION))
      aclData.remove(acl);

    //something is changed - for warning, if left from the page without saving
    req.setAttribute(Names.CHANGED_ATT , "TRUE");
    
  }

  /**
  * change permissions of the entry in the hash
  * no XML/RPC call
  */
  static void savePermissions (HttpServletRequest req ) {

    HttpSession session = (HttpSession)req.getAttribute(Names.SESS_ATT);
     
    String aclName = (String)session.getAttribute(Names.ACL_ATT);
   	Hashtable acls = (Hashtable) req.getAttribute("ACLS");     

    Vector aclData = (Vector)req.getAttribute(Names.ACL_DATA_ATT);
      
    String entryName= (String)req.getParameter("NAME");
    String aclEntryType= (String)req.getParameter("TYPE");
    String aclPermissions= (String)req.getParameter("PERMISSIONS"); 

    String aclType=(String)req.getParameter("ACL_TYPE"); 

    if (  !Util.isNullStr(aclPermissions) && aclPermissions.lastIndexOf(',') == aclPermissions.length()-1 )
      aclPermissions = aclPermissions.substring(0, aclPermissions.length()-1);


   //!!! quick hard-coded fix !!!
     if (aclEntryType.equals("anonymous") || aclEntryType.equals("authenticated") || aclEntryType.equals("owner")  ) {
        entryName=aclEntryType;
        aclEntryType="user";
     }
      

    Hashtable aclE = Util.getAclEntry(aclData, aclEntryType, entryName, aclType);
    
    aclE.put("perms", aclPermissions);

    req.setAttribute(Names.CHANGED_ATT, "TRUE");
  }

  /**
  * helper method - check if group already exists
  */
  private static boolean checkGroup(String grpName, HttpServletRequest req ) {
    Hashtable groups = (Hashtable)req.getAttribute(Names.GROUPS_PARAM_NAME);       
    return groups.containsKey(grpName);
  }

}