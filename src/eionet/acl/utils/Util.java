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

package eionet.acl.utils;
import java.util.Hashtable;
import eionet.acl.Names;
import eionet.directory.DirectoryService;

import java.util.StringTokenizer;
import java.util.Vector;

/**
* Common utils for ACL Admin Tool
*/

public class Util {

 public static String getPrmDescrs(String perms, Hashtable descs ) {
    StringBuffer s = new StringBuffer();
    StringTokenizer pT = new StringTokenizer(perms,",");

    while (pT.hasMoreTokens()) {
      s.append( (String)descs.get( pT.nextToken()  ));
      s.append("; ");
    }
    
    return s.toString();
  }


 public static boolean isNullStr(String s ) {
  if ( s==null || s.length() == 0 || s.equals(" "))
    return true;
  else
    return false;
 }


 public static String getParentAclName(String aclName ) {
    if (!aclName.equals("/")) {
      int pos = aclName.indexOf("/",1);
      if ( pos==-1)
        return "/";
      else {
        int posB=0, posA=0;
        while (posB != -1) {
          posA=posB;
          posB=aclName.indexOf("/", posB+1);
        }
        return aclName.substring(0, posA);
      }
    }
    else
     return null;
 }

/**
* returns AclEntry Hashtable from the aclEntries Vector
*/
  public static Hashtable getAclEntry(Vector aclData, String eType, String eName, String aclType ){
    Hashtable aclPermissions=new Hashtable();
    for (int i=0; i< aclData.size(); i++) {
  		Hashtable aclE=(Hashtable)aclData.elementAt(i);
    	if( ((String)aclE.get("id")).equals(eName) && ((String)aclE.get("type")).equals(eType)  && ((String)aclE.get("acltype")).equals(aclType)  ) 
      	return aclE;
    }
    return null;
  }
}