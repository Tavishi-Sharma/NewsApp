package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NewsAdapter.OnSaveButtonClickListener {
    RecyclerView recyclerView;
    List<Article> articleList = new ArrayList<>();
    NewsAdapter newsAdapter;
    LinearProgressIndicator linearProgressIndicator;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    SearchView searchView;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.newsView);
        linearProgressIndicator = findViewById(R.id.progress_bar);
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (isNetworkAvailable()) {
                    getNews("GENERAL", s);
                } else {
                    loadArticlesFromDatabase();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        setRecyclerView();
        if (isNetworkAvailable()) {
            getNews("GENERAL", null);
        } else {
            loadArticlesFromDatabase();
        }
    }

    private void insertArticlesIntoDatabase(List<Article> articles) {
        for (Article article : articles) {
            dbHelper.saveArticle(article);
        }
    }

    private void loadArticlesFromDatabase() {
        articleList.clear();
        articleList.addAll(dbHelper.getAllArticles());
        newsAdapter.notifyDataSetChanged();
    }

    void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(articleList, this,this);
        recyclerView.setAdapter(newsAdapter);
    }

    void changeInProgress(boolean show) {
        if (show) {
            linearProgressIndicator.setVisibility(View.VISIBLE);
        } else {
            linearProgressIndicator.setVisibility(View.INVISIBLE);
        }
    }

    void getNews(String category, String query) {
        changeInProgress(true);
        NewsApiClient newsApiClient = new NewsApiClient("9bc7d81f9d614f348dd8461595750d10");
        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .language("en")
                        .category(category)
                        .q(query)
                        .build(), new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        runOnUiThread(() -> {
                            changeInProgress(false);
                            articleList.clear();
                            articleList.addAll(response.getArticles());
                            newsAdapter.notifyDataSetChanged();
                            insertArticlesIntoDatabase(response.getArticles());
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT Failure", throwable.getMessage());
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        String category = btn.getText().toString();
        getNews(category, null);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSaveButtonClick(Article article) {
        dbHelper.saveArticle(article);
        Toast.makeText(this, "Article saved", Toast.LENGTH_SHORT).show();
    }
}
