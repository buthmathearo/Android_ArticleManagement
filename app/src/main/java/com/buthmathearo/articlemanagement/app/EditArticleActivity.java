package com.buthmathearo.articlemanagement.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.model.Article;
import com.buthmathearo.articlemanagement.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditArticleActivity extends AppCompatActivity {
    private String baseUrl = "http://hrdams.herokuapp.com";
    private String TAG = "OLO_RESULT";
    private EditText title, content;
    private NetworkImageView photoPickup;
    private Button btnPickUpImage, btnSave;
    private Article article;
    private Toolbar mToolbar;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setTitle("EDIT ARTICLE");
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

        title = (EditText) findViewById(R.id.editTextTitle);
        content = (EditText) findViewById(R.id.editTextContent);
        photoPickup = (NetworkImageView) findViewById(R.id.photo_pickup);
        photoPickup.setImageResource(R.drawable.no_photo);
        btnPickUpImage = (Button) findViewById(R.id.btnPickupImage);
        btnPickUpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditArticleActivity.this, "Pick", Toast.LENGTH_SHORT).show();
            }
        });

        getArticleDetail(getIntent().getStringExtra("ARTICLE_ID"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_article_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit_article:
                //Toast.makeText(EditArticleActivity.this, "Click save.", Toast.LENGTH_SHORT).show();
                editUserArticle(getIntent().getStringExtra("USER_ID"), article);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void sendDataBack() {
        Intent intent = new Intent();
        intent.putExtra("ARTICLE_ID", String.valueOf(article.getId()));
        intent.putExtra("USER_ID", String.valueOf(article.getUserId()));

        setResult(Util.KEY_EDIT_ARTICLE, intent);

    }

    public void editUserArticle(String userId, Article article){

        /*"id": "000",
        "title": "xxx",
                "description": "xxx",
                "enabled": "true",
                "userId": "000",
                "image": */

        if (title.getText().toString().equals("") || title.getText().toString() == null ||
                content.getText().toString().equals("") || content.getText().toString() == null) {
            Util.showAlertDialog(this, "Please complete all required fills.", true, SweetAlertDialog.WARNING_TYPE);
            return;
        }

        //Util.showAlertDialog(this, "url: " + article.getImageWithoutBaseUrl(), true, SweetAlertDialog.WARNING_TYPE);

        String url = baseUrl + "/api/article/hrd_u001";
        JsonObjectRequest jsonObjectRequest;
        JSONObject params;
        try {
            params = new JSONObject();
            params.put("id", article.getId());
            params.put("title", title.getText().toString().trim());
            params.put("description", content.getText().toString().trim());
            params.put("userId", userId);
            if (imgUrl == null || imgUrl.equals("")) {
                params.put("image", article.getImageWithoutBaseUrl());
            } else params.put("image", imgUrl);

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.trim(), params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                if (jsonObject.getBoolean("STATUS")) {
                                    //Toast.makeText(getBaseContext(), "Saved.", Toast.LENGTH_LONG).show();
                                    //Util.showAlertDialog(EditArticleActivity.this, "Successfully Edited.", true, SweetAlertDialog.SUCCESS_TYPE);
                                    sendDataBack();
                                    finish();
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(TAG, volleyError.toString());
                    Toast.makeText(getBaseContext(), "ERROR_MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add Request to RequestQueue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }


    // Get Article Detail by ID
    public void getArticleDetail(final String articleId) {
        String url = baseUrl + "/api/article/hrd_det001";
        JsonObjectRequest jsonObjectRequest;
        try {
            JSONObject param = new JSONObject();
            param.put("id", articleId);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.trim(), param,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                JSONObject obj = jsonObject.getJSONObject("RES_DATA");
                                /*"id": 211,
                                "title": "",
                                "description": "",
                                "publishDate": "2015-12-07",
                                "image": "http://www.kshrd.com.kh/jsp/img/logo.png",
                                "enabled": true,
                                "userId": 43*/
                                article = new Article();
                                article.setId(obj.getInt("id"));
                                article.setTitle(obj.getString("title"));
                                article.setDescription(obj.getString("description"));
                                article.setPublishDate(obj.getString("publishDate"));
                                article.setImage(obj.getString("image"));
                                article.setUserId(obj.getInt("userId"));
                                article.setEnabled(obj.getBoolean("enabled"));

                                title.setText(article.getTitle());
                                content.setText(article.getDescription());
                                photoPickup.setImageUrl(article.getImage(), AppController.getInstance().getImageLoader());
                                //Toast.makeText(EditArticleActivity.this, "Imageurl: " + article.getImage(), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }

                        }
                    }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(TAG, volleyError.toString());
                    Toast.makeText(getBaseContext(), "ERROR MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
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
