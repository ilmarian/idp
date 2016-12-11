package idp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.bytedeco.javacpp.opencv_core.BORDER_DEFAULT;
import org.bytedeco.javacpp.opencv_core.Point;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MOP_CLOSE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_RECT;
import static org.bytedeco.javacpp.opencv_imgproc.getStructuringElement;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import static org.opencv.core.CvType.CV_8U;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;

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
        Locale loc = Locale.getDefault();
        if ( ( loc.getLanguage().equals("fi") ? 
                api.Init(".", "FIN") : 
                api.Init(".", "ENG") 
            ) != 0 ) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
        if( file != null){
            string = parseFile(file,api);
        }else{
            for( File file : files ){
                string += parseFile(file,api) + "\n";
            }
        }
        // Destroy used object and release memory
        api.End();
        //System.out.println("OCR output:\n" + string);
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
    
    public void detectTextAreas(Mat image){
        Vector<Rect> boundRect = null;
        Vector< Vector< Point > > contours = null;
        Mat img_gray = null, img_sobel = null, img_threshold = null;
        cvtColor(image, img_gray, CV_BGR2GRAY);
        Imgproc.Sobel(img_gray, img_sobel, CV_8U, 1, 0, 3, 1, 0, BORDER_DEFAULT);
        threshold(img_sobel, img_threshold, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);
        opencv_core.Mat element = getStructuringElement(MORPH_RECT, new opencv_core.Size(30, 30) );
        /*
        morphologyEx(img_threshold, img_threshold, CV_MOP_CLOSE, element);
        findContours(img_threshold, contours, 0, 1);
        contours_poly( contours.size() );
        for( int i = 0; i < contours.size(); i++ ) {
            if (contours[i].size()>100) { 
                approxPolyDP( Mat(contours[i]), contours_poly[i], 3, true );
                Rect appRect( boundingRect( Mat(contours_poly[i]) ));
                if (appRect.width>appRect.height) {
                    boundRect.push_back(appRect);
                }
            }
        }
        return boundRect;*/
    }

}
