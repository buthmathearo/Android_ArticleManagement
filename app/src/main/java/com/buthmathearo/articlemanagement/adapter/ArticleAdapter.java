package com.buthmathearo.articlemanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.buthmathearo.articlemanagement.R;
import com.buthmathearo.articlemanagement.app.AppController;
import com.buthmathearo.articlemanagement.app.ArticleDetailActivity;
import com.buthmathearo.articlemanagement.model.Article;

import java.util.List;

/**
 * Created by buthmathearo on 12/6/15.
 */
public class ArticleAdapter extends BaseAdapter {
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<Article> articles;
    private ImageLoader mImageLoader;

    public ArticleAdapter(Activity activity, List<Article> articles) {
        mActivity = activity;
        this.articles = articles;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cv_list_item, null);
        }

        if (mImageLoader == null) {
            mImageLoader = AppController.getInstance().getImageLoader();
        }

        final Article article = articles.get(position);

        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.imgThumnail);
        //imageView.setImageUrl(article.getImage(), mImageLoader); //http://imgtops.sourceforge.net/bakeoff/o-png8.png
        //imageView.setImageUrl("http://imgtops.sourceforge.net/bakeoff/o-png8.png", mImageLoader);
        imageView.setImageUrl(article.getImage(), mImageLoader);

        TextView title = (TextView) convertView.findViewById(R.id.textViewTitle);
        title.setSingleLine(false);
        title.setText(article.getShortTitle());
        //title.setAllCaps(true);


        TextView description = (TextView) convertView.findViewById(R.id.textViewContent);
        description.setSingleLine(false);
        description.setText(article.getShortDescription());
        TextView publishDate = (TextView) convertView.findViewById(R.id.textViewDate);
        publishDate.setText(article.getPublishDate());

        Button btnReadMore = (Button) convertView.findViewById(R.id.btnReadMore);
        final int pos = position;
        btnReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mActivity, "click", Toast.LENGTH_LONG).show();
                Article article = articles.get(pos);
                Intent intent = new Intent(mActivity, ArticleDetailActivity.class);
                intent.putExtra("ARTICLE_ID", String.valueOf(article.getId()));
                /*intent.putExtra("USER_ID", article.getUserId());
                intent.putExtra("TITLE", article.getTitle());
                intent.putExtra("IMAGE", article.getImage());
                intent.putExtra("DATE", article.getPublishDate());
                intent.putExtra("DESCRIPTION", article.getDescription());*/
                mActivity.startActivity(intent);
            }
        });


        Button btnShare = (Button) convertView.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                myIntent.putExtra(Intent.EXTRA_TEXT, article.getTitle());
                myIntent.putExtra(Intent.EXTRA_SUBJECT,"Check out this side");
                mActivity.startActivity(Intent.createChooser(myIntent,"Share With:"));
            }
        });

        return convertView;
    }
}
