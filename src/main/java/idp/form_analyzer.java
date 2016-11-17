package idp;

import java.util.ArrayList;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;               
        
public class form_analyzer {
    
    public void reader(ArrayList<String> imglist){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        for (String img: imglist){ 
            Highgui.imread(img);
            do_stuff();
            
        }
    }
    
    private void do_stuff(){
        
    }
}
