package se.hc.presencedetectionfinal.service;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import se.hc.presencedetectionfinal.model.MessageResponse;
import se.hc.presencedetectionfinal.model.User;


public interface AppService {

    @FormUrlEncoded
    @POST("register_user")
    //Call<JSONObject> register_user(@Field("input") String request);

    Call<MessageResponse> register_user(@Field("input") String request);
    //public void register_user(@Field("first_name") String first_name, @Field("last_name") String last_name,
     //                    Callback<User> callback);


    @GET("auth_user.php")
    Call<String> getAuthorizationResponse(@Query("enc") String authorizationRequest);

    @GET("activate_beacon.php")
    Call<String> getActivationResponse(@Query("enc") String activationRequest);
}
