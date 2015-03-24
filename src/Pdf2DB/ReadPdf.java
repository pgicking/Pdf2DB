package Pdf2DB;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Created by pgicking on 3/23/15.
 */
public class ReadPdf {

    static boolean DEBUGG = false;

    public static void ExtractPDF() {
        PDFParser parser = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;

        String parsedText;
        //String fileName = "E:\\Files\\Small Files\\PDF\\JDBC.pdf";
        String fileName = "/Users/pgicking/Documents/JavaProjects/Pdf2DB/TestPdfs/Test2.pdf";
        File file = new File(fileName);
        try {
            parser = new PDFParser(new FileInputStream(file));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
            System.out.println(parsedText);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e1) {
                e.printStackTrace();
            }

        }
    }

    public LinkedHashMap<String, String> printFields(PDDocument pdfDocument) throws IOException
    {
        LinkedHashMap<String,String> AssocArray = new LinkedHashMap<String, String>();
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        List fields = acroForm.getFields();
        Iterator fieldsIter = fields.iterator();

        String Statements = null;
        if(DEBUGG) {
            System.out.println(Integer.toString(fields.size()) + " top-level fields were found on the form");
        }

        while (fieldsIter.hasNext())
        {
            PDField field = (PDField) fieldsIter.next();
            Statements = processField(field, "", field.getPartialName());
            String[] Array = Statements.split(",");
            System.out.println(Arrays.toString(Array));
            //AssocArray.put(Array[0],Array[1]);
        }

        return AssocArray;

    }
    private String processField(PDField field, String sLevel, String
            sParent) throws IOException
    {
        List kids = field.getKids();
        String partialName = field.getPartialName();
        String nested = "";

        if(DEBUGG){
            sLevel = "|--";
        }

        StringBuilder outputString = null;
        if (kids != null)
        {
            Iterator kidsIter = kids.iterator();
            if (!sParent.equals(partialName))
            {
                if (partialName != null)
                {
                    sParent = sParent + "." + partialName;
                }
            }
            //System.out.println(sLevel + sParent);
            while (kidsIter.hasNext())
            {
                Object pdfObj = kidsIter.next();
                if (pdfObj instanceof PDField)
                {
                    PDField kid = (PDField) pdfObj;
                    nested = processField(kid, "  " + sLevel, sParent);
                }
            }
        }
        else
        {
            String fieldValue = null;
            if (field instanceof PDSignatureField)
            {
                // PDSignature doesn't have a value
                fieldValue = "PDSignatureField";
            }
            else
            {
                if (field.getValue() != null)
                {

                    fieldValue = field.getValue();

                }
                else if(field instanceof PDCheckbox){
                    if(((PDCheckbox) field).isChecked()){
                        fieldValue = "True";
                    }
                    else{
                        fieldValue = "False";
                    }
                }
                else
                {
                    fieldValue = "no value available";
                }
            }

            outputString = new StringBuilder(sLevel + Sanitize(sParent));
            if (partialName != null)
            {
                //outputString.append( "." + partialName);
            }
            outputString.append("," + "'" + fieldValue + "'");
            //outputString.append(",  type=" + field.getClass().getName());
            System.out.println(outputString + " " + nested);
        }
        if(String.valueOf(outputString).compareToIgnoreCase("null") == 0)
            return String.valueOf(nested);
        else
            return String.valueOf(outputString);
    }

    public static String Sanitize(String sParent){
        int i;
        int j = 0;
        char[] array = sParent.toCharArray();
        for(i=sParent.length()-1;j<=22;i--)
        {
            array[i] = ' ';
            j++;
        }
        sParent = Arrays.toString(array);
        sParent = sParent.replace(",", "");
        sParent = sParent.replace(" ", "");
        sParent = sParent.replace("[","'");
        sParent = sParent.replace("]","'");
        sParent = sParent.replace("'","");
        sParent = sParent.replace("fc-in = 'no value available'","");
       return sParent.trim();

    }
}
