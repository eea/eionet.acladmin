package eionet.acladmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import org.junit.Test;

import com.tee.uit.client.ServiceClientException;
import com.tee.uit.client.ServiceClientIF;
/**
 *
 * @author Søren Roug, e-mail: <a href="mailto:soren.roug@eea.europa.eu">soren.roug@eea.europa.eu</a>
 *
 */
public class AcrossAppsTest {

    /**
     * Helper method.
     *
     * @param resultHash
     *            has to be printed
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
                        if (l > 0)
                            System.out.print(" | ");
                        System.out.print(values[l]);
                    }
                    System.out.println("");
                }
            }
        }
    }

    /**
     * tests getSubejctEntries method.
     *
     * @throws Exception
     *             if fake client init fails
     */
    @Test
    public void getSubjectEntries() throws Exception {

        HashMap apps = new HashMap();
        HashMap app = new HashMap();
        //app name is parsed from the url to simulate multiple applications in FakeRemoteService
        app.put("url", "http://dd.notexists.int/rpcrouter");
        apps.put("DataDict", app);
        app = new HashMap();
        app.put("url", "http://rod.notexists.int/rpcrouter");
        apps.put("ROD", app);

        FakeAcrossApps acrossApps = new FakeAcrossApps(apps, "heinlja", "hayland");
        acrossApps.init();
        Hashtable resultHash = acrossApps.getSubjectEntries("dd_admin", "localgroup");
        if (resultHash != null && resultHash.size() > 0) {
            printResultHash(resultHash);
        }
        Hashtable<String, Object> ddAppHash = (Hashtable<String, Object>) resultHash.get("DataDict");

        Hashtable ddAcls = (Hashtable)ddAppHash.get("acls");

        //heinlja's permissions in /datasets
        assertEquals("v,i,u,d", ((Hashtable) ((Vector)ddAcls.get("/datasets")).get(0)).get("permissions"));

        Hashtable<String, Object> rodAppHash = (Hashtable<String, Object>) resultHash.get("ROD");
        assertEquals(1, ddAppHash.get("total_acl_entries"));


        //in ROD there is no dd_admin group
        assertNull(rodAppHash);
    }

    /**
     * tests for getUserEntries().
     *
     * @throws Exception
     *             if client initialization fails
     */
    @Test
    public void getUserEntries() throws Exception {

        HashMap apps = new HashMap();
        HashMap app = new HashMap();
        //app name is parsed from the url in FakeRemoteService, no real remote call is done.
        app.put("url", "http://dd.somewhere.noexisting.eu/rpcrouter");
        apps.put("DataDict", app);

        app = new HashMap();
        //app name is parsed from the url in FakeRemoteService, no real remote call is done.
        app.put("url", "http://rod.somewhere.noexisting.eu/rpcrouter");
        apps.put("ROD", app);

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
                    if (j > 0)
                        System.out.print(" | ");
                    System.out.print(values[j]);
                }
                System.out.println();
            }
        }
        //heinlja has 3 entries in 2 applications
        assertEquals(3, v.size());

        Hashtable ddEntries = (Hashtable) v.get(0);
        assertEquals("user", ddEntries.get("type"));
        assertEquals("i,v", ddEntries.get("perms"));

        Hashtable rodInstruments = (Hashtable) v.get(1);
        Hashtable rodObligations = (Hashtable) v.get(2);

        assertEquals("u", rodInstruments.get("perms"));
        assertEquals("/instruments", rodInstruments.get("acl_name"));
        assertEquals("/obligations", rodObligations.get("acl_name"));

    }

    /**
     * See "Replace Global References with Getter" in "Working Effectively with Legacy Code".
     */
    class FakeAcrossApps extends AcrossApps {
        /** fake service client. */
        private ServiceClientIF serviceClient = new FakeServiceClient();

        /**
         *
         * Creates fake class.
         *
         * @param apps
         *            fake remote applications.
         * @param username
         *            user name
         * @param password
         *            fake pwd
         */
        public FakeAcrossApps(HashMap apps, String username, String password) {
            super(apps, username, password);
        }

        @Override
        protected ServiceClientIF getServiceClient(String serviceName, String appUrl) throws ServiceClientException {
            ((FakeServiceClient) serviceClient).setServiceUrl(appUrl);
            return serviceClient;
        }
    }

    /**
     * fake service client.
     */
    class FakeServiceClient implements ServiceClientIF {

        /**
         * instance of fake remote service that mocks remote service call responses.
         */
        private FakeRemoteService service = new FakeRemoteService();

        @Override
        public Object getValue(String methodName, Vector parameters) throws ServiceClientException {

            if (methodName.equals("getAclInfos")) {
                return service.getFakeAclInfos();
            } else if (methodName.equals("getLocalGroups")) {
                return service.getFakeLocalGroups();
            } else if (methodName.equals("getAclManagerInfo")) {
                // NOT used in tests yet
            }
            return null;
        }

        @Override
        public void setCredentials(String userName, String pwd) throws ServiceClientException {
        }

        /**
         * sets service url. application name is parsed from the url for testing to sHashtable)imulate test with multiple remote applications.
         * No real remote call is done
         *
         * @param url
         *            fake service url
         */
        public void setServiceUrl(String url) {
            service.setServiceUrl(url);
        }
    }

}
