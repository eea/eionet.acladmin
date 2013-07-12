package eionet.acl;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

/**
 * Mock of remoteservice to return test ACL data.
 *
 * @author kaido
 */
public class FakeRemoteService {

    /**
     * rpc router url.
     */
    private String serviceURL;

    /**
     * fake remote application name parsed from the url. needed to support multiple applications in tests
     */
    private String application;

    /**
     * sets service url and parses application name from the url.
     *
     * @param url
     *            fake service url
     */
    public void setServiceUrl(String url) {
        this.serviceURL = url;
        this.application = StringUtils.substringBefore(StringUtils.substringAfter(serviceURL, "http://"), ".");
    }

    /**
     * Fake ACL infos.
     * @return hash containing fake data
     */
    Hashtable<String, Hashtable<String, Object>> getFakeAclInfos() {
        Hashtable aclInfos = new Hashtable<String, Hashtable<String, Object>>();
        //rod:
        if (application.equals("rod")) {
            //obligations
            Hashtable aclInfo1 = new Hashtable();
            aclInfo1.put("name", "/obligations");
            aclInfo1.put("description" , "Reporting Obligation");
            aclInfo1.put("flags", "tableperms");
            aclInfo1.put("owner", true);

            Vector<Hashtable> entries1 =  new Vector();

            Hashtable e1 = new Hashtable();
            e1.put("acltype", "object"); //hard-coded

            e1.put("type", "localgroup");
            e1.put("perms", "v,i,u,d");
            e1.put("id", "rod_user");
            entries1.add(e1);

            Hashtable e2 = new Hashtable();
            e2.put("acltype", "object"); //hard-coded

            e2.put("type", "user");
            e2.put("perms", "v");
            e2.put("id", "anonymous");
            entries1.add(e2);


            aclInfo1.put("entries", entries1);
            // ------------------------------------



            //instruments
            Hashtable aclInfo2 = new Hashtable();
            aclInfo1.put("name", "/instruments");
            aclInfo1.put("description" , "Legal Instruments");
            aclInfo1.put("flags", "tableperms");
            aclInfo1.put("owner", false);

            Vector<Hashtable> entries2 =  new Vector();

            Hashtable e3 = new Hashtable();
            e3.put("acltype", "object"); //hard-coded

            e3.put("type", "localgroup");
            e3.put("perms", "v,i,u");
            e3.put("id", "rod_user");
            entries2.add(e3);

            Hashtable e4 = new Hashtable();
            e4.put("acltype", "object"); //hard-coded

            e4.put("type", "localgroup");
            e4.put("perms", "v,i,u,d");
            e4.put("id", "rod_admin");
            entries2.add(e4);

            Hashtable e5 = new Hashtable();
            e5.put("acltype", "object"); //hard-coded

            e5.put("type", "user");
            e5.put("perms", "u");
            e5.put("id", "heinlja");

            //add 'heinlja' to both acls
            entries1.add(e5);
            entries2.add(e5);

            aclInfo2.put("entries", entries2);

            aclInfos.put("/obligations", aclInfo1);
            aclInfos.put("/instruments", aclInfo2);

        } else if (application.equals("dd")) {
            //obligations
            Hashtable aclInfo1 = new Hashtable();
            aclInfo1.put("name", "/datasets");
            aclInfo1.put("description" , "Datasets");
            aclInfo1.put("flags", "tableperms");
            aclInfo1.put("owner", false);

            Vector<Hashtable> entries1 =  new Vector();

            Hashtable e1 = new Hashtable();
            e1.put("acltype", "object"); //hard-coded

            e1.put("type", "localgroup");
            e1.put("perms", "v,i");
            e1.put("id", "dd_user");
            entries1.add(e1);

            Hashtable e2 = new Hashtable();
            e2.put("acltype", "object"); //hard-coded

            e2.put("type", "user");
            e2.put("perms", "v");
            e2.put("id", "authenticated");
            entries1.add(e2);

            Hashtable e4 = new Hashtable();
            e4.put("type", "user");
            e4.put("perms", "i,v");
            e4.put("id", "heinlja");
            entries1.add(e4);

            Hashtable e3 = new Hashtable();
            e3.put("acltype", "object"); //hard-coded

            e3.put("type", "localgroup");
            e3.put("perms", "v,i,u,d");
            e3.put("id", "dd_admin");
            entries1.add(e3);

            aclInfo1.put("entries", entries1);

            aclInfos.put("/datasets", aclInfo1);

        }

        return aclInfos;
    }
    /**
     * fake local groups.
     * @return hash of local groups
     */
    Hashtable getFakeLocalGroups() {

        Hashtable h = new Hashtable();
        if (application.equals("rod")) {
            Vector g1 = new Vector();
            Vector g2 = new Vector();

            g1.add("heinlja");
            g1.add("roduser2");

            g2.add("roduser1");

            h.put("rod_user", g2);
            h.put("rod_admin", g1);

        } else if (application.equals("dd")) {
            Vector g1 = new Vector();
            Vector g2 = new Vector();
            Vector g3 = new Vector();

            g1.add("heinlja");
            g2.add("ddduser2");
            g2.add("dduser1");
            g2.add("dduser4");

            h.put("dd_admin", g1);
            h.put("dd_user", g2);
            h.put("dd_empty", g3);
        }

        return h;

    }


}
