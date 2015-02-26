package com.example.jongwookim.camera_app2;

import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jongwookim on 2/11/15.
 */
public class AlbumActivity extends ActionBarActivity {
    private File mTargetDirector;
    private File[] mFiles;
    protected static ArrayList<Photo_Image> mImages = new ArrayList<Photo_Image>();
    //where photo is stored, /DCIM/Photo is manually specified location
    private String msdCardDir = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        mTargetDirector = new File(get_path());
        mFiles = mTargetDirector.listFiles();

        //add images to Photo_Image array
        for(int i = 0;i < mFiles.length;i++) {
            mImages.add(new Photo_Image(mFiles[i].getName(), mFiles[i].getAbsolutePath()));
        }

        //using gridview and customized grid_adapter
        GridView gridView;
        Grid_Adapter adapter = new Grid_Adapter(AlbumActivity.this, R.layout.image_view, mImages);
        gridView = (GridView) findViewById(R.id.grid_view);
        if (gridView != null) {
            gridView.setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "its null!", Toast.LENGTH_LONG).show();
        }
    }

    //simple path call function
    public String get_path() {
        return msdCardDir;
    }
}
