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
public class GetAwardProgramDataRequest implements WebServiceRequest {

    private int startRow;
    private int numRows;
    private String svcSessionToken;
    private String content;

    public GetAwardProgramDataRequest(int startRow, int numRows, String svcSessionToken) {
        this.startRow = startRow;
        this.numRows = numRows;
        this.svcSessionToken = svcSessionToken;
    }

    public HttpURLConnection getConnection() throws Exception {
            
        content = "svcSessionToken=" + URLEncoder.encode(svcSessionToken) +
                "&savedSearchName=Award_Program_Data_Export" +
                "&startRow=" + String.valueOf(startRow) +//1
                "&numRows=" + String.valueOf(numRows) +//-1
                "&expandMultiValueCells=false" +
                "&parameters=";

        final URL url = new URL(Main.clickAddress + "/ClickXWebServices/DataManagement/SearchServices.asmx/performSearch");
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