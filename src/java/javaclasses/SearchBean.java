/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaclasses;

/**
 *
 * @author nk91008743
 */

import javax.faces.event.ActionEvent;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import oracle.jdbc.OracleDriver;
import org.apache.catalina.tribes.util.Arrays;

@ManagedBean(name = "searchBean")
@ViewScoped
public class SearchBean  implements Serializable{

    
    private String server;
    private String[] selectedTypes;
    private String searchText;
    private ArrayList <String> result; 
    private String resultAsText;
    private String serverOrSid = "";
    private String dbname = "";
    private String userName;
    
    
    
    @PostConstruct
    public void init(){
   
    }
    
    public String getServerOrSid() {
        return serverOrSid;
    }

    public void setServerOrSid(String serverOrSid) {
        this.serverOrSid = serverOrSid;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
    public int number;
    
    public int getNumber() {
        return number;
    }
 
    public void increment() {
        number++;
    }

    public String getResultAsText() {
        
        if(this.result!=null)
        {
            String out = "";
            for(String s: this.result)
                out += s+"\n";
            return out;
        }
        return null;
    }

    public void setResultAsText(String resultAsText) {
        this.resultAsText = resultAsText;
    }
    
    public ArrayList<String> getResult() {
        return result;
    }

    public void setResult(ArrayList<String> result) {
        this.result = result;
    }

    public String getSearchText() {
        return this.searchText;
    }

    public void setSearchText(String text) {
        this.searchText = text;
    }

    
    public String[] getSelectedTypes() {
        return selectedTypes;
    }

    public void setSelectedTypes(String[] selectedTypes) {
        this.selectedTypes = selectedTypes;
    }
    
    public String getTypesAsString(){
        return Arrays.toString(selectedTypes);
    }
    
    public void dbresult(ActionEvent actionEvent) throws SQLException{
        System.out.println("Searching "+this.getSearchText()+" on " + this.getServerOrSid());
        
        if(this.getSearchText()!=null){
            
            ArrayList<String> types = new ArrayList<>();

            types.addAll(java.util.Arrays.asList(new String[]{"nchar","bigint","int","smallint","nvarchar","uniqueidentifier","varchar","ntext","text"}));
            
            if(this.getServer().equals("mssql2008"))
                result = getResultsSQL2008(this.getServerOrSid(),this.getDbname(),this.getUserName(),this.getPassword(), types, this.getSearchText());
            else 
                result = getResultsOracle( types, this.getSearchText());
            
        }
        
    }
    public  Connection getCon(String server, String serverOrTns,String dbName, String userName, String password ){
        Connection conn = null;
        
        if(server.equals("oracle")){
            String connectionUrl = "jdbc:oracle:thin:@"+serverOrTns;
            try{
                DriverManager.registerDriver(new OracleDriver());
                conn = DriverManager.getConnection(connectionUrl,userName,password);
            }
            catch(Exception e){e.printStackTrace();}
        }
        else if(server.equals("mssql2000")||server.equals("mssql2008")){
            String connectionUrl = "jdbc:jtds:sqlserver://"+serverOrTns+";DatabaseName="+dbName;
            try{
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                
                conn = DriverManager.getConnection(connectionUrl,userName, password);
                //Temporary for local use
                //conn = DriverManager.getConnection("jdbc:jtds:sqlserver://ncatwks1527:1433;DatabaseName=AdventureWorks2008;instance=SQLEXPRESS;user=sa;password=Kobasoft1989");
            }
            catch(Exception e){e.printStackTrace();}
        }
        return conn;
        
       }
    
    public  boolean contains (ArrayList <String> list, String s){
        for(String t : list)
            if(t.equals(s))
                return true;
        return false;
    }
    public   ArrayList<String> getResultsSQL2008(String serverName, String dbName, String userName, String password, ArrayList <String> types, String text){
        Connection conn = getCon("mssql2008",serverName,dbName,userName,password);
        ArrayList<String> res = new ArrayList<>();
                
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        PreparedStatement pst3 = null;
        
        
        try{
            
            pst1 = conn.prepareStatement("select table_schema, table_name from information_schema.tables");
            rs1 = pst1.executeQuery();
            
            while(rs1.next()){
                String schema = rs1.getString(1);
                String table = rs1.getString(2);
                
                pst2 = conn.prepareStatement("select column_name, data_type from information_schema.columns  where table_schema = ? and table_name = ?");
                pst2.setString(1, schema);
                pst2.setString(2, table);
                
                rs2 = pst2.executeQuery();
                while(rs2.next()){
                    String column = rs2.getString(1);
                    String type = rs2.getString(2);
                        if(contains(types,type)){
                            String sql3 = "select count(*) from "+ schema+"."+table+" where [" + column + "] = " + text;
                            pst3 = conn.prepareStatement("select count (*) from "+schema+"."+table+" where ["+column+"]= ? ");
                            if(isNumber(text)&&(type.equals("int")||type.equals("bigint")||type.equals("smallint")))
                                pst3.setInt(1, Integer.parseInt(text));
                            else  if(!isNumber(text)&&(type.equals("varchar")||type.equals("char")||type.equals("nvarchar")||type.equals("uniqueidentifier")))
                                pst3.setString(1, text);
                            else  if(!isNumber(text)&&(type.equals("text")||type.equals("ntext")))
                            {
                                pst3 = conn.prepareStatement("select count (*) from "+schema+"."+table+" where ["+column+"] like  ? ");
                                pst3.setString(1, text);
                            }
                            
                            else 
                                continue;
                            try{
                                rs3 = pst3.executeQuery();
                                int count = 0;
                                if(rs3.next())
                                    count = rs3.getInt(1);
                                if(count >= 1){
                                    res.add(sql3);
                            
                                }
                            }
                            catch(Exception e){e.printStackTrace();}
                        }
                }
            }
        }
        
        catch(Exception e){e.printStackTrace();}
        finally{
            if(rs1!=null)try{rs1.close();}catch(Exception e){}
            if(rs2!=null)try{rs2.close();}catch(Exception e){}
            if(rs3!=null)try{rs3.close();}catch(Exception e){}
            
            if(pst1!=null)try{pst1.close();}catch(Exception e){}
            if(pst2!=null)try{pst2.close();}catch(Exception e){}
            if(pst3!=null)try{pst3.close();}catch(Exception e){}
            
            if(conn!=null)try{conn.close();}catch(Exception e){}
            
            
        }
        
    return res;
    }
    public ArrayList <String> getResultsOracle(ArrayList <String> types, String text){
        return null;
    } 
    
    public static boolean isNumber(String s){
        try{
            Double d =Double.parseDouble(s);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    
    public void onServerChange(){
        System.out.println(this.server);
    }
}
