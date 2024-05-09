package com.example.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kwabenaberko.newsapilib.models.Article;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table to store articles
        String SQL_CREATE_ARTICLES_TABLE = "CREATE TABLE IF NOT EXISTS articles (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "source TEXT, " +  // Add source column
                "title TEXT, " +
                "url TEXT)";

        // Execute SQL statement
        db.execSQL(SQL_CREATE_ARTICLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
        db.execSQL("DROP TABLE IF EXISTS articles");
        onCreate(db);
    }

    public void saveArticle(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("source", article.getSource().getName());
        values.put("title", article.getTitle());
        values.put("url", article.getUrl());
        db.insert("articles", null, values);
        db.close();
    }

    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM articles", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Article article = new Article();
                article.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                // Populate other article fields as needed (e.g., source, url)
                articles.add(article);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return articles;
    }
}
