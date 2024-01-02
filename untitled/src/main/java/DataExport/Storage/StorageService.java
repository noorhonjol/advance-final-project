package DataExport.Storage;

import java.io.File;
import java.io.IOException;
public interface StorageService {
    String uploadToCloud(File file) throws IOException;
    String getLink(String fileId) throws IOException;
}
