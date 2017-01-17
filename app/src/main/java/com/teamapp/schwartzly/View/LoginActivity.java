package com.teamapp.schwartzly.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.teamapp.schwartzly.Data.DataManager;
import com.teamapp.schwartzly.Data.Event;
import com.teamapp.schwartzly.Data.Player;
import com.teamapp.schwartzly.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String USER_ID = "user_id";
    private CallbackManager mCallbackManager;
    private LoginButton mLoginButton;
    private SharedPreferences mPrefernces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mPrefernces = PreferenceManager.getDefaultSharedPreferences(this);

        DataManager.getInstance().getEvent(new DataManager.EventFetchedCallback() {
            @Override
            public void onEventFetched(final Event activeEvent) {
                if (mPrefernces.contains(USER_ID)) {
                    startActivity(new Intent(LoginActivity.this, EventActivity.class));
                } else {
                    mCallbackManager = CallbackManager.Factory.create();
                    mLoginButton = (LoginButton) findViewById(R.id.login_button);
                    mLoginButton.setVisibility(View.VISIBLE);
                    mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Bundle params = new Bundle();
                            params.putString("fields", "id,name,picture.type(large)");
                            new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            if (response != null) {
                                                try {
                                                    JSONObject data = response.getJSONObject();
                                                    final String id = data.getString("id");
                                                    final String name = data.getString("name");
                                                    final String picture = data.getJSONObject("picture")
                                                            .getJSONObject("data").getString("url");

                                                    Player player = new Player(id, name, picture);
                                                    DataManager.getInstance().addPlayer(activeEvent, player);
                                                    mPrefernces.edit().putString(USER_ID, id).apply();

                                                    startActivity(new Intent(LoginActivity.this, EventActivity.class));
                                                } catch (Exception e) {
                                                    // TODO login failed
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).executeAsync();
                        }

                        @Override
                        public void onCancel() {
//                info.setText("Login attempt cancelled.");
                        }

                        @Override
                        public void onError(FacebookException e) {
                            // TODO login failed
//                info.setText("Login attempt failed.");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
