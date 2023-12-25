package ExternalDataStorage;

public interface IExternalDataStorage {
    boolean isAuthenticated();
    void addNewData(String urlProcessedData);

}