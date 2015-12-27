package com.buthmathearo.articlemanagement.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import android.support.v4.widget.SwipeRefreshLayout;

//import android.support.v4.widget.SwipeRefreshLayout;

/*import android.support.v4.widget.SwipeRefreshLayout;*/

public class MainActivity_bak extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView mSearchView;
    private MenuItem mMenuItem;

    private DrawerLayout drawer;
    private FrameLayout container;
    private NavigationView navigationView;
    private Menu navMenu;
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
        setContentView(R.layout.activity_main);

        /* Enable Custom Toolbar */
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        //mToolbar.setLogo(R.drawable.logo_droid_news);
        mToolbar.setNavigationIcon(R.drawable.menu_btn);
        mToolbar.setTitleTextColor(Color.WHITE);
        //mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        listView = (ListView) findViewById(R.id.listview_content);
        adapter = new ArticleAdapter(this, articleList);
        listView.setAdapter(adapter);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);


        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navMenu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_add);

    }

   @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    /* Handle Action_Search on Toolbar */
   SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            //Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
            /*Util.showAlertDialog(MainActivity.this, "Searching", true, SweetAlertDialog.PROGRESS_TYPE);
            if (role != null) {
                if (role.equalsIgnoreCase("user")) {
                    searchUserArticle(query,
                            String.valueOf(totalRecords), String.valueOf(1), userId);
                } else if (role.equalsIgnoreCase("admin")) {
                    searchAllArticles(query, String.valueOf(totalRecords), String.valueOf(1));
                }
            } else {
                searchAllArticles(query, String.valueOf(totalRecords), String.valueOf(1));
            }
            Util.hideAlertDialog();*/
            //Toast.makeText(MainActivity.this, "Search...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity_bak.this, SearchArticleActivity.class);
            intent.putExtra("USER_ID", String.valueOf(userId));
            intent.putExtra("ROLE", role);
            intent.putExtra("KEYWORD", query);
            startActivity(intent);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        boolean b = false;
        // Get menu from Navigation Drawer
        navMenu = navigationView.getMenu();
        if (checkUserLogin()){
            if (role != null){
                navMenu.findItem(R.id.nav_user_profile).setVisible(true);
                navMenu.findItem(R.id.nav_login).setTitle("Logout");
                navMenu.findItem(R.id.nav_register).setVisible(false);
                b = true;
                if (role.equalsIgnoreCase("user")) {
                    getMenuInflater().inflate(R.menu.activity_main_menu_user, menu);
                    getUserArticles(String.valueOf(userId), String.valueOf(rowCount), String.valueOf(pageCount));
                } else if (role.equalsIgnoreCase("admin")) {
                    getMenuInflater().inflate(R.menu.activity_main_menu_admin, menu);
                    getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
                }
            }
        } else {
            navMenu.findItem(R.id.nav_user_profile).setVisible(false);
            b = true;
            getMenuInflater().inflate(R.menu.activity_main_menu_normal_user, menu);
            getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
        }

        if (b) {
            mMenuItem = menu.findItem(R.id.action_search);
            mSearchView = (SearchView) mMenuItem.getActionView();
            mSearchView.setOnQueryTextListener(listener);
        }

        /* Initialize View Objects (Widgets) Event */
        setEvents();

        return true;
    }

    /* Refresh List When User Swipe List View  */
    public void refreshList() {
        //totalPages++;
        if (pageCount < totalPages) {
            pageCount++;
            if (role != null) {
                if (role.equalsIgnoreCase("user")) {
                    getUserArticles(String.valueOf(userId), String.valueOf(rowCount), String.valueOf(pageCount));
                } else if (role.equalsIgnoreCase("admin")) {
                    getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
                }
            } else {
                getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
            }
        } else {
            //Util.showAlertDialog(this, "Suggestion", "Press reload icon to get new article.", true, SweetAlertDialog.NORMAL_TYPE);
            Toast.makeText(MainActivity_bak.this, "Press Reload to back to first page.", Toast.LENGTH_LONG).show();
        }

        mSwipeRefreshLayout.setRefreshing(false);
        //Toast.makeText(MainActivity.this, "TotalRecords: " + totalRecords + ", TotalPages: " + totalPages + ", Cur.Page: " + pageCount, Toast.LENGTH_SHORT).show();
        //Util.showAlertDialog(this, "Pagination Info", "TotalRecords: " + totalRecords + ", TotalPages: " + totalPages + ", Cur.Page: " + pageCount, true, SweetAlertDialog.NORMAL_TYPE);
        Toast.makeText(MainActivity_bak.this, "Page: " + pageCount, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /* Handle All Action_Buttons on Toolbar */
        if (id == R.id.action_add_article) {
            Intent intent = new Intent(getBaseContext(), AddArticleActivity.class);
            intent.putExtra("USER_ID", "" + userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_reload) {
            pageCount = 1;
            if (role != null) {
                if (role.equalsIgnoreCase("user")) {
                    articleList.clear();
                    adapter.notifyDataSetChanged();
                    getUserArticles(String.valueOf(userId), String.valueOf(rowCount), String.valueOf(pageCount));
                } else if (role.equalsIgnoreCase("admin")) {
                    articleList.clear();
                    adapter.notifyDataSetChanged();
                    getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
                }
            } else {
                articleList.clear();
                adapter.notifyDataSetChanged();
                getAllArticles(String.valueOf(rowCount), String.valueOf(pageCount));
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        switch(item.getItemId()){
            case R.id.nav_login:
                if (item.getTitle().equals("Login")) {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                   /* if (Build.VERSION.SDK_INT >= 11) {
                        recreate();
                    } else {
                        Intent myIntent = getIntent();
                        item.setTitle("Login");
                        mUserLogin.clear();
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        startActivity(myIntent);

                    }*/

                    /* Create Confirm Dialog when user logout. */
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Confirm")
                            .setContentText("Do you want to logout?")
                            .setCancelText("No")
                            .setConfirmText("Yes")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    mUserLogin.clear();
                                    Intent loginIntent = new Intent(MainActivity_bak.this, LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish();
                                }
                    }).show();
                }

                break;
            case R.id.nav_register:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_user_profile:
                intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra("USER_ID", "" + userId);
                startActivity(intent);
                break;
            case R.id.nav_developer:
                Util.showAlertDialog(this, "Developers", "Buth Mathearo\nLim Seudy" +
                        "\nSa Sokngim\nEath Manith\nThorn Sereyvong", true, SweetAlertDialog.NORMAL_TYPE);
                break;
            case R.id.nav_share:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                myIntent.putExtra(Intent.EXTRA_TEXT, "http://hrdams.herokuapp.com");
                myIntent.putExtra(Intent.EXTRA_SUBJECT,"Check out this side");
                startActivity(Intent.createChooser(myIntent,"Share With:"));
                break;
        }

        //Toast.makeText(this, "Hello " + item.getItemId(), Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /* Initialize View Objects (Widgets) Event. */
    public void setEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "Item " + position, Toast.LENGTH_SHORT).show();
                Article article = articleList.get(position);
                Intent intent = new Intent(getBaseContext(), ArticleDetailActivity.class);
                intent.putExtra("ARTICLE_ID", String.valueOf(article.getId()));
                intent.putExtra("ROLE", role);
                intent.putExtra("USER_ID", String.valueOf(article.getUserId()));
                intent.putExtra("TITLE", article.getTitle());
                intent.putExtra("IMAGE", article.getImage());
                intent.putExtra("DATE", article.getPublishDate());
                intent.putExtra("DESCRIPTION", article.getDescription());
                listItemPosition = position;
                //startActivity(intent);
                startActivityForResult(intent, Util.KEY_EDIT_ARTICLE);
            }
        });

        /* Enable Long_Click on ListView_Item for Login User Only. */
        if (role != null) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Article article = articleList.get(position);
                    listItemPosition = position;
                    showConfirmDialog(""+article.getId(), position);
                    return true;
                }
            });
        }

    }

    /* Important methods: Check whether User has Login. SharedPref File will be checked
     * when user choose 'Remember' on LoginActivity */
    public boolean checkUserLogin() {

        mUserLogin = new UserLogin(this);
        mUserLogin.readFromSharedPrefFile(); // Get all save values from SharedPrefFile
        /*userId = 121;
        role = "admin";
        username = "Buth Mathearo"; return true;*/

        if (mUserLogin.isRemember()) {
            userId = mUserLogin.getId();
            role = mUserLogin.getRole();
            username = mUserLogin.getUsername();
            //Toast.makeText(this, "Intent NULL, User ID: " + userId + ", role: " + role + ", name: " + username , Toast.LENGTH_SHORT).show();
            return true;
        } else if (getIntent().getExtras() != null) {
            role = getIntent().getExtras().getString(UserLogin.ROLE);
            userId = getIntent().getExtras().getInt(UserLogin.USER_ID);
            username = getIntent().getExtras().getString(UserLogin.USERNAME);
            //Toast.makeText(this, "Intent not NULL, User ID: " + userId + ", role: " + role + ", name: " + username , Toast.LENGTH_SHORT).show();
            return true;
        } else return false;

        /*if (getIntent().getExtras() == null) {
            mUserLogin = new UserLogin(this);
            mUserLogin.readFromSharedPrefFile();
            if (mUserLogin.isRemember()) {
                userId = mUserLogin.getId();
                role = mUserLogin.getRole();
                username = mUserLogin.getUsername();
                Toast.makeText(this, "Intent NULL, User ID: " + userId + ", role: " + role + ", name: " + username , Toast.LENGTH_SHORT).show();
                return true;
            }
            //Toast.makeText(this, "Already Login. Remembered " + mUserLogin.isRemember(), Toast.LENGTH_SHORT).show();
        } else {
            role = getIntent().getExtras().getString(UserLogin.ROLE);
            userId = getIntent().getExtras().getInt(UserLogin.USER_ID);
            username = getIntent().getExtras().getString(UserLogin.USERNAME);
            Toast.makeText(this, "Intent not NULL, User ID: " + userId + ", role: " + role + ", name: " + username , Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;*/
    }

    /* Retrieve all articles from Server then Keep all record in an ArrayList. */
    public void getAllArticles(String row, String pageCount) {
        /*
        "row": "10",
        "pageCount": "1"
        */
        Util.showAlertDialog(this, "Loading...", false, SweetAlertDialog.PROGRESS_TYPE);
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
                            Util.hideAlertDialog();
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
                    Util.showAlertDialog(MainActivity_bak.this, "Alert", "Error: " + volleyError.toString(), true, SweetAlertDialog.ERROR_TYPE);
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

    /* Retrieve all  User's Articles by specified User ID. */
    public void getUserArticles(String userId, String row, String pageCount) {
        /*
        "row": "10",
        "pageCount": "1"*/
        Util.showAlertDialog(this, "Loading...", false, SweetAlertDialog.PROGRESS_TYPE);
        String url = baseUrl + "/api/article/user/" + userId;
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
                            Util.hideAlertDialog();
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("RES_DATA");

                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    /*"id": 211,
                                    "title": "",
                                    "description": "",
                                    "publishDate": "2015-12-07",
                                    "image": "http://www.kshrd.com.kh/jsp/img/logo.png",
                                    "enabled": true,
                                    "userId": 43*/
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
                    Util.hideAlertDialog();
                    Util.showAlertDialog(MainActivity_bak.this, "Alert", "Error: " + volleyError.toString(), true, SweetAlertDialog.ERROR_TYPE);
                    //Toast.makeText(getBaseContext(), "ERROR MESSAGE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
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

    /* Search User's Articles. */
    public void searchUserArticle(String title, String row, String pageCount, final int userId) {
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


    /* Search all articles. */
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

    /* Delete User's Article */
    public void deleletUserArticle(String articleId, final int listItemPosition){
        String url = baseUrl + "/api/article/hrd_d001";
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
                                    Util.showAlertDialog(MainActivity_bak.this, "Deleted.", true, SweetAlertDialog.SUCCESS_TYPE);
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

    /* Show a Dialog (Context Menu) When User Long_Click on ListView_Item. */
    public void showConfirmDialog(final String articleId, final int position) {
        final CharSequence[] items = {
                "Delete", "Edit"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_bak.this);
        //builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(items[item].equals("Delete")){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity_bak.this);
                    alertDialog.setTitle("Confirm");
                    alertDialog.setMessage("Do you want to delete?");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleletUserArticle(articleId, position);
                            // Write your code here to invoke YES event
                            //Toast.makeText(MainActivity.this, "You clicked on YES", Toast.LENGTH_SHORT).show();

                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            //Toast.makeText(MainActivity.this, "You clicked on NO", Toast.LENGTH_SHORT).show();
                            //dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                if(items[item].equals("Edit")){
                    //Toast.makeText(MainActivity.this, "You clicked on Edit", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), EditArticleActivity.class);
                    intent.putExtra("USER_ID", "" + articleList.get(position).getUserId());
                    intent.putExtra("ARTICLE_ID", articleId);

                    //startActivity(intent);
                    startActivityForResult(intent, Util.KEY_EDIT_ARTICLE);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
            }
        }

        /*if (data != null)
            Util.showAlertDialog(this,"MSG" ,"ART_ID: " + data.getStringExtra("ARTICLE_ID") + ", USER_ID: " + userId, true, SweetAlertDialog.NORMAL_TYPE);
*/
    }

    /* Get Article Detail by ID */
    public void getArticleDetail(final String articleId) {
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
                                //Toast.makeText(MainActivity.this, "USER_ID: " + articleList.get(listItemPosition).getUserId(), Toast.LENGTH_SHORT).show();
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
