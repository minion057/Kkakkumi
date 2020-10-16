package Khack.Q.Kkakkumi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Khack.Q.Kkakkumi.Service.RecordService;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class Test4Activity extends AppCompatActivity {

    //<editor-fold desc="변수 선언">
    Preview preview;
    Camera camera;
    PreviewView viewFinder;
    TextView txt_notice;

    ImageAnalysis imageAnalysis;
    ExecutorService cameraExecutor;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    String videoFile;
    private MediaProjection mediaProjection;
    private static final int REQUEST_CODE_MediaProjection = 101;
    Intent inte;
    DisplayMetrics displayMetrics;

    Context cont;
    RelativeLayout Rela;
    ImageView imageLE; //추가할 이미지 변수 여기에 넣기

    //<editor-fold desc="gif 재생">
    GifImageView gif;
    GifDrawable gifFromResource;
    Boolean recordAnswer = false, gifplay = false;
    Integer gifstopPosition = 0;
    //</editor-fold>

    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);

        //화면 꺼짐 방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //<editor-fold desc="변수 값 넣기">
        cont = this;
        Rela = findViewById(R.id.e_testre);
        imageLE = new ImageView(cont);
        Rela.addView(imageLE);

        //<editor-fold desc="camerX">
        viewFinder = findViewById(R.id.e_viewFinder);

        cameraExecutor = Executors.newSingleThreadExecutor();
        //</editor-fold>

        txt_notice = findViewById(R.id.e_notice);

        //<editor-fold desc="gif">
        gif = findViewById(R.id.edu_gif);
        try {
            gifFromResource = new GifDrawable( getResources(), R.raw.testgif );
            gif.setImageDrawable(gifFromResource);
            gifFromResource.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        //</editor-fold>

        //<editor-fold desc="recordvideo">

        //videoFile = setvideoname();

        videoFile = setvideoname();

        inte = new Intent(this, RecordService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            this.startForegroundService(inte);
        }
        else {
            this.startService(inte);
        }

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        //<editor-fold desc="camerX">
        startCamera();
        //</editor-fold>

        startrecord();
        //</editor-fold>
    }

    //<editor-fold desc="recordvideo">
    public String setvideoname(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");
        String menu="";
        switch (getIntent().getExtras().getInt("menu")){
            case 0: //양치
                menu = "양치";
                break;
            case 1: //손씻기
                menu = "손씻기";
                break;
            case 2: //기침막기
                menu = "기침막기";
                break;
            case 3: //마스크
                menu = "마스크";
                break;
            default:
                Log.d("V_Error", "menu No : "+String.valueOf(getIntent().getExtras().getInt("menu")));
                break;
        }
        String name = this.getExternalFilesDir(Environment.DIRECTORY_MOVIES).toString()
                        + "/" + mFormat.format(date) + "_" + menu + ".mp4";
        return name;
    }

    public void startrecord(){
        // 미디어 프로젝션 요청
        startMediaProjection();
    }

    public void stoprecord(){
        if (mediaProjection != null) {
            mediaProjection.stop();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(videoFile), "video/mp4");
            //startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        // 녹화중이면 종료하기
        if (mediaProjection != null) {
            mediaProjection.stop();
        }

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mediaProjection == null) return;

        stoprecord();

        try{
            File file = new File(videoFile);
            if(file.exists()){
                file.delete();
                Toast.makeText(Test4Activity.this, "교육 미완료로 녹화중이던 비디오 삭제!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.d("Video",e.getMessage());
            Toast.makeText(Test4Activity.this, "교육 미완료로 녹화중이던 비디오 삭제 실패!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable final Intent data) {
        // 미디어 프로젝션 응답
        recordAnswer = true;
        if (requestCode == REQUEST_CODE_MediaProjection && resultCode == RESULT_OK) {
            screenRecorder(resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 1. 미디어 프로젝션 요청
     */
    private void startMediaProjection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_MediaProjection);
        }
    }

    /**
     * 화면녹화
     *
     * @param resultCode
     * @param data
     */
    private void screenRecorder(int resultCode, @Nullable Intent data) {
        final MediaRecorder screenRecorder = createRecorder();
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        MediaProjection.Callback callback = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
                if (screenRecorder != null) {
                    screenRecorder.stop();
                    screenRecorder.reset();
                    screenRecorder.release();
                }
                mediaProjection.unregisterCallback(this);
                mediaProjection = null;
            }
        };
        mediaProjection.registerCallback(callback, null);
        mediaProjection.createVirtualDisplay(
                "sample",
                displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                screenRecorder.getSurface(), null, null);

        //녹화한 영상 재생
        screenRecorder.start();
    }

    /**
     * 미디어 레코더
     *
     * @return
     */
    private MediaRecorder createRecorder() {
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(videoFile);
        mediaRecorder.setVideoSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);//.QUALITY_HIGH);
        mediaRecorder.setVideoEncodingBitRate(cpHigh.videoBitRate);
        mediaRecorder.setVideoFrameRate(cpHigh.videoFrameRate);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.e("prepare", "IllegalStateException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("prepare", "IOException : " + e.getMessage());
        }
        return mediaRecorder;
    }
    //</editor-fold>

    //<editor-fold desc="camerX">
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 카메라 전환시 새로운 카메라 배당
                // 이때 카메라에 연결된 bind(생명주기)가 있으면 안되므로
                // 연결된 bind 모두 해제
                cameraProvider.unbindAll();

                //<editor-fold desc="firebase ML_이미지 분석 > 1초마다 실행되는 부분">
                // .setTargetResolution(new Size(1280, 720))
                // 있어야 원활하게 실행됨 (got it 뜨는게 한번만 뜸)
                imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                // 1. 한번만 실행
                /*
                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    int rotationDegrees = image.getImageInfo().getRotationDegrees();
                    // insert your code here.
                    Log.d("Analy", "Average luminosity: "+rotationDegrees);

                    FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                            new FirebaseVisionFaceDetectorOptions.Builder()
                                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                    .build();

                });*/

                // 2. 여러번 실행 > ImageAnalysis.Analyzer class 부분을 따로 만들기
                imageAnalysis.setAnalyzer(cameraExecutor, new LuminosityAnalyzer2());

                //</editor-fold>

                preview = new Preview.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.getCameraInfo()));
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Camera", "카메라 : "+e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }
    //</editor-fold>

    public class LuminosityAnalyzer2 implements ImageAnalysis.Analyzer {

        //<editor-fold desc="firebase">
        // Real-time contour detection of multiple faces
        // 실시간 여러 얼굴의 윤곽선 검출
        FirebaseVisionFaceDetectorOptions realTimeOpts;

        FirebaseVisionImage img_f = null;
        FirebaseVisionFaceDetector detector = null;
        Task<List<FirebaseVisionFace>> result = null;
        //</editor-fold>

        private int degreesToFirebaseRotation(int degrees) {
            switch (degrees) {
                case 0:
                    return FirebaseVisionImageMetadata.ROTATION_0;
                case 90:
                    return FirebaseVisionImageMetadata.ROTATION_90;
                case 180:
                    return FirebaseVisionImageMetadata.ROTATION_180;
                case 270:
                    return FirebaseVisionImageMetadata.ROTATION_270;
                default:
                    throw new IllegalArgumentException(
                            "Rotation must be 0, 90, 180, or 270.");
            }
        }

        @SuppressLint("UnsafeExperimentalUsageError")
        @Override
        public void analyze(@NonNull ImageProxy imageP) {
            // Real-time contour detection of multiple faces
            // 실시간 여러 얼굴의 윤곽선 검출
            realTimeOpts =
                    new FirebaseVisionFaceDetectorOptions.Builder()
                            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                            .build();

            // 1.
            if (imageP == null || imageP.getImage() == null) {
                Log.e("Analy", "No Image");
                return;
            }

            try{
                img_f = FirebaseVisionImage.fromMediaImage(imageP.getImage(),
                        degreesToFirebaseRotation(imageP.getImageInfo().getRotationDegrees()));
                detector = FirebaseVision.getInstance().getVisionFaceDetector(realTimeOpts);

                Point p = new Point();
                Display display = getWindowManager().getDefaultDisplay();
                display.getSize(p);

                result = detector.detectInImage(img_f)
                        .addOnSuccessListener(
                                faces -> {
                                    Log.d("Test", "start");
                                    if(recordAnswer && mediaProjection != null){
                                        if(faces.size() <= 0){
                                            txt_notice.setText("얼굴을 보여줘!");
                                            txt_notice.setVisibility(View.VISIBLE);

                                            imageLE.setVisibility(View.VISIBLE);
                                            imageLE.setImageResource(R.drawable.backgrond_btnstart);
                                            imageLE.setX(p.x / 2 -400);
                                            imageLE.setY(p.y /2 - 600);
                                            imageLE.setLayoutParams(new RelativeLayout.LayoutParams(800, 800));

                                            gif.setVisibility(View.INVISIBLE);
                                            if(gifplay) {
                                                gifplay = false;
                                                gifstopPosition = gifFromResource.getCurrentPosition();
                                                gifFromResource.stop();
                                            }
                                            Log.d("Test","No Face");
                                        }
                                        for (FirebaseVisionFace face : faces) {
                                            txt_notice.setText("잘하고 있어!");
                                            imageLE.setVisibility(View.INVISIBLE);
                                            checkgifend();
                                            gif.setVisibility(View.VISIBLE);
                                            Log.d("Test","Face bounds : " + face.getBoundingBox());
                                        /*
                                        List<FirebaseVisionPoint> leftEyeContour =
                                                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                        if(leftEyeContour.size() <= 0 ) return;

                                        for(FirebaseVisionPoint fp : leftEyeContour){
                                            imageLE.setImageResource(R.drawable.virus);
                                            imageLE.setX(p.x * fp.getX() / imageP.getWidth() + 100);
                                            imageLE.setY(p.y * fp.getY() / imageP.getHeight() + 90);
                                            imageLE.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            break;
                                        }
                                         */
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                e -> Log.d("Test", "fail"));
            }catch (Exception ex){
                Log.e("Error", "Why : "+ex.getMessage());
            }
            // 4.
            imageP.close();
        }
    }

    public void checkgifend(){
        if (gifstopPosition >= 4670
                || gifFromResource.getCurrentPosition() >= 4670){

            if(gifplay) {
                gifplay = false;
                gifstopPosition = gifFromResource.getCurrentPosition();
                gifFromResource.stop();

                //gif 재생 끝남 보상 스티커 팝업
                Intent intent = new Intent(getApplicationContext(), CharacterInfoActivity.class);
                startActivityForResult(intent,1);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        stoprecord();
                        finish();
                    }
                }, 2000); //-2초
            }
        }else{
            if(!gifplay) {
                gifplay = true;
                gifFromResource.seekTo(gifstopPosition);
                gifFromResource.start();
            }
        }

    }
}