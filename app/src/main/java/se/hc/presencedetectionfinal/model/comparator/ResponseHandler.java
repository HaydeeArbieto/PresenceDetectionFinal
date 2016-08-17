package se.hc.presencedetectionfinal.model.comparator;

/**
 * Created by Haydee on 17/08/16.
 */
public class ResponseHandler {

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {

        return userId;
    }

    public ResponseHandler(String userId) {

        this.userId = userId;
    }
}
