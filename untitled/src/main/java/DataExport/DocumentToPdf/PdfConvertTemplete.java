package DataExport.DocumentToPdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import java.io.File;
import java.io.FileNotFoundException;

public abstract class PdfConvertTemplete {

    public File convertToPdf(String filename, Object data) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(filename);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        addData(document, data);
        document.close();
        return new File(filename);
    }

    protected abstract void addData(Document document, Object data) throws FileNotFoundException;
}
