package DataExport.PdfFactoryConvertor;

import DataExport.DocumentToPdf.ActivityPdfConvertor;
import DataExport.DocumentToPdf.PdfConvertTemplete;
import org.bson.Document;

public class ActivityPdfFactory implements PdfConverterFactory{

    @Override
    public PdfConvertTemplete createConverter() {
        return new ActivityPdfConvertor();
    }
}
