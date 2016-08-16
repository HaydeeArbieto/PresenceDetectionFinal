package se.hc.presencedetectionfinal.activity;

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

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.hc.presencedetectionfinal.R;
import se.hc.presencedetectionfinal.service.AppService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String USER_ID = "se.hc.presencedetection.USER_ID";
    public static final String REGISTER_USER_URL = "http://beacons.zenzor.io/sys/api/";
    public static final String TAG = RegisterActivity.class.getSimpleName();

    Button buttonRegister;
    EditText first_name, last_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = (Button) findViewById(R.id.button_send);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //switch (view.getId()) {
         //   case R.id.button_send:
                if (first_name.getText().toString().isEmpty() || last_name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Write your firstname and lastname", Toast.LENGTH_SHORT).show();

                } else {
                    final SharedPreferences preferences = getSharedPreferences(USER_ID, MODE_PRIVATE);

                    if (!preferences.getBoolean(USER_ID, false)) {

                        Gson gson = new GsonBuilder().create();

                        final Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(REGISTER_USER_URL)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        JSONObject jsonRequest = new JSONObject();

                        try {
                            jsonRequest.put("api_key", "28742sk238sdkAdhfue243jdfhvnsa1923347");
                            jsonRequest.put("first_name", first_name.getText().toString());
                            jsonRequest.put("last_name", last_name.getText().toString());

                            Log.d("Json Request", jsonRequest.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        final AppService appService = retrofit.create(AppService.class);
                        final Call<JSONObject> result = appService.register_user(jsonRequest.toString());

                        result.enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                                JSONObject jsonResponse = response.body();

                                Log.d(TAG, "{response_value:" + response.raw().code() +  ", "  + "user_id" + "");
                                Log.d(TAG, "Response " + response.raw());
                                Log.d(TAG, "Response " + response.isSuccessful());


                                String userIdAsString = response.body().toString();

                            }

                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {

                                Log.d(TAG, "Could not fetch userid: " + t.getMessage());
                            }
                        });

                    } /*else {

                       if (scanToActivateButton != null) {

                           scanToActivateButton.setOnClickListener(new View.OnClickListener() {

                               @Override
                               public void onClick(View view) {

                                   final Intent scanIntent = new Intent(context, ScanActivity.class);
                                   startActivity(scanIntent);
                               }
                           });
                       }
                   }
               }

                Intent intent = new Intent(this, ScanActivity.class);

               }*/

               // }
        }
    }
}