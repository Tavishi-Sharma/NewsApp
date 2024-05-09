package com.example.newsapp;

import com.kwabenaberko.newsapilib.models.Article;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NewsApiClient {

    private static final String API_KEY = "9bc7d81f9d614f348dd8461595750d10";
    private static NewsApiClient instance;

    private NewsApiClient() {
        // Private constructor to prevent instantiation from outside
    }

    public static NewsApiClient getInstance() {
        if (instance == null) {
            instance = new NewsApiClient();
        }
        return instance;
    }

    public interface ArticleContentCallback {
        void onContentReceived(String articleContent);



        void onFailure(String errorMessage);
    }

    public void getArticleContent(String articleUrl, ArticleContentCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Construct the URL for the summarize endpoint
        String summarizeUrl = "https://newsapi.org/v2/summarize?url=" + articleUrl + "&apiKey=" + API_KEY;

        Request request = new Request.Builder()
                .url(summarizeUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        // Extract the summarized content from the response
                        String articleContent = json.getString("text");
                        callback.onContentReceived(articleContent);
                    } catch (JSONException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    // Handle unsuccessful response
                    callback.onFailure("Failed to retrieve article content");
                }
            }
        });
    }
}
