package Events;

public class ChangeStatusEvent {


    private final String userName;

    public ChangeStatusEvent(String userName){
        this.userName=userName;
    }

    public String getUserName() {
        return userName;
    }

}
