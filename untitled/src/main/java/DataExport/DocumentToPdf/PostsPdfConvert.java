package DataExport.DocumentToPdf;

import DataExport.DataExport;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class PostsPdfConvert extends PdfConvertTemplete {
    private static final Logger logger = LogManager.getLogger(DataExport.class);


    @Override
    protected void addData(Document pdfDocument, Object data){
            if (data instanceof List) {
                List<org.bson.Document> postsList = (List<org.bson.Document>) data;
                for (org.bson.Document post : postsList) {
                    addPostToPdf(pdfDocument, post);
                }
            } else if (data instanceof Document) {
                addPostToPdf(pdfDocument, (org.bson.Document) data);
            } else {
                throw new IllegalArgumentException("Invalid data type for posts");
            }
    }
    private void addPostToPdf(Document pdfDocument, org.bson.Document post) {

            logger.debug("Extract post Data");
            String title = post.getString("title");
            String body = post.getString("body");
            String author = post.getString("author");
            String date = post.getString("date");
            pdfDocument.add(new Paragraph("Title: " + title));
            pdfDocument.add(new Paragraph("Date: " + date));
            pdfDocument.add(new Paragraph("Author: " + author));
            pdfDocument.add(new Paragraph("Content: " + body));
            pdfDocument.add(new Paragraph("----------------"));
            logger.info("Post Data is Extracted and printed in Pdf Format");

    }
}
