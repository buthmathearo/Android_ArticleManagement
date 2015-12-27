package com.buthmathearo.articlemanagement.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

public class ArticleDetailActivity extends AppCompatActivity {
    private float titleFont;
    private float dateFont;
    private float descriptionFont;
    private Toolbar mToolbar;
    private TextView title;
    private TextView date;
    private TextView description;
    private NetworkImageView imageView;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        mToolbar.setTitle("Detail");
        mToolbar.setTitleTextColor(Color.WHITE);
        //mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        title = (TextView) findViewById(R.id.textViewTitle);
        date = (TextView) findViewById(R.id.textViewDate);
        description = (TextView) findViewById(R.id.textViewContent);

        // Get the default font size of each view object
        // Default Fonts: title: 30sp, date: 32sp, description: 36

        titleFont = 20;
        dateFont = 15;
        descriptionFont = 16;
        setFont(titleFont, dateFont, descriptionFont);

        //ImageView imageView = (ImageView) findViewById(R.id.imgThumnail);
        imageView = (NetworkImageView) findViewById(R.id.imageView);

        //Toast.makeText(ArticleDetailActivity.this, "ART_ID: " + getIntent().getStringExtra("ARTICLE_ID"), Toast.LENGTH_SHORT).show();
        getArticleDetail(getIntent().getStringExtra("ARTICLE_ID"));

    }

    // Set font to View Objects
    void setFont(float titleFont, float dateFont, float descriptionFont ){
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleFont);
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP, dateFont);
        description.setTextSize(TypedValue.COMPLEX_UNIT_SP, descriptionFont);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_detail_activity_menu, menu);
        // Disable Action_Edit if User is not login.
        if (getIntent().getStringExtra("ROLE") == null) {
            menu.findItem(R.id.action_edit).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_increase_font:
                //Util.showAlertDialog(this, "title, date, des " + titleFont + " " + dateFont + " " + descriptionFont, true, SweetAlertDialog.NORMAL_TYPE);
                if (titleFont < 70) {
                    titleFont += 10;
                    dateFont += 10;
                    descriptionFont += 10;
                    setFont(titleFont, dateFont, descriptionFont);
                }
                break;
            case R.id.action_decrease_font:
                if (titleFont > 20) {
                    titleFont -= 10;
                    dateFont -= 10;
                    descriptionFont -= 10;
                    setFont(titleFont, dateFont, descriptionFont);
                }
                break;
            case R.id.action_default_font:
                titleFont = 20;
                dateFont = 15;
                descriptionFont = 16;
                setFont(titleFont, dateFont, descriptionFont);
                break;
            case R.id.action_edit:
                Intent intent = new Intent(this, EditArticleActivity.class);
                intent.putExtra("ARTICLE_ID", getIntent().getStringExtra("ARTICLE_ID"));
                intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                startActivityForResult(intent, Util.KEY_EDIT_ARTICLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    // Send Data back to other activity when activity is finished.
    public void finishActivity() {
        // Flag: note for Article edited.
        if (flag) sendDataback();
        finish();
    }

    public void sendDataback(){
        Intent intent = new Intent();
        intent.putExtra("ARTICLE_ID", getIntent().getStringExtra("ARTICLE_ID"));
        setResult(Util.KEY_EDIT_ARTICLE, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Util.KEY_EDIT_ARTICLE && resultCode == Util.KEY_EDIT_ARTICLE) {
            getArticleDetail(data.getStringExtra("ARTICLE_ID"));
            flag = true; // Flag = true means user has edited the article.
        }

    }

    // Get Article Detail by ID
    public void getArticleDetail(final String articleId) {
        Util.showAlertDialog(this, "Fetching Data...", true, SweetAlertDialog.PROGRESS_TYPE);
        String url = Util.baseUrl + "/api/article/hrd_det001";
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
                                Article tmpArticle = new Article();
                                tmpArticle.setId(obj.getInt("id"));
                                tmpArticle.setTitle(obj.getString("title"));
                                tmpArticle.setDescription(obj.getString("description"));
                                tmpArticle.setPublishDate(obj.getString("publishDate"));
                                tmpArticle.setImage(obj.getString("image"));
                                tmpArticle.setUserId(obj.getInt("userId"));
                                tmpArticle.setEnabled(obj.getBoolean("enabled"));

                                title.setText(tmpArticle.getTitle());
                                date.setText("Date: " + tmpArticle.getPublishDate());
                                description.setText(tmpArticle.getDescription());
                                imageView.setImageUrl(tmpArticle.getImage(), AppController.getInstance().getImageLoader());
                            } catch (JSONException e) {
                                Log.d(Util.TAG, e.toString());
                            }
                            Util.hideAlertDialog();
                        }
                    }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(Util.TAG, volleyError.toString());
                    Toast.makeText(getBaseContext(), "ERROR MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add RequestQueue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            Log.d(Util.TAG, e.toString());
        } catch (Exception e) {
            Log.d(Util.TAG, e.toString());
        }
    }


}
