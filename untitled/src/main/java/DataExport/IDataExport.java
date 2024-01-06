package DataExport;

import exceptions.NotFoundException;
import exceptions.SystemBusyException;

import java.io.FileNotFoundException;

public interface IDataExport {

    String getPathOfProcessedData(String userName) throws FileNotFoundException, SystemBusyException, NotFoundException;
    /*
        in this stage you will check if data was created in data collect by check on metaData
        then you will get data from database and the make process on data (convert it to pdf and then compress it)
        and then return the path of result
     */
}
