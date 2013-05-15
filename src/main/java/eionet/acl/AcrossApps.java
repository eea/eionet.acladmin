/*
 * Created on 27.03.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package eionet.acl;

import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.text.Collator;
import com.tee.uit.client.*;
import eionet.acl.utils.Util;

/*
 *
 */
public class AcrossApps {

    private Hashtable appsData = null;
    private HashMap appsProps = null;
    private String username = null;
    private String password = null;

    private String curInitApp = null;

    /*
     *
     */
    public AcrossApps(HashMap apps, String username, String password) {
        this.appsProps = apps;
        this.username = username;
        this.password = password;
    }

    public String getCurInitApp() {
        return curInitApp;
    }

    /*
     *
     */
    public void reload() throws ServiceClientException {
        appsData = null;
        init();
    }

    /*
     *
     */
    public void init() throws ServiceClientException {

        // if already initialized, return
        if (appsData != null && appsData.size() > 0)

        // if missing props, we cannot initialize
        if (appsProps == null || appsProps.size() == 0)
            return;

        appsData = new Hashtable();
        for (Iterator iter = appsProps.keySet().iterator(); iter != null && iter.hasNext();) {
            String appName = (String) iter.next();
            curInitApp = appName;
            String appUrl  = (String) ((HashMap) appsProps.get(appName)).get("url");

            ServiceClientIF appClient = getServiceClient(Names.RPC_SERVICE_NAME, appUrl);
            if (appClient == null)
                continue;
            else
                appClient.setCredentials(username, password);

            Hashtable aclInfos = (Hashtable) appClient.getValue("getAclInfos", new Vector());
            Hashtable groups = (Hashtable) appClient.getValue("getLocalGroups", new Vector());
            Hashtable aclMgrInfo = (Hashtable) appClient.getValue("getAclManagerInfo", new Vector());

            if ((groups != null && groups.size() > 0) || (aclInfos != null && aclInfos.size() > 0)) {

                Hashtable appHash = new Hashtable();
                appHash.put("groups", groups);
                appHash.put("aclinfos", aclInfos);
                if (aclMgrInfo != null && aclMgrInfo.size() > 0)
                    appHash.put("acl_mgr_ifno", aclMgrInfo);
                appsData.put(appName, appHash);
            }
        }
    }

    protected ServiceClientIF getServiceClient(String serviceName, String appUrl) throws ServiceClientException {
        return ServiceClients.getServiceClient(serviceName, appUrl);
    }

    /*
     *
     */
    public Vector getUserEntries(String userID) {

        if (Util.isNullStr(userID))
            return null;
        if (appsData == null || appsData.size() == 0)
            return null;

        Vector result = new Vector();

        Vector appNames = new Vector(appsData.keySet());
        Collections.sort(appNames, Collator.getInstance());
        for (int t = 0; t < appNames.size(); t++) {

            String appName = (String) appNames.get(t);
            Hashtable appHash = (Hashtable) appsData.get(appName);
            if (appHash.size() == 0) continue;
            Hashtable aclInfos = (Hashtable) appHash.get("aclinfos");
            if (aclInfos == null || aclInfos.size() == 0) continue;
            HashSet groupsMemberOf = getGroupsMemberOf(userID, appHash);
            Hashtable aclMgrFlags = getAclMgrFlags(appHash);

            Vector aclNames = new Vector(aclInfos.keySet());
            Collections.sort(aclNames, Collator.getInstance());
            for (int u = 0; u < aclNames.size(); u++) {

                String aclName = (String) aclNames.get(u);
                Hashtable aclInfo = (Hashtable) aclInfos.get(aclName);
                if (aclInfo.size() == 0) continue;
                Vector entries = (Vector) aclInfo.get("entries");
                if (entries == null || entries.size() == 0) continue;

                for (int i = 0; i < entries.size(); i++) {

                    Hashtable entry = (Hashtable) entries.get(i);
                    if (entry.size() == 0) continue;
                    String id = (String) entry.get("id");
                    String type = (String) entry.get("type");
                    if (Util.isNullStr(id) || Util.isNullStr(type)) continue;
                    //boolean b = isInEntry(userID, id, type, groupsMemberOf);
                    boolean b = type.equals("user") && userID.equals(id);
                    if (!b) continue;

                    String aclType = (String) entry.get("acltype");
                    String perms = (String) entry.get("perms");
                    String flagsName = (String) aclInfo.get("flags");
                    perms = getPermsLabels(perms, flagsName, aclMgrFlags);

                    if (aclType == null) aclType = "";
                    if (perms == null) perms = "";

                    Hashtable resultRow = new Hashtable();
                    resultRow.put("app_name", appName);
                    resultRow.put("acl_name", aclName);
                    resultRow.put("acl_type", aclType);
                    resultRow.put("type", type);
                    resultRow.put("id", id);
                    resultRow.put("perms", perms);

                    result.add(resultRow);
                }
            }
        }

        return result.size() == 0 ? null : result;
    }

