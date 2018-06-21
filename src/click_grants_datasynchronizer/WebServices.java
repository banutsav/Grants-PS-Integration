package click_grants_datasynchronizer;

import click_grants_datasynchronizer.wsrequests.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;

/**
 *
 * @author Glenn
 */
public class WebServices {

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    //AWARD CLASS
    public static Element getAwardClassData(int startRow, int numRows, String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new GetAwardClassDataRequest(startRow, numRows, svcSessionToken);
            connection = handleRequest(wsr);

            final InputStream in = connection.getInputStream();
            final Element rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in).getDocumentElement();
            return rootElement;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // AWARD FINANCIALS
    public static Element getAwardFinancialData(int startRow, int numRows, String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new GetAwardFinancialDataRequest(startRow, numRows, svcSessionToken);
            connection = handleRequest(wsr);

            final InputStream in = connection.getInputStream();
            final Element rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in).getDocumentElement();
            return rootElement;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // AWARD PROGRAM DATA
    public static Element getAwardProgramData(int startRow, int numRows, String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new GetAwardProgramDataRequest(startRow, numRows, svcSessionToken);
            connection = handleRequest(wsr);

            final InputStream in = connection.getInputStream();
            final Element rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in).getDocumentElement();
            return rootElement;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void updatePerson(Person obj, String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new UpdatePersonRequest(obj, svcSessionToken);
            connection = handleRequest(wsr);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void updateAwardClass(AwardClass obj, String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new UpdateAwardClassRequest(obj, svcSessionToken);
            connection = handleRequest(wsr);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void updateAwardFinancialFund(AwardFinancialFund obj, String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new UpdateAwardFinancialFundRequest(obj, svcSessionToken);
            connection = handleRequest(wsr);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

     public static void updateAwardProgram(AwardProgram obj, String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new UpdateAwardProgramRequest(obj, svcSessionToken);
            connection = handleRequest(wsr);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
     
    //SECURITY
    public static String doLogin() throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new DoLoginRequest();
            connection = handleRequest(wsr);

            final InputStream in = connection.getInputStream();
            final Element rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in).getDocumentElement();

            final String svcSessionToken = rootElement.getTextContent();
            return svcSessionToken;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void doLogoff(String svcSessionToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            final WebServiceRequest wsr = new DoLogoffRequest(svcSessionToken);
            connection = handleRequest(wsr);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static HttpURLConnection handleRequest(WebServiceRequest wsr) throws Exception {
        final int maxAttempts = 1; //edits: utsavb

        for (int i = 1; i <= maxAttempts; i++) {

            HttpURLConnection connection = null;
            try {
                connection = wsr.getConnection();

                final OutputStream out = connection.getOutputStream();
                out.write(wsr.getContent().getBytes());

                final int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    return connection;
                } else {
                    if (responseCode == 500) {
                        final InputStream err = connection.getErrorStream();
                        printOutput(err);
                        err.close();
                    } else {
                        final InputStream in = connection.getInputStream();
                        printOutput(in);
                        in.close();
                    }
                    throw new ResponseCodeException(responseCode);
                }
            } catch (ResponseCodeException ex) {
                System.err.println("Request attempt #" + i + "/" + maxAttempts + " failed. Reason: " + ex.getMessage());
                System.err.println("-----------------------------------------------------------");

                connection.disconnect();
                Thread.sleep(30000);//wait 30 seconds between attempts
            } catch (Exception ex) {
                System.err.println("Request attempt #" + i + "/" + maxAttempts + " failed. Reason: " + ex.getMessage());
                System.err.println("-----------------------------------------------------------");
                connection.disconnect();
                if (i == maxAttempts) {
                    throw ex;//If Click isn't responding, signal that new requests should not continue to be issued
                }
                Thread.sleep(30000);//wait 30 seconds between attempts
            }

        }

        throw new MaxRequestAttemptsException();
    }

    private static void printOutput(InputStream in) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}
