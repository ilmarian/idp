/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

/**
 *
 * @author Ilmari
 */
public class pdf_converter {

    public static void pdf_converter(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        String path = System.getProperty("user.dir")+"\\src\\main\\temp\\images\\";
        for (int page = 0; page < document.getNumberOfPages(); ++page) { 
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY);
            ImageIOUtil.writeImage(bim, path+file.getName() + "-" + (page+1) + ".png", 300);            
        }
        document.close();
    }
}
