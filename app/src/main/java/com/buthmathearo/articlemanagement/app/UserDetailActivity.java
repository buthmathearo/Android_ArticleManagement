package com.buthmathearo.articlemanagement.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserDetailActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private NetworkImageView photo;
    private TextView username;
    private TextView role;
    private TextView regdate;
    private TextView ustatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setTitle("User Detail");
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

        photo = (NetworkImageView) findViewById(R.id.imageView1);
        username = (TextView)findViewById(R.id.udetail_username);
        role = (TextView)findViewById(R.id.udetail_role);
        regdate = (TextView)findViewById(R.id.udetail_regdate);
        ustatus = (TextView)findViewById(R.id.udetail_status);

        //String id = "115";
        String id = getIntent().getStringExtra("USER_ID");
        try {
            requestResponse(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void requestResponse(String id) throws JSONException {
        Util.showAlertDialog(this, "Fetching Data...", true, SweetAlertDialog.PROGRESS_TYPE);
        String url = "http://hrdams.herokuapp.com/api/user/hrd_det001";
        JSONObject req = new JSONObject();
        req.put("id",id);
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, req, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //Toast.makeText(UserDetailActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                //Log.d(Util.TAG, jsonObject.toString());
                try {
                    boolean status =jsonObject.getBoolean("STATUS");
                    if(status){
                        JSONObject object = jsonObject.getJSONObject("RES_DATA");
                        username.setText(object.getString("username"));
                        mToolbar.setTitle("Welcome, " + object.getString("username") + " !");
                        role.setText(object.getString("roles"));
                        regdate.setText(object.getString("registerDate"));
                        ustatus.setText(object.getString("enabled"));
                        //String str = Util.baseUrl + "/" + object.getString("photo");
                        /*String imgUrl = str.replace("\\", "");
                        Log.d(Util.TAG, "ImageUrl: " + imgUrl);*/
                        //Toast.makeText(UserDetailActivity.this, "Image: " + imgUrl, Toast.LENGTH_SHORT).show();
                        //Util.showAlertDialog(UserDetailActivity.this, "Title", "path: " + imgUrl, true, SweetAlertDialog.WARNING_TYPE);
                        photo.setImageUrl(Util.baseUrl + "/" + object.getString("photo"), AppController.getInstance().getImageLoader());
                        Util.hideAlertDialog();
                        //password.setText(object.getString("password"));
                    }else{
                        Toast.makeText(UserDetailActivity.this, "Invalid User Id", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("MESSAGE", volleyError.toString());
            }
        });
        //requestQueue.add(objectRequest);
        AppController.getInstance().addToRequestQueue(objectRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
