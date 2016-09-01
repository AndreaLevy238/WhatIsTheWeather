package com.example.andrea.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

   EditText cityName;
   TextView main;
   TextView description;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      cityName = (EditText) findViewById(R.id.cityName);
      main = (TextView) findViewById(R.id.main);
      description = (TextView) findViewById(R.id.description);
   }

   public void askWeather(View view)
   {
      String city = compressString(cityName.getText().toString());
      Log.i("city Name", city);
      String url = "http://api.openweathermap.org/data/2.5/weather?appid=5f2bb81ca36aad646208c2ef0be69311&q=" + city;
      InputMethodManager mgr= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
      DownloadTask downloadTask = new DownloadTask();
      downloadTask.execute(url);
   }


   public String compressString(String in)
   {
      String[] splitString = in.split(" ");
      String out = "";
      for (String aSplitString : splitString) {
         out += aSplitString;
      }
      return out;
   }

   public class DownloadTask extends AsyncTask<String, Void, String> {
      @Override
      protected String doInBackground(String... urls) {
         String result = "";
         URL url;
         HttpURLConnection urlConnection;
         try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();
            while (data != -1) {
               char current = (char) data;
               result += current;
               data = reader.read();
            }
            return result;
         } catch (MalformedURLException e) {
            Log.e("URL", "not a valid city name");
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
         return null;
      }

      @Override
      protected void onPostExecute(String result) {
         try {
            JSONObject jsonObject = new JSONObject(result);
            String weatherInfo =jsonObject.getString("weather");
            JSONArray jsonArray = new JSONArray(weatherInfo);
            String mainText = "";
            String descriptionText = "";
            for (int i = 0; i < jsonArray.length(); i++)
            {
               JSONObject jsonPart = jsonArray.getJSONObject(i);
               mainText = jsonPart.getString("main");
               descriptionText = jsonPart.getString("description");
               Log.i("main", mainText);
               Log.i("description", descriptionText);
            }
            main.setText(mainText);
            description.setText(descriptionText);
         } catch (JSONException e) {
            e.printStackTrace();
         }

      }

   }




}
