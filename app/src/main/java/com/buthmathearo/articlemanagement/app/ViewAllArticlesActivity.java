package com.buthmathearo.articlemanagement.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.adapter.ArticleAdapter;
import com.buthmathearo.articlemanagement.model.Article;
import com.buthmathearo.articlemanagement.util.UserLogin;
import com.buthmathearo.articlemanagement.util.Util;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewAllArticlesActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private List<Article> articleList = new ArrayList<Article>();
    private ListView listView;
    private UserLogin mUserLogin;
    private String role = null;
    private int userId;
    private ArticleAdapter adapter;
    private String baseUrl = "http://hrdams.herokuapp.com";
    private String TAG = "OLO_RESULT";
    private LayoutInflater inflater;
    private Toolbar mToolbar;
    private String username;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private SearchView mSearchView;
    private MenuItem mMenuItem;

    private int listItemPosition;

    // Pagination
    private int rowCount = 6;
    private int pageCount = 1;

    private int totalRecords;
    private int totalPages;
    private int remainOfRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_articles);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

       /* mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });*/

        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                refreshList();
            }
        });

        listView = (ListView) findViewById(R.id.listview_content);
        adapter = new ArticleAdapter(this, articleList);
        listView.setAdapter(adapter);


    }


    // Calculate total pages for pagination
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

    // Handle Search on Toolbar
   SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            //Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
            Util.showAlertDialog(ViewAllArticlesActivity.this, "Searching", true, SweetAlertDialog.PROGRESS_TYPE);
            searchAllArticles(query, String.valueOf(totalRecords), String.valueOf(1));

            Util.hideAlertDialog();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (checkUserLogin()){
                    getMenuInflater().inflate(R.menu.activity_main_menu_normal_user, menu);
                    getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
        }
        mMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(listener);
        setEvents();

        return true;
    }


    // Refresh list when user swipe list view
    public void refreshList() {
        //totalPages++;
        if (pageCount < totalPages) {
            pageCount++;
            if (role != null) {
                getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
            }
        } else {
            //Util.showAlertDialog(this, "Suggestion", "Press reload icon to get new article.", true, SweetAlertDialog.NORMAL_TYPE);
            Toast.makeText(ViewAllArticlesActivity.this, "Press Reload to back to first page.", Toast.LENGTH_LONG).show();
        }

        mSwipeRefreshLayout.setRefreshing(false);
        //Toast.makeText(MainActivity.this, "TotalRecords: " + totalRecords + ", TotalPages: " + totalPages + ", Cur.Page: " + pageCount, Toast.LENGTH_SHORT).show();
        //Util.showAlertDialog(this, "Pagination Info", "TotalRecords: " + totalRecords + ", TotalPages: " + totalPages + ", Cur.Page: " + pageCount, true, SweetAlertDialog.NORMAL_TYPE);
        Toast.makeText(ViewAllArticlesActivity.this, "Page: " + pageCount, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            pageCount = 1;
            if (role != null) {
                articleList.clear();
                adapter.notifyDataSetChanged();
                getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void setEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "Item " + position, Toast.LENGTH_SHORT).show();
                Article article = articleList.get(position);
                Intent intent = new Intent(getBaseContext(), ArticleDetailActivity.class);
                intent.putExtra("ARTICLE_ID", "" + article.getId());
                intent.putExtra("USER_ID", article.getUserId());
                intent.putExtra("TITLE", article.getTitle());
                intent.putExtra("IMAGE", article.getImage());
                intent.putExtra("DATE", article.getPublishDate());
                intent.putExtra("DESCRIPTION", article.getDescription());
                intent.putExtra(UserLogin.USER_ID,userId);
                listItemPosition = position;
                //startActivity(intent);
                startActivityForResult(intent, Util.KEY_EDIT_ARTICLE);
            }
        });


    }

    // Check User Login and return User's role
    public boolean checkUserLogin() {

        mUserLogin = new UserLogin(this);
        mUserLogin.readFromSharedPrefFile();
        /*userId = 121;
        role = "admin";
        username = "Buth Mathearo"; return true;*/
        if (mUserLogin.isRemember()) {
            userId = mUserLogin.getId();
            role = mUserLogin.getRole();
            username = mUserLogin.getUsername();
            Toast.makeText(this, "Intent NULL, User ID: " + userId + ", role: " + role  , Toast.LENGTH_SHORT).show();
            return true;
        } else if (getIntent().getExtras() != null) {
            role = getIntent().getExtras().getString(UserLogin.ROLE);
            //Toast.makeText(this, "Intent not NULL, User ID: " + userId + ", role: " + role  , Toast.LENGTH_SHORT).show();
            return true;
        } else return false;


    }

    public void getAllArticles(String row, String pageCount) {
        /*
        "row": "10",
        "pageCount": "1"*/
        //Util.showAlertDialog(this, "Loading...", false, SweetAlertDialog.PROGRESS_TYPE);
        String url = baseUrl + "/api/article/hrd_r001";
        JsonObjectRequest jsonObjectRequest;
        JSONObject params;
        try {
            params = new JSONObject();
            params.put("row", row.trim());
            params.put("pageCount", pageCount.trim());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.trim(), params,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            //Util.hideAlertDialog();
                            try {
                                totalRecords = jsonObject.getInt("TOTAL_REC");
                                totalPages = getTotalPages(totalRecords);
                                //Toast.makeText(MainActivity.this, "TotalRcords: " + totalRecords +  ", TotalPages: " + totalPages, Toast.LENGTH_LONG).show();
                                JSONArray jsonArray = jsonObject.getJSONArray("RES_DATA");

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
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }
                            // Notify to Adapter
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(TAG, volleyError.toString());
                    //Toast.makeText(getBaseContext(), "ERROR MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                    Util.hideAlertDialog();
                    Util.showAlertDialog(ViewAllArticlesActivity.this, "Alert", "Error: " + volleyError.toString(), true, SweetAlertDialog.ERROR_TYPE);
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



    public void searchAllArticles(String title, String row, String pageCount) {
        String url = baseUrl + "/api/article/search/" + title;
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
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                            }
                            // Notify to Adapter
                            adapter.notifyDataSetChanged();
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