    /*
     *
     */
    public Hashtable getSubjectEntries(String subjectID, String subjectType) {

        if (Util.isNullStr(subjectID) || Util.isNullStr(subjectType))
            return null;
        if (appsData == null || appsData.size() == 0)
            return null;

        Hashtable result = new Hashtable();

        Vector appNames = new Vector(appsData.keySet());
        Collections.sort(appNames, Collator.getInstance());
        for (int t = 0; t < appNames.size(); t++) {

            int countAddedACLEntries = 0;
            Hashtable resultAppHash = null;

            String appName = (String) appNames.get(t);
            Hashtable appHash = (Hashtable) appsData.get(appName);
            if (appHash.size() == 0) continue;
            Hashtable aclInfos = (Hashtable) appHash.get("aclinfos");
            if (aclInfos == null || aclInfos.size() == 0) continue;
            Hashtable aclMgrFlags = getAclMgrFlags(appHash);
            HashSet groupsMemberOf = !subjectType.equals("user") ? null : getGroupsMemberOf(subjectID, appHash);
            if (groupsMemberOf != null && groupsMemberOf.size() > 0) {
                resultAppHash = new Hashtable();
                resultAppHash.put("groups", groupsMemberOf);
            }

            Vector aclNames = new Vector(aclInfos.keySet());
            Collections.sort(aclNames, Collator.getInstance());
            for (int u = 0; u < aclNames.size(); u++) {

                String aclName = (String) aclNames.get(u);
                Hashtable aclInfo = (Hashtable) aclInfos.get(aclName);
                if (aclInfo.size() == 0) continue;
                Vector entries = (Vector) aclInfo.get("entries");
                if (entries == null || entries.size() == 0) continue;

                for (int i = 0; i < entries.size(); i++) {

                    Hashtable entry = (Hashtable) entries.get(i);
                    if (entry.size() == 0) continue;
                    String id = (String) entry.get("id");
                    String type = (String) entry.get("type");
                    if (Util.isNullStr(id) || Util.isNullStr(type)) continue;
                    boolean b = id.equals(subjectID) && type.equals(subjectType);
                    if (!b) continue;

                    String aclType = (String) entry.get("acltype");
                    String perms = (String) entry.get("perms");
                    String flagsName = (String) aclInfo.get("flags");
                    perms = getPermsLabels(perms, flagsName, aclMgrFlags);

                    if (aclType == null) aclType = "";
                    if (perms == null) perms = "";

                    if (resultAppHash == null) resultAppHash = new Hashtable();

                    boolean wasAdded = addACLEntry(resultAppHash, aclName, aclType, perms);
                    if (wasAdded) countAddedACLEntries++;
                }
            }

            if (resultAppHash != null && resultAppHash.size() > 0) {
                resultAppHash.put("total_acl_entries", new Integer(countAddedACLEntries));
                result.put(appName, resultAppHash);
            }
        }

        return result.size() == 0 ? null : result;
    }

    /*
     *
     */
    private static boolean addACLEntry(
            Hashtable resultAppHash, String aclName, String aclType, String permissions) {

        if (resultAppHash == null)
            return false;

        if (aclName == null || aclType == null || permissions == null)
            return false;

        Hashtable aclsHash = (Hashtable) resultAppHash.get("acls");
        if (aclsHash == null) aclsHash = new Hashtable();

        Vector aclEntries = (Vector) aclsHash.get(aclName);
        if (aclEntries == null) aclEntries = new Vector();

        Hashtable entryHash = new Hashtable();
        entryHash.put("acltype", aclType);
        entryHash.put("permissions", permissions);
        aclEntries.add(entryHash);

        aclsHash.put(aclName, aclEntries);
        resultAppHash.put("acls", aclsHash);

        return true;
    }

    /*
     *
     */
    private static String getPermsLabels(String perms, String flagsName, Hashtable aclMgrFlags) {

        if (Util.isNullStr(perms) || flagsName == null || aclMgrFlags == null || aclMgrFlags.size() == 0)
            return perms;

        Hashtable labelsMap = (Hashtable) aclMgrFlags.get(flagsName);
        if (labelsMap == null || labelsMap.size() == 0)
            return perms;
        else
            return Util.getPrmDescrs(perms, labelsMap);
    }

    /*
     *
     */
    private static Hashtable getAclMgrFlags(Hashtable appHash) {

        if (appHash == null || appHash.size() == 0)
            return null;

        Hashtable aclMgrInfo = (Hashtable) appHash.get("acl_mgr_ifno");
        if (aclMgrInfo == null || aclMgrInfo.size() == 0)
            return null;
        else
            return (Hashtable) aclMgrInfo.get("flags");
    }

    /*
     *
     */
    private static boolean isInEntry(String userID, String id, String type, HashSet groupsMemberOf) {

        if (Util.isNullStr(userID) || Util.isNullStr(id) || Util.isNullStr(type))
            return false;

        if (type.equals("user") && id.equals(userID))
            return true;
        else if (type.equals("localgroup")) {
            if (groupsMemberOf != null && groupsMemberOf.contains(id))
                return true;
        }

        return false;
    }

    /*
     *
     */
    private static HashSet getGroupsMemberOf(String userID, Hashtable appHash) {

        if (Util.isNullStr(userID) || appHash == null || appHash.size() == 0)
            return null;

        Hashtable groups = (Hashtable) appHash.get("groups");
        if (groups == null || groups.size() == 0)
            return null;

        HashSet result = new HashSet();
        for (Iterator i = groups.keySet().iterator(); i != null && i.hasNext();) {
            String groupName = (String) i.next();
            Vector members = (Vector) groups.get(groupName);
            if (members.contains(userID))
                result.add(groupName);
        }

        return result.size() == 0 ? null : result;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
