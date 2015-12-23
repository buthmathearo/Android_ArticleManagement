package com.buthmathearo.articlemanagement.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.buthmathearo.articlemanagement.R;

public class SearchArticleActivity_bak extends AppCompatActivity {
    private EditText mEditTextSearch;
    private Button btnBack, btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_article);
        initializeWidget();

    }

    private void initializeWidget() {
        mEditTextSearch = (EditText) findViewById(R.id.editTextSearch);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnSearch = (Button) findViewById(R.id.btn_search);

        initializeWidgetEvent();
    }

    private void initializeWidgetEvent() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
