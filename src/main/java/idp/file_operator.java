/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idp;

/**
 *
 * @author Ilmari
 */

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class file_operator {
    static String path = System.getProperty("user.dir")+"\\src\\main\\temp\\csv\\";
    
    public void write_csv(){   
        String[] data = form_analyzer.getOutput();
        try {
            CSVWriter writer = new CSVWriter(
                    new OutputStreamWriter(new FileOutputStream(path+data[0]+".csv", true), "UTF-8"), ',',  CSVWriter.NO_QUOTE_CHARACTER);            
            writer.writeNext(data);
            writer.close();
        } catch (IOException ex) {}
    }
    
    public void read_csv(){
        String filename = "";
        File f = new File(path);
        ArrayList<File> files = new ArrayList(Arrays.asList(f.listFiles()));
        try {     
            for (int i = 0; i<files.size(); i++){
                filename = files.get(i).getAbsolutePath().replace(path, "");
                FileReader reader = new FileReader(files.get(i).getAbsolutePath());  
                gui.addTab(filename, reader, path+filename);
            } 
        } catch (IOException ex) {}
    }

    public void save_csv(int file_count){
        try {
            for (int i=0; i<file_count; i++){
                FileWriter writer = new FileWriter(path+gui.jTabbedPane1.getTitleAt(i));            
                writer.write(gui.jta.get(i).getText());
                writer.close();  
            }
        } catch (IOException ex) {}
    }
 
    public void delete_files(){
        File del = new File(System.getProperty("user.dir")+"\\src\\main\\temp\\images\\");
        try {
            FileUtils.cleanDirectory(del);
            del = new File(System.getProperty("user.dir")+"\\src\\main\\temp\\csv\\");
            FileUtils.cleanDirectory(del);
        } catch (IOException ex) {
            Logger.getLogger(file_operator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
