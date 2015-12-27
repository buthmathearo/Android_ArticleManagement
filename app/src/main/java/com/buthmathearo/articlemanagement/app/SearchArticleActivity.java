package com.buthmathearo.articlemanagement.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.adapter.ArticleAdapter;
import com.buthmathearo.articlemanagement.model.Article;
import com.buthmathearo.articlemanagement.util.Util;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import android.support.v4.widget.SwipeRefreshLayout;

//import android.support.v4.widget.SwipeRefreshLayout;

//import android.support.v4.widget.SwipeRefreshLayout;

public class SearchArticleActivity extends AppCompatActivity {
    private EditText mEditTextSearch;
    private Button btnBack, btnSearch;
    private ListView lstView;
    private ArticleAdapter adapter;
    private ArrayList<Article> articleList = new ArrayList<>();
    private String role = null;
    private String keyword;
    private String userId;
    private Toolbar mToolbar;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private boolean flag = false;
    // Pagination
    private int rowCount = 20;
    private int pageCount = 1;

    private int totalRecords;
    private int totalPages;
    private int remainOfRecords;
    private String TAG = "OLO_RESULT";
    private int listItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_article);
        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        initializeWidget();

        role = getIntent().getStringExtra("ROLE");
        keyword = getIntent().getStringExtra("KEYWORD");
        userId = getIntent().getStringExtra("USER_ID");
        showSearchResult();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Util.KEY_EDIT_ARTICLE){
            // Get Data Back from EditArticleActivity
            if (resultCode == Util.KEY_EDIT_ARTICLE){
                //Toast.makeText(MainActivity.this, "ID: " + data.getStringExtra("ARTICLE_ID"), Toast.LENGTH_SHORT).show();
                getArticleDetail(data.getStringExtra("ARTICLE_ID"));
                Util.showAlertDialog(this, "Succefully edited an article.", true, SweetAlertDialog.SUCCESS_TYPE);
                flag = true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    private void initializeWidget() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        mToolbar.setTitle("Search Result");
        mToolbar.setTitleTextColor(Color.WHITE);
        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_search_swipe_refresh_layout);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.activity_search_swipe_refresh_layout);

        mEditTextSearch = (EditText) findViewById(R.id.editTextSearch);
        lstView = (ListView) findViewById(R.id.list_view_search_activity);
        adapter = new ArticleAdapter(this, articleList);
        lstView.setAdapter(adapter);

        initializeWidgetEvent();
    }

    private void initializeWidgetEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    if (pageCount <= totalPages) {
                        pageCount++;
                        showSearchResult();
                    } else {
                        Toast.makeText(SearchArticleActivity.this, "No more record.", Toast.LENGTH_SHORT).show();
                    }
                }
                mSwipyRefreshLayout.setRefreshing(false);
            }
        });

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemPosition = position;
                Intent intent = new Intent(SearchArticleActivity.this, ArticleDetailActivity.class);
                intent.putExtra("ARTICLE_ID", String.valueOf(articleList.get(position).getId()));
                intent.putExtra("ROLE", role);
                intent.putExtra("USER_ID", String.valueOf(articleList.get(position).getUserId()));
                //startActivity(intent);
                startActivityForResult(intent, Util.KEY_EDIT_ARTICLE);
            }
        });

        /* Enable Long_Click on ListView_Item for Login User Only. */
        if (role != null) {
            /*Start Sokngim Code*/
            lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    listItemPosition = position;
                    final CharSequence[] items = {
                            "Delete", "Edit"
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchArticleActivity.this);
                    //builder.setTitle("Make your selection");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if(items[item].equals("Delete")){
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchArticleActivity.this);
                                alertDialog.setTitle("Confirm Delete...");
                                alertDialog.setMessage("Are you sure you want delete this?");
                                alertDialog.setIcon(R.mipmap.ic_launcher);
                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Write your code here to invoke YES event
                                        // Toast.makeText(SearchArticleActivity.this, "You clicked on YES", Toast.LENGTH_SHORT).show();
                                        deleletUserArticle(articleList.get(position).getId()+"", position);

                                    }
                                });
                                alertDialog.show();
                            }
                            if(items[item].equals("Edit")){
                                //Toast.makeText(SearchArticleActivity.this, "You clicked on Edit", Toast.LENGTH_SHORT).show();
                                listItemPosition = position;
                                Intent intent = new Intent(getApplicationContext(),EditArticleActivity.class);
                                intent.putExtra("USER_ID", "" + articleList.get(position).getUserId());
                                intent.putExtra("ARTICLE_ID",articleList.get(position).getId()+"");
                                startActivityForResult(intent, Util.KEY_EDIT_ARTICLE);
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });

            /*end Sokngim Code*/
        }
    }

    // Perform search
    public void showSearchResult() {
        Util.showAlertDialog(this, "Searching", true, SweetAlertDialog.PROGRESS_TYPE);

        if (role != null) {
            if (role.equalsIgnoreCase("user")) {
                searchUserArticle(keyword,
                        String.valueOf(rowCount), String.valueOf(pageCount), Integer.parseInt(userId));

            } else if (role.equalsIgnoreCase("admin")) {
                searchAllArticles(keyword, String.valueOf(rowCount), String.valueOf(pageCount));
            }
        } else {
            searchAllArticles(keyword, String.valueOf(rowCount), String.valueOf(pageCount));
        }
        Util.hideAlertDialog();
    }

    /* Calculate total pages for pagination */
    public int getTotalPages(int totalRecords) {
        if (totalRecords >= rowCount ) {
            totalPages = totalRecords / rowCount;
            remainOfRecords = totalRecords % rowCount;
            if (remainOfRecords > 0) totalPages++;
        } else {
            totalPages = 1;
        }
        return totalPages;
    }

    /* Search all articles. */
    public void searchAllArticles(String title, String row, String pageCount) {
        String url = Util.baseUrl + "/api/article/search/" + title;
        JsonObjectRequest jsonObjectRequest;
        JSONObject params;
        try {
            params = new JSONObject();
            params.put("row", row.trim());
            params.put("pageCount", pageCount.trim());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.trim(), params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            //Util.showAlertDialog(SearchArticleActivity.this, keyword, jsonObject.toString(), true, SweetAlertDialog.NORMAL_TYPE);
                            //articleList.clear();
                            //Toast.makeText(getBaseContext(), "Search: " + jsonObject.toString(), Toast.LENGTH_LONG).show();
                            try {

                                JSONArray jsonArray = jsonObject.getJSONArray("RES_DATA");
                                totalRecords = jsonObject.getInt("TOTAL_REC");
                                totalPages = getTotalPages(totalRecords);
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    Article article = new Article();
                                    article.setId(obj.getInt("id"));
                                    article.setTitle(obj.getString("title"));
                                    article.setDescription(obj.getString("description"));
                                    article.setPublishDate(obj.getString("publishDate"));
                                    article.setImage(obj.getString("image"));
                                    article.setUserId(obj.getInt("userId"));
                                    article.setEnabled(obj.getBoolean("enabled"));

                                    articleList.add(article);
                                }

                                mToolbar.setTitle("Search Result (" + totalRecords + " Rec.)");

                            } catch (JSONException e) {
                                Log.d(Util.TAG, e.toString());
                            }
                            // Notify to Adapter
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(Util.TAG, volleyError.toString());
                    ///Toast.makeText(getBaseContext(), "ERROR_MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                    Util.showAlertDialog(SearchArticleActivity.this, "Alert", "Please check your Internet Connection.", true, SweetAlertDialog.ERROR_TYPE);
                }
            });

            // Add Request to RequestQueue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            Log.d(Util.TAG, e.toString());
        } catch (Exception e) {
            Log.d(Util.TAG, e.toString());
        }
    }


    /* Search User's Articles. */
    public void searchUserArticle(String title, String row, String pageCount, final int userId) {
        String url = Util.baseUrl + "/api/article/search/" + title;
        JsonObjectRequest jsonObjectRequest;
        JSONObject params;
        try {
            params = new JSONObject();
            params.put("row", row.trim());
            params.put("pageCount", pageCount.trim());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.trim(), params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            articleList.clear();
                            //Toast.makeText(getBaseContext(), "Search: " + jsonObject.toString(), Toast.LENGTH_LONG).show();
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("RES_DATA");
                                totalRecords = jsonObject.getInt("TOTAL_REC");
                                totalPages = getTotalPages(totalRecords);
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    if (userId == obj.getInt("userId")){
                                        Article article = new Article();
                                        article.setId(obj.getInt("id"));
                                        article.setTitle(obj.getString("title"));
                                        article.setDescription(obj.getString("description"));
                                        article.setPublishDate(obj.getString("publishDate"));
                                        article.setImage(obj.getString("image"));
                                        article.setUserId(obj.getInt("userId"));
                                        article.setEnabled(obj.getBoolean("enabled"));

                                        articleList.add(article);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.d(Util.TAG, e.toString());
                            }
                            // Notify to Adapter
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(Util.TAG, volleyError.toString());
                    Toast.makeText(getBaseContext(), "ERROR_MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add Request to RequestQueue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            Log.d(Util.TAG, e.toString());
        } catch (Exception e) {
            Log.d(Util.TAG, e.toString());
        }
    }


    /* Delete User's Article */
    public void deleletUserArticle(String articleId, final int listItemPosition){
        String url = Util.baseUrl + "/api/article/hrd_d001";
        JsonObjectRequest jsonObjectRequest;
        JSONObject params;
        try {
            params = new JSONObject();
            params.put("id", articleId.trim());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.trim(), params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                if (jsonObject.getBoolean("STATUS")) {
                                    articleList.remove(listItemPosition);
                                    Util.showAlertDialog(SearchArticleActivity.this, "Deleted.", true, SweetAlertDialog.SUCCESS_TYPE);
                                    //Toast.makeText(getBaseContext(), "Deleted ID: " + listItemPosition, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }
                            adapter.notifyDataSetChanged();
                            //listView.removeViewAt(listItemPosition);
                            //Toast.makeText(getBaseContext(), "Search: " + jsonObject.toString(), Toast.LENGTH_LONG).show();
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


    /* Get Article Detail by ID */
    public void getArticleDetail(final String articleId) {
        Toast.makeText(SearchArticleActivity.this, "ART_ID: " + articleId, Toast.LENGTH_SHORT).show();
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

                                articleList.get(listItemPosition).setTitle(tmpArticle.getShortTitle());
                                articleList.get(listItemPosition).setDescription(tmpArticle.getShortDescription());
                                articleList.get(listItemPosition).setImage(tmpArticle.getImageWithoutBaseUrl());
                                adapter.notifyDataSetChanged();

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
