package se.hc.presencedetectionfinal.model;


import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName("response_value")
    private String response_value;

    @SerializedName("id_user")
    private String id_user;

    public MessageResponse(String response_value, String id_user){
        this.response_value = response_value;
        this.id_user = id_user;
    }

    public String getResponse_value() {
        return response_value;
    }

    public void setResponse_value(String response_value) {
        this.response_value = response_value;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "response_value='" + response_value + '\'' +
                ", id_user='" + id_user + '\'' +
                '}';
    }
}
