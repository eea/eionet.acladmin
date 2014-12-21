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

package eionet.acladmin;

/**
 * Constants used by Acl Admin Tool.
 */
public interface Names {

  //Request + session Attribute names
  public static final String APP_ATT        = "UIT_APP_ATT";
  public static final String ACL_ATT        = "UIT_ACL_ATT";
  public static final String CHANGED_ATT    = "UIT_CHANGED_ATT";
  public static final String  ACL_INFO_ATT      = "UIT_ACL_INFO_ATT";
  public static final String  ACL_DATA_ATT      = "UIT_ACL_DATA_ATT";
  public static final String  ACL_CHILDREN_ATT  = "UIT_ACL_CHILDREN_ATT";
  public static final String SESS_ATT         = "ACL__UIT_SESS";
  public static final String USER_ATT         = "UIT_ACL_USR_ATT";
  public static final String ERROR_ATT        = "UIT_ERROR_ATT";
  public static final String APPLICATIONS_ATT = "UIT_ACL_APPS_ATT";
  public static final String APPCLIENTS_ATT = "UIT_ACL_APPCLIENTS_ATT";
  public static final String NOTOWNER_ATT = "UIT_ACL_NOTOWNER_ATT";
  public static final String ATT_ACROSS_APPS = "UIT_ACL_ATT_ACROSS_APPS";
  public static final String ATT_SEARCH_ACROSS_APPS_RESULT = "UIT_ACL_ATT_SEARCH_ACROSS_APPS_RESULT";
  public static final String ATT_ERR_APP = "UIT_ACL_ATT_ERR_APP";

  //JSP names
  public static final String INDEX_JSP = "index.jsp";
  //public static final String ERROR_JSP = "error.jsp";
  public static final String MAIN_JSP = "main.jsp";
  public static final String GROUP_JSP = "group.jsp";
  public static final String GROUPS_JSP = "groups.jsp";
  public static final String LOGIN_JSP = "login.jsp";
  public static final String PERMISSIONS_JSP = "permissions.jsp";
  public static final String JSP_SUBJECT_ACROSS_APPS = "subjectAcrossApps.jsp";

  //actions
  public static final String ACL_ADD_ACTION = "A";
  public static final String ACL_DEL_ACTION = "D";
  public static final String GROUP_ADD_ACTION = "O";
  public static final String GROUP_DEL_ACTION = "Q";
  public static final String MEMBER_ADD_ACTION = "M";
  public static final String MEMBER_DEL_ACTION = "N";
  public static final String LOGIN_ACTION = "F";
  public static final String LOGOUT_ACTION = "I";
  public static final String APPLOGIN_ACTION = "E";
  public static final String SHOW_PERMISSIONS_ACTION = "P";
  public static final String SHOW_APPS_ACTION = "C";
  public static final String SHOW_APP_ACTION = "H";
  public static final String SHOW_GRP_ACTION = "B";
  public static final String SHOW_GROUPS_ACTION = "Y";
  public static final String SAVE_GROUPS_ACTION = "Z";
  public static final String SAVE_PERMS_ACTION = "X";
  public static final String ACL_SAVE_ACTION = "W";
  public static final String ACT_SEARCH_ACROSS_APPS = "search_across_apps";

  //public static final String ERROR_ACTION = "XXX";

  //Parameters
  public static final String GROUPS_PARAM_NAME = "GROUPS";
  public static final String PERMS_PARAM_NAME = "PRMS";

  public static final String RPC_SERVICE_NAME = "XService";

  public static final String PRM_ACROSS_APPS_USERNAME = "apps_user";
  public static final String PRM_ACROSS_APPS_PASSWORD = "apps_pwd";
  public static final String PRM_SUBJECT_ID = "subject_id";
  public static final String PRM_SUBJECT_TYPE = "subject_type";
  public static final String PRM_RELOAD_ACROSS_APPS = "reload_across_apps";
}
