package DataExport.PdfFactoryConvertor;

import DataExport.DocumentToPdf.PostsPdfConvert;
import DataExport.DocumentToPdf.PdfConvertTemplete;
import org.bson.Document;

public class PostsPdfFactory implements PdfConverterFactory {
    @Override
    public PdfConvertTemplete createConverter() {
        return new PostsPdfConvert();
    }
}