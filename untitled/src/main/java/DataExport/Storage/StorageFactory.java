package DataExport.Storage;
import java.io.IOException;
import java.security.GeneralSecurityException;
public class StorageFactory {
    public static StorageService getStorage(String serviceType) throws GeneralSecurityException, IOException {
        switch (serviceType) {
            case "GoogleDrive":
                return new GoogleDrive();
            default:
                throw new IllegalArgumentException("Unknown service type: " + serviceType);
        }
    }
}
