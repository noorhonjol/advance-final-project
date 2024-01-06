package CreationAndMetaData;

import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import iam.UserProfile;
import org.bson.Document;

public interface IDataCreation {

    void requestToCollectData(UserProfile userProfile) throws BadRequestException, SystemBusyException;
    Document getMetaData(String userName) throws NotFoundException, SystemBusyException;

}