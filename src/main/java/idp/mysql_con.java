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

/**
 * TODO: sql queryjen teko gui:sta
 * @author Ilmari
 */

public class mysql_con {
    static int lport;
    static String rhost;
    static int rport;
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
        }catch(JSchException e){gui.jTextArea1.append(gui.getTimeStamp()+ " <System>: " + e.toString());}
    }
    
    public void mysql_conn() {
        Connection con = null;
        String url = "jdbc:mysql://localhost:" + lport + "/";
        String db = s.getDb();
        String dbUser = s.getDbUser();
        String dbPasswd = s.getDbPassword();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url+db, dbUser, dbPasswd);

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
                    
                }else if (sql.toLowerCase().startsWith("drop")) {
                    gui.jTextArea1.append(gui.getTimeStamp()+" <System>: imac");
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
                }
            }
            catch (SQLException s){
                gui.jTextArea1.append(gui.getTimeStamp()+" <System>: SQL statement is not executed\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}