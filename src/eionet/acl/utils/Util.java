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


  	/*
  	 * 
  	 */
  	public static Vector getChildrenAclsRecursive(ServiceClientIF appClient, String aclName)
  																throws ServiceClientException{  		
  		if (appClient==null)
  			return null;
  		
  		Vector result = new Vector();
  		
  		Vector params = new Vector();
  		params.add(aclName);
  		Vector childrenAcls = (Vector)appClient.getValue("getChildrenAcls", params);
  		for (int i=0; childrenAcls!=null && i<childrenAcls.size(); i++){
  			String name = (String)childrenAcls.get(i);
  			result.add(name);
  			Vector v = getChildrenAclsRecursive(appClient, name);
  			if (v!=null && v.size()>0)
  				result.add(v);
  		}
  		
  		return result;
  	}
  	
    /**
     * Calls replaceTags(in, false, false). See the documentation of that method.
     *  
     * @param in
     * @return
     */
    public static String replaceTags(String in){
        return replaceTags(in, false, false);
    }
    
    /**
     * Calls replaceTags(in, dontCreateHTMLAnchors, false). See the documentation of that method.
     * 
     * @param in
     * @param dontCreateHTMLAnchors
     * @return
     */
    public static String replaceTags(String in, boolean dontCreateHTMLAnchors){
    	return replaceTags(in, dontCreateHTMLAnchors, false);
    }
    
    /**
     * Escapes the string for valid HTML content.
     * By default, this method creates HTML anchors (<a href"...">...</a>) for URLs it finds in the string. This can be switched
     * off by setting dontCreateHTMLAnchors to true.
     * Also by default, this method converts discovered line breaks into HTML line breaks (<br>). This can be switched off by setting
     * dontCreateHTMLLineBreaks to true.
     * 
     * @param in
     * @param inTextarea
     * @return
     */
    public static String replaceTags(
    		String in, boolean dontCreateHTMLAnchors, boolean dontCreateHTMLLineBreaks){
    	
	    in = (in != null ? in : "");
	    
	    StringBuffer ret = new StringBuffer();
	    for (int i = 0; i < in.length(); i++) {
	      char c = in.charAt(i);
	      if (c == '<')
	        ret.append("&lt;");
	      else if (c == '>')
	        ret.append("&gt;");
	      else if (c == '\"')
	          ret.append("&quot;");
	      else if (c == '\"')
	    	  ret.append("&#039;");
	      else if (c == '\\')
	          ret.append("&#092;");
	      else if (c == '&'){
	    	  boolean startsEscapeSequence = false;
	    	  int j = in.indexOf(';', i);
	    	  if (j>0){
	    		  String s = in.substring(i,j+1);
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
	    
	    String retString = ret.toString();
	    if (dontCreateHTMLAnchors==false)
	    	retString=setAnchors(retString, true, 50);
	    
	    ret = new StringBuffer();
	    for (int i = 0; i < retString.length(); i++) {
	    	char c = retString.charAt(i);
	    	if (c == '\n' && dontCreateHTMLLineBreaks==false)
	    		ret.append("<br/>");
	    	else if (c == '\r' && i!=(retString.length()-1) && retString.charAt(i+1)=='\n' && dontCreateHTMLLineBreaks==false){
	    		ret.append("<br/>");
				i = i + 1;
	    	}
	    	else
	    		ret.append(c);
	    }

	    return ret.toString();
	}

	/**
	* Finds all urls in a given string and replaces them with HTML anchors.
	* If boolean newWindow==true then target will be a new window, else no.
	* If boolean cutLink > 0 then cut the displayed link length at cutLink.
	*/
	public static String setAnchors(String s, boolean newWindow, int cutLink){

		StringBuffer buf = new StringBuffer();
        
		StringTokenizer st = new StringTokenizer(s, " \t\n\r\f", true);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = processForLink(token, newWindow, cutLink);
			buf.append(token);
		}
        
		return buf.toString();
	}

    /**
     * 
     * @param s
     * @return
     */
    public static String processForLink(String in, boolean newWindow, int cutLink){
    	
    	if (in==null || in.trim().length()==0)
    		return in;
    	
    	HashSet urlSchemes = new HashSet();
    	urlSchemes.add("http://");
    	urlSchemes.add("https://");
    	urlSchemes.add("ftp://");
    	urlSchemes.add("mailto://");
    	urlSchemes.add("ldap://");
    	urlSchemes.add("file://");
    	
    	int beginIndex = -1;
    	Iterator iter = urlSchemes.iterator();
    	while (iter.hasNext() && beginIndex<0)
    		beginIndex = in.indexOf((String)iter.next());
    	
    	if (beginIndex<0)
    		return in;
    	
    	int endIndex = -1;
    	String s = null;
    	for (endIndex=in.length(); endIndex>beginIndex; endIndex--){
    		s = in.substring(beginIndex, endIndex);
    		if (isURL(s))
    			break;
    	}
    	
    	if (s==null)
    		return in;
    	
    	HashSet endChars = new HashSet();
    	endChars.add(new Character('!'));
    	endChars.add(new Character('\''));
    	endChars.add(new Character('('));
    	endChars.add(new Character(')'));
    	endChars.add(new Character('.'));
    	endChars.add(new Character(':'));
    	endChars.add(new Character(';'));
    	
    	for (endIndex=endIndex-1; endIndex > beginIndex; endIndex--){
    		char c = in.charAt(endIndex);
    		if (!endChars.contains(new Character(c)))
    			break;
    	}
    	
    	StringBuffer buf = new StringBuffer(in.substring(0, beginIndex));
    	
    	String link = in.substring(beginIndex, endIndex+1);
    	StringBuffer _buf = new StringBuffer("<a ");
		_buf.append(" href=\"");
		_buf.append(link);
		_buf.append("\">");
		
		if (cutLink<link.length())
			_buf.append(link.substring(0, cutLink)).append("...");
		else
			_buf.append(link);
			
		_buf.append("</a>");
		buf.append(_buf.toString());
    	
    	buf.append(in.substring(endIndex+1));
    	return buf.toString();
    }

    /**
     * Checks if the given string is a well-formed URL
     */
     public static boolean isURL(String s){
         try {
             URI url = new URI(s);
         }
         //catch (MalformedURLException e){
         catch (URISyntaxException e){
             return false;
         }
         
         return true;
     }
}