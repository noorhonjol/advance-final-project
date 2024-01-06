package DataExport.PdfFactoryConvertor;

public class PdfFactoryConvertor {
    public static  PdfConverterFactory getFactoryByTypeOfService(String dataType) {
        switch (dataType) {
            case "user-profile":
                return new IAMPdfFactory();
            case "posts":
                return new PostsPdfFactory();
            case "payment-info":
                return new PaymentPdfFactory();
            case "userActivities":
                return new ActivityPdfFactory();
            default:
                throw new IllegalArgumentException("Unknown data type: " + dataType);
        }
    }
}