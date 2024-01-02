package DataExport.PdfFactoryConvertor;


import DataExport.DocumentToPdf.PaymentPdfConvert;
import DataExport.DocumentToPdf.PdfConvertTemplete;
import org.bson.Document;

public class PaymentPdfFactory implements PdfConverterFactory {
    public PdfConvertTemplete createConverter() {
        return new PaymentPdfConvert();
    }

}
