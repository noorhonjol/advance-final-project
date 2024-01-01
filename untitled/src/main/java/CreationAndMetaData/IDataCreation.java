package CreationAndMetaData;

import iam.UserProfile;
import org.bson.Document;

import java.util.List;

public interface IDataCreation {

    void requestToCollectData(UserProfile userProfile);//this produce Event


    Document getMetaData(String userName);
}
