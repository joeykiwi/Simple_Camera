package com.example.jongwookim.camera_app2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jongwookim on 2/26/15.
 */
public class InstaActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta);

        new HttpAsyncTask().execute();
    }

    public class HttpAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {

        private String mAccessToken = "136440246.1fb234f.b1a9b21d5d4142b6b2198cae0c6488e8";
        private JSONObject jArray;

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            try {
                URL url = new URL("https://api.instagram.com/v1/users/1364402426/media/recent?access_token=1364402426.1fb234f.b1a9b21d5d4142b6b2198cae0c6488e8");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                String response = streamToString(httpURLConnection.getInputStream());
                Log.d("Joey", response);
                jArray = (JSONObject) new JSONTokener(response).nextValue();
                String username = jArray.getJSONObject("user").getString("username");
                Log.d("Joey", username);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jArray;

//            InputStream inputStream = null;
//            String result = "";
//            try {
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpResponse httpResponse = httpClient.execute(new HttpPost("http://api.instagram.com/v1/users/1364402426/media/recent?access_token=1364402426.1fb234f.b1a9b21d5d4142b6b2198cae0c6488e8"));
//                inputStream = httpResponse.getEntity().getContent();
//
//                String line = "";
//                if (inputStream != null) {
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuilder sb = new StringBuilder();
//
//                    while ((line = bufferedReader.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    inputStream.close();
//                    result = sb.toString();
//                    Log.d("Joey", result);
//                } else {
//                    Log.d("Joey", "did not work");
//
//                }
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                jArray = new JSONObject(result);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return jArray;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

        }

        public String streamToString(InputStream is) throws IOException {
            String string = "";

            if (is != null) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is));

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    reader.close();
                } finally {
                    is.close();
                }

                string = stringBuilder.toString();
            }

            return string;
        }
    }
}
