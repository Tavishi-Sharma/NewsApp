package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Switch;

import com.kwabenaberko.newsapilib.models.Article;

public class DetailNewsActivity extends AppCompatActivity {
    WebView webView;
    Switch summarySwitch;
    String articleContent,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        url=getIntent().getStringExtra("url");
        webView=findViewById(R.id.webView);
        summarySwitch = findViewById(R.id.summarySwitch);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        summarySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fetchArticleContent();
            } else {
                // Display the full article
                webView.loadUrl(url);
            }
        });
        fetchArticleContent();
    }

    private void fetchArticleContent() {
        NewsApiClient.getInstance().getArticleContent(url, new NewsApiClient.ArticleContentCallback() {

            @Override
            public void onContentReceived(String content) {
                if (content != null) {
                    articleContent = content;
                    if (summarySwitch.isChecked()) {
                        webView.loadData(articleContent, "text/html", "UTF-8");
                    }
                    Log.d("DetailNewsActivity", "Content received: " + content); // Add this line for debugging
                } else {
                    Log.d("DetailNewsActivity", "Content is null"); // Add this line for debugging
                }
            }





            @Override
            public void onFailure(String errorMessage) {
                // Handle failure, maybe show error message
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}