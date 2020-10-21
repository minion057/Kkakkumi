package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.ByteBuffer;

import Khack.Q.Kkakkumi.Service.RecordService;

public class recordActivity extends AppCompatActivity {

    private MediaProjectionManager mpManager;
    private MediaProjection mProjection;
    private static final int REQUEST_MEDIA_PROJECTION = 1001;

    private int displayWidth, displayHeight;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;
    private int screenDensity;
    private ImageView imageView;

    Intent inte;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.stopService(inte);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        inte = new Intent(this, RecordService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            this.startForegroundService(inte);
        }
        else {
            this.startService(inte);
        }

        Button button = findViewById(R.id.btn_re);
        button.setOnClickListener(view -> getScreenshot());

        imageView = findViewById(R.id.imgView_re);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenDensity = displayMetrics.densityDpi;
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        mpManager = (MediaProjectionManager)
                getSystemService(MEDIA_PROJECTION_SERVICE);

        if(mpManager != null){
            startActivityForResult(mpManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_MEDIA_PROJECTION == requestCode) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this,
                        "User cancelled", Toast.LENGTH_LONG).show();
                return;
            }
            setUpMediaProjection(resultCode, data);
        }
    }

    private void setUpMediaProjection(int code, Intent intent) {
        mProjection = mpManager.getMediaProjection(code, intent);
        setUpVirtualDisplay();
    }

    private void setUpVirtualDisplay() {
        imageReader = ImageReader.newInstance(
                displayWidth, displayHeight, PixelFormat.RGBA_8888, 2);

        virtualDisplay = mProjection.createVirtualDisplay("ScreenCapture",
                displayWidth, displayHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);
    }

    private void getScreenshot() {
        Log.d("debug", "getScreenshot");

        Image image = imageReader.acquireLatestImage();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();

        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * displayWidth;

        Bitmap bitmap = Bitmap.createBitmap(
                displayWidth + rowPadding / pixelStride, displayHeight,
                Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();

        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        if (virtualDisplay != null) {
            Log.d("debug","release VirtualDisplay");
            virtualDisplay.release();
        }
        super.onDestroy();
    }
}