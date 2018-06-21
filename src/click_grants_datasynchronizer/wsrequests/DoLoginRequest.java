package click_grants_datasynchronizer.wsrequests;

import click_grants_datasynchronizer.Main;
import click_grants_datasynchronizer.WebServiceRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author Glenn
 */
public class DoLoginRequest implements WebServiceRequest {

    private String content;

    public HttpURLConnection getConnection() throws Exception {
        content = "storeName=" + URLEncoder.encode(Main.clickStoreName) +
                "&userName=" + URLEncoder.encode(Main.clickUsername) +
                "&password=" + URLEncoder.encode(Main.clickPassword);

        
        final URL url = new URL(Main.clickAddress + "/ClickXWebServices/DataManagement/SearchServices.asmx/Login");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", "localhost");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
        return connection;
    }

    public String getContent() {
        return content;
    }
}