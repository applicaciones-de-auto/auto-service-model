/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.model.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Model_JobOrder_Master implements GEntity {
    final String XML = "Model_JobOrder_Master.xml";
    private final String psDefaultDate = "1900-01-01";
    private String psBranchCd;
    private String psExclude = "sOwnrNmxx»cClientTp»sAddressx»sCoOwnrNm»sCSNoxxxx»sFrameNox»sEngineNo»cVhclNewx»sPlateNox»sVhclDesc»sVSPNOxxx»sBranchCD»sBranchNm»sCoBuyrNm»sEmployNm"; //»

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_JobOrder_Master(GRider foValue) {
        if (foValue == null) {
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }

        poGRider = foValue;

        initialize();
    }
    
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);        
            poEntity.updateObject("dTransact", poGRider.getServerDate()); 
            poEntity.updateObject("dPromised", poGRider.getServerDate());   
            poEntity.updateString("cTranStat", TransactionStatus.STATE_OPEN); //TransactionStatus.STATE_OPEN why is the value of STATE_OPEN is 0 while record status active is 1
            poEntity.updateString("cPrintedx", "0"); 
            //RecordStatus.ACTIVE
            poEntity.updateInt("nKMReadng", 0);   
            
            poEntity.updateBigDecimal("nLaborAmt", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nPartsAmt", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nTranAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nRcvryAmt", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nPartFeex", new BigDecimal("0.00"));
            
            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Gets the column index name.
     *
     * @param fnValue - column index number
     * @return column index name
     */
    @Override
    public String getColumn(int fnValue) {
        try {
            return poEntity.getMetaData().getColumnLabel(fnValue);
        } catch (SQLException e) {
        }
        return "";
    }

    /**
     * Gets the column index number.
     *
     * @param fsValue - column index name
     * @return column index number
     */
    @Override
    public int getColumn(String fsValue) {
        try {
            return MiscUtil.getColumnIndex(poEntity, fsValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets the total number of column.
     *
     * @return total number of column
     */
    @Override
    public int getColumnCount() {
        try {
            return poEntity.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public String getTable() {
        return "diagnostic_master";
    }
    
    /**
     * Gets the value of a column index number.
     *
     * @param fnColumn - column index number
     * @return object value
     */
    @Override
    public Object getValue(int fnColumn) {
        try {
            return poEntity.getObject(fnColumn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the value of a column index name.
     *
     * @param fsColumn - column index name
     * @return object value
     */
    @Override
    public Object getValue(String fsColumn) {
        try {
            return poEntity.getObject(MiscUtil.getColumnIndex(poEntity, fsColumn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets column value.
     *
     * @param fnColumn - column index number
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(int fnColumn, Object foValue) {
        try {
            poJSON = MiscUtil.validateColumnValue(System.getProperty("sys.default.path.metadata") + XML, MiscUtil.getColumnLabel(poEntity, fnColumn), foValue);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poEntity.updateObject(fnColumn, foValue);
            poEntity.updateRow();

            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put("value", getValue(fnColumn));
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }
    
    /**
     * Sets column value.
     *
     * @param fsColumn - column index name
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(String fsColumn, Object foValue) {
        poJSON = new JSONObject();

        try {
            return setValue(MiscUtil.getColumnIndex(poEntity, fsColumn), foValue);
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }

    /**
     * Set the edit mode of the entity to new.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject newRecord() {
        pnEditMode = EditMode.ADDNEW;

        //replace with the primary key column info
        setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"JO"));
        setDSNo(MiscUtil.getNextCode(getTable(), "sDSNoxxxx", true, poGRider.getConnection(), poGRider.getBranchCode()));
        setTransactDte(poGRider.getServerDate());
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Opens a record.
     *
     * @param fsValue - filter values
     * @return result as success/failed
     */
    @Override
    public JSONObject openRecord(String fsValue) {
        poJSON = new JSONObject();

        String lsSQL = getSQL(); //MiscUtil.makeSelect(this, psExclude); //exclude the columns called thru left join
        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox = " + SQLUtil.toSQL(fsValue)
                                                //+ " GROUP BY a.sTransNox "
                                                );

        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            if (loRS.next()) {
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
                    setValue(lnCtr, loRS.getObject(lnCtr));
                }

                pnEditMode = EditMode.UPDATE;

                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "No record to load.");
            }
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Save the entity.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject saveRecord() {
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL; 
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"JO"));
                setDSNo(MiscUtil.getNextCode(getTable(), "sDSNoxxxx", true, poGRider.getConnection(), poGRider.getBranchCode()));
                setEntryBy(poGRider.getUserID());
                setEntryDte(poGRider.getServerDate());
                setModifiedBy(poGRider.getUserID());
                setModifiedDte(poGRider.getServerDate());
                
                lsSQL = MiscUtil.makeSQL(this, psExclude);
                
               // lsSQL = "Select * FROM " + getTable() + " a left join (" + makeSQL() + ") b on a.column1 = b.column "
                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), getTargetBranchCd()) > 0) {
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON.put("result", "error");
                        poJSON.put("message", poGRider.getErrMsg());
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "No record to save.");
                }
            } else {
                Model_JobOrder_Master loOldEntity = new Model_JobOrder_Master(poGRider);
                
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getTransNo());

                if ("success".equals((String) loJSON.get("result"))) {
                    setModifiedBy(poGRider.getUserID());
                    setModifiedDte(poGRider.getServerDate());
                    
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sTransNox = " + SQLUtil.toSQL(this.getTransNo()), psExclude);

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), getTargetBranchCd()) > 0) {
                            poJSON.put("result", "success");
                            poJSON.put("message", "Record saved successfully.");
                        } else {
                            poJSON.put("result", "error");
                            poJSON.put("message", poGRider.getErrMsg());
                        }
                    } else {
                        poJSON.put("result", "success");
                        poJSON.put("message", "No updates has been made.");
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Record discrepancy. Unable to save record.");
                }
            }
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid update mode. Unable to save record.");
            return poJSON;
        }

        return poJSON;
    }
    
    private String getTargetBranchCd(){
        if (!poGRider.getBranchCode().equals(getBranchCD())){
            return getBranchCD();
        } else {
            return "";
        }
    }

    /**
     * Prints all the public methods used<br>
     * and prints the column names of this entity.
     */
    @Override
    public void list() {
        Method[] methods = this.getClass().getMethods();

        System.out.println("--------------------------------------------------------------------");
        System.out.println("LIST OF PUBLIC METHODS FOR " + this.getClass().getName() + ":");
        System.out.println("--------------------------------------------------------------------");
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        try {
            int lnRow = poEntity.getMetaData().getColumnCount();

            System.out.println("--------------------------------------------------------------------");
            System.out.println("ENTITY COLUMN INFO");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Total number of columns: " + lnRow);
            System.out.println("--------------------------------------------------------------------");

            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++) {
                System.out.println("Column index: " + (lnCtr) + " --> Label: " + poEntity.getMetaData().getColumnLabel(lnCtr));
                if (poEntity.getMetaData().getColumnType(lnCtr) == Types.CHAR
                        || poEntity.getMetaData().getColumnType(lnCtr) == Types.VARCHAR) {

                    System.out.println("Column index: " + (lnCtr) + " --> Size: " + poEntity.getMetaData().getColumnDisplaySize(lnCtr));
                }
            }
        } catch (SQLException e) {
        }

    }
    
    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, psExclude);
    }
    
    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SQL Select Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this);
    }
    
    private String getSQL(){
        return    " SELECT "                                                                         
                + "   a.sTransNox "                                                                  
                + " , a.dTransact "                                                                  
                + " , a.sDSNoxxxx "                                                                  
                + " , a.sSerialID "                                                                  
                + " , a.sClientID "                                                                  
                + " , a.sWorkCtgy "                                                                  
                + " , a.sJobTypex "                                                                  
                + " , a.sLaborTyp "                                                                  
                + " , a.nKMReadng "                                                                  
                + " , a.sEmployID "                                                                  
                + " , a.sRemarksx "                                                                  
                + " , a.cPaySrcex "                                                                  
                + " , a.sSourceNo "                                                                  
                + " , a.sSourceCD "                                                                  
                + " , a.dPromised "                                                                  
                + " , a.sInsurnce "                                                                  
                + " , a.cCompUnit "                                                                  
                + " , a.sActvtyID "                                                                  
                + " , a.nLaborAmt "                                                                  
                + " , a.nPartsAmt "                                                                  
                + " , a.nTranAmtx "                                                                  
                + " , a.nRcvryAmt "                                                                  
                + " , a.nPartFeex "                                                                  
                + " , a.cPrintedx "                                                                  
                + " , a.cTranStat "                                                                  
                + " , a.sEntryByx "                                                                  
                + " , a.dEntryDte "                                                                  
                + " , a.sModified "                                                                  
                + " , a.dModified "                                                                  
                + " , b.sCompnyNm AS sOwnrNmxx "                                                     
                + " , b.cClientTp "                                                                  
                + " , IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                         
                + "   IFNULL(CONCAT(d.sAddressx,' ') , ''), "                                        
                + "   IFNULL(CONCAT(e.sBrgyName,' '), ''),  "                                        
                + "   IFNULL(CONCAT(f.sTownName, ', '),''), "                                        
                + "   IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx "                         
                + " , k.sCompnyNm AS sCoOwnrNm "                                                     
                + " , h.sCSNoxxxx "                                                                  
                + " , h.sFrameNox "                                                                  
                + " , h.sEngineNo "                                                                
                + " , h.cVhclNewx "                                                                  
                + " , i.sPlateNox "                                                                  
                + " , j.sDescript AS sVhclDesc"                                                                  
                + " , l.sVSPNOxxx "                                                                  
                + " , l.sBranchCD "                                                                  
                + " , n.sBranchNm "                                                                  
                + " , m.sCompnyNm AS sCoBuyrNm "                                                     
                + " , o.sCompnyNm AS sEmployNm "                                                     
                + " FROM diagnostic_master a "                                                       
                + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "                         
                + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = '1' "  
                + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                             
                + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                             
                + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                             
                + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "                             
                + " LEFT JOIN vehicle_serial h ON h.sSerialID = a.sSerialID "                        
                + " LEFT JOIN vehicle_serial_registration i ON i.sSerialID = a.sSerialID "           
                + " LEFT JOIN vehicle_master j ON j.sVhclIDxx = h.sVhclIDxx "                        
                + " LEFT JOIN client_master k ON k.sClientID = h.sCoCltIDx " /*co-owner*/            
                + " LEFT JOIN vsp_master l ON l.sTransNox = a.sSourceNo "                            
                + " LEFT JOIN client_master m ON m.sClientID = l.sCoCltIDx " /*co-buyer*/            
                + " LEFT JOIN branch n ON n.sBranchCd = l.sBranchCD "                                
                + " LEFT JOIN ggc_isysdbf.client_master o ON o.sClientID = a.sEmployID "    ;                          
    }
    
    private static String xsDateShort(Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fdValue);
        return date;
    }

    private static String xsDateShort(String fsValue) throws org.json.simple.parser.ParseException, java.text.ParseException {
        SimpleDateFormat fromUser = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lsResult = "";
        lsResult = myFormat.format(fromUser.parse(fsValue));
        return lsResult;
    }
    
    /*Convert Date to String*/
    private LocalDate strToDate(String val) {
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(val, date_formatter);
        return localDate;
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTransNo(String fsValue) {
        return setValue("sTransNox", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getTransNo() {
        return (String) getValue("sTransNox");
    }
    
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setTransactDte(Date fdValue) {
        return setValue("dTransact", fdValue);
    }

    /**
     * @return The ID of this record.
     */
    public Date getTransactDte() {
        return (Date) getValue("dTransact");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setDSNo(String fsValue) {
        return setValue("sDSNoxxxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getDSNo() {
        return (String) getValue("sDSNoxxxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSerialID(String fsValue) {
        return setValue("sSerialID", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getSerialID() {
        return (String) getValue("sSerialID");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setClientID(String fsValue) {
        return setValue("sClientID", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getClientID() {
        return (String) getValue("sClientID");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setWorkCtgy(String fsValue) {
        return setValue("sWorkCtgy", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getWorkCtgy() {
        return (String) getValue("sWorkCtgy");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setJobType(String fsValue) {
        return setValue("sJobTypex", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getJobType() {
        return (String) getValue("sJobTypex");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setLaborTyp(String fsValue) {
        return setValue("sLaborTyp", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getLaborTyp() {
        return (String) getValue("sLaborTyp");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fnValue
     * @return result as success/failed
     */
    public JSONObject setKMReadng(Double fnValue) {
        return setValue("nKMReadng", fnValue);
    }

    /**
     * @return The Value of this record.
     */
    public Double getKMReadng() {
        //return Integer.parseInt(String.valueOf(getValue("nKMReadng")));
        return Double.parseDouble(String.valueOf(getValue("nKMReadng")));
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEmployID(String fsValue) {
        return setValue("sEmployID", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getEmployID() {
        return (String) getValue("sEmployID");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setRemarks(String fsValue) {
        return setValue("sRemarksx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getRemarks() {
        return (String) getValue("sRemarksx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPaySrce(String fsValue) {
        return setValue("cPaySrcex", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPaySrce() {
        return (String) getValue("cPaySrcex");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceNo(String fsValue) {
        return setValue("sSourceNo", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getSourceNo() {
        return (String) getValue("sSourceNo");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceCD(String fsValue) {
        return setValue("sSourceCD", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getSourceCD() {
        return (String) getValue("sSourceCD");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setPromisedDte(Date fdValue) {
        Timestamp timestamp = new Timestamp(((Date) fdValue).getTime());
        return setValue("dPromised", timestamp); //dPromised datatype is time stamp so convert it into timestamp
    }
    
    /**
     * @return The Value of this record.
     */
    public Date getPromisedDte() {
        Date date = null;
        if(getValue("dPromised") == null || getValue("dPromised").equals("")){
            date = SQLUtil.toDate(psDefaultDate, SQLUtil.FORMAT_SHORT_DATE);
        } else {
            date = SQLUtil.toDate(xsDateShort((Date) getValue("dPromised")), SQLUtil.FORMAT_SHORT_DATE);
        }
            
        return date;
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setInsurnce(String fsValue) {
        return setValue("sInsurnce", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getInsurnce() {
        return (String) getValue("sInsurnce");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setCompUnit(String fsValue) {
        return setValue("cCompUnit", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getCompUnit() {
        return (String) getValue("cCompUnit");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setActvtyID(String fsValue) {
        return setValue("sActvtyID", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getActvtyID() {
        return (String) getValue("sActvtyID");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setLaborAmt(BigDecimal fdbValue) {
        return setValue("nLaborAmt", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getLaborAmt() {
        if(getValue("nLaborAmt") == null || getValue("nLaborAmt").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nLaborAmt")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setPartsAmt(BigDecimal fdbValue) {
        return setValue("nPartsAmt", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getPartsAmt() {
        if(getValue("nPartsAmt") == null || getValue("nPartsAmt").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nPartsAmt")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setTranAmt(BigDecimal fdbValue) {
        return setValue("nTranAmtx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getTranAmt() {
        if(getValue("nTranAmtx") == null || getValue("nTranAmtx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nTranAmtx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setRcvryAmt(BigDecimal fdbValue) {
        return setValue("nRcvryAmt", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getRcvryAmt() {
        if(getValue("nRcvryAmt") == null || getValue("nRcvryAmt").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nRcvryAmt")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setPartFee(BigDecimal fdbValue) {
        return setValue("nPartFeex", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getPartFee() {
        if(getValue("nPartFeex") == null || getValue("nPartFeex").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nPartFeex")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPrinted(String fsValue) {
        return setValue("cPrintedx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPrinted() {
        return (String) getValue("cPrintedx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTranStat(String fsValue) {
        return setValue("cTranStat", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTranStat() {
        return (String) getValue("cTranStat");
    }
    
//    /**
//     * Sets record as active.
//     *
//     * @param fbValue
//     * @return result as success/failed
//     */
//    public JSONObject setActive(boolean fbValue) {
//        return setValue("cTranStat", fbValue ? "1" : "0");
//    }
//
//    /**
//     * @return If record is active.
//     */
//    public boolean isActive() {
//        return ((String) getValue("cTranStat")).equals("1");
//    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEntryBy(String fsValue) {
        return setValue("sEntryByx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getEntryBy() {
        return (String) getValue("sEntryByx");
    }
    
    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setEntryDte(Date fdValue) {
        return setValue("dEntryDte", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getEntryDte() {
        return (Date) getValue("dEntryDte");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModifiedBy(String fsValue) {
        return setValue("sModified", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getModifiedBy() {
        return (String) getValue("sModified");
    }
    
    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setModifiedDte(Date fdValue) {
        return setValue("dModified", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getModifiedDte() {
        return (Date) getValue("dModified");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setOwnrNm(String fsValue) {
        return setValue("sOwnrNmxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getOwnrNm() {
        return (String) getValue("sOwnrNmxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setClientTp(String fsValue) {
        return setValue("cClientTp", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getClientTp() {
        return (String) getValue("cClientTp");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setAddress(String fsValue) {
        return setValue("sAddressx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getAddress() {
        return (String) getValue("sAddressx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setCoOwnrNm(String fsValue) {
        return setValue("sCoOwnrNm", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getCoOwnrNm() {
        return (String) getValue("sCoOwnrNm");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setVSPNo(String fsValue) {
        return setValue("sVSPNOxxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getVSPNo() {
        return (String) getValue("sVSPNOxxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setVhclNew(String fsValue) {
        return setValue("cVhclNewx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getVhclNew() {
        return (String) getValue("cVhclNewx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setCoBuyrNm(String fsValue) {
        return setValue("sCoBuyrNm", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getCoBuyrNm() {
        return (String) getValue("sCoBuyrNm");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setVhclDesc(String fsValue) {
        return setValue("sVhclDesc", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getVhclDesc() {
        return (String) getValue("sVhclDesc");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setCSNo(String fsValue) {
        return setValue("sCSNoxxxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getCSNo() {
        return (String) getValue("sCSNoxxxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPlateNo(String fsValue) {
        return setValue("sPlateNox", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPlateNo() {
        return (String) getValue("sPlateNox");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setFrameNo(String fsValue) {
        return setValue("sFrameNox", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getFrameNo() {
        return (String) getValue("sFrameNox");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEngineNo(String fsValue) {
        return setValue("sEngineNo", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getEngineNo() {
        return (String) getValue("sEngineNo");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEmployNm(String fsValue) {
        return setValue("sEmployNm", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getEmployNm() {
        return (String) getValue("sEmployNm");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchCD(String fsValue) {
        return setValue("sBranchCD", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBranchCD() {
        return (String) getValue("sBranchCD");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchNm(String fsValue) {
        return setValue("sBranchNm", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBranchNm() {
        return (String) getValue("sBranchNm");
    }
}
