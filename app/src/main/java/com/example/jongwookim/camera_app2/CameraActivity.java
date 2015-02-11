package com.example.jongwookim.camera_app2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




public class CameraActivity extends ActionBarActivity implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    String sdCardDir = Environment.getExternalStorageDirectory().toString() + "/DCIM/Photo";
    private GridView gridView;
    private File targetDirector;
    private File[] files;
    protected static ArrayList<Photo_Image> images = new ArrayList<Photo_Image>();

    Button snapButton;
    ToggleButton previewButton;
    ToggleButton silentButton;
    boolean silent = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParams);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.snap_Frame);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        previewButton = (ToggleButton) findViewById(R.id.preview_button);
        previewButton.setChecked(true);
        previewButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    camera.startPreview();
                } else {
                    camera.stopPreview();
                }
            }
        });

        silentButton = (ToggleButton) findViewById(R.id.silent_button);
        silentButton.setChecked(false);
        silentButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    silent = true;
                } else {
                    silent = false;
                }
            }
        });



        LinearLayout cameraSurface = (LinearLayout) findViewById(R.id.control_backgroud);
        cameraSurface.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapButton.setEnabled(false);
                camera.autoFocus(autoFocusCallback);
            }
        });



        snapButton = (Button) findViewById(R.id.snap_button);
        snapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                if (silent) {
                    camera.takePicture(null, rawCallback, jpgCallback);
                } else {
                    camera.takePicture(sCallback, rawCallback, jpgCallback);
                }
            }

        });
    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            snapButton.setEnabled(true);
        }
    };

    Camera.PictureCallback jpgCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            previewButton.setChecked(false);

            controlInflater = LayoutInflater.from(getBaseContext());
            View viewControl = controlInflater.inflate(R.layout.save, null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            getWindow().addContentView(viewControl, layoutParams);

            Button saveButton = (Button) findViewById(R.id.save_button);
            saveButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap finalBitmap = rotatePortrait(bitmapPicture);


                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File takenImage = new File(sdCardDir, timeStamp + "_Image.jpeg");

                    FileOutputStream outStream;
                    try {
                        outStream = new FileOutputStream(takenImage);
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "The photo will save as " + takenImage.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    };



    Camera.ShutterCallback sCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            int streamType = AudioManager.STREAM_SYSTEM;
            mgr.setStreamSolo(streamType, true);
            mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mgr.setStreamMute(streamType, true);
            //do nothing yet
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //do nothing yet
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private Bitmap rotatePortrait(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = camera.open();
        camera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
}