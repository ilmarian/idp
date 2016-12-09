package idp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

/**
 *
 * @author jannt
 */
public class ImageParser {

    private ImageParser defaultInstance;
    private Mat image;
    private File file;
    private List<File> files;

    public ImageParser() {
        files = new ArrayList();
    }
    
    public ImageParser(File image){
        this.file = image;
        this.image = Highgui.imread(image.getPath());
    }
    
    public ImageParser getInstance() {
        if( defaultInstance != null ){
            defaultInstance = new ImageParser();
        }
        return defaultInstance;
    }

    public void setImage(Mat image) {
        this.image = image;
    }
    
    public void setImage(File image) {
        this.file = image;
        this.image = Highgui.imread(image.getPath());
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public void addFile(File file){
        files.add(file);
    }
    
    public void addAll(ArrayList<File> files) {
        this.files.addAll(files);
    }
    
    public String parse(){
        String string = "";
        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init(".", "ENG") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
        if( file != null){
            string = parseFile(file,api);
        }else{
            for( File file : files ){
                System.out.println("File path: " + file.getPath());
                string += parseFile(file,api) + "\n";
            }
        }
        // Destroy used object and release memory
        api.End();
        System.out.println("OCR output:\n" + string);
        return string;
    }

    private String parseFile(File file, TessBaseAPI api) {
        BytePointer outText;
        String tmp;
        PIX source = pixRead(file.getPath());
        api.SetImage(source);
        // Get OCR result
        outText = api.GetUTF8Text();
        tmp = outText.getString();
        outText.deallocate();
        pixDestroy(source);
        return tmp;
    }

}
