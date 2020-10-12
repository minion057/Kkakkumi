package Khack.Q.Kkakkumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

    //<editor-fold desc="변수 선언">
    Preview preview;
    Camera camera;
    PreviewView viewFinder;

    ImageCapture imageCapture;
    ImageAnalysis imageAnalysis;
    ExecutorService cameraExecutor;

    int select_c = 0;
    String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    File outputVideo, outputPicture;
    Button btn_capture, btn_change, btn_record;
    Boolean recording;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    MediaRecorder mediaRecorder;
    SurfaceView surfaceView;
    File vFile;

    TextView txt_notice;

    //</editor-fold>

    Context cont;
    RelativeLayout Rela;
    ImageView imageLE;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //<editor-fold desc="변수 값 넣기">
        cont = this;
        Rela = findViewById(R.id.testre);
        imageLE = new ImageView(cont);
        txt_notice = findViewById(R.id.edu_notice);

        //<editor-fold desc="camerX">
        // 찍기 위한 클릭리스너
        btn_capture = findViewById(R.id.camera_btn_capture);
        btn_capture.setOnClickListener(view ->  takePhoto());
        // 카메라 전환을 위한 클릭리스너
        btn_change = findViewById(R.id.camera_btn_change);
        btn_change.setOnClickListener(view ->  {
            select_c = select_c >= 1 ? 0 : 1;
            startCamera();
        });

        viewFinder = findViewById(R.id.viewFinder);

        // 사진 저장 경로
        outputPicture = new File(cont.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");
        makefolder(outputPicture);

        cameraExecutor = Executors.newSingleThreadExecutor();
        //</editor-fold>

        //</editor-fold>

        // camerax start
        startCamera();
    }

    //<editor-fold desc="파일 경로 생성">
    public void makefolder(File dir){
        try {
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d("File : ", dir.toString() + " 경로 생성 시도");
            }
        } catch (Exception e) {
            Log.e("File : ", e.getMessage());
        }
        if (dir == null || !dir.exists()) { Log.d("File : ", dir.toString() + "경로 생성 실패"); }
        else { Log.d("File : ", dir.toString() +"경로 생성 성공"); }
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

                //<editor-fold desc="firebase ML_캡처">
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();
                //</editor-fold>

                //<editor-fold desc="firebase ML_이미지 분석 > 1초마다 실행되는 부분">
                // .setTargetResolution(new Size(1280, 720))
                // 있어야 원활하게 실행됨 (got it 뜨는게 한번만 뜸)
                imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                //<editor-fold desc="1. 한번만 실행">
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
                //</editor-fold>

                // 2. 여러번 실행 > ImageAnalysis.Analyzer class 부분을 따로 만들기
                imageAnalysis.setAnalyzer(cameraExecutor, new LuminosityAnalyzer2());

                //</editor-fold>

                preview = new Preview.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                if(select_c == 1){
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();
                }

                camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture, imageAnalysis);
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.getCameraInfo()));
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Camera", "카메라 : "+e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        File photoFile = new File(outputPicture, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback(){

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri saveUri = Uri.fromFile(photoFile);
                        String msg = "Photo capture succeeded: "+saveUri;
                        Log.d("Camera Save", msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e("Camera", "Photo capture failed: "+ exc.getMessage());
                    }
                });
    }
    //</editor-fold>


    //<editor-fold desc="camerax 얼굴 검출">
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
                                    if(faces.size() <= 0){
                                        //drawface(null);
                                        txt_notice.setText("얼굴을 보여줘!");
                                        txt_notice.setVisibility(View.VISIBLE);

                                        imageLE.setImageResource(R.drawable.backgrond_btnstart);
                                        imageLE.setX(p.x / 2 -400);
                                        imageLE.setY(p.y /2 - 600);
                                        imageLE.setLayoutParams(new RelativeLayout.LayoutParams(800, 800));

                                        Log.d("Test","No Face");
                                    }
                                    for (FirebaseVisionFace face : faces) {
                                        //drawface(face);
                                        txt_notice.setText("잘하고 있어!");

                                        Log.d("Test","Face bounds : " + face.getBoundingBox());
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
    //</editor-fold>
}