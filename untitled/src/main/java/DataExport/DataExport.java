package DataExport;
import CreationAndMetaData.*;
import CollectData.IDataCollect;
import DataExport.DocumentToPdf.PdfConvertTemplete;
import DataExport.PdfFactoryConvertor.PdfFactoryConvertor;
import DataExport.Storage.StorageFactory;
import DataExport.Storage.StorageService;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import org.bson.Document;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class DataExport implements IDataExport {
    private static final Logger logger = LogManager.getLogger(DataExport.class);
    protected IDataCollect dataCollector;

    private static final IDataCreation dataCreation = new DataCreation();

    public DataExport(IDataCollect dataCollector) {
        this.dataCollector = dataCollector;
    }
    List<File> pdfFiles = new ArrayList<>();
    @Override
    public String getPathOfProcessedData(String userName) throws SystemBusyException, NotFoundException {
        logger.info("Starting data export for user: " + userName);
        Document metaData = getMetaData(userName);
        if (metaData == null) {
            logger.info("No metadata found for user: " + userName);
            return null;
        }

        String status = metaData.getString("status");
        if (!"Complete".equals(status)) {
            logger.info("User data is not ready for export. Status: " + status);
            return null;
        }
        try {
            Document userData = dataCollector.getCollectedData(userName);
            for (String serviceType : userData.keySet()) {
                if(serviceType.equals("_id")||serviceType.equals("userName")){
                    continue;
                }
                Object serviceData = userData.get(serviceType);
                logger.info("Converting data to PDF for service type: " + serviceType);
                PdfConvertTemplete converter = PdfFactoryConvertor.getFactoryByTypeOfService(serviceType.trim()).createConverter();
                File pdfFile = converter.convertToPdf(userName + "-" + serviceType + ".pdf", serviceData);
                pdfFiles.add(pdfFile);
                logger.info("Data for service type: " + serviceType + " converted successfully and added to list");
            }
            String zipFileName = userName + "-Data.zip";
            ZipCompressor.zipFiles(pdfFiles, zipFileName);
            logger.info("PDF Files Compressed to ZIP File");
            logger.info("Data export completed successfully for user: " + userName);
            return zipFileName;
        } catch (FileNotFoundException e) {
            logger.error("Data export failed for user: " + userName + ", File Not Found", e);
        } catch (Exception e) {
            logger.error("Data export failed for user: " + userName, e);
        }
        return null;
    }
    public String exportAndUploadData(String userName, String storageServiceType) {
        logger.info("start uploading data to Cloud storage and return a a link storage");
        try {
            String zipFilePath = getPathOfProcessedData(userName);
            File zipFile = new File(zipFilePath);
            logger.debug("Zip is created at this path: {}", zipFilePath);
            StorageService storageService = StorageFactory.getStorage(storageServiceType);
            logger.info("Uploading compressed file to {} storage service", storageServiceType);
            String fileId = storageService.uploadToCloud(zipFile);
            logger.info("file uploaded to {}", storageServiceType);
            String FileStorageLink = storageService.getLink(fileId);
            logger.debug("Retrieved folder link: {}", FileStorageLink);
            logger.info("Data uploaded to the cloud successfully and a link is provided");
            return FileStorageLink;
        } catch (IOException e) {
            logger.error("IO Exception happened {}", e.getMessage());
        } catch (GeneralSecurityException | SystemBusyException | NotFoundException e) {
            logger.error("Security Exception happened {}", e.getMessage());
        }catch(IllegalArgumentException e){
            logger.error("Illegal Argument Exception Exception happened {} Service type Not supported", e.getMessage());
        }
        return null;
    }
    private Document getMetaData(String userName) throws SystemBusyException, NotFoundException {
        logger.info("Getting metadata for user: " + userName);
        Document metaData = dataCreation.getMetaData(userName);
        logger.info("Metadata retrieved for user: " + userName);
        return metaData;
    }
}
