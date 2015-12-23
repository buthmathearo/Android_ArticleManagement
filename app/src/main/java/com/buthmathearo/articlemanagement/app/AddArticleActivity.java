package com.buthmathearo.articlemanagement.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddArticleActivity extends AppCompatActivity {
    private Button mBtnAddArticle;
    private Button mBtnBack;
    private Button mBtnUploadImage;
    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private NetworkImageView mImage;
    private Toolbar mToolbar;
    private Bitmap mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setTitle("ADD ARTICLE");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initWidget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_article_activity_menu, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_article) {
            addArticle(mEditTextTitle.getText().toString(), mEditTextContent.getText().toString(),
                    getIntent().getStringExtra("USER_ID"), "");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void initWidget() {
        mBtnUploadImage = (Button) findViewById(R.id.btnPickupImage);
        mBtnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        mEditTextTitle = (EditText) findViewById(R.id.editTextTitle);
        mEditTextContent = (EditText) findViewById(R.id.editTextContent);

        mImage = (NetworkImageView) findViewById(R.id.photo_pickup);
        mImage.setDefaultImageResId(R.drawable.no_photo);
    }

    public void clearWidget() {
        mEditTextTitle.setText("");
        mEditTextContent.setText("");

        mImage.setImageResource(R.drawable.no_photo);
    }

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Util.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Util.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                mImage.setImageBitmap(mBitmap);
                //Toast.makeText(AddArticleActivity.this, "Set Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Add Article
    public void addArticle(final String title, final String content, final String userId, final String imageUrl) {

        if (title.equals("") || title == null || content.equals("") || content == null) {
            //Toast.makeText(AddArticleActivity.this, "Please complete all required fills.", Toast.LENGTH_LONG).show();
            Util.showAlertDialog(this,"Warning", "Please complete all required fills.", true, SweetAlertDialog.WARNING_TYPE);
            return;
        }

        /*"title": "xxx",
        "description": "xxx",
        "userId": "000",
        "image": ""*/

        try {
            JSONObject params = new JSONObject();
            params.put("title", title.trim());
            params.put("description", content.trim());
            params.put("userId", userId.trim());
            params.put("image", imageUrl);

            String url = Util.baseUrl + "/api/article/hrd_c001";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                if (jsonObject.getBoolean("STATUS")) {

                                    //Toast.makeText(getBaseContext(), "ADDED SUCCESS.", Toast.LENGTH_LONG).show();
                                    //Util.getInstance().showAlertDialog(AddArticleActivity.this, "ADDED SUCCESS", true);
                                    /*Article article = new Article();
                                    article.setTitle(title.trim());
                                    article.setDescription(content.trim());
                                    article.setUserId(Integer.parseInt(userId));
                                    article.setImage(imageUrl);*/

                                    /*params.put("userId", userId.trim());
                                    params.put("image", imageUrl);*/
                                    //Util util = new Util();
                                    Util.showAlertDialog(AddArticleActivity.this, "Successfully added an article.", true, SweetAlertDialog.SUCCESS_TYPE);
                                    clearWidget();

                                }
                            } catch (JSONException e) {
                                Log.d(Util.TAG, e.toString());
                            }
                        }
                    }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(Util.TAG, volleyError.toString());
                }
            });
            // Add to request queue
            AppController.getInstance().addToRequestQueue(request);
        } catch (JSONException e) {
            Log.d(Util.TAG, e.toString());
        }
    }
}
