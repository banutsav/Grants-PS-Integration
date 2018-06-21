package click_grants_datasynchronizer;

import java.net.HttpURLConnection;

/**
 *
 * @author Glenn
 */
public interface WebServiceRequest {

    public HttpURLConnection getConnection() throws Exception;
    
    public String getContent();
}