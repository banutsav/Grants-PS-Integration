package click_grants_datasynchronizer.wsrequests;

import click_grants_datasynchronizer.Main;
import click_grants_datasynchronizer.AwardProgram;
import click_grants_datasynchronizer.WebServiceRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Glenn
 */
public class UpdateAwardProgramRequest implements WebServiceRequest {

    private AwardProgram obj;
    private String svcSessionToken;
    private String content;

    public UpdateAwardProgramRequest(AwardProgram obj, String svcSessionToken) {
        this.obj = obj;
        this.svcSessionToken = svcSessionToken;
    }

    public HttpURLConnection getConnection() throws Exception {
        final String requestXmlString
                = "<WebServiceRoot>"
                + "   <WebService Name='Award_Program_Upload'>"
                + "       <Attribute Caption='Program Code' DataType='String' ReferenceType='' ReadOnly='False' Required='False' Value='" + StringEscapeUtils.escapeXml(obj.Program_Code) + "'/>"
                + "       <Attribute Caption='Name' DataType='String' ReferenceType='' ReadOnly='False' Required='False' Value='" + StringEscapeUtils.escapeXml(obj.Name) + "'/>"
                + "       <Attribute Caption='Short Description' DataType='String' ReferenceType='' ReadOnly='False' Required='False' Value='" + StringEscapeUtils.escapeXml(obj.Short_Description) + "'/>"
                + "       <Attribute Caption='Is Active' DataType='String' ReferenceType='' ReadOnly='False' Required='False' Value='true'/>"
                + "   </WebService>"
                + "</WebServiceRoot>";

        content = "svcSessionToken=" + URLEncoder.encode(svcSessionToken)
                + "&requestXmlString=" + URLEncoder.encode(requestXmlString);
        //System.out.println("XML = \n" + requestXmlString);
        final URL url = new URL(Main.clickAddress + "/ClickXWebServices/DataManagement/DataManagementServices.asmx/Add");
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
