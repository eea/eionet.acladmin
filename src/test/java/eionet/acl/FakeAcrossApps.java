package eionet.acl;

import java.util.HashMap;
import com.tee.uit.client.ServiceClientIF;
import com.tee.uit.client.ServiceClientException;

/*
 * See "Replace Global References with Getter" in "Working Effectively with Legacy Code".
 */
public class FakeAcrossApps extends AcrossApps {

    private ServiceClientIF serviceClient = new FakeServiceClient();

    public FakeAcrossApps(HashMap apps, String username, String password) {
        super(apps, username, password);
    }

    @Override
    protected ServiceClientIF getServiceClient(String serviceName, String appUrl) throws ServiceClientException {
        return serviceClient;
    }
}
