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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import eionet.acl.Names;
import eionet.directory.DirectoryService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.tee.uit.client.ServiceClientException;
import com.tee.uit.client.ServiceClientIF;

/**
 * Common utils for ACL Admin Tool.
 */
public class Util {

    /**
     * @param perms
     * @param descs
     * @return
     */
    public static String getPrmDescrs(String perms, Hashtable descs ) {
        StringBuffer s = new StringBuffer();
        StringTokenizer pT = new StringTokenizer(perms,",");

        while (pT.hasMoreTokens()) {
            s.append( (String)descs.get( pT.nextToken()  ));
            s.append("; ");
        }

        return s.toString();
    }


    /**
     * @param s
     * @return
     */
    public static boolean isNullStr(String s ) {
        if (s == null || s.length() == 0 || s.equals(" "))
            return true;
        else
            return false;
    }


    /**
     * @param aclName
     * @return
     */
    public static String getParentAclName(String aclName ) {
        if (!aclName.equals("/")) {
            int pos = aclName.indexOf("/",1);
            if ( pos == -1)
                return "/";
            else {
                int posB = 0, posA = 0;
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
     * Returns AclEntry Hashtable from the aclEntries Vector.
     *
     * @param aclData
     * @param eType
     * @param eName
     * @param aclType
     * @return
     */
    public static Hashtable getAclEntry(Vector aclData, String eType, String eName, String aclType ) {
        Hashtable aclPermissions=new Hashtable();
        for (int i = 0; i < aclData.size(); i++) {
            Hashtable aclE=(Hashtable)aclData.elementAt(i);
            if ( ((String)aclE.get("id")).equals(eName) && ((String)aclE.get("type")).equals(eType) && ((String)aclE.get("acltype")).equals(aclType)  )
                return aclE;
        }
        return null;
    }


    /**
     * @param appClient
     * @param aclName
     * @return
     * @throws ServiceClientException
     */
    public static Vector getChildrenAclsRecursive(ServiceClientIF appClient, String aclName)
    throws ServiceClientException {
        if (appClient == null)
            return null;

        Vector result = new Vector();

        Vector params = new Vector();
        params.add(aclName);
        Vector childrenAcls = (Vector)appClient.getValue("getChildrenAcls", params);
        for (int i = 0; childrenAcls != null && i < childrenAcls.size(); i++) {
            String name = (String)childrenAcls.get(i);
            result.add(name);
            Vector v = getChildrenAclsRecursive(appClient, name);
            if (v != null && v.size()>0)
                result.add(v);
        }

        return result;
    }

    /**
     * Replaces the following characters with their XML escape codes: ', ", <, >, \, &.
     * If an ampersand is found and it is the start of an escape sequence, the ampersand is not escaped.
     *
     * @param in
     * @param inTextarea
     * @return
     */
    public static String replaceTags(String in) {

        in = (in != null ? in : "");

        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '<')
                ret.append("&lt;");
            else if (c == '>')
                ret.append("&gt;");
            else if (c == '"')
                ret.append("&quot;");
            else if (c == '\'')
                ret.append("&#039;");
            else if (c == '\\')
                ret.append("&#092;");
            else if (c == '&') {
                boolean startsEscapeSequence = false;
                int j = in.indexOf(';', i);
                if (j > 0) {
                    String s = in.substring(i, j+1);
                    UnicodeEscapes unicodeEscapes = new UnicodeEscapes();
                    if (unicodeEscapes.isXHTMLEntity(s) || unicodeEscapes.isNumericHTMLEscapeCode(s))
                        startsEscapeSequence = true;
                }

                if (startsEscapeSequence)
                    ret.append(c);
                else
                    ret.append("&amp;");
            }
            else
                ret.append(c);
        }

        return ret.toString();
    }
}
