package DataExport.PdfFactoryConvertor;

import DataExport.DocumentToPdf.*;
import org.bson.Document;
public class IAMPdfFactory implements PdfConverterFactory {

    @Override
    public PdfConvertTemplete createConverter() {
        return new IAMPdfConvert();
    }
}
