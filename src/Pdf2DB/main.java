/**
 * Created by pgicking on 3/23/15.
 */

package Pdf2DB;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class PDFReader{

    public static void main(String []Args){
        PDDocument pdDoc = null;
        PDFParser parser = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;
        String fileName = "/Users/pgicking/Documents/JavaProjects/Pdf2DB/TestPdfs/Test2.pdf";
        final JFileChooser fc = new JFileChooser();


//        Component aComponent = null;
//        int returnVal = fc.showOpenDialog(aComponent);

        File file = new File(fileName);
        try {
            parser = new PDFParser(new FileInputStream(file));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            SQLHandler.GenerateSQLStatements(new ReadPdf().printFields(pdDoc));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
