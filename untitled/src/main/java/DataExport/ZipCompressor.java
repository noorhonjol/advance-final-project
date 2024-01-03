package DataExport;
import java.io.File;
import org.zeroturnaround.zip.ZipUtil;
import java.util.List;

public class ZipCompressor {
       public static void zipFiles(List<File> files,String ZipFilePath){
        File zip=new File(ZipFilePath);
           ZipUtil.packEntries(files.toArray(new File[0]),zip);
    }
}
