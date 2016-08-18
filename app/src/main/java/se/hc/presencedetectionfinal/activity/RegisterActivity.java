package se.hc.presencedetectionfinal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.hc.presencedetectionfinal.R;
import se.hc.presencedetectionfinal.model.MessageResponse;
import se.hc.presencedetectionfinal.model.User;
import se.hc.presencedetectionfinal.model.comparator.ResponseHandler;
import se.hc.presencedetectionfinal.service.AppService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String USER_ID = "se.hc.presencedetectionfinal.USER_ID";
    public static final String REGISTER_USER_URL = "http://beacons.zenzor.io/sys/api/";
    public static final String TAG = RegisterActivity.class.getSimpleName();
    public SharedPreferences preferences;

    private Context context = this;
    private MessageResponse messageResponse;

    Button buttonRegister;
    EditText first_name, last_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferences = getSharedPreferences(USER_ID, MODE_PRIVATE);
        buttonRegister = (Button) findViewById(R.id.button_send);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (first_name.getText().toString().isEmpty() || last_name.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Write your firstname and lastname", Toast.LENGTH_SHORT).show();

        } else {

            if (!preferences.getBoolean(USER_ID, false)) {

                Gson gson = new GsonBuilder().create();

                final Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(REGISTER_USER_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                User user = new User (first_name.getText().toString(), last_name.getText().toString());
                Log.d("Request", gson.toJson(user).toString());

                final AppService appService = retrofit.create(AppService.class);
                final Call<MessageResponse> result = appService.register_user(gson.toJson(user));

                result.enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                        MessageResponse messageResponse = response.body();
                        Log.d("Response1", response.message());
                        Log.d("Response2", messageResponse.getId_user());

                        preferences.edit().putString(USER_ID, messageResponse.getId_user()).apply();
                        Log.d("Sharepreference***", preferences.getString(USER_ID, null));

                        Intent intent =  new Intent(context, ScanAndSuscribeActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {

                        Log.d(TAG, "Could not fetch users id: " + t.getMessage());
                    }
                });

            } else {


                Intent intent =  new Intent(context, ScanAndSuscribeActivity.class);
                startActivity(intent);
            }
        }
    }
}
