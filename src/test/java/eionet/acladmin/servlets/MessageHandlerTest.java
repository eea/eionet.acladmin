/*
 * MessageHandlerTest.java
 * 
 * Created on Apr 27, 2015
 *            
 */

package eionet.acladmin.servlets;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class MessageHandlerTest {
    
    @Test
    public void createMessage() throws IOException{
        MessageHandler messageHandler = new MessageHandler();
        
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("baseac", BaseACSub.class.getName());
        ServletUnitClient sc = sr.newClient();
        WebRequest request = new GetMethodWebRequest("http://localhost/main");
        InvocationContext ic = sc.newInvocation(request);

        HttpServletRequest req = ic.getRequest();
        
        messageHandler.attachAdviceMessage(req, "advice test message");
        
        assertEquals(MessageHandler.ADVICE_MESSAGE_CLASS, ((MessageHandler.Message)req.getAttribute(MessageHandler.ADVICE_MESSAGE_KEY)).getClassText() );
        assertEquals("advice test message", ((MessageHandler.Message)req.getAttribute(MessageHandler.ADVICE_MESSAGE_KEY)).getMessageText() );
        
        messageHandler.attachSystemMessage(req, "system test message");
        
        assertEquals(MessageHandler.SYSTEM_MESSAGE_CLASS, ((MessageHandler.Message)req.getAttribute(MessageHandler.SYSTEM_MESSAGE_KEY)).getClassText() );
        assertEquals("system test message", ((MessageHandler.Message)req.getAttribute(MessageHandler.SYSTEM_MESSAGE_KEY)).getMessageText() );
    }
}
