package DataExport.DocumentToPdf;

import DataExport.DataExport;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.List;

public class ActivityPdfConvertor extends PdfConvertTemplete {
    private static final Logger logger = LogManager.getLogger(DataExport.class);

    @Override
    protected void addData(Document pdfDocument, Object data) {
        if (data instanceof List) {
            List<org.bson.Document> activityList = (List<org.bson.Document>) data;
            for (org.bson.Document activity : activityList) {
                addActivityToPdf(pdfDocument, activity);
            }
        } else if (data instanceof Document) {
            addActivityToPdf(pdfDocument, (org.bson.Document) data);
        } else {
            throw new IllegalArgumentException("Invalid data type for activities");
        }
    }
    private void addActivityToPdf(Document pdfDocument, org.bson.Document activity) {
        System.out.println(activity);
        if (activity.isEmpty()) {
            logger.info("No activity Data");
            pdfDocument.add(new Paragraph("No Data "));
        } else {
            logger.debug("Extract Activity Data");
            String activityType = activity.getString("activityType");
            String activityDate = activity.getString("activityDate");
            pdfDocument.add(new Paragraph("Activity Type: " + activityType));
            pdfDocument.add(new Paragraph("Activity Date: " + activityDate));
            pdfDocument.add(new Paragraph("------------------------------"));
            logger.info("Activity Data is Extracted and printed in Pdf Format");

        }

    }
}
