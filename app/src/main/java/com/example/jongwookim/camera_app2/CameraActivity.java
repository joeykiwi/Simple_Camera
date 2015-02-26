package com.example.jongwookim.camera_app2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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

    Camera mcamera;
    Camera.Parameters params;
    boolean mpreviewing = false;
    //used for overlay layout
    LayoutInflater mcontrolInflater = null;
    SurfaceHolder surfaceHolder;

    Button snapButton;
    ToggleButton previewButton;
    ToggleButton silentButton;
    boolean silent = false;

    String DEBUG_TAG = "Joey";

    //cameraId is used for front and back camera
    int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    float mDist;
    int zoomController = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //button overlay procedure
        mcontrolInflater = LayoutInflater.from(getBaseContext());
        View viewControl = mcontrolInflater.inflate(R.layout.control, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParams);

        //getting holder for surfaceview (for camera use)
        //override function locates below
        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.snap_Frame);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //preview button
        previewButton = (ToggleButton) findViewById(R.id.preview_button);
        previewButton.setChecked(true);
        previewButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mcamera.startPreview();
                } else {
                    mcamera.stopPreview();
                }
            }
        });

        //silent button
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


//        used for auto focus on surface touch but it is currently commented out because of zoom feature

//        LinearLayout cameraSurface = (LinearLayout) findViewById(R.id.control_backgroud);
//        cameraSurface.setOnClickListener(new LinearLayout.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                snapButton.setEnabled(false);
//                mcamera.autoFocus(autoFocusCallback);
//            }
//        });


        //sanp button
        snapButton = (Button) findViewById(R.id.snap_button);
        snapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                if (silent) {
                    mcamera.takePicture(null, rawCallback, jpgCallback);
                } else {
                    mcamera.takePicture(sCallback, rawCallback, jpgCallback);
                }
            }

        });

        //back and front camera switching button
        ToggleButton selfieButton = (ToggleButton) findViewById(R.id.selfie_button);
        selfieButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mcamera.stopPreview();
                    mcamera.release();
                    mcamera = null;

                    cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

                    mcamera = mcamera.open(cameraId);
                    mcamera.setDisplayOrientation(90);
                    try {
                        mcamera.setPreviewDisplay(surfaceHolder);
                        mcamera.startPreview();
                    } catch(IOException ie) {
                        ie.getStackTrace();
                    }

                } else {
                    mcamera.stopPreview();
                    mcamera.release();
                    mcamera = null;

                    cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

                    mcamera = mcamera.open(cameraId);
                    mcamera.setDisplayOrientation(90);
                    try {
                        mcamera.setPreviewDisplay(surfaceHolder);
                        mcamera.startPreview();
                    } catch(IOException ie) {
                        ie.getStackTrace();
                    }

                }
            }
        });

        //flash button
        final ToggleButton flashButton = (ToggleButton) findViewById(R.id.flash_button);
        flashButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (cameraId != Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        params = mcamera.getParameters();
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mcamera.setParameters(params);
                    } else {
                        Toast.makeText(getApplicationContext(), "only for back camera", Toast.LENGTH_SHORT).show();
                        flashButton.setChecked(false);
                    }

                } else {
                    params = mcamera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mcamera.setParameters(params);

                }
            }
        });
    }


//    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
//        @Override
//        public void onAutoFocus(boolean success, Camera camera) {
//            snapButton.setEnabled(true);
//        }
//    };


    //this function is called when camera takes a picture
    Camera.PictureCallback jpgCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            previewButton.setChecked(false);

            //create save button when camera takes a picture and stops preview with the snapped image
            mcontrolInflater = LayoutInflater.from(getBaseContext());
            View viewControl = mcontrolInflater.inflate(R.layout.save, null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            getWindow().addContentView(viewControl, layoutParams);

            //save button
            Button saveButton = (Button) findViewById(R.id.save_button);
            saveButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //image location and saving procedure
                    String msdCardDir = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
                    Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap finalBitmap = rotatePortrait(bitmapPicture);


                    //timestamp used for unique image name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File takenImage = new File(msdCardDir, timeStamp + "_Image.jpeg");

                    FileOutputStream outStream;
                    try {
                        //procedure of compressing bitmap images to JPEG image files
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
            //this manages shutter but it is not currently used, I basically turned off this callback function
            //when silent mode is applied
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

    //this function manages motion event of finger (pinch mode)
    //currently only double finger usage works (zoom in/out feature)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Camera.Parameters eventParameter = mcamera.getParameters();
        int action = MotionEventCompat.getActionMasked(event);

        if (event.getPointerCount() > 1) {
            switch (action) {
                case (MotionEvent.ACTION_POINTER_DOWN):
                    Log.d(DEBUG_TAG, "Action pointer Down");
                    mDist = getFingerSpacing(event);
                    break;
                case (MotionEvent.ACTION_MOVE):
                    Log.d(DEBUG_TAG, "Action pointer move");
                    handleZoom(event, params);
                    break;
            }
        }
        return true;
    }

    //where actually zoom is controlled
    private void handleZoom(MotionEvent event, Camera.Parameters parameters) {
        int maxZoom = parameters.getMaxZoom();
        int zoom = parameters.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom) {
                zoomController++;
                if (zoomController % 3 == 1) {
                    zoom++;
                }
            }

        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0) {
                zoomController--;
                if (zoomController % 3 == 1) {
                  zoom--;
                }
            }
        }
        mDist = newDist;
        parameters.setZoom(zoom);
        mcamera.setParameters(parameters);
    }

    //get position of two finger and amount of zoom
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //currently, the image is taken landscape mode and I manually change its matrix to show like portrait
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
        //where camera button is clicked, surface is created and also camera opens
        mcamera = mcamera.open(cameraId);
        params = mcamera.getParameters();
//        params.set("orientation", "portrait");
//        mcamera.setParameters(params);
        mcamera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //this actually starts preview
        if (mpreviewing) {
            mcamera.stopPreview();
            mpreviewing = false;
        }

        if (mcamera != null) {
            try {
                mcamera.setPreviewDisplay(surfaceHolder);
                mcamera.startPreview();
                mpreviewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //when surface destroyed, I thought this is like deconstructor
        mcamera.stopPreview();
        mcamera.release();
        mcamera = null;
        mpreviewing = false;
    }
}