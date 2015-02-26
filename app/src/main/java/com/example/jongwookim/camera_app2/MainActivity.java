package com.example.jongwookim.camera_app2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

//main activity simply starts album activity or camera activity
//      //////////
//      //      //----->camera activity
//      // main //
//      //      //----->album activity
//      //////////
public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(cameraIntent);

            }
        });

        Button albumButton = (Button) findViewById(R.id.album_button);
        albumButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                startActivity(albumIntent);
            }
        });


        Button instaButton = (Button) findViewById(R.id.insta_button);
        instaButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent instaIntent = new Intent(MainActivity.this, InstaActivity.class);
               startActivity(instaIntent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

