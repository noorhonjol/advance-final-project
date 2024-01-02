package DataExport.DocumentToPdf;

import DataExport.DataExport;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.List;

public class PaymentPdfConvert extends PdfConvertTemplete {
    private static final Logger logger = LogManager.getLogger(DataExport.class);

    @Override
    protected void addData(Document pdfDocument, Object data) throws FileNotFoundException {
        if (data instanceof List) {
            List<org.bson.Document> paymentList = (List<org.bson.Document>) data;
            for (org.bson.Document payment : paymentList) {
                addPaymentToPdf(pdfDocument, payment);
            }
        } else if (data instanceof Document) {
            addPaymentToPdf(pdfDocument, (org.bson.Document) data);
        } else {
            throw new IllegalArgumentException("Invalid data type for payments");
        }
    }

    private void addPaymentToPdf(Document pdfDocument, org.bson.Document payment) {
        logger.debug("Extract payment Data");
        if (payment.isEmpty()) {
            logger.info("No payment Data ");
            pdfDocument.add(new Paragraph("No Data for non premium users"));
        } else {
            String id = payment.getString("id");
            String userName = payment.getString("userName");
            Double amount = payment.getDouble("amount");
            String description = payment.getString("description");

            pdfDocument.add(new Paragraph("Payment ID: " + id));
            pdfDocument.add(new Paragraph("User Name: " + userName));
            pdfDocument.add(new Paragraph("Amount: $" + amount));
            pdfDocument.add(new Paragraph("Description: " + description));
            pdfDocument.add(new Paragraph("----------"));
            logger.debug("Payment Data is Extracted and converted to pdf");
        }
    }
}
