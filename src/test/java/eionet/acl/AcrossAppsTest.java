package eionet.acl;

import static junit.framework.Assert.assertEquals;

import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import org.junit.Test;


/**
 *
 * @author Søren Roug, e-mail: <a href="mailto:soren.roug@eea.europa.eu">soren.roug@eea.europa.eu</a>
 *
 */
public class AcrossAppsTest {

    /**
     * Helper method.
     *
     */
    private static void printResultHash(Hashtable resultHash) {

        Vector appNames = new Vector(resultHash.keySet());
        Collections.sort(appNames, Collator.getInstance());
        for (int i = 0; i < appNames.size(); i++) {

            String appName = (String) appNames.get(i);
            Hashtable appHash = (Hashtable) resultHash.get(appName);
            if (appHash == null || appHash.size() == 0)
                continue;

            HashSet groupsHash = (HashSet) appHash.get("groups");
            Hashtable aclsHash = (Hashtable) appHash.get("acls");
            if ((aclsHash == null || aclsHash.size() == 0) && (groupsHash == null || groupsHash.size() == 0))
                continue;
            Integer o = (Integer) appHash.get("total_acl_entries");
            int totalAclEntries = o == null ? -1 : o.intValue();

            String strGroups = groupsHash == null ? " " : groupsHash.toString();

            Vector aclNames = new Vector(aclsHash.keySet());
            Collections.sort(aclNames, Collator.getInstance());
            for (int j = 0; j < aclNames.size(); j++) {

                String aclName = (String) aclNames.get(j);
                Vector aclEntries = (Vector) aclsHash.get(aclName);
                if (aclEntries == null || aclEntries.size() == 0)
                    continue;

                for (int k = 0; k < aclEntries.size(); k++) {

                    Hashtable aclEntry = (Hashtable) aclEntries.get(k);
                    String aclType = (String) aclEntry.get("acltype");
                    String permissions = (String) aclEntry.get("permissions");

                    String[] values = new String[5];
                    values[0] = appName;
                    values[1] = strGroups;
                    values[2] = aclName;
                    values[3] = aclType;
                    values[4] = permissions;
                    for (int l = 0; l < values.length; l++) {
                        if (l > 0) System.out.print(" | ");
                        System.out.print(values[l]);
                    }
                    System.out.println("");
                }
            }
        }
    }

    /*
     * TODO: Add some real assertions
     */
    @Test
    public void getSubjectEntries() throws Exception {

        HashMap apps = new HashMap();
        HashMap app = new HashMap();
        //app.put("url", "http://dd.eionet.eu.int/rpcrouter");
        app.put("url", "http://localhost:8080/datadict/public/rpcrouter");
        apps.put("DataDict", app);
        app = new HashMap();
        app.put("url", "http://rod.eionet.eu.int/rpcrouter");
        apps.put("ROD", app);

        FakeAcrossApps acrossApps = new FakeAcrossApps(apps, "heinlja", "hayland");
        acrossApps.init();
        Hashtable resultHash = acrossApps.getSubjectEntries("rod_admin", "localgroup");
        if (resultHash != null && resultHash.size() > 0) {
            printResultHash(resultHash);
        }
    }

    /*
     * TODO: Add some real assertions
     */
    @Test
    public void getUserEntries() throws Exception {

        HashMap apps = new HashMap();
        HashMap app = new HashMap();
        //app.put("url", "http://dd.eionet.eu.int/rpcrouter");
        app.put("url", "http://localhost:8080/datadict/public/rpcrouter");
        apps.put("DataDict", app);

        FakeAcrossApps acrossApps = new FakeAcrossApps(apps, "heinlja", "xxx");
        acrossApps.init();
        Vector v = acrossApps.getUserEntries("heinlja");
        if (v != null && v.size() > 0) {
            System.out.println("size=" + v.size());
            for (int i = 0; i < v.size(); i++) {
                Hashtable rowHash = (Hashtable) v.get(i);
                String[] values = new String[6];
                values[0] = (String) rowHash.get("app_name");
                values[1] = (String) rowHash.get("acl_name");
                values[2] = (String) rowHash.get("acl_type");
                values[3] = (String) rowHash.get("type");
                values[4] = (String) rowHash.get("id");
                values[5] = (String) rowHash.get("perms");
                for (int j = 0; j < values.length; j++) {
                    if (j > 0) System.out.print(" | ");
                    System.out.print(values[j]);
                }
                System.out.println();
            }
        }
    }

}
