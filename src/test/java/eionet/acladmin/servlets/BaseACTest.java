package eionet.acladmin.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eionet.acladmin.Names;
import eionet.acl.SignOnException;

import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.GetMethodWebRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import eionet.acl.AppUser;


public class BaseACTest {

    @Test
    public void testIsAllowed() throws Exception {
        BaseACSub bacs = new BaseACSub();
        assertTrue(bacs.isAllowed("enriko"));
    }

    private void createSession(HttpServletRequest req) {
        // Force a session to be created
        HttpSession session = req.getSession();

        AppUser appUser = new AppUser();
        appUser.authenticateForTest("kaido");
        session.setAttribute(Names.USER_ATT, appUser);
        req.setAttribute(Names.SESS_ATT, session);
    }

    @Test
    public void testBaseSub() throws Exception {
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("baseac", BaseACSub.class.getName());
        ServletUnitClient sc = sr.newClient();
        WebRequest request = new GetMethodWebRequest("http://localhost/baseac");
        InvocationContext ic = sc.newInvocation(request);

        HttpServletRequest req = ic.getRequest();
        assertNull("A session already exists", req.getSession(false));

        WebResponse response = ic.getServletResponse();
        assertEquals(200, response.getResponseCode());

        createSession(req);
        BaseACSub bacs = (BaseACSub) ic.getServlet();
        assertNotNull(bacs.getSession(req));
        bacs.guard(req, ic.getResponse());
    }
}

