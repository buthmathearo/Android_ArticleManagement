package com.buthmathearo.articlemanagement.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Eath Manith on 12/6/2015.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConPassword;
    private Button btnCancleRegister;
    private Button btnOkRegister;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setTitle("Register");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Event OK button register
        btnOkRegister = (Button) findViewById(R.id.btnOk_register);
        btnOkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etUsername = (EditText) findViewById(R.id.username_register);
                EditText etPasswd = (EditText) findViewById(R.id.password_register);
                registerRequestResponse(etUsername.getText().toString(), etPasswd.getText().toString());
            }
        });

        //Event Cancle button register
        btnCancleRegister = (Button) findViewById(R.id.btnCancle_register);
        btnCancleRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent1);*/
                finish();
            }
        });
    }

    public void registerRequestResponse(String userName, String passwd){
        RequestQueue queue = new Volley().newRequestQueue(getApplicationContext());
        String mUrl = "http://hrdams.herokuapp.com/api/user/hrd_c001";

        etUsername = (EditText) findViewById(R.id.username_register);
        etPassword = (EditText) findViewById(R.id.password_register);
        etConPassword = (EditText) findViewById(R.id.comfirm_password_register);

        // if all edittexts are not null
        if (!(etUsername.getText().toString().equals("") && etPassword.getText().toString().equals("") && etConPassword.getText().toString().equals(""))) {
            //if password = conformpassword
            if (etPassword.getText().toString().equals(etConPassword.getText().toString())) {
                JSONObject req = new JSONObject();
                try {
                    req.put("username", userName);
                    req.put("password", passwd);
                    req.put("roles", "User");
                    req.put("photo", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, mUrl, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            //  STATUS = true
                            if (jsonObject.getBoolean("STATUS")){
                                //Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                //startActivity(intent);
                                finish();
                            }
                            // STATUS = false
                            else {
                                //Toast.makeText(RegisterActivity.this, "Username already existed.", Toast.LENGTH_LONG).show();
                                Util.showAlertDialog(RegisterActivity.this, "Username already existed.", true, SweetAlertDialog.WARNING_TYPE);
                                etUsername.setText("");
                                etPassword.setText("");
                                etConPassword.setText("");
                                etUsername.requestFocus();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(RegisterActivity.this, volleyError.toString(), Toast.LENGTH_SHORT).show();
                        Util.showAlertDialog(RegisterActivity.this, "Error", "Error: " + volleyError.toString(), true, SweetAlertDialog.ERROR_TYPE);

                    }
                });
                queue.add(objectRequest);
            }else{
                //Toast.makeText(RegisterActivity.this, "Password and conform password must be the same.", Toast.LENGTH_SHORT).show();
                Util.showAlertDialog(RegisterActivity.this, "Warning", "Password and conform password must be the same.", true, SweetAlertDialog.WARNING_TYPE);
            }
        }else {
            //Toast.makeText(RegisterActivity.this, "Please enter information.", Toast.LENGTH_SHORT).show();
            Util.showAlertDialog(RegisterActivity.this, "Warning", "Please enter information.", true, SweetAlertDialog.WARNING_TYPE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}