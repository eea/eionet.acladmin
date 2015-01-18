package eionet.acladmin;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

import eionet.rpcserver.UITServiceRoster;

public class UITServiceRosterTest {

    static String rpcContextLocation = "java:comp/env/rpc/";
     
    @BeforeClass
    public static void setUpCore() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        InitialContext ic = new InitialContext();

        ic.createSubcontext("java:");
        ic.createSubcontext("java:comp");
        ic.createSubcontext("java:comp/env");
        ic.createSubcontext("java:comp/env/somethingelse");
        ic.createSubcontext("java:comp/env/rpc");
        ic.bind(rpcContextLocation + "acl.admin", Boolean.valueOf(true));
        ic.bind(rpcContextLocation + "services.definition.file", "ServiceDefinition.xml");

    }

    @Test
    public void loadViaJNDI() throws Exception {
        Hashtable<Object, Object> props = UITServiceRoster.loadProperties();

        assertEquals(Boolean.valueOf(true), props.get("acl.admin"));
        assertEquals("ServiceDefinition.xml", props.get("services.definition.file"));
    }

}
