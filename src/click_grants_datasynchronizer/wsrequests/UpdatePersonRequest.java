package click_grants_datasynchronizer.wsrequests;

import click_grants_datasynchronizer.Main;
import click_grants_datasynchronizer.Person;
import click_grants_datasynchronizer.WebServiceRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Glenn
 */
public class UpdatePersonRequest implements WebServiceRequest {

    private Person obj;
    private String svcSessionToken;
    private String content;

    public UpdatePersonRequest(Person obj, String svcSessionToken) {
        this.obj = obj;
        this.svcSessionToken = svcSessionToken;
    }

    public HttpURLConnection getConnection() throws Exception {
        final String requestXmlString =
                "<WebServiceRoot>" +
                "   <WebService Name='Person_People_Soft_ID' value='" + StringEscapeUtils.escapeXml(obj.UGAID) + "' valueidentifier='ID'>" +
                "       <Attribute Caption='personCustomExtension.PeopleSoft ID' DataType='String' ReferenceType='' ReadOnly='False' Required='False' Value='" + StringEscapeUtils.escapeXml(obj.EMPID) + "'/>" +
                "   </WebService>" +
                "</WebServiceRoot>";

        content = "svcSessionToken=" + URLEncoder.encode(svcSessionToken) +
                "&requestXmlString=" + URLEncoder.encode(requestXmlString);

        final URL url = new URL(Main.clickAddress + "/ClickXWebServices/DataManagement/DataManagementServices.asmx/Update");
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