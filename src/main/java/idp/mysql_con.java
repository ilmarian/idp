/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idp;

import java.sql.*;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * 
 * @author Ilmari
 */

public class mysql_con {
    static int lport;
    static String rhost;
    static int rport;
    Connection con = null;
    secrets s = new secrets();

    public void ssh_conn(String user, String pw) throws IOException{        
        String host = "cs.uef.fi";
        int port=22;
        try{
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            lport = 3307;
            rhost = "localhost";
            rport = 3306;
            session.setPassword(pw);
            session.setConfig("StrictHostKeyChecking", "no");
            gui.jTextArea1.append(gui.getTimeStamp() + " <System>: Establishing Connection...\n");
            session.connect();
            int assinged_port=session.setPortForwardingL(lport, rhost, rport);
            gui.jTextArea1.append(gui.getTimeStamp()+" <System>: Connection successful localhost:"+assinged_port+" -> "+rhost+":"+rport+"\n");
        }catch(JSchException e){gui.jTextArea1.append(gui.getTimeStamp()+ " <System>: " + e.toString() +"\n");}
    }
        
    public void mysql_conn() {
        String url = "jdbc:mysql://localhost:" + lport + "/";
        String db = s.getDb();
        String dbUser = s.getDbUser();
        String dbPasswd = s.getDbPassword();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url+db, dbUser, dbPasswd);
            Statement st = con.createStatement();
            st.execute("SET NAMES 'utf8'");           
        }catch (Exception e){
            e.printStackTrace(); 
        }
    }
    
    public void mysql_query() {        
        mysql_conn();   
        try{
            String sql = gui.jTextField1.getText();
            Statement st = con.createStatement();

            if (sql.toLowerCase().startsWith("select")){
                ResultSet rs = st.executeQuery(sql);
                int col = rs.getMetaData().getColumnCount();
                StringBuilder sb = new StringBuilder();                    
                for (int i=1; i <= col; i++){
                    sb.append(rs.getMetaData().getColumnName(i).toUpperCase() + "\t");
                }
                sb.append("\n");
                while (rs.next()){
                    for (int i=1; i <= col; i++){
                        sb.append(rs.getString(i)+ "\t");
                    }
                    sb.append("\n");
                }
                gui.jTextArea1.append(gui.getTimeStamp()+ " <System>: Displaying results:\n"+ sb);
            }else if(sql.toLowerCase().startsWith("create table")) {
                int update = st.executeUpdate(sql);
                if(update == 0){
                    gui.jTextArea1.append(gui.getTimeStamp()+" <System>: Table created successfully\n");
                }
                else{
                    gui.jTextArea1.append(gui.getTimeStamp()+" <System>: Table creation failed\n");
                }
            }else if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("update") 
                    || sql.toLowerCase().startsWith("delete")){
                int update = st.executeUpdate(sql);
                if(update >= 1){
                    gui.jTextArea1.append(gui.getTimeStamp()+" <System>: Update completed successfully\n");
                }
                else{
                    gui.jTextArea1.append(gui.getTimeStamp()+" <System>: Update failed\n");
                }
            } else gui.jTextArea1.append(gui.getTimeStamp()+"<System>: Unauthorized query\n");
        }catch (SQLException s){
            gui.jTextArea1.append(gui.getTimeStamp()+" <System>: SQL statement is not executed\n");
        }
    }
    
    public void export_csv(){
        mysql_conn();
        for(int i=0; i<gui.jTabbedPane1.getTabCount(); i++){
            String file_path = System.getProperty("user.dir")+"\\src\\main\\temp\\csv\\"+gui.jTabbedPane1.getTitleAt(i);
            String table = removeExtension(gui.jTabbedPane1.getTitleAt(i)).toLowerCase();
            file_path = file_path.replace("\\", "/");
            try {
                String sql = "LOAD DATA LOCAL INFILE '" + file_path + "' INTO TABLE "+ table +" CHARACTER SET 'utf8' FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n'";
                System.out.println(sql);
                Statement st = con.createStatement();
                st.execute(sql);

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
