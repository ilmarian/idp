package idp;

import java.util.List;   
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;
        
public class form_analyzer {
    static String output = "";
    file_operator fo = new file_operator();
    
    public void reader(List<String> imglist){
        
        for (String img: imglist){ 
            PIX image = pixRead(img);
            do_stuff(image);
        }
    }
    private void do_stuff(PIX image){
        BytePointer outText;
        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-ocr with Finnish
        if (api.Init(System.getProperty("user.dir"), "fin") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        // Open input image with leptonica library;
        api.SetImage(image);
        // Get OCR result
        outText = api.GetUTF8Text();
        output = outText.getString();

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);
        fo.write_csv(output.trim().split(" "));
    }
}