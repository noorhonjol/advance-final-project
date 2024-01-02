package DataExport.DocumentToPdf;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class IAMPdfConvert extends PdfConvertTemplete {

    protected void addData(Document pdfDocument, Object data) {
        if (!(data instanceof org.bson.Document)) {
            throw new IllegalArgumentException("Invalid data type for user profile");
        }

        org.bson.Document userProfile = (org.bson.Document) data;

        String firstName = userProfile.getString("firstName");
        String lastName = userProfile.getString("lastName");
        String phoneNumber = userProfile.getString("phoneNumber");
        String email = userProfile.getString("email");
        String userName = userProfile.getString("userName");
        String role = userProfile.getString("role");
        String department = userProfile.getString("department");
        String organization = userProfile.getString("organization");
        String country = userProfile.getString("country");
        String city = userProfile.getString("city");
        String street = userProfile.getString("street");
        String postalCode = userProfile.getString("postalCode");
        String building = userProfile.getString("building");
        String userType = userProfile.getString("userType");

        pdfDocument.add(new Paragraph("User Profile"));
        pdfDocument.add(new Paragraph("First Name: " + firstName));
        pdfDocument.add(new Paragraph("Last Name: " + lastName));
        pdfDocument.add(new Paragraph("Phone Number: " + phoneNumber));
        pdfDocument.add(new Paragraph("Email: " + email));
        pdfDocument.add(new Paragraph("Username: " + userName));
        pdfDocument.add(new Paragraph("Role: " + role));
        pdfDocument.add(new Paragraph("Department: " + department));
        pdfDocument.add(new Paragraph("Organization: " + organization));
        pdfDocument.add(new Paragraph("Country: " + country));
        pdfDocument.add(new Paragraph("City: " + city));
        pdfDocument.add(new Paragraph("Street: " + street));
        pdfDocument.add(new Paragraph("Postal Code: " + postalCode));
        pdfDocument.add(new Paragraph("Building: " + building));
        pdfDocument.add(new Paragraph("User Type: " + userType));
        pdfDocument.add(new Paragraph("----------------------"));
    }
}
