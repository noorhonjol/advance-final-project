package DataExport.PdfFactoryConvertor;
import DataExport.DocumentToPdf.PdfConvertTemplete;
import org.bson.Document;

public interface PdfConverterFactory {
    PdfConvertTemplete createConverter();

}
