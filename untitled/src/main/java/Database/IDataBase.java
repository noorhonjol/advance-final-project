package Database;

public interface IDataBase {
    void insert(String document);
    Object find(String query);

    /*
        this interface for connect with database
        use singleton design pattern
     */

}
