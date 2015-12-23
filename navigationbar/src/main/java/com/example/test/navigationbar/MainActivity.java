package com.example.test.navigationbar;

import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.PointF;

import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageDetail;
    ImageView imageView;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;


    String[] item = {"Save Image", "Share Image"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

         imageView = (ImageView) findViewById(R.id.image_view);

        //event on long click on image
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, item[which], Toast.LENGTH_LONG).show();

                    }
                    // The 'which' argument contains the index position
                    // of the selected it
                });
                builder.create();
                builder.show();
                return false;
            }
        });

        //event on click zoom in and out
       imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                System.out.println("matrix=" + savedMatrix.toString());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:/*
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;*/
                        return false;
                       // break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 2f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        //return false;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                       /* mode = NONE;*/
                        return false;
                       // break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 2f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;


            }


            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                float result = (float) Math.sqrt(x * x + y * y);
                return result;
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });


        //animation
        RotateAnimation anim = new RotateAnimation(30, 360, imageView.getWidth()/2, imageView.getHeight()/2);
        anim.setFillAfter(true);
        anim.setRepeatCount(0);
        anim.setDuration(10000);
        imageView.startAnimation(anim);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //using option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.save_image:
                Toast.makeText(getApplicationContext(), "Save Image Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.crop:
                Toast.makeText(getApplicationContext(), "Crop Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.rotate_left:
                //Toast.makeText(getApplicationContext(), "Rotate Left Selected", Toast.LENGTH_LONG).show();
                Matrix matrix = new Matrix();
                imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
                matrix.postRotate((float) 90, 50, 50);
                imageView.setImageMatrix(matrix);
                return true;
            case R.id.rotate_right:
                Toast.makeText(getApplicationContext(), "Rotate Right Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(getApplicationContext(), "Navigation", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
