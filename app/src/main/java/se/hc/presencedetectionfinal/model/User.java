package se.hc.presencedetectionfinal.model;


import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("api_key")
    private String api_key = "28742sk238sdkAdhfue243jdfhvnsa1923347";

    @SerializedName("first_name")
    private String first_name;

    @SerializedName("last_name")
    private String last_name;


    public User(){}

    public User(String first_name, String last_name)
    {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public String getFirst_name(){
        return first_name;

    }
    public String getLast_name()
    {
        return last_name;
    }

    public void setFirst_name(){
        this.first_name = first_name;
    }

    public void setLast_name(){
        this.last_name = last_name;
    }
}
