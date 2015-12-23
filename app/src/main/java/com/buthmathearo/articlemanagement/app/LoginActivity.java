package com.buthmathearo.articlemanagement.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.util.UserLogin;
import com.buthmathearo.articlemanagement.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText mEditTextUsername;
    private EditText mEditTextPasswd;
    private CheckBox chkBoxIsRemember;
    private Button btnLogin;
    private String baseUrl = "http://hrdams.herokuapp.com";
    private String TAG = "OLO_RESULT";
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setTitle("Login");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Initialize View Object Here.
        mEditTextPasswd = (EditText) findViewById(R.id.password_login);
        mEditTextUsername = (EditText) findViewById(R.id.username_login);
        chkBoxIsRemember = (CheckBox) findViewById(R.id.chk_rememberpwd_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(mEditTextUsername.getText().toString(), mEditTextPasswd.getText().toString());
            }
        });

        Button btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void login(String username, String passwd) {
        String url = baseUrl + "/api/login";
        JsonObjectRequest jsonObjectRequest = null;
        JSONObject params;
        try {
            params = new JSONObject();
            params.put("username", username.trim());
            params.put("password", passwd.trim());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.trim(), params,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                // Verify if User Login True
                                if (jsonObject.getString("STATUS").toString().equalsIgnoreCase("TRUE")) {
                                    JSONObject resData = jsonObject.getJSONObject("RES_DATA");
                                    //Toast.makeText(LoginActivity.this, "MESSAGE: " + resData.getString("username"), Toast.LENGTH_SHORT).show();
                                    UserLogin userLogin;
                                    if (chkBoxIsRemember.isChecked()) {
                                        userLogin = new UserLogin(LoginActivity.this);
                                        userLogin.setId(resData.getInt("id"));
                                        userLogin.setUsername(resData.getString("username"));
                                        userLogin.setUsername(resData.getString("password"));
                                        userLogin.setRole(resData.getString("roles"));
                                        userLogin.setLogin(true);
                                        userLogin.setRemember(true);

                                        // Write to Shared Preference File
                                        userLogin.writeToSharedPrefFile();
                                    }

                                    //Toast.makeText(LoginActivity.this, "MESSAGE: " + userLogin.getUsername() + " " + userLogin.getRole(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra(UserLogin.USER_ID, resData.getInt("id"));
                                    intent.putExtra(UserLogin.USERNAME, resData.getString("username"));
                                    intent.putExtra(UserLogin.ROLE, resData.getString("roles"));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    // Destroy this LoginAcitity
                                    finish();
                                } else {
                                    // User Login Failed.
                                    //Toast.makeText(getBaseContext(), "Login failed. Username or password is not correct.", Toast.LENGTH_LONG).show();
                                    Util.showAlertDialog(LoginActivity.this, "Login Failed", "Username or password is not correct.", true, SweetAlertDialog.ERROR_TYPE);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }
                        }
                    }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(Util.TAG, volleyError.toString());
                    //Toast.makeText(LoginActivity.this, "ERROR MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                    Util.showAlertDialog(LoginActivity.this,"Error", "Please check your Internet Connection.", true, SweetAlertDialog.ERROR_TYPE);
                }
            });

            // Add RequestQueue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            Log.d(TAG, e.toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

}
