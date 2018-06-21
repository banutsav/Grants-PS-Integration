package click_grants_datasynchronizer;

import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Glenn Owens
 */
public class Main {

    public static String clickAddress;
    public static String clickStoreName;
    public static String clickUsername;
    public static String clickPassword;
    public static String mysqlAddress;
    public static String mysqlUsername;
    public static String mysqlPassword;
    public static String mysqlExportSchema;
    public static String mysqlImportSchema;
    public static String clickSQLStoreName;
    public static String clickSQLUsername;
    public static String clickSQLPassword;
    public static String ssisdb;
    public static String ssisuser;
    public static String ssispass;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final long startTime = Calendar.getInstance().getTimeInMillis();

        System.out.println("");
        System.out.println("=================================");
        System.out.println("Upload People Soft data to Grants");
        System.out.println("=================================");
        System.out.println(Calendar.getInstance().getTime().toString());
        System.out.println("");

        try {
            loadProperties();
            final String svcSessionToken = WebServices.doLogin();
            try {
                // CHART STRING DATA FOR FINANCIAL ACCOUNTS

                //CLASS CODE
                getAwardClassData(svcSessionToken);
                getClassCodes();
                updateAwardClass(svcSessionToken);

                //FUND CODE
                getAwardFinancialData(svcSessionToken);
                getFundCodes();
                updateAwardFinancialFund(svcSessionToken);
                
                //PROGRAM CODE
                getAwardProgramData(svcSessionToken);
                getProgramCodes();
                updateAwardProgram(svcSessionToken);
                
                //EMP ID DATA FOR PEOPLE
                getPersons();
                updatePersons(svcSessionToken);
                
            } catch (Exception ex) {
                throw ex;
            } finally {
                WebServices.doLogoff(svcSessionToken);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        final long endTime = Calendar.getInstance().getTimeInMillis();
        final long elapsedTime = endTime - startTime;
        System.out.println("Total elapsed time: " + (elapsedTime / 60000) + "m " + (elapsedTime % 60000 / 1000) + "s");

        System.out.println("Program completed.");
    }

    // class code data export from grants to mysql1
    private static void getAwardClassData(String svcSessionToken) throws Exception {
        System.out.println("Getting Award Class Data from Grants...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Statement stmt = null;

        try {

            conn = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt = conn.createStatement();
            stmt.execute("START TRANSACTION;");

            try {

                //BEGIN TRANSACTION
                final String deleteQuery = "DELETE FROM `" + Main.mysqlExportSchema + "`.`Grants_Award_Class_Export`";
                stmt.executeUpdate(deleteQuery);

                final int batchSize = 500; // default 500
                boolean doContinue = true;

                for (int i = 0; doContinue; i++) {
                    final int startRow = batchSize * i + 1;
                    System.out.println("Getting " + batchSize + " Award Class Records from: " + startRow);
                    final Element rootElement = WebServices.getAwardClassData(startRow, batchSize, svcSessionToken);

                    final int returnedRowCount = Integer.valueOf(rootElement.getAttribute("returnedRowCount"));
                    if (returnedRowCount < batchSize) {
                        doContinue = false;//this will be the final iteration
                    }

                    final Element resultSet = (Element) rootElement.getElementsByTagName("resultSet").item(0);
                    final NodeList rowElements = resultSet.getElementsByTagName("row");

                    for (int j = 0; j < rowElements.getLength(); j++) {//each row
                        final Element rowElement = (Element) rowElements.item(j);

                        final HashMap<String, String> record = new HashMap();
                        final NodeList valueElements = rowElement.getElementsByTagName("value");
                        for (int k = 0; k < valueElements.getLength(); k++) {//each value
                            final Element valueElement = (Element) valueElements.item(k);

                            final String field = valueElement.getAttribute("columnHeader");
                            final String value = valueElement.getTextContent().trim();
                            record.put(field, value);
                        }

                        final String insertQuery = "INSERT INTO `" + Main.mysqlExportSchema + "`.`Grants_Award_Class_Export` SET "
                                + "`ID`='" + StringEscapeUtils.escapeSql(record.get("ID")) + "'"
                                + ",`Class Code`='" + StringEscapeUtils.escapeSql(record.get("Class Code")) + "'"
                                + ",`Description`='" + StringEscapeUtils.escapeSql(record.get("Description")) + "'"
                                + ",`Is Active`='" + StringEscapeUtils.escapeSql(record.get("Is Active")) + "'"
                                + ",`Short Description`='" + StringEscapeUtils.escapeSql(record.get("Short Description")) + "'"
                                + ",`Sort Order`='" + StringEscapeUtils.escapeSql(record.get("Sort Order")) + "'";

                        stmt.executeUpdate(insertQuery);
                    }
                }

                //END TRANSACTION
                stmt.execute("COMMIT;");

            } catch (Exception ex) {
                stmt.execute("ROLLBACK;");
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // award financial data export from grants to mysql1
    private static void getAwardFinancialData(String svcSessionToken) throws Exception {
        System.out.println("Getting Award Financial Fund Data from Grants...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Statement stmt = null;

        try {

            conn = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt = conn.createStatement();
            stmt.execute("START TRANSACTION;");

            try {

                //BEGIN TRANSACTION
                final String deleteQuery = "DELETE FROM `" + Main.mysqlExportSchema + "`.`Grants_Award_FinancialFund_Export`";
                stmt.executeUpdate(deleteQuery);

                final int batchSize = 500; // default 500
                boolean doContinue = true;

                for (int i = 0; doContinue; i++) {
                    final int startRow = batchSize * i + 1;
                    System.out.println("Getting " + batchSize + " Award Financial Fund Records from: " + startRow);
                    final Element rootElement = WebServices.getAwardFinancialData(startRow, batchSize, svcSessionToken);

                    final int returnedRowCount = Integer.valueOf(rootElement.getAttribute("returnedRowCount"));
                    if (returnedRowCount < batchSize) {
                        doContinue = false;//this will be the final iteration
                    }

                    final Element resultSet = (Element) rootElement.getElementsByTagName("resultSet").item(0);
                    final NodeList rowElements = resultSet.getElementsByTagName("row");

                    for (int j = 0; j < rowElements.getLength(); j++) {//each row
                        final Element rowElement = (Element) rowElements.item(j);

                        final HashMap<String, String> record = new HashMap();
                        final NodeList valueElements = rowElement.getElementsByTagName("value");
                        for (int k = 0; k < valueElements.getLength(); k++) {//each value
                            final Element valueElement = (Element) valueElements.item(k);

                            final String field = valueElement.getAttribute("columnHeader");
                            final String value = valueElement.getTextContent().trim();
                            record.put(field, value);
                        }

                        final String insertQuery = "INSERT INTO `" + Main.mysqlExportSchema + "`.`Grants_Award_FinancialFund_Export` SET "
                                + "`ID`='" + StringEscapeUtils.escapeSql(record.get("ID")) + "'"
                                + ",`Name`='" + StringEscapeUtils.escapeSql(record.get("Name")) + "'"
                                + ",`Short Description`='" + StringEscapeUtils.escapeSql(record.get("Short Description")) + "'"
                                + ",`Is Active`='" + StringEscapeUtils.escapeSql(record.get("Is Active")) + "'"
                                + ",`Fund Code`='" + StringEscapeUtils.escapeSql(record.get("Fund Code")) + "'";

                        stmt.executeUpdate(insertQuery);
                    }
                }

                //END TRANSACTION
                stmt.execute("COMMIT;");

            } catch (Exception ex) {
                stmt.execute("ROLLBACK;");
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // award program data export from grants to mysql1
    private static void getAwardProgramData(String svcSessionToken) throws Exception {
        System.out.println("Getting Award Program Data from Grants...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Statement stmt = null;

        try {

            conn = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt = conn.createStatement();
            stmt.execute("START TRANSACTION;");

            try {

                //BEGIN TRANSACTION
                final String deleteQuery = "DELETE FROM `" + Main.mysqlExportSchema + "`.`Grants_Award_Program_Export`";
                stmt.executeUpdate(deleteQuery);

                final int batchSize = 500; // default 500
                boolean doContinue = true;

                for (int i = 0; doContinue; i++) {
                    final int startRow = batchSize * i + 1;
                    System.out.println("Getting " + batchSize + " Award Program data Records from: " + startRow);
                    final Element rootElement = WebServices.getAwardProgramData(startRow, batchSize, svcSessionToken);

                    final int returnedRowCount = Integer.valueOf(rootElement.getAttribute("returnedRowCount"));
                    if (returnedRowCount < batchSize) {
                        doContinue = false;//this will be the final iteration
                    }

                    final Element resultSet = (Element) rootElement.getElementsByTagName("resultSet").item(0);
                    final NodeList rowElements = resultSet.getElementsByTagName("row");

                    for (int j = 0; j < rowElements.getLength(); j++) {//each row
                        final Element rowElement = (Element) rowElements.item(j);

                        final HashMap<String, String> record = new HashMap();
                        final NodeList valueElements = rowElement.getElementsByTagName("value");
                        for (int k = 0; k < valueElements.getLength(); k++) {//each value
                            final Element valueElement = (Element) valueElements.item(k);

                            final String field = valueElement.getAttribute("columnHeader");
                            final String value = valueElement.getTextContent().trim();
                            record.put(field, value);
                        }

                        final String insertQuery = "INSERT INTO `" + Main.mysqlExportSchema + "`.`Grants_Award_Program_Export` SET "
                                + "`ID`='" + StringEscapeUtils.escapeSql(record.get("ID")) + "'"
                                + ",`Name`='" + StringEscapeUtils.escapeSql(record.get("Name")) + "'"
                                + ",`Short Description`='" + StringEscapeUtils.escapeSql(record.get("Short Description")) + "'"
                                + ",`Is Active`='" + StringEscapeUtils.escapeSql(record.get("Is Active")) + "'"
                                + ",`default if cost share`='" + StringEscapeUtils.escapeSql(record.get("default if cost share")) + "'"
                                + ",`Sort Order`='" + StringEscapeUtils.escapeSql(record.get("Sort Order")) + "'"
                                + ",`Program Code`='" + StringEscapeUtils.escapeSql(record.get("Program Code")) + "'";

                        stmt.executeUpdate(insertQuery);
                    }
                }

                //END TRANSACTION
                stmt.execute("COMMIT;");

            } catch (Exception ex) {
                stmt.execute("ROLLBACK;");
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // class code data from ovpr-ssis mssql db
    private static void getClassCodes() throws Exception {
        System.out.println("Getting Class Code data from OVPR-SSIS MSSQL db...");

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        //credentials to connect to OVPR mysql database
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn1 = null;
        Statement stmt1 = null;

        try {
            System.out.println("Connecting to the MSSQL datastore...");
            String connectionUrl = "jdbc:sqlserver://" + ssisdb + ":1433;databaseName=OVPR_DB_OIRDW;schema=dbo;" + "user=" + ssisuser + ";password=" + ssispass;
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("connected");
            int count = 0;
            String SQL = "SELECT"
                    + " Class_ID"
                    + ", Class_Descr"
                    + ",Class_DescrShort"
                    + " FROM [OVPR_DB_OIRDW].[dbo].[PSFinancials_ODS_Class_CF_Current_VW]";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);

            // OVPR sql database
            conn1 = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt1 = conn1.createStatement();

            //BEGIN TRANSACTION
            final String deleteQuery = "DELETE FROM `" + Main.mysqlExportSchema + "`.`class_codes`";
            stmt1.executeUpdate(deleteQuery);

            System.out.println("Iterating through the MSSQL resultset...");

            while (rs.next()) {
                final String insertQuery = "INSERT INTO `" + Main.mysqlExportSchema + "`.`class_codes` SET"
                        + " `Class_ID`='" + StringEscapeUtils.escapeSql(rs.getString("Class_ID")) + "'"
                        + ",`Class_Descr`='" + StringEscapeUtils.escapeSql(rs.getString("Class_Descr")) + "'"
                        + ",`Class_DescrShort`='" + StringEscapeUtils.escapeSql(rs.getString("Class_DescrShort")) + "'";

                stmt1.executeUpdate(insertQuery);
                count++;
            }

            System.out.println("Total Class Code records = " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (stmt1 != null) {
                try {
                    stmt1.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
            if (conn1 != null) {
                try {
                    conn1.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // fund code data from ovpr-ssis mssql db
    private static void getFundCodes() throws Exception {
        System.out.println("Getting Fund Code data from OVPR-SSIS MSSQL db...");

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        //credentials to connect to OVPR mysql database
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn1 = null;
        Statement stmt1 = null;

        try {
            System.out.println("Connecting to the MSSQL datastore...");
            String connectionUrl = "jdbc:sqlserver://" + ssisdb + ":1433;databaseName=OVPR_DB_OIRDW;schema=dbo;" + "user=" + ssisuser + ";password=" + ssispass;
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("connected");
            int count = 0;
            String SQL = "SELECT"
                    + " Fund_Code"
                    + ", Fund_Descr"
                    + ", Fund_DescrShort"
                    + " FROM [OVPR_DB_OIRDW].[dbo].[PSFinancials_ODS_Fund_Current_VW]";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);

            // OVPR sql database
            conn1 = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt1 = conn1.createStatement();

            //BEGIN TRANSACTION
            final String deleteQuery = "DELETE FROM `" + Main.mysqlExportSchema + "`.`fund_codes`";
            stmt1.executeUpdate(deleteQuery);

            System.out.println("Iterating through the MSSQL resultset...");

            while (rs.next()) {
                final String insertQuery = "INSERT INTO `" + Main.mysqlExportSchema + "`.`fund_codes` SET"
                        + " `Fund_Code`='" + StringEscapeUtils.escapeSql(rs.getString("Fund_Code")) + "'"
                        + ",`Fund_Descr`='" + StringEscapeUtils.escapeSql(rs.getString("Fund_Descr")) + "'"
                        + ",`Fund_DescrShort`='" + StringEscapeUtils.escapeSql(rs.getString("Fund_DescrShort")) + "'";

                stmt1.executeUpdate(insertQuery);
                count++;
            }

            System.out.println("Total Fund Code records = " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (stmt1 != null) {
                try {
                    stmt1.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
            if (conn1 != null) {
                try {
                    conn1.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private static void getProgramCodes() throws Exception {
        System.out.println("Getting Program Code data from OVPR-SSIS MSSQL db...");

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        //credentials to connect to OVPR mysql database
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn1 = null;
        Statement stmt1 = null;

        try {
            System.out.println("Connecting to the MSSQL datastore...");
            String connectionUrl = "jdbc:sqlserver://" + ssisdb + ":1433;databaseName=OVPR_DB_OIRDW;schema=dbo;" + "user=" + ssisuser + ";password=" + ssispass;
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("connected");
            int count = 0;
            String SQL = "SELECT"
                    + " Program_Code"
                    + ", Program_Descr"
                    + ", Program_DescrShort"
                    + " FROM [OVPR_DB_OIRDW].[dbo].[PSFinancials_ODS_Program_Current_VW]";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);

            // OVPR sql database
            conn1 = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt1 = conn1.createStatement();

            //BEGIN TRANSACTION
            final String deleteQuery = "DELETE FROM `" + Main.mysqlExportSchema + "`.`program_codes`";
            stmt1.executeUpdate(deleteQuery);

            System.out.println("Iterating through the MSSQL resultset...");

            while (rs.next()) {
                final String insertQuery = "INSERT INTO `" + Main.mysqlExportSchema + "`.`program_codes` SET"
                        + " `Program_Code`='" + StringEscapeUtils.escapeSql(rs.getString("Program_Code")) + "'"
                        + ",`Program_Descr`='" + StringEscapeUtils.escapeSql(rs.getString("Program_Descr")) + "'"
                        + ",`Program_DescrShort`='" + StringEscapeUtils.escapeSql(rs.getString("Program_DescrShort")) + "'";

                stmt1.executeUpdate(insertQuery);
                count++;
            }

            System.out.println("Total Program Code records = " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (stmt1 != null) {
                try {
                    stmt1.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
            if (conn1 != null) {
                try {
                    conn1.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // person data export from grants mssql db to mysql1
    private static void getPersons() throws Exception {
        System.out.println("Getting Persons via a direct sql query to GRANTS...");

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        //credentials to connect to OVPR mysql database
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn1 = null;
        Statement stmt1 = null, stmt2 = null;

        try {
            System.out.println("connecting to the datastore");
            String connectionUrl = "jdbc:sqlserver://" + clickSQLStoreName + ":1433;databaseName=grants;schema=dbo;" + "user=" + clickSQLUsername + ";password=" + clickSQLPassword;
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("connected");
            int count = 0;

            String SQL = "Select vp.id AS 'ID', "
                    + " vp.userId AS 'User ID' "
                    + " From [grants].[dbo].[view__Person] AS vp"
                    + " where vp.id IS NOT NULL and vp.id != '' and (vp.id LIKE '8%' or vp.id LIKE '9%')"
                    + " order by vp.id";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);

            // OVPR sql database
            conn1 = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt1 = conn1.createStatement();

            //BEGIN TRANSACTION
            final String deleteQuery = "DELETE FROM `" + Main.mysqlExportSchema + "`.`grants_people`";
            stmt1.executeUpdate(deleteQuery);

            System.out.println("Iterating through the click resultset...");

            while (rs.next()) {
                final String insertQuery = "INSERT INTO `" + Main.mysqlExportSchema + "`.`grants_people` SET"
                        + " `ugaid`='" + StringEscapeUtils.escapeSql(rs.getString("ID")) + "'"
                        + ",`userid`='" + StringEscapeUtils.escapeSql(rs.getString("User ID")) + "'";

                stmt1.executeUpdate(insertQuery);
                count++;
            }
            System.out.println("Total people records = " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (stmt1 != null) {
                try {
                    stmt1.close();
                } catch (Exception ex) {
                }
            }
            if (stmt2 != null) {
                try {
                    stmt2.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
            if (conn1 != null) {
                try {
                    conn1.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // update the award class CDT
    private static void updateAwardClass(String svcSessionToken) throws Exception {
        System.out.println("Updating Award Class data in Grants...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        int count = 0, batch_size = 10;
        try {
            conn = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt = conn.createStatement();

            final String query = "select"
                    + " `Class_ID`"
                    + ",`Class_Descr`"
                    + ",`Class_DescrShort`"
                    + "from `" + Main.mysqlImportSchema + "`.`Class_Codes_Add`"
                    + "order by `Class_ID`";
            final ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                final AwardClass obj = new AwardClass();
                obj.Class_ID = rs.getString("Class_ID");
                obj.Class_Descr = rs.getString("Class_Descr");
                obj.Class_DescrShort = rs.getString("Class_DescrShort");
                try {
                    WebServices.updateAwardClass(obj, svcSessionToken);
                } catch (MaxRequestAttemptsException ex) {
                    System.err.println(ex.getMessage());
                }
                count++;
                if (count % batch_size == 0) {
                    int start = (((count / batch_size) - 1) * batch_size) + 1;
                    int end = start + batch_size - 1;
                    System.out.println("Updated " + start + " to " + end + " records");
                }
            }
            System.out.println("Total Award Class CDT records updated = " + count);
            rs.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // update award financial fund CDT
    private static void updateAwardFinancialFund(String svcSessionToken) throws Exception {
        System.out.println("Updating Award Financial Fund data in Grants...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        int count = 0, batch_size = 10;
        try {
            conn = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt = conn.createStatement();

            final String query = "select"
                    + " `Fund_Code`"
                    + ",`Fund_Descr`"
                    + ",`Fund_DescrShort`"
                    + "from `" + Main.mysqlImportSchema + "`.`Fund_Codes_Add`"
                    + "order by `Fund_Code`";
            final ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                final AwardFinancialFund obj = new AwardFinancialFund();
                obj.Fund_Code = rs.getString("Fund_Code");
                obj.Name = rs.getString("Fund_Descr");
                obj.Short_Description = rs.getString("Fund_DescrShort");
                try {
                    WebServices.updateAwardFinancialFund(obj, svcSessionToken);
                } catch (MaxRequestAttemptsException ex) {
                    System.err.println(ex.getMessage());
                }
                count++;
                if (count % batch_size == 0) {
                    int start = (((count / batch_size) - 1) * batch_size) + 1;
                    int end = start + batch_size - 1;
                    System.out.println("Updated " + start + " to " + end + " records");
                }
            }
            System.out.println("Total Award Financial Fund CDT records updated = " + count);
            rs.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // update award program CDT
    private static void updateAwardProgram(String svcSessionToken) throws Exception {
        System.out.println("Updating Award Program data in Grants...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        int count = 0, batch_size = 10;
        try {
            conn = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt = conn.createStatement();

            final String query = "select"
                    + " `Program_Code`"
                    + ",`Program_Descr`"
                    + ",`Program_DescrShort`"
                    + "from `" + Main.mysqlImportSchema + "`.`Program_Codes_Add`"
                    + "order by `Program_Code`";
            final ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                final AwardProgram obj = new AwardProgram();
                obj.Program_Code = rs.getString("Program_Code");
                obj.Name = rs.getString("Program_Descr");
                obj.Short_Description = rs.getString("Program_DescrShort");
                try {
                    WebServices.updateAwardProgram(obj, svcSessionToken);
                } catch (MaxRequestAttemptsException ex) {
                    System.err.println(ex.getMessage());
                }
                count++;
                if (count % batch_size == 0) {
                    int start = (((count / batch_size) - 1) * batch_size) + 1;
                    int end = start + batch_size - 1;
                    System.out.println("Updated " + start + " to " + end + " records");
                }
            }
            System.out.println("Total Award Financial Fund CDT records updated = " + count);
            rs.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    // update person data with the EmpID from PeopleSoft
    private static void updatePersons(String svcSessionToken) throws Exception {
        System.out.println("Updating EMPID for Persons in Click...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = null;
        Statement stmt = null;
        int count = 0, batch_size = 100;
        try {
            conn = DriverManager.getConnection(Main.mysqlAddress, Main.mysqlUsername, Main.mysqlPassword);
            stmt = conn.createStatement();

            final String query = "select"
                    + " `ugaid`"
                    + ",`EMPID`"
                    + "from `" + Main.mysqlImportSchema + "`.`employee_id`"
                    + "order by `UGAID`";
            final ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                final Person obj = new Person();
                obj.UGAID = rs.getString("UGAID");
                obj.EMPID = rs.getString("EMPID");
                //System.out.println("Updating Person " + obj.UGAID + ": " + obj.EMPID);
                try {
                    WebServices.updatePerson(obj, svcSessionToken);
                } catch (MaxRequestAttemptsException ex) {
                    System.err.println(ex.getMessage());
                }
                count++;
                if (count % batch_size == 0) {
                    int start = (((count / batch_size) - 1) * batch_size) + 1;
                    int end = start + batch_size - 1;
                    System.out.println("Updated " + start + " to " + end + " records");
                }
            }
            System.out.println("Total person records updated = " + count);
            rs.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    //UTILITY
    private static void loadProperties() throws Exception {
        FileInputStream in = null;
        try {
            final String temp = Main.class.getProtectionDomain().getCodeSource().getLocation().toString();
            final String jarFolder = temp.substring(temp.indexOf("/"), temp.lastIndexOf("/") + 1);
            in = new FileInputStream(jarFolder + "config.properties");
            final Properties props = new Properties();
            props.load(in);
            clickAddress = props.getProperty("clickAddress");
            clickStoreName = props.getProperty("clickStoreName");
            clickUsername = props.getProperty("clickUsername");
            clickPassword = props.getProperty("clickPassword");
            mysqlAddress = props.getProperty("mysqlAddress");
            mysqlUsername = props.getProperty("mysqlUsername");
            mysqlPassword = props.getProperty("mysqlPassword");
            mysqlExportSchema = props.getProperty("mysqlExportSchema");
            mysqlImportSchema = props.getProperty("mysqlImportSchema");

            //properties for grants mssql DB
            clickSQLStoreName = props.getProperty("clickSQLStoreName");
            clickSQLUsername = props.getProperty("clickSQLUsername");
            clickSQLPassword = props.getProperty("clickSQLPassword");

            //properties for OVPR-SSIS mssql db
            ssisdb = props.getProperty("ssisdb");
            ssisuser = props.getProperty("ssisuser");
            ssispass = props.getProperty("ssispass");

        } catch (Exception ex) {
            throw ex;
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
    }

}
