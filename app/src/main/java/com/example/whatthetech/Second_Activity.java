package com.example.whatthetech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Second_Activity extends AppCompatActivity {

    ArrayList<String> titleList = new ArrayList<String>();
    ArrayList<String> urlList = new ArrayList<String>();
    SQLiteDatabase articlesDatabase;
    ListView listView;



    public void addToList(){
        try {
            Cursor c = articlesDatabase.rawQuery("SELECT * FROM articles", null);
            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int urlIndex = c.getColumnIndex("url");
            c.moveToFirst();

            while (c != null) {

                titleList.add(c.getString(titleIndex));
                urlList.add(c.getString(urlIndex));

                c.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }//e

    public class DownloadContent extends AsyncTask<String , Void , String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            //think of it like a browser
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                //gather the data as it coming through
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Result", "Failed");
                return "Failed";

            }

        }//end doInBackground method

    }//end DownloadContent class

    public void addToSql(String id){

        String result= null;
        DownloadContent task = new DownloadContent();
        int theId = Integer.parseInt(id);
        try {
            result = task.execute("https://hacker-news.firebaseio.com/v0/item/"+ id+".json?print=pretty").get();
            JSONObject jsonObject = new JSONObject(result);
            String title= jsonObject.getString("title");
            String url= jsonObject.getString("url");
            Log.i("title", title);
            Log.i("url", url);

            //Insert the data into the Database
            String sql = "INSERT INTO articles (id, title, url) VALUES(?, ?, ?)";
            SQLiteStatement statement = articlesDatabase.compileStatement(sql);
            statement.bindString(1, id);
            statement.bindString(2, title);
            statement.bindString(3, url);
            statement.execute();


        } catch (Exception e){
            e.printStackTrace();
        }

    }//end addToSql method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_);


        articlesDatabase = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articlesDatabase.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, title VARCHAR, url VARCHAR)");
        articlesDatabase.execSQL("DELETE FROM articles");

        listView = (ListView) findViewById(R.id.listView);
        String result = null;
        DownloadContent task = new DownloadContent();
        try{
            result= task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();

            //get the id of the top articles
            JSONArray jsonArray = new JSONArray(result);

            int numberOfItems = 20;

            for (int i=0;i < numberOfItems; i++) {
                String articleId = jsonArray.getString(i);
                addToSql(articleId);
                Log.i("ArticleId", articleId);
            }

            addToList();

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview, titleList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                    intent.putExtra("url", urlList.get(i));
                    startActivity(intent);
                }
            });

        } catch(Exception e){
            e.printStackTrace();
        }

    }//end onCreate method


}//end MainActivity class