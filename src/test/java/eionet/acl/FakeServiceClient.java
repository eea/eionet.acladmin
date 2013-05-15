package eionet.acl;

import java.util.HashMap;
import java.util.Vector;
import com.tee.uit.client.ServiceClientIF;
import com.tee.uit.client.ServiceClientException;

/*
 * See "Replace Global References with Getter" in "Working Effectively with Legacy Code".
 */
public class FakeServiceClient implements ServiceClientIF {

    @Override
    public Object getValue(String methodName, Vector parameters) throws ServiceClientException {
        return null;
    }

    @Override
    public void setCredentials (String userName, String pwd) throws ServiceClientException {
    }
}
